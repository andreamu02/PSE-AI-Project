package it.unipd.dei.pseaiproject.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import it.unipd.dei.pseaiproject.BottomSheetFragment
import it.unipd.dei.pseaiproject.databinding.ActivityModelBinding


class ModelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomSheetFragment = BottomSheetFragment()

        setSupportActionBar(binding.toolbar)

        binding.backButtonToolbar.setOnClickListener {
            finish()
        }

        binding.modalButton.setOnClickListener {
            // Mostra il modale
            bottomSheetFragment.show(supportFragmentManager, "BottomSheetDialog")
        }

        /*
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Gestisci l'evento del pulsante back
                finish()
            }
        })*/
    }
}