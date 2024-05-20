package it.unipd.dei.pseaiproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class ForgotPasswordActivity : AppCompatActivity() {

    // Dichiarazione delle variabili
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var email : EditText
    private lateinit var resetButton : MaterialButton
    private lateinit var backButton : MaterialButton
    private lateinit var progressBar : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotpassword)

        // Inizializzazione dei componenti della UI
        email = findViewById(R.id.forgot_email)
        resetButton = findViewById(R.id.reset_button)
        backButton = findViewById(R.id.back_button)
        progressBar = findViewById(R.id.forgetPasswordProgressbar)

        // Ottieni l'istanza di FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        // Listener per il pulsante di back
        backButton.setOnClickListener {
            finish()
        }

        // Listener per il pulsante di reset
        resetButton.setOnClickListener {
            if(email.text.isNotEmpty()){
                resetPassword()
            }else{
                Toast.makeText(this, "È necessario inserire una email", Toast.LENGTH_SHORT).show()
            }
        }

    }

    // Metodo per avviare il processo di reset della password
    private fun resetPassword(){
        //Mostra la progress bar
        progressBar.visibility = View.VISIBLE
        // Nascondi il pulsante "Reset Password"
        resetButton.visibility = View.INVISIBLE

        // Invia l'email per il reset della password tramite Firebase Authentication
        firebaseAuth.sendPasswordResetEmail(email.text.toString()).addOnCompleteListener {task ->
            if(task.isSuccessful){
                // Se l'operazione è completata con successo, mostra un messaggio all'utente
                Toast.makeText(this, "Il link per il reset della password è stato inviato alla tua mail", Toast.LENGTH_SHORT).show()
                // Avvia l'attività di login
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                // Se si verifica un errore, gestiscilo
                handleResetPasswordError(task.exception)
                progressBar.visibility = View.INVISIBLE
                resetButton.visibility = View.VISIBLE
            }
        }
    }

    // Metodo per gestire gli errori durante il reset della password
    private fun handleResetPasswordError(exception: Exception?) {
        when (exception) {
            is FirebaseAuthInvalidUserException -> {
                // Se l'email non è associata ad alcun account, mostra un messaggio all'utente
                Toast.makeText(this,"L'email inserita non è associata ad alcun account. Riprova.", Toast.LENGTH_SHORT).show()
            }
            else -> {
                // Se si verifica un altro tipo di errore, mostra un messaggio generico all'utente
                Toast.makeText(this, "Errore di reset della password: ${exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}