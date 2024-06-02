package it.unipd.dei.pseaiproject.spinner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import it.unipd.dei.pseaiproject.R
import it.unipd.dei.pseaiproject.style.ThemeType

/**
 * Adattatore personalizzato per personalizzare l'aspetto degli elementi di uno spinner.
 * Riceve il contesto, la lista degli elementi dello spinner e il tema per decidere i colori dello spinner.
 *
 * @param context Il contesto in cui viene utilizzato lo spinner.
 * @param data La lista degli elementi da visualizzare nello spinner.
 * @param theme Il tipo di tema usato per determinare i colori dello spinner.
 */
class CustomSpinnerAdapter(context: Context, data: List<SpinnerItem>, private val theme: ThemeType) :
    ArrayAdapter<SpinnerItem>(context, 0, data) {

    /**
     * Crea e ritorna una vista per l'elemento dello spinner selezionato.
     * Imposta il colore del testo e dello sfondo di un elemento generico dello spinner
     * e associa un'icona a un determinato testo.
     *
     * @param position La posizione dell'elemento all'interno del set di dati dell'adattatore.
     * @param convertView La vecchia vista da riutilizzare, se possibile.
     * @param parent Il genitore a cui questa vista verrà eventualmente allegata.
     * @return Una vista corrispondente ai dati nella posizione specificata.
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val holder: ViewHolder
        var textColor = R.color.white
        var backgroundColor = R.color.black
        if (theme.name == ThemeType.LIGHT.name) {
            textColor = R.color.black
            backgroundColor = R.color.lightBrown
        }
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.spinner_item_layout, parent, false)
            val textView = view.findViewById<TextView>(R.id.text)
            val imageView = view.findViewById<ImageView>(R.id.icon)
            textView.setTextColor(ContextCompat.getColor(context, textColor))
            view.setBackgroundColor(ContextCompat.getColor(context, backgroundColor))
            holder = ViewHolder()
            holder.icon = imageView
            holder.text = textView
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        val item = getItem(position)
        holder.icon?.setImageResource(item?.icon ?: 0)
        holder.text?.text = item?.text

        return view!!
    }

    /**
     * Gestisce la visualizzazione degli elementi nel menu a tendina dello spinner.
     * Crea e ritorna una vista per ogni elemento dello spinner.
     *
     * @param position La posizione dell'elemento all'interno del set di dati dell'adattatore.
     * @param convertView La vecchia vista da riutilizzare, se possibile.
     * @param parent Il genitore a cui questa vista verrà eventualmente allegata.
     * @return Una vista corrispondente ai dati nella posizione specificata.
     */
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }

    /**
     * Classe interna statica per ottimizzare le prestazioni dell'adattatore.
     * Usando questa classe si evitano chiamate ripetute a findViewById (che sono costose).
     */
    private class ViewHolder {
        var icon: ImageView? = null
        var text: TextView? = null
    }
}
