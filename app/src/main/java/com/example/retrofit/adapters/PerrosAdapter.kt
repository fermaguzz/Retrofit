package com.example.retrofit

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class PerrosAdapter(dogs: List<String>?, contexto: Context) :

    RecyclerView.Adapter<PerrosAdapter.ContenedorDeVista> () {
    var innerDogs: List<String>? = dogs
    var innerContext: Context = contexto

    inner class ContenedorDeVista(view: View):
        RecyclerView.ViewHolder(view), View.OnClickListener {
        val ivPerro : ImageView


        init {
            ivPerro = view.findViewById(R.id.ivPerro)
        }

        override fun onClick(p0: View?) {
            TODO("Not yet implemented")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContenedorDeVista {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_dog, parent, false)
        return ContenedorDeVista(view)
    }

    override fun onBindViewHolder(holder: ContenedorDeVista, position: Int) {
        val dog: String = innerDogs!!.get(position)
        Picasso.get().load(dog).into(holder.ivPerro)
    }

    override fun getItemCount(): Int {
        return innerDogs!!.size
    }

}