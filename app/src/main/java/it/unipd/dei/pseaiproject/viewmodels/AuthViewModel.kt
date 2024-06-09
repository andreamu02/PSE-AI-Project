package it.unipd.dei.pseaiproject.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * ViewModel per la gestione delle operazioni di autenticazione.
 */
class AuthViewModel : ViewModel() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    // LiveData per comunicare i messaggi di errore alla UI
    private val _authException = MutableLiveData<String>()
    val authException: LiveData<String> = _authException

    // LiveData per gestire la visibilità della progress bar durante il reset della password
    private val _resetPasswordState = MutableLiveData<Boolean>()
    val resetPasswordState: LiveData<Boolean> = _resetPasswordState

    // LiveData per gestire la visibilità della progress bar durante il signIn
    private val _signInState = MutableLiveData<Boolean>()
    val signInState: LiveData<Boolean> = _signInState

    // LiveData per gestire la visibilità della progress bar durante il signIn
    private val _signUpState = MutableLiveData<Boolean>()
    val signUpState: LiveData<Boolean> = _signUpState

    /**
     * Effettua il login con l'email e la password fornite.
     */
    suspend fun signIn(email: String, password: String): Boolean {
        _signInState.value = true
        return try {
            withContext(Dispatchers.IO) {
                firebaseAuth.signInWithEmailAndPassword(email, password).await()
            }
            true
        } catch (e: Exception) {
            handleAuthException(e)
            false
        } finally {
            _signInState.value = false
        }
    }

    /**
     * Registra un nuovo utente con l'email e la password fornite.
     */
    suspend fun signUp(email: String, password: String): Boolean {
        _signUpState.value = true
        return try {
            withContext(Dispatchers.IO) {
                firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            }
            true
        } catch (e: Exception) {
            handleAuthException(e)
            false
        } finally {
            _signUpState.value = false
        }
    }

    /**
     * Invia un'email per il reset della password all'indirizzo fornito.
     */
    suspend fun resetPassword(email: String): Boolean {
        _resetPasswordState.value = true
        return try {
            withContext(Dispatchers.IO) {
                firebaseAuth.sendPasswordResetEmail(email).await()
            }
            _authException.value = "Il link per il reset della password è stato inviato alla tua email"
            true
        } catch (e: Exception) {
            handleAuthException(e)
            false
        } finally {
            _resetPasswordState.value = false
        }
    }

    /**
     * Restituisce l'utente attualmente autenticato.
     */
    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    /**
     * Gestisce le eccezioni di autenticazione e aggiorna il LiveData [_authException] con il messaggio appropriato.
     */
    private fun handleAuthException(exception: Exception?) {
        _authException.value = when (exception) {
            is FirebaseAuthWeakPasswordException -> "La password è troppo debole. Inserisci una password più sicura."
            is FirebaseAuthUserCollisionException -> "Esiste già un account con questa mail."
            is FirebaseAuthInvalidUserException -> "L'email inserita non è valida. Riprova."
            is FirebaseAuthInvalidCredentialsException -> "Password errata. Riprova."
            is FirebaseAuthException -> {
                when (exception.errorCode) {
                    "ERROR_NETWORK_REQUEST_FAILED" -> "Errore di rete. Controlla la tua connessione e riprova."
                    else -> "Errore di Firebase: ${exception.message}"
                }
            }
            else -> "Errore di Firebase: ${exception?.message}"
        }
    }
}
