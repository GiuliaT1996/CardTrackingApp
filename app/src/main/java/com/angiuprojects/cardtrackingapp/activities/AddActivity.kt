package com.angiuprojects.cardtrackingapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.angiuprojects.cardtrackingapp.R
import com.angiuprojects.cardtrackingapp.entities.Card
import com.angiuprojects.cardtrackingapp.queries.Queries
import com.angiuprojects.cardtrackingapp.utilities.Constants
import com.google.android.material.snackbar.Snackbar

class AddActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        setDropdown(R.id.archetype_auto_complete, getSuggetionList(0))
        setDropdown(R.id.duelist_auto_complete, getSuggetionList(1))
        setDropdown(R.id.set_auto_complete, getSuggetionList(2))
    }

    fun onClickClose(view: View){
        finish()
        val i = Intent(this, MenuActivity::class.java)
        startActivity(i)
    }

    fun onClickCreate(view: View) {
        val name = getTextFromInput(R.id.name_auto_complete)
        val archetype = getTextFromInput(R.id.archetype_auto_complete)
        val duelist = getTextFromInput(R.id.duelist_auto_complete)
        val set = getTextFromInput(R.id.set_auto_complete)

        val inTransit = findViewById<CheckBox>(R.id.in_transit)

        val snackBarView = findViewById<View>(android.R.id.content)

        if(name == "" || archetype == "") {
            Snackbar.make(
                snackBarView, "Inserire tutti i campi obbligatori!",
                Snackbar.LENGTH_LONG
            ).show()

            return
        }



        val card = Card(name, archetype, duelist, set, inTransit = inTransit.isChecked, 0.0)
        Queries.getInstance().addUpdateCard(card, updatePrice = true)

        Snackbar.make(
            snackBarView, "Carta creata correttamente",
            Snackbar.LENGTH_LONG
        ).show()

        onClickClose(view)
    }

    private fun getSuggetionList(field: Int): MutableList<String> {

        var suggestionList = mutableListOf<String>()

        when(field) {
            0 -> suggestionList = Constants.getInstance().getInstanceCards()?.map { it.archetype }?.sorted()?.distinct()
                ?.toMutableList()!!
            1 -> suggestionList = Constants.getInstance().getInstanceCards()?.map { it.duelist }?.sorted()?.distinct()
                ?.toMutableList()!!
            2 -> suggestionList = Constants.getInstance().getInstanceCards()?.map { it.set }?.sorted()?.distinct()
                ?.toMutableList()!!
            else -> Log.e(Constants.getInstance().CARD_TRACKING_DEBUGGER, "Nessun campo selezionato")
        }
        return suggestionList
    }

    private fun setDropdown(@IdRes id: Int, suggestionList: List<String>) {
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(id)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, suggestionList)
        autoCompleteTextView.setAdapter(adapter)

        autoCompleteTextView.setDropDownBackgroundDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.filter_spinner_dropdown_bg,
                null
            ))
    }

    private fun getTextFromInput(@IdRes id: Int): String {
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(id)
        return if (autoCompleteTextView != null && autoCompleteTextView.text != null && autoCompleteTextView.text.isNotEmpty())
            autoCompleteTextView.text.toString() else ""
    }

    override fun onBackPressed() {
        super.onBackPressed()
        onClickClose(View(this))
    }

}