package it.unipd.dei.pseaiproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_signup)


        val email = findViewById<EditText>(R.id.email_input_signup)
        val password = findViewById<EditText>(R.id.password_input_signup)
        val confirmPassword = findViewById<EditText>(R.id.confirm_password_input_signup)
        val loginButton = findViewById<Button>(R.id.signup_button)
        val textView = findViewById<TextView>(R.id.already_signup)

        firebaseAuth = FirebaseAuth.getInstance()

        loginButton.setOnClickListener{

            if(email.text.isNotEmpty() && password.text.isNotEmpty() && confirmPassword.text.isNotEmpty()){
                if(password.text.toString() == confirmPassword.text.toString()){
                    firebaseAuth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString()).addOnCompleteListener{
                        if(it.isSuccessful){
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }else{
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Toast.makeText(this, "Le due password non combaciano", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Non sono ammessi campi vuoti", Toast.LENGTH_SHORT).show()
            }
        }

        textView.setOnClickListener{
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}