package it.unipd.dei.pseaiproject.detection

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector

/**
 * Classe helper per il rilevamento degli oggetti utilizzando TensorFlow Lite.
 *
 * @param context Il contesto dell'applicazione.
 * @param objectDetectorListener Il listener per gestire i risultati e gli errori del rilevamento degli oggetti.
 */
class ObjectDetectorHelper(
    val context: Context,
    val objectDetectorListener: DetectorListener?) {

    private var threshold: Float
    private var numThreads: Int
    private var maxResults: Int
    private var currentDelegate: Int
    private var sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private var objectDetector: ObjectDetector? = null

    private var isError: Boolean = false

    init {
        threshold = sharedPreferences.getFloat(KEY_THRESHOLD, 0.5f)
        numThreads = sharedPreferences.getInt(KEY_NUM_THREADS, 2)
        maxResults = sharedPreferences.getInt(KEY_MAX_RESULTS, 3)
        currentDelegate = sharedPreferences.getInt(KEY_DELEGATE, DELEGATE_CPU)
        setupObjectDetector()
    }

    /**
     * Cancella l'istanza dell'oggetto rilevatore.
     */
    private fun clearObjectDetector() {
        objectDetector = null
    }

    /**
     * Configura il rilevatore di oggetti con le opzioni specificate.
     */
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
            isError = false
        } catch (e: IllegalStateException) {
            if(!isError){
                val delegate = if (currentDelegate == DELEGATE_CPU) "CPU" else (if (currentDelegate == DELEGATE_GPU) "GPU" else "NNAPI")
                objectDetectorListener?.onError(
                    "Il rilevatore di oggetti non è riuscito a inizializzarsi con il delegato $delegate. Verrà utilizzato il delegato CPU."
                )
                Log.e("Test", "TFLite non è riuscito a caricare il modello con errore: " + e.message)
            }
            isError = true
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                currentDelegate = DELEGATE_CPU
                setupObjectDetector()
            }, 1000)
        }
    }

    /**
     * Esegue la rilevazione degli oggetti su un'immagine.
     *
     * @param image L'immagine su cui eseguire il rilevamento.
     * @param imageRotation La rotazione dell'immagine in gradi.
     */
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

    /**
     * Interfaccia per il listener degli eventi del rilevatore di oggetti.
     */
    interface DetectorListener {
        fun onError(error: String)
        fun onResults(
            results: MutableList<Detection>?,
            inferenceTime: Long,
            imageHeight: Int,
            imageWidth: Int
        )
    }

    /**
     * Imposta la soglia di punteggio per il rilevamento degli oggetti.
     *
     * @param threshold La soglia di punteggio.
     */
    fun setThreshold(threshold: Float){
        this.threshold = threshold
        sharedPreferences.edit().putFloat(KEY_THRESHOLD, threshold).apply()
        clearObjectDetector()
        Log.d("BottomSheetFragmentUpdate", "Saved threshold: $this.threshold")
        setupObjectDetector()
    }

    /**
     * Imposta il numero massimo di risultati per il rilevamento degli oggetti.
     *
     * @param maxResults Il numero massimo di risultati.
     */
    fun setMaxResults(maxResults: Int){
        this.maxResults = maxResults
        sharedPreferences.edit().putInt(KEY_MAX_RESULTS, maxResults).apply()
        clearObjectDetector()
        Log.d("BottomSheetFragmentUpdate", "Saved maxResult: $this.maxResults")
        setupObjectDetector()
    }

    /**
     * Imposta il delegato da utilizzare per l'esecuzione del modello (CPU, GPU, NNAPI).
     *
     * @param delegate Il delegato da utilizzare.
     */
    fun setDelegate(delegate: Int){
        this.currentDelegate = delegate
        sharedPreferences.edit().putInt(KEY_DELEGATE, delegate).apply()
        clearObjectDetector()
        setupObjectDetector()
    }

    fun getDelegate(): Int {
        return this.currentDelegate
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
