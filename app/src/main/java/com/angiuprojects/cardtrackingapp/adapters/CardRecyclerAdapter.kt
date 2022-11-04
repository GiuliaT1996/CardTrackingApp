package com.angiuprojects.cardtrackingapp.adapters

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.angiuprojects.cardtrackingapp.R
import com.angiuprojects.cardtrackingapp.entities.Card
import com.angiuprojects.cardtrackingapp.handlers.EditPopUpHandler
import com.google.android.material.textfield.TextInputLayout

class CardRecyclerAdapter(private val dataSet : MutableList<Card>, private val context: Context) : RecyclerView.Adapter<CardRecyclerAdapter.MyViewHolder>() {

    lateinit var dialog: Dialog

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView
        var archetype: TextView
        var duelist: TextView
        var set: TextView
        var pricey: ImageView
        var inTransit: ImageView
        var layout: RelativeLayout

        init {
            name = view.findViewById(R.id.name)
            archetype = view.findViewById(R.id.archetype)
            duelist = view.findViewById(R.id.duelist)
            set = view.findViewById(R.id.set)
            pricey = view.findViewById(R.id.pricey)
            inTransit = view.findViewById(R.id.inTransit)
            layout = view.findViewById(R.id.card_view)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view, parent, false)

        return MyViewHolder(view)
    }

    private fun onLongClickEdit(view: View, holder: MyViewHolder) {
        EditPopUpHandler.getInstance().populateEditPopUp(dataSet[holder.adapterPosition], view, holder.adapterPosition, this)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.name.text = dataSet[holder.adapterPosition].name
        holder.archetype.text = dataSet[holder.adapterPosition].archetype
        holder.set.text = dataSet[holder.adapterPosition].set
        holder.duelist.text = dataSet[holder.adapterPosition].duelist

        if(!dataSet[holder.adapterPosition].pricey)
            holder.pricey.visibility = View.INVISIBLE
        else
            holder.pricey.visibility = View.VISIBLE

        if(!dataSet[holder.adapterPosition].inTransit)
            holder.inTransit.visibility = View.INVISIBLE
        else
            holder.inTransit.visibility = View.VISIBLE

        holder.layout.setOnLongClickListener {
            onLongClickEdit(holder.layout, holder)
            true
        }

    }

    override fun getItemCount() = dataSet.size

}