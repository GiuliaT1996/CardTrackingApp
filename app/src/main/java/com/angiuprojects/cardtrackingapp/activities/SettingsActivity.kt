package com.angiuprojects.cardtrackingapp.activities

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.TextView
import com.angiuprojects.cardtrackingapp.R
import com.angiuprojects.cardtrackingapp.queries.Queries
import com.angiuprojects.cardtrackingapp.utilities.CardMarketUtils
import com.angiuprojects.cardtrackingapp.utilities.Constants
import com.angiuprojects.cardtrackingapp.utilities.Utils

class SettingsActivity : AppCompatActivity() {

    private lateinit var dialog: Dialog

    private val message = "Impostazione modificata con successo."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setCheckbox()
    }

    private fun setCheckbox(){
        if(Constants.getInstance().getInstanceSettings()?.get(0)?.enabled == true)
            findViewById<ImageButton>(R.id.unchecked).visibility = View.GONE
        else
            findViewById<ImageButton>(R.id.checked).visibility = View.GONE

    }

    fun onClickClose(view: View) {
        finish()
    }

    fun onClickDeleteSet(view: View) {
        createDialog(false, R.layout.set_choice_popup, "")
    }

    fun onClickStartCardMarketRequests(view: View) {
        CardMarketUtils.callCardMarketCoroutine()
        updateSettingsAndVisibility(true)
        createDialog(true, R.layout.simple_message_popup, message)

    }

    fun onClickStopCardMarketRequests(view: View) {
        CardMarketUtils.job?.cancel()
        updateSettingsAndVisibility(false)
        createDialog(true, R.layout.simple_message_popup, message)
    }

    private fun updateSettingsAndVisibility(newConfig: Boolean) {
        Constants.getInstance().getInstanceSettings()?.get(0)?.enabled = newConfig

        if(newConfig) {
            findViewById<ImageButton>(R.id.unchecked).visibility = View.GONE
            findViewById<ImageButton>(R.id.checked).visibility = View.VISIBLE
        } else {
            findViewById<ImageButton>(R.id.unchecked).visibility = View.VISIBLE
            findViewById<ImageButton>(R.id.checked).visibility = View.GONE
        }

        Constants.getInstance().getInstanceSettings()?.get(0)
            ?.let { Queries.getInstance().addUpdateSetting(it) }
    }

    private fun createDialog(isChoiceSetPopUp: Boolean, popUpId: Int, message: String) {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        dialog = Dialog(this)
        val popUpView: View = inflater.inflate(popUpId, null)
        dialog.setContentView(popUpView)

        if (isChoiceSetPopUp) {
            popUpView.findViewById<TextView>(R.id.message).text = message
            dialog.window!!.setLayout(800,500)
        } else
            handleSpinner(popUpView)

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun handleSpinner(popUpView: View) {
        val filterSpinner = popUpView.findViewById<AutoCompleteTextView>(R.id.filter_spinner)
        filterSpinner.setText("")
        val setList = Constants.getInstance().getInstanceCards()?.map { it.set }?.filter { it != "" }?.distinct()?.sorted()
            ?.toMutableList()!!

        Utils.setSpinnerInfo(setList, filterSpinner, this)

        filterSpinner?.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->

                val names = StringBuilder()
                val filtered = Utils.filter(2, filterSpinner.text.toString())
                names.append("Carte nel set: \n")
                filtered.forEach {
                    names.append(it.name).append("\n")
                }

                popUpView.findViewById<TextView>(R.id.card_list_textview).text = names.toString()
            }

        popUpView.findViewById<ImageButton>(R.id.ok_button).setOnClickListener{onClickDeleteSelectedSet(filterSpinner)}
    }

    private fun onClickDeleteSelectedSet(filterSpinner: AutoCompleteTextView) {

        val names = StringBuilder()
        val filtered = Utils.filter(2, filterSpinner.text.toString())
        names.append("Modificate le carte: \n")
        filtered.forEach {
            val position = Constants.getInstance().getInstanceCards()?.indexOf(it)
            if (position != null) {
                Constants.getInstance().getInstanceCards()?.get(position)?.set = ""
                Constants.getInstance().getInstanceCards()?.get(position)
                    ?.let { it1 -> Queries.getInstance().addUpdateCard(it1, false) }
                names.append(it.name).append("\n")
            }
        }
        dialog.dismiss()
        Log.i(Constants.getInstance().CARD_TRACKING_DEBUGGER, "Set: ${filterSpinner.text}")
        Log.i(Constants.getInstance().CARD_TRACKING_DEBUGGER, names.toString())
        createDialog(true, R.layout.simple_message_popup, names.toString())
    }

    private fun onClickClosePopUp() {
        dialog.dismiss()
    }
}