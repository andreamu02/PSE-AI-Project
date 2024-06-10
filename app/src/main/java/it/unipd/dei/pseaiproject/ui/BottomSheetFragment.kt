package it.unipd.dei.pseaiproject.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import it.unipd.dei.pseaiproject.databinding.FragmentBottomSheetBinding
import it.unipd.dei.pseaiproject.detection.ObjectDetectorHelper
import kotlin.math.round

/**
 * Un fragment per regolare i parametri del modello.
 */
class BottomSheetFragment : BottomSheetDialogFragment() {

    // Binding per il layout del frammento
    private lateinit var binding: FragmentBottomSheetBinding

    // Oggetto detector per la rilevazione degli oggetti
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

        // Recupera i valori dei parametri salvati dalle preferenze condivise
        val sharedPreferences = requireContext().getSharedPreferences("appPreferences", 0)
        val savedThreshold = sharedPreferences.getFloat("threshold", 0.5f) // Valore predefinito 0.5f
        val savedMaxResults = sharedPreferences.getInt("maxResults", 3) // Valore predefinito 3
        val savedDelegate = sharedPreferences.getString("selectedDelegate", "CPU")

        // Espandi completamente il foglio inferiore quando viene aperto
        val bottomSheet = view.parent as? View
        if (bottomSheet != null) {
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        // Inizializza i valori dei parametri e il detector
        binding.firstParamValue.text = savedThreshold.toString()
        binding.secondParamValue.text = savedMaxResults.toString()
        detector?.setThreshold(savedThreshold)
        detector?.setMaxResults(savedMaxResults)
        if (savedDelegate != null) {
            updateValue(null, savedDelegate)
        }

        // Imposta il listener per lo spinner
        binding.thirdParamSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedValue = parent.getItemAtPosition(position).toString()
                updateValue(null, selectedValue)
                saveToPreferences(selectedValue)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Gestire il caso in cui non viene selezionato nulla, se necessario
            }
        }

        // Listener per il pulsante di chiusura
        binding.closeButton.setOnClickListener {
            this.dismiss()
        }

        // Listener per il primo set di pulsanti
        binding.firstParamMinus.setOnClickListener {
            updateValue(binding.firstParamValue, -0.1f)
        }

        binding.firstParamPlus.setOnClickListener {
            updateValue(binding.firstParamValue, 0.1f)
        }

        // Listener per il secondo set di pulsanti
        binding.secondParamMinus.setOnClickListener {
            updateValue(binding.secondParamValue, -1)
        }

        binding.secondParamPlus.setOnClickListener {
            updateValue(binding.secondParamValue, 1)
        }
    }

    // Metodo per aggiornare i valori dei TextView
    private fun updateValue(textView: TextView?, delta: Any) {
        when (delta) {
            is Float -> {
                val currentValue = round(textView?.text.toString().toFloat() * 100) / 100
                val newValue = round((currentValue + delta) * 100) / 100
                if (newValue in 0.1..0.9) {
                    textView?.text = newValue.toString()
                    detector?.setThreshold(newValue)
                    saveToPreferences(newValue)
                }
            }
            is Int -> {
                val currentValue = textView?.text.toString().toInt()
                val newValue = currentValue + delta
                if (newValue in 1..9) {
                    textView?.text = newValue.toString()
                    detector?.setMaxResults(newValue)
                    saveToPreferences(newValue)
                }
            }
            is String -> {
                val delegateValue = when (delta) {
                    "CPU" -> 0
                    "GPU" -> 1
                    "NNAPI" -> 2
                    else -> return
                }
                detector?.setDelegate(delegateValue)
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    val usedDelegate = detector?.getDelegate()
                    if (usedDelegate != delegateValue){
                        if (usedDelegate != null) {
                            val delegateString = when (usedDelegate) {
                                0 -> "CPU"
                                1 -> "GPU"
                                2 -> "NNAPI"
                                else -> {null}
                            }
                            if (delegateString != null) {
                                saveToPreferences(delegateString)
                                binding.thirdParamSpinner.setSelection(usedDelegate)
                            }else{
                                saveToPreferences("CPU")
                                binding.thirdParamSpinner.setSelection(0)
                            }
                        }
                    }
                }, 1100)
                saveToPreferences(delta)
            }
            else -> throw IllegalArgumentException("Unsupported delta type")
        }
    }

    /**
     * Imposta l'oggetto ObjectDetectorHelper.
     */
    fun setDetectorObject(detector: ObjectDetectorHelper) {
        this.detector = detector
    }

    // Metodo per salvare i valori dei parametri nelle preferenze condivise
    private fun saveToPreferences(value: Any) {
        val sharedPreferences = requireContext().getSharedPreferences("appPreferences", 0)
        val editor = sharedPreferences.edit()

        when (value) {
            is Float -> editor.putFloat("threshold", value)
            is Int -> editor.putInt("maxResults", value)
            is String -> editor.putString("selectedDelegate", value)
            else -> throw IllegalArgumentException("Unsupported type")
        }
        editor.apply()
    }
}
