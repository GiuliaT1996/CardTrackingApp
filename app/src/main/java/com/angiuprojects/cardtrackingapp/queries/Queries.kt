package com.angiuprojects.cardtrackingapp.queries

import android.util.Log
import com.angiuprojects.cardtrackingapp.entities.Card
import com.angiuprojects.cardtrackingapp.utilities.Constants
import com.google.firebase.database.*

class Queries {

    private val DB_INSTANCE: FirebaseDatabase =
        FirebaseDatabase.getInstance("https://card-tracking-system-default-rtdb.firebaseio.com/")
    private lateinit var myRef: DatabaseReference

    private val DB_CARD_PATH = "Cards";


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

    fun addUpdateCard(c: Card) {
        myRef = DB_INSTANCE.getReference(DB_CARD_PATH)
        myRef.child(c.name).setValue(c)
    }

    fun deleteCard(c: Card) {
        myRef = DB_INSTANCE.getReference(DB_CARD_PATH)
        myRef.child(c.name).removeValue()
    }

}