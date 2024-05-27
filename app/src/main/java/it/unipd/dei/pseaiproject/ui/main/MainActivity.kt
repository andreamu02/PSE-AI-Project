package it.unipd.dei.pseaiproject.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import it.unipd.dei.pseaiproject.CameraActivity
import it.unipd.dei.pseaiproject.CustomSpinnerAdapter
import it.unipd.dei.pseaiproject.R
import it.unipd.dei.pseaiproject.SpinnerItem
import it.unipd.dei.pseaiproject.SpinnerItemSelectedListener
import it.unipd.dei.pseaiproject.StyleManager

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val styleManager = StyleManager(this)
        val theme = styleManager.loadThemePreference(this)

        firebaseAuth = FirebaseAuth.getInstance()

        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val spinner: Spinner = findViewById(R.id.spinner)
        val start : Button = findViewById(R.id.getStarted)

        start.setOnClickListener {
            val myIntent = Intent(this, CameraActivity::class.java)
            startActivity(myIntent)
        }

        val spinnerItems = listOf(
            SpinnerItem(R.drawable.home, "Home"),
            SpinnerItem(R.drawable.information_slab_box, "Info"),
            SpinnerItem(R.drawable.theme_light_dark, "Change theme"),
            SpinnerItem(R.drawable.logout, "Logout")
        )

        spinner.adapter = CustomSpinnerAdapter(this, spinnerItems, theme)

        // Gestire gli eventi di selezione
        spinner.onItemSelectedListener = SpinnerItemSelectedListener(this, firebaseAuth, theme, styleManager, spinner)
    }
}
