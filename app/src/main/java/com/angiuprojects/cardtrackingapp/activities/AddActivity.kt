package com.angiuprojects.cardtrackingapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.angiuprojects.cardtrackingapp.R
import com.angiuprojects.cardtrackingapp.entities.Card
import com.angiuprojects.cardtrackingapp.queries.Queries
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class AddActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
    }

    fun onClickClose(view: View){
        finish()
        val i = Intent(this, MenuActivity::class.java)
        startActivity(i)
    }

    fun onClickCreate(view: View) {
        val name = getTextFromInput(R.id.name_input_text)
        val archetype = getTextFromInput(R.id.name_archetype_text)
        val duelist = getTextFromInput(R.id.name_duelist_text)
        val set = getTextFromInput(R.id.name_set_text)

        val inTransit = findViewById<CheckBox>(R.id.in_transit)

        val snackBarView = findViewById<View>(android.R.id.content)

        if(name == null || archetype == null || duelist == null || set == null) {
            Snackbar.make(
                snackBarView, "Inserire tutti i campi obbligatori!",
                Snackbar.LENGTH_LONG
            ).show()

            return
        }



        val card = Card(name, archetype, duelist, set, inTransit = inTransit.isChecked, 0.0)
        Queries.getInstance().addUpdateCard(card)

        Snackbar.make(
            snackBarView, "Carta creata correttamente",
            Snackbar.LENGTH_LONG
        ).show()

        onClickClose(view)
    }


    private fun getTextFromInput(@IdRes id: Int): String? {
        val text = findViewById<TextInputLayout>(id)
        return if (text != null && text.editText != null && text.editText!!.text != null && text.editText!!.text.toString()
                .isNotEmpty()
        ) text.editText!!.text.toString() else null
    }

    override fun onBackPressed() {
        super.onBackPressed()
        onClickClose(View(this))
    }

}