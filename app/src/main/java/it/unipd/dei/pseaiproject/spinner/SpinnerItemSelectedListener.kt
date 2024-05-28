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

//classe che implementa le azioni eseguite dallo spinner a seconda dell'item scelto
class SpinnerItemSelectedListener(private val activity: AppCompatActivity, private val firebaseAuth: FirebaseAuth, private val theme: ThemeType, private val styleManager: StyleManager, private val spinner: Spinner) : AdapterView.OnItemSelectedListener {

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        //item selezionato
        val selectedItem = parent.getItemAtPosition(position) as SpinnerItem
        //Toast.makeText(activity, "Selected: $selectedItem", Toast.LENGTH_SHORT).show()

        //nome dell'attività: Le azioni sono diverse a seconda dell'attività su cui sono per cui
        //è necessario differenziare queste azioni sulla base dell'attività che sta usando lo spinner
        val activityName = activity.javaClass.simpleName

        // Esegui un'azione basata sull'elemento selezionato
        //azione di logout: ti riporta nella pagina di login
        if(selectedItem.text == "Logout"){
            firebaseAuth.signOut()
            val intent = Intent(activity, SignInActivity::class.java)
            activity.startActivity(intent)
            activity.finish()
        }
        //azione di cambio tema: cambia il tema da dark a light o viceversa
        if(selectedItem.text == "Change theme"){
            if(theme.themeResId == R.style.DarkStyle) styleManager.applyTheme(ThemeType.LIGHT)
            else styleManager.applyTheme(ThemeType.DARK)
            if (activityName == "MainActivity") spinner.setSelection(0)
            else spinner.setSelection(1)
            activity.recreate()
        }
        //azione di info:
        //  -Se l'item è stato selezionato nella MainActivity allora apre InfoActivity
        //  -Se l'item è stato selezionato nella InfoActivity allora non fa nulla
        if(selectedItem.text == "Info") {
            if (activityName == "MainActivity")
            {
                val intent = Intent(activity, InfoActivity::class.java)
                activity.startActivity(intent)
                spinner.setSelection(0)
            }
        }
        //azione di home:
        //  -Se l'item è stato selezionato nella InfoActivity allora apre MainActivity
        //  -Se l'item è stato selezionato nella MainActivity allora non fa nulla
        if (selectedItem.text == "Home") {
            if (activityName == "InfoActivity")
            {
                val intent = Intent(activity, MainActivity::class.java)
                activity.startActivity(intent)
                activity.finish()
            }
        }
    }
            // Aggiungi altri casi se necessario
    override fun onNothingSelected(parent: AdapterView<*>) {
        // Gestisci il caso in cui nessun elemento è selezionato, se necessario
    }
}
