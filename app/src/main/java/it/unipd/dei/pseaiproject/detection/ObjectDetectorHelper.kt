package it.unipd.dei.pseaiproject.detection

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector

class ObjectDetectorHelper(
    private var threshold: Float = 0.5f,
    private var numThreads: Int = 2,
    private var maxResults: Int = 3,
    private var currentDelegate: Int = DELEGATE_CPU,
    val context: Context,
    val objectDetectorListener: DetectorListener?) {

    private var objectDetector: ObjectDetector? = null

    init {
        setupObjectDetector()
    }

    private fun clearObjectDetector() {
        objectDetector = null
    }

    private fun setupObjectDetector() {
        val optionsBuilder =
            ObjectDetector.ObjectDetectorOptions.builder()
                .setScoreThreshold(threshold)
                .setMaxResults(maxResults)

        // Imposta le opzioni generali di rilevamento, incluso il numero di thread utilizzati
        val baseOptionsBuilder = BaseOptions.builder().setNumThreads(numThreads)

        // Usa l'hardware specificato per eseguire il modello. Predefinito su CPU
        when (currentDelegate) {
            DELEGATE_CPU -> {
                // Predefinito, non è necessario fare nulla
            }
            DELEGATE_GPU -> {
                if (CompatibilityList().isDelegateSupportedOnThisDevice) {
                    baseOptionsBuilder.useGpu()
                } else {
                    objectDetectorListener?.onError("GPU non è supportata su questo dispositivo")
                }
            }
            DELEGATE_NNAPI -> {
                baseOptionsBuilder.useNnapi()
            }
        }

        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        try {
            objectDetector =
                ObjectDetector.createFromFileAndOptions(context, "mobilenetv1.tflite", optionsBuilder.build())
        } catch (e: IllegalStateException) {
            objectDetectorListener?.onError(
                "Il rilevatore di oggetti non è riuscito a inizializzarsi. Vedi i log di errore per i dettagli"
            )
            Log.e("Test", "TFLite non è riuscito a caricare il modello con errore: " + e.message)
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

    fun setThreshold(threshold: Float){
        this.threshold = threshold
        clearObjectDetector()
        Log.d("BottomSheetFragmentUpdate", "Saved threshold: $this.threshold")
        setupObjectDetector()
    }

    fun setMaxResults(maxResults: Int){
        this.maxResults = maxResults
        clearObjectDetector()
        setupObjectDetector()
    }

    fun setDelegate(delegate: Int){
        when (delegate) {
            DELEGATE_CPU -> {
                this.currentDelegate = DELEGATE_CPU
                clearObjectDetector()
                setupObjectDetector()
            }
            DELEGATE_GPU -> {
                this.currentDelegate = DELEGATE_GPU
                clearObjectDetector()
                setupObjectDetector()
            }
            DELEGATE_NNAPI -> {
                this.currentDelegate = DELEGATE_NNAPI
                clearObjectDetector()
                setupObjectDetector()
            }
        }
    }
    companion object {
        const val DELEGATE_CPU = 0
        const val DELEGATE_GPU = 1
        const val DELEGATE_NNAPI = 2
    }
}
