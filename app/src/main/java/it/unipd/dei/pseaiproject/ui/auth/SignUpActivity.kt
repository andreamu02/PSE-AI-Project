package it.unipd.dei.pseaiproject.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import it.unipd.dei.pseaiproject.MainActivity
import it.unipd.dei.pseaiproject.R
import it.unipd.dei.pseaiproject.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

    private val authViewModel : AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_signup)

        // Inizializzazione dei componenti della UI
        val email = findViewById<EditText>(R.id.email_input_signup)
        val password = findViewById<EditText>(R.id.password_input_signup)
        val confirmPassword = findViewById<EditText>(R.id.confirm_password_input_signup)
        val signUpButton = findViewById<MaterialButton>(R.id.signup_button)
        val textView = findViewById<TextView>(R.id.already_signup)
        val progressBar = findViewById<ProgressBar>(R.id.signUpProgressbar)

        // Imposta il listener per il pulsante di registrazione
        signUpButton.setOnClickListener{
            // Verifica che i campi email, password e conferma password non siano vuoti
            if(email.text.isNotEmpty() && password.text.isNotEmpty() && confirmPassword.text.isNotEmpty()){
                // Verifica che le password corrispondano
                if(password.text.toString() == confirmPassword.text.toString()){
                    signUp(email.text.toString(), password.text.toString())
                }else{
                    // Mostra un messaggio se le password non coincidono
                    Toast.makeText(this, "Le due password non combaciano", Toast.LENGTH_SHORT).show()
                }
            }else{
                // Mostra un messaggio se i campi sono vuoti
                Toast.makeText(this, "Non sono ammessi campi vuoti", Toast.LENGTH_SHORT).show()
            }}

        // Osserva i messaggi di errore dall'AuthViewModel
        authViewModel.authException.observe(this, Observer { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        })

        // Imposta il listener per il link di accesso
        textView.setOnClickListener{
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        // Osserva lo stato del processo di reset della password
        authViewModel.signUpState.observe(this, Observer { isResetting ->
            if (isResetting) {
                // Mostra la ProgressBar e nascondi il pulsante "Reset Password"
                progressBar.visibility = View.VISIBLE
                signUpButton.visibility = View.INVISIBLE
            } else {
                // Nascondi la ProgressBar e mostra il pulsante "Reset Password"
                progressBar.visibility = View.INVISIBLE
                signUpButton.visibility = View.VISIBLE
            }
        })
    }

    // Funzione privata per il signUp
    private fun signUp(email: String, password: String){
        // Avvia una coroutine per chiamare signUp
        lifecycleScope.launch {
            // Chiamata a signUn
            val signUpSuccess = authViewModel.signUp(email, password)

            if(signUpSuccess){
                // Se la registrazione è riuscita, avvia l'attività principale
                val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                startActivity(intent)
            }}
    }
}
