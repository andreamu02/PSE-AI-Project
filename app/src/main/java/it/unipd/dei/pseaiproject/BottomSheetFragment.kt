package it.unipd.dei.pseaiproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import it.unipd.dei.pseaiproject.databinding.FragmentBottomSheetBinding
import it.unipd.dei.pseaiproject.detection.ObjectDetectorHelper
import kotlin.math.round

class BottomSheetFragment : BottomSheetDialogFragment() {

    // Classe generata automaticamente basata sul nome del file xml
    private lateinit var binding: FragmentBottomSheetBinding
    private var detector: ObjectDetectorHelper? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Listener per il pulsante di chiusura
        binding.closeButton.setOnClickListener {
            this.dismiss()
        }

        // Listener per il primo set di pulsanti
        binding.firstParamMinus.setOnClickListener {
            updateValueThreshold(binding.firstParamValue, -0.1f)
        }

        binding.firstParamPlus.setOnClickListener {
            updateValueThreshold(binding.firstParamValue, 0.1f)
        }

        // Listener per il secondo set di pulsanti
        binding.secondParamMinus.setOnClickListener {
            updateValueElements(binding.secondParamValue, -1)
        }

        binding.secondParamPlus.setOnClickListener {
            updateValueElements(binding.secondParamValue, 1)
        }
    }

    // Metodo per aggiornare i valori dei TextView
    private fun updateValueThreshold(textView: TextView, delta: Float) {
        val currentValue = round(textView.text.toString().toFloat()*100)/100
        val newValue = round((currentValue + delta)*100)/100
        if(newValue < 0.1 || newValue > 0.9){
            return
        }
        textView.text = newValue.toString()
        detector?.setThreshold(newValue)
    }

    private fun updateValueElements(textView: TextView, delta: Int){
        val currentValue = textView.text.toString().toInt()
        val newValue = currentValue + delta
        if(newValue < 1 || newValue > 9){
            return
        }
        textView.text = newValue.toString()
        detector?.setMaxResults(newValue)
    }

    fun setDetectorObject(detector: ObjectDetectorHelper){
        this.detector = detector
    }
}
