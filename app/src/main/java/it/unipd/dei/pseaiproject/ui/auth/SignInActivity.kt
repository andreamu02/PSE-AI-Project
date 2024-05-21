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

class SignInActivity : AppCompatActivity() {

    private val authViewModel : AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        // Inizializzazione dei componenti della UI
        val email = findViewById<EditText>(R.id.email_input)
        val password = findViewById<EditText>(R.id.password_input)
        val loginButton = findViewById<MaterialButton>(R.id.login_button)
        val textView = findViewById<TextView>(R.id.not_signup)
        val forgotPassword = findViewById<TextView>(R.id.forgot_password)
        val progressBar = findViewById<ProgressBar>(R.id.signInProgressbar)


        // Listener per il pulsante di login
        loginButton.setOnClickListener{
            // Verifica che i campi email e password non siano vuoti
            if(email.text.isNotEmpty() && password.text.isNotEmpty()) {
                signIn(email.text.toString(), password.text.toString())
            }else{
                // Mostra un messaggio se i campi sono vuoti
                Toast.makeText(this, "Non sono ammessi campi vuoti", Toast.LENGTH_SHORT).show()
            }
        }

        // Osserva i messaggi di errore dall'AuthViewModel
        authViewModel.authException.observe(this, Observer { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        })

        // Imposta il listener per il link di registrazione
        textView.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Imposta il listener per il link di recupero password
        forgotPassword.setOnClickListener{
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }



        // Osserva lo stato del processo di reset della password
        authViewModel.signInState.observe(this, Observer { isResetting ->
            if (isResetting) {
                // Mostra la ProgressBar e nascondi il pulsante "Reset Password"
                progressBar.visibility = View.VISIBLE
                loginButton.visibility = View.INVISIBLE
            } else {
                // Nascondi la ProgressBar e mostra il pulsante "Reset Password"
                progressBar.visibility = View.INVISIBLE
                loginButton.visibility = View.VISIBLE
            }
        })
    }

    // Metodo chiamato quando l'attività diventa visibile
    override fun onStart() {
        super.onStart()

        // Verifica se l'utente è già autenticato
        if(authViewModel.getCurrentUser() != null){
            // Se l'utente è già autenticato, avvia l'attività principale
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    // Funzione privata per il signIn
    private fun signIn(email: String, password: String){
        // Avvia una coroutine per chiamare signIn
        lifecycleScope.launch {
            // Chiamata a signIn
            val signInSuccess = authViewModel.signIn(email, password)

            // Se il login ha avuto successo, avvia MainActivity
            if (signInSuccess) {
                val intent = Intent(this@SignInActivity, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
}