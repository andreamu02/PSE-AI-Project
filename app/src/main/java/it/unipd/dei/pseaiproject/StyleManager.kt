package it.unipd.dei.pseaiproject

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity

//classe che, data un'attività, ne cambia lo stile grafico salvando la scelta in memoria
class StyleManager(private val activity: AppCompatActivity) {

    //stringhe che fungono da chiavi per lo stato dell'attività
    private val PREFS_NAME = "theme_prefs"
    private val KEY_THEME = "key_theme"

    //Metodo che, dato un tema, lo applica all'attività
    fun applyTheme(themeType: ThemeType) {
        // Salva il tema scelto nelle SharedPreferences
        saveThemePreference(activity, themeType)

        // Applica il tema
        activity.setTheme(themeType.themeResId)
    }

    //metodo che salva l'ultimo tema usato in memoria
    private fun saveThemePreference(context: Context, themeType: ThemeType) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(KEY_THEME, themeType.name)
        editor.apply()
    }

    //metodo di applicazione dei colori alla toolbar, se presente.
    fun applyColors(
        actionBarColor: Int,
        toolbarTitleColor: Int,
    ) {
        val toolbar: Toolbar? = activity.findViewById(R.id.toolbar)
        toolbar?.setBackgroundColor(actionBarColor)
        toolbar?.setTitleTextColor(toolbarTitleColor)
    }

    //metodo che, data un'attività, prende l'ultimo tema usato prima della sua chiusura e lo applica all'attività
    //I temi disponibili sono: dark e light
    fun loadThemePreference(activity: AppCompatActivity): ThemeType {
        val prefs: SharedPreferences = activity.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        val themeName = prefs.getString("key_theme", ThemeType.DARK.name)
        var backgroundAB = R.color.black
        var titleColor = R.color.ametista
        if (themeName == ThemeType.DARK.name)
        {
            activity.setTheme(R.style.DarkStyle)
            activity.window.setBackgroundDrawableResource(R.drawable.home_background_dark)
            applyColors(backgroundAB, titleColor)
        }
        else{
            activity.setTheme(R.style.LightStyle)
            backgroundAB = R.color.white
            titleColor = R.color.green
            activity.window.setBackgroundDrawableResource(R.drawable.home_background_light)
            applyColors(backgroundAB, titleColor)
        }
        return ThemeType.valueOf(themeName!!)
    }
}
