package it.unipd.dei.pseaiproject

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Intent per avviare LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)

    }
}