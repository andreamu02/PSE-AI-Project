package it.unipd.dei.pseaiproject

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

// Classe che estende View, serve per visualizzare le rilevazioni di oggetti su un'area di disegno
class DetectionOverlayView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    // Configura un oggetto Paint per disegnare le bounding box
    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        color = android.graphics.Color.RED
    }

    private val textPaint = Paint().apply {
        color = android.graphics.Color.RED
        textSize = 50f
    }

    // Una lista che conterrà gli oggetti rilevati
    private var detections: List<Detection> = emptyList()

    fun setDetections(detections: List<Detection>) {
        this.detections = detections
        invalidate()
    }

    // Override del metodo onDraw di View che disegna la vista
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        detections.forEach { detection ->
            // Disegna un rettangolo per ciascuna rilevazione usando le coordinate sinistra, superiore, destra e inferiore
            canvas.drawRect(detection.left, detection.top, detection.right, detection.bottom, paint)
            // Disegna l'etichetta associata alla rilevazione vicino al rettangolo
            canvas.drawText(detection.label, detection.left, detection.top, textPaint)
        }
    }

    // Classe dati per rappresentare una rilevazione di oggetto
    data class Detection(val left: Float, val top: Float, val right: Float, val bottom: Float, val label: String)
}
