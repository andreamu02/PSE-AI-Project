package it.unipd.dei.pseaiproject.ui

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import it.unipd.dei.pseaiproject.R
import it.unipd.dei.pseaiproject.DetectionOverlayView
import it.unipd.dei.pseaiproject.detection.ObjectDetectorHelper
import org.tensorflow.lite.task.vision.detector.Detection
import java.util.LinkedList
import java.util.concurrent.Executors

class CameraFragment : Fragment(), ObjectDetectorHelper.DetectorListener {

    // View per mostrare il feed della fotocamera
    private lateinit var previewView: PreviewView

    // View per mostrare le rilevazioni degli oggetti
    private lateinit var overlayView: DetectionOverlayView

    // Executor per eseguire operazioni della fotocamera in un thread separato
    private val cameraExecutor = Executors.newSingleThreadExecutor()

    private var lastTimeUpdateScreen = 0L

    // Variabili per il buffer di bitmap, l'analisi delle immagini e l'helper per il rilevamento degli oggetti
    private lateinit var bitmapBuffer: Bitmap
    private var imageAnalyzer: ImageAnalysis? = null
    private lateinit var objectDetectorHelper: ObjectDetectorHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        previewView = view.findViewById(R.id.previewView)
        overlayView = view.findViewById(R.id.overlayView)

        // Controlla se i permessi sono concessi, altrimenti richiedili
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions.launch(arrayOf(Manifest.permission.CAMERA))
        }

        objectDetectorHelper = ObjectDetectorHelper(
            context = requireContext(),
            objectDetectorListener = this)
    }

    // Metodo per gestire la richiesta dei permessi della fotocamera
    private val requestPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions.all { it.value }) {
            startCamera()
        } else {
            Toast.makeText(requireContext(), "Permessi non concessi dall'utente.", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }
    }

    // Metodo per verificare se tutti i permessi necessari sono stati concessi
    private fun allPermissionsGranted() = arrayOf(Manifest.permission.CAMERA).all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    // Metodo per avviare la fotocamera
    private fun startCamera() {
        // Ottiene il provider della fotocamera
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({

            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Seleziona la fotocamera posteriore come default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            // Imposta la strategia per la selezione della risoluzione
            val resolutionSelector = ResolutionSelector.Builder()
                .setAspectRatioStrategy(AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY)
                .build()

            // Configura la preview della fotocamera
            val preview = Preview.Builder()
                .setResolutionSelector(resolutionSelector)
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            // Configura l'analisi delle immagini
            imageAnalyzer = ImageAnalysis.Builder()
                .setResolutionSelector(resolutionSelector)
                .setTargetRotation(previewView.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { image ->
                        // Inizializza il buffer di bitmap solo una volta che l'analizzatore è in esecuzione
                        if (!::bitmapBuffer.isInitialized) {
                            bitmapBuffer = Bitmap.createBitmap(
                                image.width,
                                image.height,
                                Bitmap.Config.ARGB_8888
                            )
                        }
                        detectObjects(image)
                    }
                }

            // Scollega tutti i casi d'uso precedenti e collega quelli nuovi
            cameraProvider.unbindAll()

            try {
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer)

                preview.setSurfaceProvider(previewView.surfaceProvider)
            } catch (exc: Exception) {
                Log.e("CameraXApp", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    // Metodo per l'analisi delle immagini e il rilevamento degli oggetti
    private fun detectObjects(image: ImageProxy) {
        // Copia i bit RGB nel buffer bitmap condiviso
        image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }

        val imageRotation = image.imageInfo.rotationDegrees
        // Passa il Bitmap e la rotazione all'helper per il rilevamento e l'elaborazione degli oggetti
        objectDetectorHelper.detect(bitmapBuffer, imageRotation)
    }

    // Metodo chiamato quando la configurazione dell'attività cambia
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        imageAnalyzer?.targetRotation = previewView.display.rotation
    }

    // Metodo chiamato in caso di errore durante il rilevamento degli oggetti
    override fun onError(error: String) {
        requireActivity().runOnUiThread {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }
    }

    // Metodo chiamato quando ci sono risultati dal rilevamento degli oggetti
    override fun onResults(
        results: MutableList<Detection>?,
        inferenceTime: Long,
        imageHeight: Int,
        imageWidth: Int
    ) {
        activity?.runOnUiThread {
            if (isAdded) {
                overlayView.setResults(
                    results ?: LinkedList(),
                    imageHeight,
                    imageWidth
                )
                if (lastTimeUpdateScreen == 0L || (System.currentTimeMillis() - lastTimeUpdateScreen >= 250)) {
                    overlayView.invalidate()                    // Invalida la view per forzarne il ridisegno ogni 0.5 s
                    lastTimeUpdateScreen = System.currentTimeMillis()
                }
            }
        }
    }
}
