package it.unipd.dei.pseaiproject.viewmodels

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

    // Suspend fun per signIn, così si aspetta che venga eseguita la funzione in Firebase
    suspend fun signIn(email: String, password: String): Boolean {
        // Segnala l'inizio del processo di signIn
        _signInState.value = true

        // Avvia una coroutine nel contesto principale
        return try {
            withContext(Dispatchers.IO) {
                // Effettua il tentativo di accesso con Firebase Authentication
                firebaseAuth.signInWithEmailAndPassword(email, password).await()
            }
            true
        } catch (e: Exception) {
            // Se l'accesso fallisce, gestisci l'errore
            handleAuthException(e)
            false
        } finally {
            // Fine del processo di signIn
            _signInState.value = false
        }
    }

    // Suspend fun per signUp, così si aspetta che venga eseguita la funzione in Firebase
    suspend fun signUp(email: String, password: String): Boolean {
        // Segnala l'inizio del processo di signUp
        _signUpState.value = true
        return try {
            // Avvia una coroutine nel contesto principale
            withContext(Dispatchers.IO) {
                // Effettua il tentativo di registrazione con Firebase Authentication
                firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            }
            true
        } catch (e: Exception) {
            // Se la registrazione fallisce, gestisci l'errore
            handleAuthException(e)
            false
        } finally {
            // Segnala la fine del processo di signUp
            _signUpState.value = false
        }
    }

    // Suspend fun per il reset della password, così si aspetta che venga eseguita la funzione in Firebase
    suspend fun resetPassword(email: String): Boolean {
        // Segnala l'inizio del processo di reset della password
        _resetPasswordState.value = true

        return try {
            // Avvia una coroutine nel contesto principale
            withContext(Dispatchers.IO) {
                // Invia l'email per il reset della password tramite Firebase Authentication
                firebaseAuth.sendPasswordResetEmail(email).await()
            }
            // Se l'operazione è completata con successo, mostra un messaggio all'utente
            _authException.value = "Il link per il reset della password è stato inviato alla tua email"
            true
        } catch (e: Exception) {
            // Se il reset della password fallisce, gestisci l'errore
            handleAuthException(e)
            false
        }finally {
            // Segnala la fine del processo di reset della password
            _resetPasswordState.value = false
        }
    }

    // Funzione per ottenere l'utente attuale
    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    // Metodo per gestire gli errori di accesso
    private fun handleAuthException(exception: Exception?) {
        _authException.value = when (exception) {
            is FirebaseAuthWeakPasswordException -> "La password è troppo debole. Inserisci una password più sicura."
            is FirebaseAuthUserCollisionException -> "Esiste già un account con questa mail."
            is FirebaseAuthInvalidUserException -> "L'email inserita non è valida. Riprova."
            is FirebaseAuthInvalidCredentialsException -> "Password errata. Riprova."
            else -> "Errore di Firebase: ${exception?.message}"
        }
    }
}