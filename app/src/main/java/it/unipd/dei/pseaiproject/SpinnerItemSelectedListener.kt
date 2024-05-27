package it.unipd.dei.pseaiproject

import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import it.unipd.dei.pseaiproject.ui.auth.SignInActivity
import it.unipd.dei.pseaiproject.ui.main.InfoActivity
import it.unipd.dei.pseaiproject.ui.main.MainActivity

class SpinnerItemSelectedListener(private val activity: AppCompatActivity, private val firebaseAuth: FirebaseAuth, private val theme: ThemeType, private val styleManager: StyleManager, private val spinner: Spinner) : AdapterView.OnItemSelectedListener {

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        val selectedItem = parent.getItemAtPosition(position) as SpinnerItem
        //Toast.makeText(activity, "Selected: $selectedItem", Toast.LENGTH_SHORT).show()

        val activityName = activity.javaClass.simpleName

        // Esegui un'azione basata sull'elemento selezionato
        if(selectedItem.text == "Logout"){
            firebaseAuth.signOut()
            val intent = Intent(activity, SignInActivity::class.java)
            activity.startActivity(intent)
            activity.finish()
        }
        if(selectedItem.text == "Change theme"){
            if(theme.themeResId == R.style.DarkStyle) styleManager.applyTheme(ThemeType.LIGHT)
            else styleManager.applyTheme(ThemeType.DARK)
            if (activityName == "MainActivity") spinner.setSelection(0)
            else spinner.setSelection(1)
            activity.recreate()
        }
        if(selectedItem.text == "Info") {
            if (activityName == "MainActivity")
            {
                val intent = Intent(activity, InfoActivity::class.java)
                activity.startActivity(intent)
                spinner.setSelection(0)
            }
        }
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
