package it.unipd.dei.pseaiproject.ui.main

import android.graphics.PorterDuff
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import it.unipd.dei.pseaiproject.R
import it.unipd.dei.pseaiproject.databinding.ActivityModelBinding
import it.unipd.dei.pseaiproject.style.StyleManager
import it.unipd.dei.pseaiproject.ui.BottomSheetFragment
import it.unipd.dei.pseaiproject.viewmodels.CameraViewModel

/**
 * Activity che gestisce il modello di rilevamento oggetti.
 */
class ModelActivity : AppCompatActivity() {

    // View binding per l'activity
    private lateinit var binding: ActivityModelBinding

    // Fragment per il bottom sheet
    private var bottomSheetFragment: BottomSheetFragment? = null

    // ViewModel per la fotocamera
    private val cameraViewModel: CameraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inizializzazione del manager dello stile
        val styleManager = StyleManager(this)
        styleManager.loadThemePreference(this)

        // Inflating del layout usando view binding
        binding = ActivityModelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // Listener per il pulsante "Indietro"
        binding.backButtonToolbar.setOnClickListener {
            finish()
        }

        // Impostazione del colore del logo
        val logoImage = binding.logoImage
        logoImage.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.SRC_IN)

        // Inizializzazione del fragment per il bottom sheet
        bottomSheetFragment = BottomSheetFragment()

        // Osservatore per l'helper del rilevamento oggetti
        cameraViewModel.objectDetectorHelper.observe(this) { helper ->
            helper?.let {
                bottomSheetFragment?.setDetectorObject(it)
            }
        }

        // Listener per il pulsante per aprire il bottom sheet
        binding.modalButton.setOnClickListener {
            bottomSheetFragment?.show(supportFragmentManager, "BottomSheetDialog")
        }
    }
}
