package it.unipd.dei.pseaiproject.spinner

import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import it.unipd.dei.pseaiproject.R
import it.unipd.dei.pseaiproject.style.StyleManager
import it.unipd.dei.pseaiproject.style.ThemeType
import it.unipd.dei.pseaiproject.ui.auth.SignInActivity
import it.unipd.dei.pseaiproject.ui.main.InfoActivity
import it.unipd.dei.pseaiproject.ui.main.MainActivity

/**
 * Classe che implementa le azioni eseguite dallo spinner a seconda dell'item scelto.
 *
 * @param activity L'attività che contiene lo spinner.
 * @param firebaseAuth L'istanza di FirebaseAuth per gestire il logout.
 * @param theme Il tema corrente dell'applicazione.
 * @param styleManager Il gestore dello stile per applicare i temi.
 * @param spinner Lo spinner associato a questo listener.
 */
class SpinnerItemSelectedListener(
    private val activity: AppCompatActivity,
    private val firebaseAuth: FirebaseAuth,
    private val theme: ThemeType,
    private val styleManager: StyleManager,
    private val spinner: Spinner
) : AdapterView.OnItemSelectedListener {

    /**
     * Metodo chiamato quando un elemento dello spinner viene selezionato.
     * Esegue azioni diverse a seconda dell'elemento selezionato e dell'attività corrente.
     *
     * @param parent L'adattatore contenente la vista.
     * @param view La vista dell'elemento selezionato.
     * @param position La posizione dell'elemento selezionato.
     * @param id L'id dell'elemento selezionato.
     */
    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        // Elemento selezionato
        val selectedItem = parent.getItemAtPosition(position) as SpinnerItem

        // Nome dell'attività corrente
        val activityName = activity.javaClass.simpleName

        // Esegui un'azione basata sull'elemento selezionato
        when (selectedItem.text) {
            "Logout" -> {
                firebaseAuth.signOut()
                val intent = Intent(activity, SignInActivity::class.java)
                activity.startActivity(intent)
                activity.finish()
            }
            "Change theme" -> {
                if (theme.themeResId == R.style.DarkStyle) {
                    styleManager.applyTheme(ThemeType.LIGHT)
                } else {
                    styleManager.applyTheme(ThemeType.DARK)
                }
                if (activityName == "MainActivity") spinner.setSelection(0) else spinner.setSelection(1)
                activity.recreate()
            }
            "Info" -> {
                if (activityName == "MainActivity") {
                    val intent = Intent(activity, InfoActivity::class.java)
                    activity.startActivity(intent)
                    spinner.setSelection(0)
                    activity.finish()
                }
            }
            "Home" -> {
                if (activityName == "InfoActivity") {
                    val intent = Intent(activity, MainActivity::class.java)
                    activity.startActivity(intent)
                    activity.finish()
                }
            }
        }
    }

    /**
     * Metodo chiamato quando nessun elemento dello spinner è selezionato.
     *
     * @param parent L'adattatore contenente la vista.
     */
    override fun onNothingSelected(parent: AdapterView<*>) {
        // Gestisci il caso in cui nessun elemento è selezionato, se necessario
    }
}
