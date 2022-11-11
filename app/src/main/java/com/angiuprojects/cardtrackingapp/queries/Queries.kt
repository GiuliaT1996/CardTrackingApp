package com.angiuprojects.cardtrackingapp.queries

import android.util.Log
import com.angiuprojects.cardtrackingapp.entities.Card
import com.angiuprojects.cardtrackingapp.entities.Settings
import com.angiuprojects.cardtrackingapp.utilities.Constants
import com.angiuprojects.cardtrackingapp.utilities.Utils
import com.google.firebase.database.*
import java.lang.Exception

class Queries {

    private val DB_INSTANCE: FirebaseDatabase =
        FirebaseDatabase.getInstance("https://card-tracking-system-default-rtdb.firebaseio.com/")
    private lateinit var myRef: DatabaseReference

    private val DB_CARD_PATH = "Cards";
    private val DB_SETTINGS_PATH = "Settings";


    companion object {

        private lateinit var singleton: Queries

        fun initializeQueriesSingleton(): Queries {
            singleton = Queries()
            return singleton
        }

        fun getInstance(): Queries {
            return singleton
        }
    }

    fun getCards() {
        myRef = DB_INSTANCE.getReference(DB_CARD_PATH)
        myRef.orderByChild("name").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, prevChildKey: String?) {

                val item: Card? = dataSnapshot.getValue(Card::class.java)

                if (item != null) {
                   Constants.getInstance().getInstanceCards()?.add(item)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun addUpdateCard(c: Card, updatePrice: Boolean) {
        myRef = DB_INSTANCE.getReference(DB_CARD_PATH)

        c.name = c.name.replace(".", "")

        if(updatePrice) {
            try {
                val price = Utils.cardMarketInfo(c)
                c.minPrice = price
            } catch(e: Exception) {
                c.minPrice = 0.0
                Log.e(Constants.getInstance().CARD_TRACKING_DEBUGGER, "URL non raggiungibile, impostato prezzo di default")
            }
        }


        myRef.child(c.name).setValue(c)
    }

    fun deleteCard(c: Card) {
        myRef = DB_INSTANCE.getReference(DB_CARD_PATH)
        myRef.child(c.name).removeValue()
    }

    fun getSettings() {
        myRef = DB_INSTANCE.getReference(DB_SETTINGS_PATH)
        myRef.orderByChild("settingName").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, prevChildKey: String?) {

                val item: Settings? = dataSnapshot.getValue(Settings::class.java)

                if (item != null) {
                    Constants.getInstance().getInstanceSettings()?.add(item)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun addUpdateSetting(s: Settings) {
        myRef = DB_INSTANCE.getReference(DB_SETTINGS_PATH)
        myRef.child(s.settingName).setValue(s)
    }

}