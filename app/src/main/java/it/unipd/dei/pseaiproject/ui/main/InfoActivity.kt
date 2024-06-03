package it.unipd.dei.pseaiproject.ui.main

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import it.unipd.dei.pseaiproject.spinner.CustomSpinnerAdapter
import it.unipd.dei.pseaiproject.R
import it.unipd.dei.pseaiproject.spinner.SpinnerItem
import it.unipd.dei.pseaiproject.spinner.SpinnerItemSelectedListener
import it.unipd.dei.pseaiproject.style.StyleManager


//classe per gestire l'activity info
class InfoActivity: AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Carica il tema stilistico usato prima di chiudere l'app
        val styleManager = StyleManager(this)
        val theme = styleManager.loadThemePreference(this)

        firebaseAuth = FirebaseAuth.getInstance()

        setContentView(R.layout.activity_info)

        // Setta la toolbar come actionbar per la window
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //setto i colori dei vari widget e anche delle immagini
        styleManager.setWidgetAppearance(this, toolbar, null, findViewById(R.id.horizontal_view), findViewById(R.id.vertical_view))
        styleManager.setImageViewDrawableColor(findViewById(R.id.androidStudio), R.drawable.android_studio, this)
        styleManager.setImageViewDrawableColor(findViewById(R.id.android), R.drawable.android, this)

        // attivo i link ipertestuali della contenuti nella TextView di informazioni
        val textView = findViewById<TextView>(R.id.TextView)
        textView.movementMethod = LinkMovementMethod.getInstance()

        val spinner: Spinner = findViewById(R.id.spinner)

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
        spinner.setSelection(1)

        // Gestire gli eventi di selezione
        spinner.onItemSelectedListener = SpinnerItemSelectedListener(this, firebaseAuth, theme, styleManager, spinner)
    }
}