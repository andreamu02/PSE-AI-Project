package it.unipd.dei.pseaiproject.detection

import android.content.Context
import android.content.SharedPreferences
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
    val context: Context,
    val objectDetectorListener: DetectorListener?) {

    private var threshold: Float
    private var numThreads: Int
    private var maxResults: Int
    private var currentDelegate: Int
    private var sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private var objectDetector: ObjectDetector? = null

    init {
        threshold = sharedPreferences.getFloat(KEY_THRESHOLD, 0.5f)
        numThreads = sharedPreferences.getInt(KEY_NUM_THREADS, 2)
        maxResults = sharedPreferences.getInt(KEY_MAX_RESULTS, 3)
        currentDelegate = sharedPreferences.getInt(KEY_DELEGATE, DELEGATE_CPU)
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
        sharedPreferences.edit().putFloat(KEY_THRESHOLD, threshold).apply()
        clearObjectDetector()
        Log.d("BottomSheetFragmentUpdate", "Saved threshold: $this.threshold")
        setupObjectDetector()
    }

    fun setMaxResults(maxResults: Int){
        this.maxResults = maxResults
        sharedPreferences.edit().putInt(KEY_MAX_RESULTS, maxResults).apply()
        clearObjectDetector()
        setupObjectDetector()
    }

    fun setDelegate(delegate: Int){
        this.currentDelegate = delegate
        sharedPreferences.edit().putInt(KEY_DELEGATE, delegate).apply()
        clearObjectDetector()
        setupObjectDetector()
    }

    companion object {
        const val PREFS_NAME = "ObjectDetectorPrefs"
        const val KEY_THRESHOLD = "threshold"
        const val KEY_NUM_THREADS = "numThreads"
        const val KEY_MAX_RESULTS = "maxResults"
        const val KEY_DELEGATE = "delegate"

        const val DELEGATE_CPU = 0
        const val DELEGATE_GPU = 1
        const val DELEGATE_NNAPI = 2
    }
}
