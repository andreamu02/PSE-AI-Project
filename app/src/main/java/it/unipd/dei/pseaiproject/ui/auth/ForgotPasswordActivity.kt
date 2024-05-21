package it.unipd.dei.pseaiproject.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import it.unipd.dei.pseaiproject.R
import it.unipd.dei.pseaiproject.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {

    // Dichiarazione delle variabili
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var email: EditText
    private lateinit var resetButton: MaterialButton
    private lateinit var backButton: MaterialButton
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotpassword)

        // Inizializzazione dei componenti della UI
        email = findViewById(R.id.forgot_email)
        resetButton = findViewById(R.id.reset_button)
        backButton = findViewById(R.id.back_button)
        progressBar = findViewById(R.id.forgetPasswordProgressbar)

        // Listener per il pulsante di back
        backButton.setOnClickListener {
            finish()
        }

        // Listener per il pulsante di reset
        resetButton.setOnClickListener {
            if (email.text.isNotEmpty()) {
                // Avvia una coroutine per chiamare signUp
                lifecycleScope.launch {
                    // Chiamata a resetPassword
                    val resetPasswordSuccess = authViewModel.resetPassword(email.text.toString())

                    if (resetPasswordSuccess) {
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            "Il link per il reset della password è stato inviato alla tua mail",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Avvia l'attività di login
                        val intent = Intent(this@ForgotPasswordActivity, SignInActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            } else {
                Toast.makeText(this, "È necessario inserire una email", Toast.LENGTH_SHORT).show()
            }
        }

        // Osserva i messaggi di errore dall'AuthViewModel
        authViewModel.authException.observe(this, Observer { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        })

        // Osserva lo stato del processo di reset della password
        authViewModel.resetPasswordState.observe(this, Observer { isResetting ->
            if (isResetting) {
                // Mostra la ProgressBar e nascondi il pulsante "Reset Password"
                progressBar.visibility = View.VISIBLE
                resetButton.visibility = View.INVISIBLE
            } else {
                // Nascondi la ProgressBar e mostra il pulsante "Reset Password"
                progressBar.visibility = View.INVISIBLE
                resetButton.visibility = View.VISIBLE
            }
        })
    }
}