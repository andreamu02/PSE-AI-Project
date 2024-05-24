package it.unipd.dei.pseaiproject.ui.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import it.unipd.dei.pseaiproject.CustomSpinnerAdapter
import it.unipd.dei.pseaiproject.R
import it.unipd.dei.pseaiproject.SpinnerItem
import it.unipd.dei.pseaiproject.ui.auth.SignInActivity

class InfoActivity: AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        val isDarkTheme = sharedPreferences.getBoolean("isDarkTheme", false)
        var backgroundAB = R.color.black
        var titleColor = R.color.ametista
        var spinnerTextColor = R.color.white
        if (isDarkTheme) {
            setTheme(R.style.DarkStyle)
            window.setBackgroundDrawableResource(R.drawable.home_background_dark)
        } else {
            setTheme(R.style.LightStyle)
            window.setBackgroundDrawableResource(R.drawable.home_background_light)
            backgroundAB = R.color.white
            titleColor = R.color.green
            spinnerTextColor = R.color.black
        }
        firebaseAuth = FirebaseAuth.getInstance()

        setContentView(R.layout.activity_info)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val spinner: Spinner = findViewById(R.id.spinner)

        val spinnerItems = listOf(
            SpinnerItem(R.drawable.home, "Home"),
            SpinnerItem(R.drawable.information_slab_box, "Info"),
            SpinnerItem(R.drawable.theme_light_dark, "Change theme"),
            SpinnerItem(R.drawable.logout, "Logout")
        )

        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(backgroundAB)))
        toolbar.setTitleTextColor(resources.getColor(titleColor))

        val adapter = CustomSpinnerAdapter(this, spinnerItems, spinnerTextColor, backgroundAB)
        spinner.adapter = adapter
        spinner.setSelection(1)

        // Gestire gli eventi di selezione
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val item = parent.getItemAtPosition(position) as SpinnerItem
                if (item.text == "Logout") {
                    firebaseAuth.signOut()
                    val intent = Intent(this@InfoActivity, SignInActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                if (item.text == "Change theme") {
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putBoolean("isDarkTheme", !isDarkTheme)
                    editor.apply()
                    spinner.setSelection(1)
                    recreate()
                }
                if (item.text == "Home") {
                    // Apri la nuova Activity
                    val intent = Intent(this@InfoActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                //Toast.makeText(this@MainActivity, "Selected: $item", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }
}