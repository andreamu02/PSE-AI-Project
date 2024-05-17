package it.unipd.dei.pseaiproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

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