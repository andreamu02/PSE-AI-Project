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
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import java.lang.Exception

class SignUpActivity : AppCompatActivity() {

    // Istanza di FirebaseAuth per l'autenticazione
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_signup)

        // Inizializzazione dei componenti della UI
        val email = findViewById<EditText>(R.id.email_input_signup)
        val password = findViewById<EditText>(R.id.password_input_signup)
        val confirmPassword = findViewById<EditText>(R.id.confirm_password_input_signup)
        val loginButton = findViewById<MaterialButton>(R.id.signup_button)
        val textView = findViewById<TextView>(R.id.already_signup)

        // Ottieni l'istanza di FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        // Imposta il listener per il pulsante di registrazione
        loginButton.setOnClickListener{
            // Verifica che i campi email, password e conferma password non siano vuoti
            if(email.text.isNotEmpty() && password.text.isNotEmpty() && confirmPassword.text.isNotEmpty()){
                // Verifica che le password corrispondano
                if(password.text.toString() == confirmPassword.text.toString()){
                    // Registra un nuovo account tramite Firebase Authentication
                    firebaseAuth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString()).addOnCompleteListener{task ->
                        if(task.isSuccessful){
                            // Se la registrazione è riuscita, avvia l'attività principale
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }else{
                            // Se la registrazione fallisce, gestisci l'errore
                            handleSignUpException(task.exception)
                        }
                    }
                }else{
                    // Mostra un messaggio se le password non coincidono
                    Toast.makeText(this, "Le due password non combaciano", Toast.LENGTH_SHORT).show()
                }
            }else{
                // Mostra un messaggio se i campi sono vuoti
                Toast.makeText(this, "Non sono ammessi campi vuoti", Toast.LENGTH_SHORT).show()
            }
        }

        // Imposta il listener per il link di accesso
        textView.setOnClickListener{
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    // Metodo per gestire le eccezioni durante la registrazione
    private fun handleSignUpException(exception: Exception?){
        when (exception) {
            is FirebaseAuthWeakPasswordException -> {
                // Gestisce l'errore di password troppo debole
                Toast.makeText(this, "La password è troppo debole. Inserisci una password più sicura.", Toast.LENGTH_SHORT).show()
            }
            is FirebaseAuthInvalidCredentialsException -> {
                // Gestisce l'errore di email non valida
                Toast.makeText(this, "L'email inserita non è valida. Controlla l'email e riprova.", Toast.LENGTH_SHORT).show()
            }
            is FirebaseAuthUserCollisionException -> {
                // Gestisce l'errore di conflitto di account
                Toast.makeText(this, "Esiste già un account con questa mail.", Toast.LENGTH_SHORT).show()
            }
            else -> {
                // Gestisce gli altri tipi di errori
                Toast.makeText(this, "Errore di registrazione: ${exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}