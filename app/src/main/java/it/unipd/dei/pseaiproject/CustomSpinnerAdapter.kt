package it.unipd.dei.pseaiproject

import android.content.Context
import android.graphics.drawable.VectorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat

//Classe che personalizza l'aspetto degli elementi di uno spinner
//In ingresso riceve il contesto, la lista degli elementi dello spinner e il tema con cui decidere i colori dello spinner
class CustomSpinnerAdapter(context: Context, data: List<SpinnerItem>, private val theme: ThemeType) :
    ArrayAdapter<SpinnerItem>(context, 0, data) {

        //metodo che crea e ritorna una vista per l'elemento dello spinner selezionato
        //usato per settare il colore del testo e dello sfondo di un generico elemento dello spinner
        //e per bindare un'icona ad un certo testo
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val holder: ViewHolder
        var textColor = R.color.white
        var backgroundColor = R.color.ametista
        if (theme.name == ThemeType.LIGHT.name)
        {
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

    //metodo che gestisce la visualizzazione degli elementi nel menu a tendina dello spinner
    //crea e ritorna una vista per ogni elemento dello spinner
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }

    //classe interna statica per ottimizzare le prestazioni dell'adattatore
    //Tramite questa classe: posso evitare chiamate ripetute a findViewById (costose)
    private class ViewHolder {
        var icon: ImageView? = null
        var text: TextView? = null
    }
}
