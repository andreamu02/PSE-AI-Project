package it.unipd.dei.pseaiproject.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import it.unipd.dei.pseaiproject.databinding.ActivityModelBinding


class ModelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.backButtonToolbar.setOnClickListener {
            finish()
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