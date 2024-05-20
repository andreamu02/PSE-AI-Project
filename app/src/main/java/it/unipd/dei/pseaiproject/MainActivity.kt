package it.unipd.dei.pseaiproject

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        val isDarkTheme = sharedPreferences.getBoolean("isDarkTheme", false)
        if (isDarkTheme) {
            setTheme(R.style.DarkStyle)
        } else {
            setTheme(R.style.LightStyle)
        }

        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val spinner: Spinner = findViewById(R.id.spinner)

        val spinnerItems = listOf(
            SpinnerItem(R.drawable.home, "Home"),
            SpinnerItem(R.drawable.information_slab_box, "Info"),
            SpinnerItem(R.drawable.theme_light_dark, "Change Theme")
        )

        var backgroundAB = R.color.black
        var titleColor = R.color.ametista
        var spinnerTextColor = R.color.white
        if (!isDarkTheme) {
            backgroundAB = R.color.white
            titleColor = R.color.green
            spinnerTextColor = R.color.black
        }

        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(backgroundAB)))
        toolbar.setTitleTextColor(resources.getColor(titleColor))

        val adapter = CustomSpinnerAdapter(this, spinnerItems, spinnerTextColor)
        spinner.adapter = adapter
        val buttonChangeTheme = findViewById<Button>(R.id.changeTheme) // Bottone per cambiare tema
        buttonChangeTheme.setOnClickListener {
            // Cambia tema e riavvia l'attività
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putBoolean("isDarkTheme", !isDarkTheme)
            editor.apply()

            recreate()
        }

        // Gestire gli eventi di selezione
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                //val item = parent.getItemAtPosition(position).toString()
                //Toast.makeText(this@MainActivity, "Selected: $item", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        val textView = findViewById<TextView>(R.id.prova)
        val button = findViewById<Button>(R.id.button)
        firebaseAuth = FirebaseAuth.getInstance()

        if(firebaseAuth.currentUser != null){
            textView.text = firebaseAuth.currentUser!!.email.toString()
        }

        button.setOnClickListener{
            firebaseAuth.signOut()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}