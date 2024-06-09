package it.unipd.dei.pseaiproject.style

import it.unipd.dei.pseaiproject.R

/**
 * Enumerazione che definisce i tipi di tema disponibili nell'applicazione.
 *
 * @property themeResId L'ID della risorsa del tema.
 */
enum class ThemeType(val themeResId: Int) {
    DARK(R.style.DarkStyle),
    LIGHT(R.style.LightStyle)
}
