package com.angiuprojects.cardtrackingapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.angiuprojects.cardtrackingapp.R
import com.angiuprojects.cardtrackingapp.entities.Settings
import com.angiuprojects.cardtrackingapp.handlers.EditPopUpHandler
import com.angiuprojects.cardtrackingapp.queries.Queries
import com.angiuprojects.cardtrackingapp.utilities.Constants
import com.angiuprojects.cardtrackingapp.utilities.ExcelUtils
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LogoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logo)

        Constants.initializeConstantSingleton()
        Queries.initializeQueriesSingleton()
        EditPopUpHandler.inizializeInstance()

        Queries.getInstance().getCards()
        Queries.getInstance().getSettings()

        animateImage(this)

    }

    private fun readFromExcelCoroutine(context: Context) = runBlocking { /* this: CoroutineScope */
        launch {
            ExcelUtils.readFromExcelFile(context)
        }
    }

    private fun animateImage(context: Context) {
        val animation: Animation = AlphaAnimation(0.0f, 1.0f)
        animation.duration = 3000

        val nameText = findViewById<TextView>(R.id.app_name)
        nameText.startAnimation(animation)

        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                val i = Intent(context, MenuActivity::class.java)
                startActivity(i)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }
}