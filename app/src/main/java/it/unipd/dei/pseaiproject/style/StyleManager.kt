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

/**
 * Classe che gestisce lo stile grafico dell'applicazione.
 *
 * @property activity L'attività corrente.
 */
class StyleManager(private val activity: AppCompatActivity) {

    /**
     * Metodo che applica il tema specificato all'attività e lo salva nelle SharedPreferences.
     *
     * @param themeType Il tipo di tema da applicare.
     */
    fun applyTheme(themeType: ThemeType) {
        // Salva il tema scelto nelle SharedPreferences
        saveThemePreference(activity, themeType)

        // Applica il tema
        activity.setTheme(themeType.themeResId)
    }

    /**
     * Metodo privato che salva il tema scelto nelle SharedPreferences.
     *
     * @param context Il contesto corrente.
     * @param themeType Il tipo di tema da salvare.
     */
    private fun saveThemePreference(context: Context, themeType: ThemeType) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(KEY_THEME, themeType.name)
        editor.apply()
    }

    /**
     * Metodo che carica il tema preferito dall'utente e lo applica all'attività.
     *
     * @param activity L'attività su cui applicare il tema.
     * @return Il tipo di tema caricato.
     */
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

    /**
     * Metodo che imposta il colore di un'immagine in base al tema corrente.
     *
     * @param imageView L'ImageView da modificare.
     * @param drawableResId La risorsa drawable dell'immagine.
     * @param context Il contesto corrente.
     */
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

    /**
     * Metodo che imposta l'aspetto dei widget presenti nella UI a seconda del tema corrente.
     *
     * @param context Il contesto corrente.
     * @param toolbar La toolbar da modificare.
     * @param button Il pulsante da modificare.
     * @param view1 La prima vista da modificare.
     * @param view2 La seconda vista da modificare.
     */
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

    /**
     * Metodo che imposta l'aspetto di un widget EditText a seconda del tema corrente.
     *
     * @param context Il contesto corrente.
     * @param editText L'EditText da modificare.
     */
    fun setEditTextAppearance(context: Context, editText: EditText) {
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

    /**
     * Metodo che imposta l'aspetto della statusBarv a seconda del tema corrente.
     *
     * @param activity l'attività di cui bisogna cambiare il colore della statusBar
     */
    fun changeStatusBarColor(activity: AppCompatActivity) {
        val prefs: SharedPreferences = activity.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        val themeName = prefs.getString("key_theme", ThemeType.DARK.name)
        if (themeName == ThemeType.DARK.name) activity.window.statusBarColor = ContextCompat.getColor(activity, R.color.colorPrimary)
        else activity.window.statusBarColor = ContextCompat.getColor(activity, R.color.brownWhite)
    }

    companion object {
        // Costanti per le SharedPreferences
        const val PREFS_NAME = "theme_prefs"
        const val KEY_THEME = "key_theme"
    }
}
