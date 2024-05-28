package it.unipd.dei.pseaiproject.ui.main

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import it.unipd.dei.pseaiproject.BottomSheetFragment
import it.unipd.dei.pseaiproject.R


class ModelActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_model)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val backButton: ImageButton = findViewById(R.id.back_button_toolbar)
        val modalButton: ImageButton = findViewById(R.id.modal_button)
        val bottomSheetFragment = BottomSheetFragment()

        setSupportActionBar(toolbar)

        backButton.setOnClickListener {
            finish()
        }

        modalButton.setOnClickListener {
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