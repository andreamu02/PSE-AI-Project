package com.example.progetto

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat

class CustomSpinnerAdapter(context: Context, private val data: List<SpinnerItem>, private val textColor: Int) :
    ArrayAdapter<SpinnerItem>(context, 0, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val holder: ViewHolder

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.spinner_item_layout, parent, false)
            val textView = view.findViewById<TextView>(R.id.text)
            textView.setTextColor(ContextCompat.getColor(context, textColor))
            holder = ViewHolder()
            holder.icon = view.findViewById(R.id.icon)
            holder.text = view.findViewById(R.id.text)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        val item = getItem(position)
        holder.icon?.setImageResource(item?.icon ?: 0)
        holder.text?.text = item?.text

        return view!!
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }

    private class ViewHolder {
        var icon: ImageView? = null
        var text: TextView? = null
    }
}
