package com.example.botondepanicov1.wifi_direct

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.botondepanicov1.R
import java.util.ArrayList

class MapDevicesAdapter (context: Context, resource: Int) :
ArrayAdapter<Ingredient>(context, resource) {
    private lateinit var name: TextView
    private lateinit var distance: TextView
    private lateinit var indice: TextView
    private lateinit var date: TextView



    private val ingredientList: List<Ingredient> = ArrayList()
    private var chatMessageList: MutableList<Ingredient> = ArrayList()

    private lateinit var imageView: ImageView

    override fun add(`object`: Ingredient?) {
        chatMessageList.add(`object`!!)
        super.add(`object`)
    }

    fun MapDevicesAdapter(context: Context, textViewResourceId: Int) {
        textViewResourceId
        context
    }



    override fun getCount(): Int {
        return chatMessageList.size
    }

    override fun clear() {
        chatMessageList.clear()
    }

    fun setChatMessageList(chatMessageList: MutableList<Ingredient>) {
        this.chatMessageList = chatMessageList
    }

    override fun getItem(index: Int): Ingredient {
        return chatMessageList[index]
    }


    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var row = convertView
        val ingredient = getItem(position)

        val inflater =
            this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        row = inflater.inflate(R.layout.adapter_dispositivos_encontrados_wifi, parent, false)
        name = row.findViewById(R.id.name)
        distance = row.findViewById(R.id.distancia)
        indice = row.findViewById(R.id.indice)
        date = row.findViewById(R.id.fecha)
        imageView = row.findViewById(R.id.imageLocation)
        name.text = ingredient.name
        val distanceString: String
        val charDistance = java.lang.String.valueOf(ingredient.getDistance())
        distanceString = if (ingredient.getDistance() > 200.0) {
            "No se pudo obtener la distancia"
        } else if (charDistance.length < 3) {
            "Distancia: $charDistance metros"
        } else {
            "Distancia: " + java.lang.String.valueOf(ingredient.getDistance())
                .substring(0, 3) + " metros"
        }
        distance.text = distanceString
        date.text = "Fecha de actualización:  " + ingredient.getDate()
        indice.text = "" + ingredient.getIndice()
        if (row != null) {
            when (position) {
                0 -> imageView.background = row.resources.getDrawable(R.drawable.locationblue)
                1 -> imageView.background = row.resources.getDrawable(R.drawable.locationblue)
                2 -> imageView.background = row.resources.getDrawable(R.drawable.locationblue)
                3 -> imageView.background = row.resources.getDrawable(R.drawable.locationblue)
                4 -> imageView.background = row.resources.getDrawable(R.drawable.locationblue)
                5 -> imageView.background = row.resources.getDrawable(R.drawable.locationblue)
                6 -> imageView.background = row.resources.getDrawable(R.drawable.locationblue)
                7 -> imageView.background = row.resources.getDrawable(R.drawable.locationblue)
                8 -> imageView.background = row.resources.getDrawable(R.drawable.locationblue)
                9 -> imageView.background = row.resources.getDrawable(R.drawable.locationblue)
            }
        }
        return row
    }
}

