package com.angiuprojects.cardtrackingapp.handlers

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.IdRes
import com.angiuprojects.cardtrackingapp.R
import com.angiuprojects.cardtrackingapp.adapters.CardRecyclerAdapter
import com.angiuprojects.cardtrackingapp.entities.Card
import com.angiuprojects.cardtrackingapp.queries.Queries
import com.angiuprojects.cardtrackingapp.utilities.Utils
import com.google.android.material.textfield.TextInputLayout

class EditPopUpHandler {

    lateinit var dialog: Dialog

    companion object {
        private lateinit var editPopUpIstance: EditPopUpHandler

        fun inizializeInstance() {
            editPopUpIstance = EditPopUpHandler()
        }

        fun getInstance(): EditPopUpHandler {
            return editPopUpIstance
        }
    }

    private fun modifyCard(card: Card, view: View, position: Int, adapter: CardRecyclerAdapter) {

        if(getTextFromInputDialog(R.id.name_input_text, dialog) != null) {
            card.name = getTextFromInputDialog(R.id.name_input_text, dialog)!!
        }
        if(getTextFromInputDialog(R.id.archetype_input_text, dialog) != null) {
            card.archetype = getTextFromInputDialog(R.id.archetype_input_text, dialog)!!
        }
        if(getTextFromInputDialog(R.id.name_duelist_text, dialog) != null) {
            card.duelist = getTextFromInputDialog(R.id.name_duelist_text, dialog)!!
        }
        if(getTextFromInputDialog(R.id.name_set_text, dialog) != null) {
            card.set = getTextFromInputDialog(R.id.name_set_text, dialog)!!
        }

        card.inTransit = dialog.findViewById<CheckBox>(R.id.in_transit).isChecked

        Queries.getInstance().addUpdateCard(card, true)
        adapter.notifyItemChanged(position)
        dialog.dismiss()
    }

    fun populateEditPopUp(card: Card, view: View, position: Int, adapter: CardRecyclerAdapter) {

        dialog = Dialog(view.context)
        dialog.setContentView(R.layout.edit_popup)

        dialog.findViewById<TextInputLayout>(R.id.name_input_text).editText?.setText(card.name)
        dialog.findViewById<TextInputLayout>(R.id.archetype_input_text).editText?.setText(card.archetype)
        dialog.findViewById<TextInputLayout>(R.id.name_duelist_text).editText?.setText(card.duelist)
        dialog.findViewById<TextInputLayout>(R.id.name_set_text).editText?.setText(card.set)
        dialog.findViewById<CheckBox>(R.id.in_transit).isChecked = card.inTransit

        if (card.minPrice <= 0.0)
            dialog.findViewById<TextView>(R.id.cardmarket_price).text = "N/A"
        else
            dialog.findViewById<TextView>(R.id.cardmarket_price).text = Utils.doubleToString(card.minPrice)

        dialog.findViewById<ImageButton>(R.id.back_button).setOnClickListener { dialog.dismiss() }
        dialog.findViewById<ImageButton>(R.id.modify_button).setOnClickListener { modifyCard(card, view, position, adapter) }

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

    }

    private fun getTextFromInputDialog(@IdRes id: Int, dialog: Dialog): String? {
        val text = dialog.findViewById<TextInputLayout>(id)
        return if (text != null && text.editText != null && text.editText!!.text != null && text.editText!!.text.toString()
                .isNotEmpty()
        ) text.editText!!.text.toString() else null
    }
}