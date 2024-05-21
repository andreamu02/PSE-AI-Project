package it.unipd.dei.pseaiproject.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.Exception

class AuthViewModel : ViewModel() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    // LiveData per comunicare i messaggi di errore alla UI
    private val _authException = MutableLiveData<String>()
    val authException: LiveData<String> = _authException

    // LiveData per gestire la visibilità della progress bar durante reset della password
    private val _resetPasswordState = MutableLiveData<Boolean>()
    val resetPasswordState: LiveData<Boolean> = _resetPasswordState

    // LiveData per gestire la visibilità della progress bar durante il signIn
    private val _signInState = MutableLiveData<Boolean>()
    val signInState: LiveData<Boolean> = _signInState

    // LiveData per gestire la visibilità della progress bar durante il signIn
    private val _signUpState = MutableLiveData<Boolean>()
    val signUpState: LiveData<Boolean> = _signUpState

    suspend fun signIn(email: String, password: String): Boolean {
        var signinSucces = false

        // Segnala l'inizio del processo di signIn
        _signInState.value = true

        // Avvia una coroutine nel contesto principale
        withContext(Dispatchers.Main) {
            try {
                // Effettua il tentativo di accesso con Firebase Authentication
                firebaseAuth.signInWithEmailAndPassword(email, password).await()

                // Se l'accesso è riuscito, imposta il flag a true
                signinSucces = true
            } catch (e: Exception) {
                // Se l'accesso fallisce, gestisci l'errore
                handleAuthException(e)
            }

            // Segnala la fine del processo di signIn
            _signInState.value = false
        }
        return signinSucces
    }

    suspend fun signUp(email: String, password: String): Boolean {
        var signupSuccess = false

        // Segnala l'inizio del processo di signUp
        _signUpState.value = true

        // Avvia una coroutine nel contesto principale
        withContext(Dispatchers.Main) {
            try {
                // Effettua il tentativo di registrazione con Firebase Authentication
                firebaseAuth.createUserWithEmailAndPassword(email, password).await()

                // Se la registrazione è riuscita, ritorna true
                signupSuccess = true
            } catch (e: Exception) {
                // Se la registrazione fallisce, gestisci l'errore
                handleAuthException(e)
            }

            // Segnala la fine del processo di signUp
            _signUpState.value = false
        }
        return signupSuccess
    }

    suspend fun resetPassword(email: String): Boolean {
        var resetPasswordSuccess = false

        // Segnala l'inizio del processo di reset della password
        _resetPasswordState.value = true

        // Avvia una coroutine nel contesto principale
        withContext(Dispatchers.Main) {
            try {
                // Invia l'email per il reset della password tramite Firebase Authentication
                firebaseAuth.sendPasswordResetEmail(email).await()

                resetPasswordSuccess = true
                // Se l'operazione è completata con successo, mostra un messaggio all'utente
                _authException.value =
                    "Il link per il reset della password è stato inviato alla tua email"
            } catch (e: Exception) {
                // Se si verifica un errore, gestiscilo
                handleAuthException(e)
            }

            // Segnala la fine del processo di reset della password
            _resetPasswordState.value = false
        }
        return resetPasswordSuccess
    }

    // Funzione per ottenere l'utente attuale
    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    // Metodo per gestire gli errori di accesso
    private fun handleAuthException(exception: Exception?) {
        when (exception) {
            is FirebaseAuthWeakPasswordException -> {
                _authException.value =
                    "La password è troppo debole. Inserisci una password più sicura."
            }
            is FirebaseAuthUserCollisionException -> {
                // Gestisce l'errore di conflitto di account
                _authException.value = "Esiste già un account con questa mail."
            }
            is FirebaseAuthInvalidUserException -> {
                // Gestisce l'errore di utente non trovato
                _authException.value = "L'email inserita non è valida. Riprova."
            }
            is FirebaseAuthInvalidCredentialsException -> {
                // Gestisce l'errore di password errata
                _authException.value = "Password errata. Riprova."
            }
            else -> {
                // Gestisce gli altri tipi di errori
                _authException.value = "Errore di Firebase: ${exception?.message}"
            }
        }
    }
}