package it.unipd.dei.pseaiproject.spinner

/**
 * Data class per definire un generico item dello spinner.
 * Contiene un'icona rappresentativa dell'azione compiuta dallo spinner
 * e una stringa contenente la descrizione testuale dell'azione.
 *
 * @param icon L'icona rappresentativa dell'azione.
 * @param text La descrizione testuale dell'azione.
 */
data class SpinnerItem(val icon: Int, val text: String)
