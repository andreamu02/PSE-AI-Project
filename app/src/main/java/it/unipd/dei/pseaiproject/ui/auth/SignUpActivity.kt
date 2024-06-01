package it.unipd.dei.pseaiproject.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import it.unipd.dei.pseaiproject.databinding.ActivitySignupBinding
import it.unipd.dei.pseaiproject.style.StyleManager
import it.unipd.dei.pseaiproject.ui.main.MainActivity
import it.unipd.dei.pseaiproject.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

    // Utilizza il delegato by viewModels() per creare e ottenere un'istanza di AuthViewModel
    private val authViewModel : AuthViewModel by viewModels()
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val styleManager = StyleManager(this)
        //TODO CHIEDERE SE SERVE QUESTO
        val theme = styleManager.loadThemePreference(this)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        styleManager.setEditTextAppearance(this, binding.emailInputSignup)
        styleManager.setEditTextAppearance(this, binding.passwordInputSignup)
        styleManager.setEditTextAppearance(this, binding.confirmPasswordInputSignup)
        with(binding) {
            // Imposta il listener per il pulsante di registrazione
            signupButton.setOnClickListener {
                val email = emailInputSignup.text.toString()
                val password = passwordInputSignup.text.toString()
                val confirmPassword = confirmPasswordInputSignup.text.toString()

                // Verifica che i campi email, password e conferma password non siano vuoti
                if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                    // Verifica che le password corrispondano
                    if (password == confirmPassword) {
                        signUp(email, password)
                    } else {
                        // Mostra un messaggio se le password non coincidono
                        Toast.makeText(this@SignUpActivity, "Le due password non combaciano", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Mostra un messaggio se i campi sono vuoti
                    Toast.makeText(this@SignUpActivity, "Non sono ammessi campi vuoti", Toast.LENGTH_SHORT).show()
                }
            }

            alreadySignup.setOnClickListener {
                startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
            }
        }

        // Osserva i messaggi di errore dall'AuthViewModel
        authViewModel.authException.observe(this) { errorMessage ->
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
        }

        // Osserva lo stato del processo di signUp
        authViewModel.signUpState.observe(this) { isSigningUp ->
            binding.signUpProgressbar.visibility = if (isSigningUp) View.VISIBLE else View.INVISIBLE
            binding.signupButton.visibility = if (isSigningUp) View.INVISIBLE else View.VISIBLE
        }
    }

    // Funzione privata per il signUp
    private fun signUp(email: String, password: String){
        // Avvia una coroutine per chiamare signUp
        lifecycleScope.launch {
            // Chiamata a signUp
            if(authViewModel.signUp(email, password)){
                // Se la registrazione è riuscita, avvia l'attività principale
                startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
            }}
    }
}
