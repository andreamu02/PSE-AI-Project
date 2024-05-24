package it.unipd.dei.pseaiproject

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import it.unipd.dei.pseaiproject.ml.ObjectDetectionModel
import org.tensorflow.lite.support.image.TensorImage
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {

    // View per mostrare il feed della fotocamera
    private lateinit var previewView: PreviewView

    // View per mostrare le rilevazioni degli oggetti
    private lateinit var overlayView: DetectionOverlayView

    // Executor per eseguire operazioni della fotocamera in un thread separato
    private val cameraExecutor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        previewView = findViewById(R.id.previewView)
        overlayView = findViewById(R.id.overlayView)

        // Controlla se i permessi sono concessi, altrimenti richiedili
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions.launch(arrayOf(Manifest.permission.CAMERA))
        }
    }

    // Richiesta permessi fotocamera
    private val requestPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions.all { it.value }) {
            startCamera()
        } else {
            Toast.makeText(this, "Permessi non concessi dall'utente.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    // Controlla se tutti i permessi sono concessi
    private fun allPermissionsGranted() = arrayOf(Manifest.permission.CAMERA).all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    // Inizializza la fotocamera
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Configura la preview della fotocamera
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            // Configura la cattura delle immagini
            val imageCapture = ImageCapture.Builder().build()

            // Configura l'analisi delle immagini
            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        val bitmap = imageProxyToBitmap(imageProxy)
                        detectObjects(bitmap)
                        imageProxy.close()
                    }
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Scollega tutti i casi d'uso e collega quelli nuovi
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer
                )
            } catch (exc: Exception) {
                Log.e("CameraXApp", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    // Converte un oggetto ImageProxy in un Bitmap
    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        yBuffer.get(nv21, 0, ySize)
        uBuffer.get(nv21, ySize, uSize)
        vBuffer.get(nv21, ySize + uSize, vSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 100, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    // Rileva oggetti in un Bitmap e aggiorna l'overlay
    private fun detectObjects(bitmap: Bitmap) {
        // Inizializza il modello
        val model = ObjectDetectionModel.newInstance(this)

        // Crea gli input per il modello
        val image = TensorImage.fromBitmap(bitmap)

        // Esegui l'inferenza del modello e ottieni i risultati
        val outputs = model.process(image)
        val locations = outputs.locationsAsTensorBuffer
        val classes = outputs.classesAsTensorBuffer
        val scores = outputs.scoresAsTensorBuffer
        val numberOfDetections = outputs.numberOfDetectionsAsTensorBuffer

        val detections = mutableListOf<DetectionOverlayView.Detection>()

        // Elabora i risultati e aggiungi le bounding box
        val numberOfObjects = numberOfDetections.intArray[0]
        for (i in 0 until numberOfObjects) {
            val score = scores.floatArray[i]
            if (score > 0) {
                val location = locations.floatArray.sliceArray(i * 4 until (i + 1) * 4)
                val left = location[0] * bitmap.width
                val top = location[1] * bitmap.height
                val right = location[2] * bitmap.width
                val bottom = location[3] * bitmap.height

                val detectedClass = classes.intArray[i]
                val label = "Class: $detectedClass, Score: ${"%.2f".format(score)}"
                detections.add(DetectionOverlayView.Detection(left, top, right, bottom, label))
            }
        }

        // Aggiorna l'overlay con le rilevazioni
        runOnUiThread {
            overlayView.setDetections(detections)
        }

        model.close()
    }
}
