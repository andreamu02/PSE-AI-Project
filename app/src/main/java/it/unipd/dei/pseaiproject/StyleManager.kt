package it.unipd.dei.pseaiproject

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity

class StyleManager(private val activity: AppCompatActivity) {

    private val PREFS_NAME = "theme_prefs"
    private val KEY_THEME = "key_theme"

    fun applyTheme(themeType: ThemeType) {
        // Salva il tema scelto nelle SharedPreferences
        saveThemePreference(activity, themeType)

        // Applica il tema
        activity.setTheme(themeType.themeResId)
    }

    private fun saveThemePreference(context: Context, themeType: ThemeType) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(KEY_THEME, themeType.name)
        editor.apply()
    }

    fun applyColors(
        actionBarColor: Int,
        toolbarTitleColor: Int,
    ) {
        val toolbar: Toolbar? = activity.findViewById(R.id.toolbar)
        toolbar?.setBackgroundColor(actionBarColor)
        toolbar?.setTitleTextColor(toolbarTitleColor)
    }

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
