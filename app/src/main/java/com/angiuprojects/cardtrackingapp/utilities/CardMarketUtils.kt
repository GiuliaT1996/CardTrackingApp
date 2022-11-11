package com.angiuprojects.cardtrackingapp.utilities

import android.util.Log
import com.angiuprojects.cardtrackingapp.entities.Card
import com.angiuprojects.cardtrackingapp.queries.Queries
import kotlinx.coroutines.*
import java.lang.Exception

class CardMarketUtils {

    companion object {
        var job: Job? = null

        @OptIn(DelicateCoroutinesApi::class)
        fun callCardMarketCoroutine() = runBlocking { /* this: CoroutineScope */
            job = GlobalScope.launch {
                callCardMarket()
            }
        }

        private suspend fun callCardMarket() {

            Log.i(Constants.getInstance().CARD_TRACKING_DEBUGGER,
                Constants.getInstance().getInstanceCards()?.size.toString()
            )

            for(card in Constants.getInstance().getInstanceCards()!!) {
                Log.i(Constants.getInstance().CARD_TRACKING_DEBUGGER, card.name)
                getMinPrice(card)
            }
        }

        private suspend fun getMinPrice(item: Card){

            try {
                val price = Utils.cardMarketInfo(item)
                item.minPrice = price

                Queries.getInstance().addUpdateCard(item, updatePrice = false)
            } catch (e: Exception) {
                Log.e(
                    Constants.getInstance().CARD_TRACKING_DEBUGGER,
                    "Errore cardmarket per la carta " + item.name
                )
                delay(50000)
                getMinPrice(item)
            }
        }
    }
}