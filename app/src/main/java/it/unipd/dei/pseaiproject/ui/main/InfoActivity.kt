package it.unipd.dei.pseaiproject.ui.main

import android.graphics.*
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import it.unipd.dei.pseaiproject.R
import it.unipd.dei.pseaiproject.ml.ObjectDetectionModel
import org.tensorflow.lite.support.image.TensorImage

class InfoActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_object_detection)

        imageView = findViewById(R.id.imageView)

        // Load a sample bitmap (replace this with your bitmap loading code)
        val bitmap = loadSampleBitmap()

        // Perform object detection
        detectObjects(bitmap)
    }

    private fun loadSampleBitmap(): Bitmap {
        // Load your bitmap here
        return BitmapFactory.decodeResource(resources, R.drawable.apple_image)
    }

    private fun detectObjects(bitmap: Bitmap) {
        // Initialize the model
        val model = ObjectDetectionModel.newInstance(this)

        // Creates inputs for reference
        val image = TensorImage.fromBitmap(bitmap)

        // Runs model inference and gets result
        val outputs = model.process(image)
        val locations = outputs.locationsAsTensorBuffer
        val classes = outputs.classesAsTensorBuffer
        val scores = outputs.scoresAsTensorBuffer
        val numberOfDetections = outputs.numberOfDetectionsAsTensorBuffer

        // Create a mutable bitmap to draw on
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)
        val paint = Paint()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4f
        paint.color = Color.RED

        val textPaint = Paint()
        textPaint.color = Color.RED
        textPaint.textSize = 50f

        // Process results and draw bounding boxes
        val numberOfObjects = numberOfDetections.intArray[0]
        for (i in 0 until numberOfObjects) {
            val score = scores.floatArray[i]
            if (score > 0) { // confidence threshold
                val location = locations.floatArray.sliceArray(i * 4 until (i + 1) * 4)
                val left = location[0] * bitmap.width
                val top = location[1] * bitmap.height
                val right = location[2] * bitmap.width
                val bottom = location[3] * bitmap.height

                // Draw bounding box
                canvas.drawRect(left, top, right, bottom, paint)

                // Draw class label
                val detectedClass = classes.intArray[i]
                canvas.drawText("Class: $detectedClass, Score: ${"%.2f".format(score)}", left, top, textPaint)
            }
        }

        // Set the modified bitmap to the ImageView
        imageView.setImageBitmap(mutableBitmap)

        // Releases model resources if no longer used
        model.close()
    }
}
