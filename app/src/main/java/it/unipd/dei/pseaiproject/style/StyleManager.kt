package it.unipd.dei.pseaiproject.style

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import it.unipd.dei.pseaiproject.R

//classe che, data un'attività, ne cambia lo stile grafico salvando la scelta in memoria
class StyleManager(private val activity: AppCompatActivity) {

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

    //metodo che, data un'attività, prende l'ultimo tema usato prima della sua chiusura e lo applica all'attività
    //I temi disponibili sono: dark e light
    fun loadThemePreference(activity: AppCompatActivity): ThemeType {
        val prefs: SharedPreferences = activity.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        val themeName = prefs.getString("key_theme", ThemeType.DARK.name)
        if (themeName == ThemeType.DARK.name)
        {
            activity.setTheme(R.style.DarkStyle)
            activity.window.setBackgroundDrawableResource(R.drawable.home_background_dark)
        }
        else{
            activity.setTheme(R.style.LightStyle)
            activity.window.setBackgroundDrawableResource(R.drawable.home_background_light)
        }
        return ThemeType.valueOf(themeName!!)
    }

    //metodo per cambiare il colore di un'immagine data come parametro
    fun setImageViewDrawableColor(imageView: ImageView, drawableResId: Int, context: Context) {
        val drawable = ContextCompat.getDrawable(context, drawableResId)
        val prefs: SharedPreferences = activity.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        val themeName = prefs.getString("key_theme", ThemeType.DARK.name)
        var color = R.color.black
        if (themeName == ThemeType.DARK.name) color = R.color.white
        if (drawable != null) {
            drawable.setTint(ContextCompat.getColor(context, color))
            imageView.setImageDrawable(drawable)
        }
    }

    //metodo che setta i colori di widget presenti nella UI a seconda del tema
    fun setWidgetAppearance(context: Context, toolbar: Toolbar, button: Button?, view1: View?, view2: View?)
    {
        val prefs: SharedPreferences = activity.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        val themeName = prefs.getString("key_theme", ThemeType.DARK.name)
        var color = R.color.black
        var viewColor = R.color.lightGrey
        if (themeName == ThemeType.DARK.name)
        {
            color = R.color.white
            viewColor = R.color.colorPrimary
            button?.setTextAppearance(R.style.CustomButtonDark)
        }
        else button?.setTextAppearance(R.style.CustomButtonLight)
        toolbar.setTitleTextAppearance(context, R.style.ToolbarTitleText)
        toolbar.setTitleTextColor(ContextCompat.getColor(context, color))
        view1?.setBackgroundColor(ContextCompat.getColor(context, viewColor))
        view2?.setBackgroundColor(ContextCompat.getColor(context, viewColor))
    }

    //metodo che setta i colori di widget di tipo EditText a seconda del tema
    fun setEditTextAppearance(context: Context, editText: EditText)
    {
        val prefs: SharedPreferences = activity.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        val themeName = prefs.getString("key_theme", ThemeType.DARK.name)
        var colorHint = R.color.darkGrey
        var color = R.color.black
        if (themeName == ThemeType.DARK.name)
        {
            color = R.color.white
            colorHint = R.color.white
        }
        editText.setTextColor(ContextCompat.getColor(context, color))
        editText.setHintTextColor(ContextCompat.getColor(context, colorHint))

    }

    companion object {
        //stringhe che fungono da chiavi per lo stato dell'attività
        const val PREFS_NAME = "theme_prefs"
        const val KEY_THEME = "key_theme"
    }
}
