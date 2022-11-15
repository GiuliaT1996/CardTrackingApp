package com.angiuprojects.cardtrackingapp.handlers

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.*
import androidx.annotation.IdRes
import androidx.core.content.res.ResourcesCompat
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

        val newName = getTextFromInputDialog(R.id.name_auto_complete, dialog)
        if(card.name != newName && newName != "") {
            Queries.getInstance().deleteCard(card)
            card.name = newName
        }
        card.archetype = getTextFromInputDialog(R.id.archetype_auto_complete, dialog)
        card.duelist = getTextFromInputDialog(R.id.duelist_auto_complete, dialog)
        card.set = getTextFromInputDialog(R.id.set_auto_complete, dialog)


        card.inTransit = dialog.findViewById<CheckBox>(R.id.in_transit).isChecked

        Queries.getInstance().addUpdateCard(card, true)
        adapter.notifyItemChanged(position)
        dialog.dismiss()
    }

    fun populateEditPopUp(card: Card, view: View, position: Int, adapter: CardRecyclerAdapter, context: Context) {

        dialog = Dialog(view.context)
        dialog.setContentView(R.layout.edit_popup)

        dialog.findViewById<AutoCompleteTextView>(R.id.name_auto_complete).setText(card.name)
        dialog.findViewById<AutoCompleteTextView>(R.id.archetype_auto_complete).setText(card.archetype)
        dialog.findViewById<AutoCompleteTextView>(R.id.duelist_auto_complete).setText(card.duelist)
        dialog.findViewById<AutoCompleteTextView>(R.id.set_auto_complete).setText(card.set)
        setDropdown(R.id.archetype_auto_complete, Utils.getSuggetionList(0), context)
        setDropdown(R.id.duelist_auto_complete, Utils.getSuggetionList(1), context)
        setDropdown(R.id.set_auto_complete, Utils.getSuggetionList(2), context)
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

    private fun setDropdown(@IdRes id: Int, suggestionList: List<String>, context: Context) {
        val autoCompleteTextView = dialog.findViewById<AutoCompleteTextView>(id)

        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, suggestionList)
        autoCompleteTextView.setAdapter(adapter)

        autoCompleteTextView.setDropDownBackgroundDrawable(
            ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.filter_spinner_dropdown_bg,
                null
            ))
    }

    private fun getTextFromInputDialog(@IdRes id: Int, dialog: Dialog): String {
        val autoCompleteTextView = dialog.findViewById<AutoCompleteTextView>(id)
        return if (autoCompleteTextView != null && autoCompleteTextView.text != null && autoCompleteTextView.text.isNotEmpty())
            autoCompleteTextView.text.toString() else ""
    }
}