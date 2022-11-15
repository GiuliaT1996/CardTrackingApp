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
import pl.droidsonroids.gif.GifImageView


class CardRecyclerAdapter(private val dataSet : MutableList<Card>, private val context: Context) : RecyclerView.Adapter<CardRecyclerAdapter.MyViewHolder>() {

    private lateinit var dialog: Dialog

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView
        var archetype: TextView
        var duelist: TextView
        var toBeReleased: ImageButton
        var pricey: ImageButton
        var priceyText: TextView
        var loadingPrice: GifImageView
        var inTransit: ImageView
        var layout: RelativeLayout

        init {
            name = view.findViewById(R.id.name)
            archetype = view.findViewById(R.id.archetype)
            duelist = view.findViewById(R.id.duelist)
            toBeReleased = view.findViewById(R.id.to_be_released)
            pricey = view.findViewById(R.id.pricey_image)
            priceyText = view.findViewById(R.id.pricey_text)
            loadingPrice = view.findViewById(R.id.loading_price)
            inTransit = view.findViewById(R.id.inTransit)
            layout = view.findViewById(R.id.card_view)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view, parent, false)

        return MyViewHolder(view)
    }

    private fun onLongClickEdit(view: View, holder: MyViewHolder, context: Context) {
        EditPopUpHandler.getInstance().populateEditPopUp(dataSet[holder.adapterPosition], view, holder.adapterPosition, this, context)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.name.text = dataSet[holder.adapterPosition].name
        holder.archetype.text = dataSet[holder.adapterPosition].archetype
        holder.duelist.text = dataSet[holder.adapterPosition].duelist

        handlePriceSettings(holder)

        if(!dataSet[holder.adapterPosition].inTransit)
            holder.inTransit.visibility = View.INVISIBLE
        else
            holder.inTransit.visibility = View.VISIBLE

        if(dataSet[holder.adapterPosition].set.trim() == "")
            holder.toBeReleased.visibility = View.INVISIBLE
        else {
            holder.toBeReleased.visibility = View.VISIBLE
            holder.toBeReleased.setOnClickListener{onClickShowSetPopUp(dataSet[holder.adapterPosition].set.trim())}
        }

        holder.layout.setOnLongClickListener {
            onLongClickEdit(holder.layout, holder, context)
            true
        }

    }

    private fun handlePriceSettings(holder: MyViewHolder) {
        if (dataSet[holder.adapterPosition].minPrice <= 0.0)
            holder.priceyText.text = "N/A"
        else
            holder.priceyText.text = Utils.doubleToString(dataSet[holder.adapterPosition].minPrice)

        if (dataSet[holder.adapterPosition].minPrice <= Constants.getInstance().pricey)
            ViewCompat.setBackgroundTintList(holder.pricey, ColorStateList.valueOf(Color.GRAY))
        else
            ViewCompat.setBackgroundTintList(
                holder.pricey,
                ColorStateList.valueOf(context.getColor(R.color.goldenrod))
            )

        holder.pricey.setOnClickListener { onClickCallCardMarket(holder.adapterPosition, holder) }
    }

    private fun onClickShowSetPopUp(set: String) {
        createMessagePopUp(set, 300)
    }

    private fun onClickCallCardMarket(position: Int, holder: MyViewHolder) {
        switchPriceIcons(true, holder)

        val c = dataSet[position]

        try {
            val price = Utils.cardMarketInfo(c)
            c.minPrice = price
            Queries.getInstance().addUpdateCard(c, updatePrice = false)
            this.notifyItemChanged(position)
            switchPriceIcons(false, holder)
        } catch(e: Exception) {
            Log.e(Constants.getInstance().CARD_TRACKING_DEBUGGER, "Errore cardmarket per la carta " + c.name)
            createMessagePopUp("CardMarket: Attenzione. Non è stato possibile recuperare il prezzo per la carta ${c.name}", 600)
            switchPriceIcons(false, holder)
        }
    }

    private fun switchPriceIcons(isConnecting: Boolean, holder: MyViewHolder) {
        if(isConnecting) {
            holder.pricey.visibility = View.INVISIBLE
            holder.loadingPrice.visibility = View.VISIBLE
        } else {
            holder.pricey.visibility = View.VISIBLE
            holder.loadingPrice.visibility = View.GONE
        }
    }

    private fun createMessagePopUp(message: String, height: Int) {

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        dialog = Dialog(context)
        val popUpView: View = inflater.inflate(R.layout.simple_message_popup, null)
        dialog.setContentView(popUpView)

        popUpView.findViewById<TextView>(R.id.message).text = message

        dialog.window!!.setLayout(500,height)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    override fun getItemCount() = dataSet.size

}