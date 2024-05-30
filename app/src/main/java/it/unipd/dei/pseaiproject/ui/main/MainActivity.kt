package it.unipd.dei.pseaiproject.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import it.unipd.dei.pseaiproject.CameraActivity
import it.unipd.dei.pseaiproject.spinner.CustomSpinnerAdapter
import it.unipd.dei.pseaiproject.R
import it.unipd.dei.pseaiproject.spinner.SpinnerItem
import it.unipd.dei.pseaiproject.spinner.SpinnerItemSelectedListener
import it.unipd.dei.pseaiproject.style.StyleManager

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Carica il tema stilistico usato prima di chiudere l'app
        val styleManager = StyleManager(this)
        val theme = styleManager.loadThemePreference(this)

        firebaseAuth = FirebaseAuth.getInstance()

        setContentView(R.layout.activity_main)
        // Setta la toolbar come actionbar per la window
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //cambia il colore delle immagini e delle icone
        styleManager.setImageViewDrawableColor(findViewById(R.id.androidStudio), R.drawable.android_studio, this)
        styleManager.setImageViewDrawableColor(findViewById(R.id.android), R.drawable.android, this)
        styleManager.setImageViewDrawableColor(findViewById(R.id.ImageView), R.drawable.object_recognition, this)

        val spinner: Spinner = findViewById(R.id.spinner)
        val start : Button = findViewById(R.id.getStarted)

        //cambio il colore del bottone e della toolbar
        styleManager.setWidgetAppearance(this, toolbar, start, findViewById(R.id.horizontal_view), findViewById(R.id.vertical_view))
        //bottone per iniziare ad usare il modello
        start.setOnClickListener {
            val myIntent = Intent(this, ModelActivity::class.java)
            startActivity(myIntent)
        }

        // Creazione degli elementi dello spinner
        val spinnerItems = listOf(
            SpinnerItem(R.drawable.home, "Home"),
            SpinnerItem(R.drawable.information_slab_box, "Info"),
            SpinnerItem(R.drawable.theme_light_dark, "Change theme"),
            SpinnerItem(R.drawable.logout, "Logout")
        )

        // Creazione e inizializzazione dello spinner tramite gli elementi precedentemente definiti
        val adapter = CustomSpinnerAdapter(this, spinnerItems, theme)
        spinner.adapter = adapter

        // Gestione degli eventi di selezione
        spinner.onItemSelectedListener = SpinnerItemSelectedListener(this, firebaseAuth, theme, styleManager, spinner)
    }
}
