package it.unipd.dei.pseaiproject.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import it.unipd.dei.pseaiproject.style.StyleManager
import it.unipd.dei.pseaiproject.ui.main.MainActivity
import it.unipd.dei.pseaiproject.databinding.ActivitySigninBinding
import it.unipd.dei.pseaiproject.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {

    // Utilizza il delegato by viewModels() per creare e ottenere un'istanza di AuthViewModel
    private val authViewModel : AuthViewModel by viewModels()
    private lateinit var binding: ActivitySigninBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val styleManager = StyleManager(this)
        val theme = styleManager.loadThemePreference(this)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding){
            // Listener per il pulsante di login
            loginButton.setOnClickListener {
                val email = emailInput.text.toString()
                val password = passwordInput.text.toString()
                // Verifica che i campi email e password non siano vuoti
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    signIn(email, password)
                } else {
                    // Mostra un messaggio se i campi sono vuoti
                    Toast.makeText(this@SignInActivity, "Non sono ammessi campi vuoti", Toast.LENGTH_SHORT).show()
                }
            }

            // Imposta il listener per il link di registrazione
            notSignup.setOnClickListener {
                startActivity(Intent(this@SignInActivity, SignUpActivity::class.java))
            }

            // Imposta il listener per il link di recupero password
            forgotPassword.setOnClickListener {
                startActivity(Intent(this@SignInActivity, ForgotPasswordActivity::class.java))
            }
        }

        // Osserva i messaggi di errore dall'AuthViewModel
        authViewModel.authException.observe(this) { errorMessage ->
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
        }

        // Osserva lo stato del processo di reset della password
        authViewModel.signInState.observe(this) { isSigningIn ->
            binding.signInProgressbar.visibility = if (isSigningIn) View.VISIBLE else View.INVISIBLE
            binding.loginButton.visibility = if (isSigningIn) View.INVISIBLE else View.VISIBLE
        }
    }

    // Metodo chiamato quando l'attività diventa visibile
    override fun onStart() {
        super.onStart()

        // Verifica se l'utente è già autenticato
        if(authViewModel.getCurrentUser() != null){
            // Se l'utente è già autenticato, avvia l'attività principale
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    // Funzione privata per il signIn
    private fun signIn(email: String, password: String){
        // Avvia una coroutine per chiamare signIn
        lifecycleScope.launch {
            // Chiamata a signIn
            if(authViewModel.signIn(email, password)){
                // Se il login ha avuto successo, avvia MainActivity
                startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                finish()
            }
        }
    }
}