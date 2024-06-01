package it.unipd.dei.pseaiproject

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import org.tensorflow.lite.task.vision.detector.Detection
import java.util.LinkedList
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

// Classe che estende View, serve per visualizzare le rilevazioni di oggetti su un'area di disegno
class DetectionOverlayView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    // Lista dei risultati delle rilevazioni
    private var results: List<Detection> = LinkedList<Detection>()
    // Paint per il disegno delle bounding box
    private var boxPaint = Paint()
    // Paint per lo sfondo del testo
    private var textBackgroundPaint = Paint()
    // Paint per il testo
    private var textPaint = Paint()

    // Fattore di scala per adeguare le dimensioni dell'immagine alla View
    private var scaleFactor: Float = 1f

    // Rect utilizzato per calcolare le dimensioni del testo
    private var bounds = Rect()

    init {
        initPaints()
    }

    // Metodo per inizializzare i Paint utilizzati nel disegno
    private fun initPaints() {
        textBackgroundPaint.color = Color.BLACK // Colore di sfondo del testo
        textBackgroundPaint.style = Paint.Style.FILL // Stile di riempimento
        textBackgroundPaint.textSize = 50f // Dimensione del testo

        textPaint.color = Color.RED
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 50f

        boxPaint.color = ContextCompat.getColor(context!!, R.color.bounding_box_color)
        boxPaint.strokeWidth = 8F // Larghezza del bordo della bounding box
        boxPaint.style = Paint.Style.STROKE // Stile del bordo
    }

    // Metodo per disegnare sulla Canvas
    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        for (result in results) {
            val boundingBox = result.boundingBox // Bounding box della rilevazione

            // Coordinate scalate
            var top = boundingBox.top * scaleFactor
            var bottom = boundingBox.bottom * scaleFactor
            var left = boundingBox.left * scaleFactor
            var right = boundingBox.right * scaleFactor

            // Assicura che le coordinate scalate siano all'interno dei limiti della View
            top = max(0f, min(top, viewHeight))
            bottom = max(0f, min(bottom, viewHeight))
            left = max(0f, min(left, viewWidth))
            right = max(0f, min(right, viewWidth))

            // Disegna la bounding box solo se è all'interno dei limiti della View
            if (top < viewHeight && left < viewWidth) {
                // Disegna la bounding box attorno agli oggetti rilevati
                val drawableRect = RectF(left, top, right, bottom)
                canvas.drawRect(drawableRect, boxPaint)

                // Crea il testo da visualizzare accanto agli oggetti rilevati
                val drawableText =
                    result.categories[0].label + " " +
                            String.format(Locale.getDefault(), "%.2f", result.categories[0].score)

                // Disegna un rettangolo dietro il testo di visualizzazione
                textBackgroundPaint.getTextBounds(drawableText, 0, drawableText.length, bounds)
                val textWidth = bounds.width()
                val textHeight = bounds.height()
                canvas.drawRect(
                    left,
                    top,
                    left + textWidth + 8,
                    top + textHeight + 8,
                    textBackgroundPaint
                )

                // Disegna il testo per l'oggetto rilevato
                canvas.drawText(drawableText, left, top + bounds.height(), textPaint)
            }
        }
    }

    // Metodo per impostare i risultati delle rilevazioni e l'immagine
    fun setResults(
        detectionResults: MutableList<Detection>,
        imageHeight: Int,
        imageWidth: Int,
    ) {
        results = detectionResults // Aggiorna la lista dei risultati

        // PreviewView è in modalità FILL_START. Quindi dobbiamo scalare la bounding box per
        // adattarla alla dimensione in cui le immagini catturate saranno visualizzate.
        scaleFactor = max(width * 1f / imageWidth, height * 1f / imageHeight) // Calcola il fattore di scala
    }

}
