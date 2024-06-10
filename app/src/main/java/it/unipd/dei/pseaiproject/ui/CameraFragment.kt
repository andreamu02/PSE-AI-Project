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
import androidx.fragment.app.activityViewModels
import it.unipd.dei.pseaiproject.databinding.FragmentCameraBinding
import it.unipd.dei.pseaiproject.detection.DetectionOverlayView
import it.unipd.dei.pseaiproject.detection.ObjectDetectorHelper
import it.unipd.dei.pseaiproject.viewmodels.CameraViewModel
import org.tensorflow.lite.task.vision.detector.Detection
import java.util.LinkedList
import java.util.concurrent.Executors

/**
 * Un fragment che gestisce l'acquisizione video dalla fotocamera e il rilevamento degli oggetti.
 */
class CameraFragment : Fragment(), ObjectDetectorHelper.DetectorListener {

    private var binding: FragmentCameraBinding? = null
    private lateinit var previewView: PreviewView
    private lateinit var overlayView: DetectionOverlayView
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var lastTimeUpdateScreen = 0L
    private var results: MutableList<Detection>? = null
    private var previousResults: MutableList<Detection>? = null
    private val cameraViewModel: CameraViewModel by activityViewModels()
    private lateinit var bitmapBuffer: Bitmap
    private var imageAnalyzer: ImageAnalysis? = null
    private lateinit var objectDetectorHelper: ObjectDetectorHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        previewView = binding!!.previewView
        overlayView = binding!!.overlayView
        binding!!.overlayView

        // Controlla se i permessi sono concessi, altrimenti richiedili
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions.launch(arrayOf(Manifest.permission.CAMERA))
        }

        objectDetectorHelper = ObjectDetectorHelper(
            context = requireContext(),
            objectDetectorListener = this
        )

        cameraViewModel.setObjectDetectorHelper(objectDetectorHelper)
    }

    /**
     * Metodo chiamato per richiedere i permessi di accesso alla fotocamera.
     */
    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { it.value }) {
                startCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permessi non concessi dall'utente.",
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().finish()
            }
        }

    /**
     * Verifica se tutti i permessi necessari sono stati concessi.
     */
    private fun allPermissionsGranted() = arrayOf(Manifest.permission.CAMERA).all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Avvia la fotocamera.
     */
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({

            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            val resolutionSelector = ResolutionSelector.Builder()
                .setAspectRatioStrategy(AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY)
                .build()

            val preview = Preview.Builder()
                .setResolutionSelector(resolutionSelector)
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            imageAnalyzer = ImageAnalysis.Builder()
                .setResolutionSelector(resolutionSelector)
                .setTargetRotation(previewView.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { image ->
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

            cameraProvider.unbindAll()

            try {
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer
                )

                preview.setSurfaceProvider(previewView.surfaceProvider)
            } catch (exc: Exception) {
                Log.e("CameraXApp", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    /**
     * Analizza le immagini e rileva gli oggetti.
     */
    private fun detectObjects(image: ImageProxy) {
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

    // Metodo chiamato quando ci sono risultati dal rilevamento degli oggettii
    override fun onResults(
        results: MutableList<Detection>?,
        inferenceTime: Long,
        imageHeight: Int,
        imageWidth: Int,
        isVolumeOn: Boolean
    ) {
        activity?.runOnUiThread {
            if (isAdded) {
                this.results = results
                overlayView.setResults(
                    results ?: LinkedList(),
                    imageHeight,
                    imageWidth,
                    isVolumeOn
                )
                if (lastTimeUpdateScreen == 0L || (System.currentTimeMillis() - lastTimeUpdateScreen >= 5000 || resultsAreDifferent())) {
                    overlayView.invalidate()
                    lastTimeUpdateScreen = System.currentTimeMillis()
                }
                previousResults = this.results
            }
        }
    }

    /**
     * Verifica se i risultati ottenuti dal rilevamento degli oggetti sono diversi dai risultati precedenti.
     */
    private fun resultsAreDifferent(): Boolean {
        if (this.results == null && this.previousResults == null) {
            return false
        } else if ((this.results == null && this.previousResults != null) || (this.results != null && this.previousResults == null)) {
            return true
        }
        if (this.results!!.size != this.previousResults!!.size) {
            return true
        }
        val foundResults: MutableList<Detection> = mutableListOf()
        for (result in this.results!!) {
            var found = false
            for (previousResult in this.previousResults!!) {
                if (result !in foundResults && result.categories[0].label == previousResult.categories[0].label) {
                    found = true
                    foundResults.add(result)
                    break
                }
            }
            if (!found) {
                return true
            }
        }
        return false
    }


}
