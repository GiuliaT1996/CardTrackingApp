package com.angiuprojects.cardtrackingapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.angiuprojects.cardtrackingapp.R
import com.angiuprojects.cardtrackingapp.handlers.EditPopUpHandler
import com.angiuprojects.cardtrackingapp.queries.Queries
import com.angiuprojects.cardtrackingapp.utilities.Constants

class LogoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logo)

        Constants.initializeConstantSingleton()
        Queries.initializeQueriesSingleton()
        EditPopUpHandler.inizializeInstance()

        Queries.getInstance().getCards()

        animateImage(this)
        //animateText(R.id.app_name)
    }

    private fun animateImage(context: Context) {
        val animation: Animation = AlphaAnimation(0.0f, 1.0f)
        animation.duration = 4000

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