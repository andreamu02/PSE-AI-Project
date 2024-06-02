package it.unipd.dei.pseaiproject.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import it.unipd.dei.pseaiproject.R
import it.unipd.dei.pseaiproject.style.StyleManager
import it.unipd.dei.pseaiproject.databinding.ActivityForgotpasswordBinding
import it.unipd.dei.pseaiproject.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {

    // Utilizza il delegato by viewModels() per creare e ottenere un'istanza di AuthViewModel
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var binding: ActivityForgotpasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val styleManager = StyleManager(this)
        binding = ActivityForgotpasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        styleManager.setEditTextAppearance(this, findViewById(R.id.forgot_email))
        with(binding){
            // Listener per il pulsante back
            backButton.setOnClickListener { finish() }

            // Listener per il pulsante di reset
            resetButton.setOnClickListener {
                val emailText = forgotEmail.text.toString()
                if (emailText.isNotEmpty()) {
                    resetPassword(emailText)
                } else {
                    Toast.makeText(this@ForgotPasswordActivity, "È necessario inserire una email", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Osserva i messaggi di errore dall'AuthViewModel
        authViewModel.authException.observe(this) { errorMessage ->
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
        }

        // Osserva lo stato del processo di reset della password
        authViewModel.resetPasswordState.observe(this) { isResetting ->
            binding.forgetPasswordProgressbar.visibility = if (isResetting) View.VISIBLE else View.INVISIBLE
            binding.resetButton.visibility = if (isResetting) View.INVISIBLE else View.VISIBLE
        }
    }

    // Funzione privata per il reset della password
    private fun resetPassword(email: String){
        // Avvia una coroutine per chiamare signUp
        lifecycleScope.launch {
            // Chiamata a resetPassword
            if(authViewModel.resetPassword(email)){
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Il link per il reset della password è stato inviato alla tua mail",
                    Toast.LENGTH_SHORT
                ).show()
                // Avvia l'attività di login
                startActivity(Intent(this@ForgotPasswordActivity, SignInActivity::class.java))
                finish()
            }
        }
    }
}