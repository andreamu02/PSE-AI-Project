package it.unipd.dei.pseaiproject

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector

//import org.tensorflow.lite.gpu.CompatibilityList

class ObjectDetectorHelper(
    private var threshold: Float = 0.5f,
    var numThreads: Int = 2,
    private var maxResults: Int = 3,
    var currentDelegate: Int = 0,
    val context: Context,
    val objectDetectorListener: DetectorListener?) {

    //TODO Lazy Val maybe
    private var objectDetector: ObjectDetector? = null

    init {
        setupObjectDetector()
    }

    private fun setupObjectDetector() {
        val optionsBuilder =
            ObjectDetector.ObjectDetectorOptions.builder()
                .setScoreThreshold(threshold)
                .setMaxResults(maxResults)

        // Imposta le opzioni generali di rilevamento, incluso il numero di thread utilizzati
        /*
        val baseOptionsBuilder = BaseOptions.builder().setNumThreads(numThreads)

        // Use the specified hardware for running the model. Default to CPU
        when (currentDelegate) {
            DELEGATE_CPU -> {
                // Default
            }
            DELEGATE_GPU -> {
                if (CompatibilityList().isDelegateSupportedOnThisDevice) {
                    baseOptionsBuilder.useGpu()
                } else {
                    objectDetectorListener?.onError("GPU is not supported on this device")
                }
            }
            DELEGATE_NNAPI -> {
                baseOptionsBuilder.useNnapi()
            }
        }

        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())
        */
        try {
            objectDetector =
                ObjectDetector.createFromFileAndOptions(context, "mobilenetv1.tflite", optionsBuilder.build())
        } catch (e: IllegalStateException) {
            objectDetectorListener?.onError(
                "Object detector failed to initialize. See error logs for details"
            )
            Log.e("Test", "TFLite failed to load model with error: " + e.message)
        }
    }

    // Metodo per eseguire la rilevazione degli oggetti sull'immagine
    fun detect(image: Bitmap, imageRotation: Int) {
        // Se l'oggetto ObjectDetector è nullo, prova a configurarlo
        if (objectDetector == null) {
            setupObjectDetector()
        }

        // Tempo di inferenza, calcolato come differenza tra il tempo di sistema all'inizio e alla fine del processo
        var inferenceTime = SystemClock.uptimeMillis()

        // Crea un ImageProcessor per l'immagine
        val imageProcessor = ImageProcessor.Builder()
            .add(Rot90Op(-imageRotation / 90))
            .build()

        // Preprocessa l'immagine e la converte in un TensorImage per la rilevazione
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(image))

        // Esegue la rilevazione degli oggetti
        val results = objectDetector?.detect(tensorImage)

        // Calcola il tempo di inferenza
        inferenceTime = SystemClock.uptimeMillis() - inferenceTime

        // Passa i risultati e altre informazioni al listener
        objectDetectorListener?.onResults(
            results,
            inferenceTime,
            tensorImage.height,
            tensorImage.width
        )
    }

    // Interfaccia per il listener degli eventi del rilevatore di oggetti
    interface DetectorListener {
        fun onError(error: String)
        fun onResults(
            results: MutableList<Detection>?,
            inferenceTime: Long,
            imageHeight: Int,
            imageWidth: Int
        )
    }
    /*
    companion object {
        const val DELEGATE_CPU = 0
        const val DELEGATE_GPU = 1
        const val DELEGATE_NNAPI = 2
    }*/
}