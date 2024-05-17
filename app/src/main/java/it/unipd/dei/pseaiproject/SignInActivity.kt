package it.unipd.dei.pseaiproject

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import java.lang.Exception

class SignInActivity : AppCompatActivity() {

    // Istanza di FirebaseAuth per l'autenticazione
    private lateinit var firebaseAuth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        // Inizializzazione dei componenti della UI
        val email = findViewById<EditText>(R.id.email_input)
        val password = findViewById<EditText>(R.id.password_input)
        val loginButton = findViewById<MaterialButton>(R.id.login_button)
        val textView = findViewById<TextView>(R.id.not_signup)
        val forgotPassword = findViewById<TextView>(R.id.forgot_password)

        // Ottieni l'istanza di FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        // Listener per il pulsante di login
        loginButton.setOnClickListener{
            // Verifica che i campi email e password non siano vuoti
            if(email.text.isNotEmpty() && password.text.isNotEmpty()){
                // Effettua il tentativo di accesso con Firebase Authentication
                firebaseAuth.signInWithEmailAndPassword(email.text.toString(), password.text.toString()).addOnCompleteListener{task ->
                    // Se l'accesso è riuscito, avvia l'attività principale
                    if(task.isSuccessful){
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }else{
                        // Se l'accesso fallisce, gestisci l'errore
                        handleSignInError(task.exception)
                    }
                }
            }else{
                // Mostra un messaggio se i campi sono vuoti
                Toast.makeText(this, "Non sono ammessi campi vuoti", Toast.LENGTH_SHORT).show()
            }
        }

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
    }

    // Metodo chiamato quando l'attività diventa visibile
    override fun onStart() {
        super.onStart()

        // Verifica se l'utente è già autenticato
        if(firebaseAuth.currentUser != null){
            // Se l'utente è già autenticato, avvia l'attività principale
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    // Metodo per gestire gli errori di accesso
    private fun handleSignInError(exception: Exception?){
        when (exception) {
            is FirebaseAuthInvalidCredentialsException -> {
                // Gestisce l'errore di password errata
                Toast.makeText(this, "Password errata. Riprova.", Toast.LENGTH_SHORT).show()
            }
            is FirebaseAuthInvalidUserException -> {
                // Gestisce l'errore di utente non trovato
                Toast.makeText(this, "L'email inserita non esiste. Riprova.", Toast.LENGTH_SHORT).show()
            }
            else -> {
                // Gestisce gli altri tipi di errori
                Toast.makeText(this, "Errore di autenticazione: ${exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}