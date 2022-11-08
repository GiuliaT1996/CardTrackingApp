package com.angiuprojects.cardtrackingapp.adapters

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.angiuprojects.cardtrackingapp.R
import com.angiuprojects.cardtrackingapp.entities.Card
import com.angiuprojects.cardtrackingapp.handlers.EditPopUpHandler
import com.angiuprojects.cardtrackingapp.queries.Queries
import com.angiuprojects.cardtrackingapp.utilities.Constants
import com.angiuprojects.cardtrackingapp.utilities.Utils


class CardRecyclerAdapter(private val dataSet : MutableList<Card>, private val context: Context) : RecyclerView.Adapter<CardRecyclerAdapter.MyViewHolder>() {

    lateinit var dialog: Dialog

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView
        var archetype: TextView
        var duelist: TextView
        var set: TextView
        var pricey: ImageButton
        var priceyText: TextView
        var inTransit: ImageView
        var layout: RelativeLayout

        init {
            name = view.findViewById(R.id.name)
            archetype = view.findViewById(R.id.archetype)
            duelist = view.findViewById(R.id.duelist)
            set = view.findViewById(R.id.set)
            pricey = view.findViewById(R.id.pricey_image)
            priceyText = view.findViewById(R.id.pricey_text)
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

        if(dataSet[holder.adapterPosition].minPrice <= 0.0)
            holder.priceyText.text = "N/A"
        else
            holder.priceyText.text = Utils.doubleToString(dataSet[holder.adapterPosition].minPrice)

        if(dataSet[holder.adapterPosition].minPrice <= Constants.getInstance().pricey)
            ViewCompat.setBackgroundTintList(holder.pricey, ColorStateList.valueOf(Color.GRAY))
        else
            ViewCompat.setBackgroundTintList(holder.pricey, ColorStateList.valueOf(context.getColor(R.color.goldenrod)))

        holder.pricey.setOnClickListener{onClickCallCardMarket(holder.adapterPosition, holder.pricey)}

        if(!dataSet[holder.adapterPosition].inTransit)
            holder.inTransit.visibility = View.INVISIBLE
        else
            holder.inTransit.visibility = View.VISIBLE

        holder.layout.setOnLongClickListener {
            onLongClickEdit(holder.layout, holder)
            true
        }

    }

    private fun onClickCallCardMarket(position: Int, priceyButton: ImageButton) {
        priceyButton.isClickable = false

        val c = dataSet[position]

        try {
            val price = Utils.cardMarketInfo(c)
            c.minPrice = price
            Queries.getInstance().addUpdateCard(c)
            this.notifyItemChanged(position)

            createOkErrorPopUp("CardMarket: Prezzo minimo per la carta ${c.name}: ${if (price > 0) Utils.doubleToString(price) else "N/A"}", priceyButton)
        } catch(e: Exception) {
            Log.e(Constants.getInstance().CARD_TRACKING_DEBUGGER, "Errore cardmarket per la carta " + c.name)
            createOkErrorPopUp("CardMarket: Attenzione. Non è stato possibile recuperare il prezzo per la carta ${c.name}", priceyButton)
        }
    }

    private fun createOkErrorPopUp(message: String, priceyButton: ImageButton) {

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        dialog = Dialog(context)
        val popUpView: View = inflater.inflate(R.layout.simple_message_popup, null)
        dialog.setContentView(popUpView)

        popUpView.findViewById<TextView>(R.id.message).text = message
        popUpView.findViewById<ImageButton>(R.id.ok_button).setOnClickListener{onClickClosePopUp(priceyButton)}
        popUpView.findViewById<ImageButton>(R.id.back_button).setOnClickListener{onClickClosePopUp(priceyButton)}

        dialog.window!!.setLayout(900,700)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun onClickClosePopUp(priceyButton: ImageButton) {
        dialog.dismiss()
        priceyButton.isClickable = true
    }

    override fun getItemCount() = dataSet.size

}