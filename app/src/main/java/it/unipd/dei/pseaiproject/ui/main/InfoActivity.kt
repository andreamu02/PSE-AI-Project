package it.unipd.dei.pseaiproject.ui.main

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import it.unipd.dei.pseaiproject.R
import it.unipd.dei.pseaiproject.spinner.CustomSpinnerAdapter
import it.unipd.dei.pseaiproject.spinner.SpinnerItem
import it.unipd.dei.pseaiproject.spinner.SpinnerItemSelectedListener
import it.unipd.dei.pseaiproject.style.StyleManager

/**
 * Activity per gestire le informazioni dell'applicazione.
 */
class InfoActivity : AppCompatActivity() {
    // Oggetto per l'autenticazione Firebase
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Carica il tema stilistico utilizzato prima della chiusura dell'app
        val styleManager = StyleManager(this)
        val theme = styleManager.loadThemePreference(this)

        firebaseAuth = FirebaseAuth.getInstance()

        setContentView(R.layout.activity_info)

        // Imposta la toolbar come action bar per la finestra
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Imposta i colori dei vari widget e delle immagini
        styleManager.setWidgetAppearance(this, toolbar, null, findViewById(R.id.horizontal_view), findViewById(R.id.vertical_view))
        styleManager.setImageViewDrawableColor(findViewById(R.id.androidStudio), R.drawable.android_studio, this)
        styleManager.setImageViewDrawableColor(findViewById(R.id.android), R.drawable.android, this)

        // Abilita i link ipertestuali contenuti nella TextView delle informazioni
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

        // Creazione e inizializzazione dello spinner con gli elementi definiti precedentemente
        val adapter = CustomSpinnerAdapter(this, spinnerItems, theme)
        spinner.adapter = adapter
        spinner.setSelection(1)

        // Gestisce gli eventi di selezione dello spinner
        spinner.onItemSelectedListener = SpinnerItemSelectedListener(this, firebaseAuth, theme, styleManager, spinner)

        // Callback per gestire la pressione del tasto indietro del telefono
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@InfoActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        onBackPressedDispatcher.addCallback(this, callback)
    }
}
