package com.angiuprojects.cardtrackingapp.utilities

import android.os.StrictMode
import android.util.Log
import com.angiuprojects.cardtrackingapp.entities.Card
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.lang.Exception
import java.text.DecimalFormat

class Utils {

    companion object {
        fun doubleToString(price: Double) : String {
            val format = DecimalFormat("#,###0.00")
            format.isDecimalSeparatorAlwaysShown = false
            return format.format(price).toString() + " €"
        }

        fun cardMarketInfo(card: Card): Double{

            val stringBuilder = StringBuilder()
            val priceList = mutableListOf<Double>()

            val objectClass = callCardMarket(constructUrl(card.name))

            if (objectClass != null) {
                for(oc in objectClass) {

                    //get set name of current item
                    val setName = getSetName(oc)
                    //get list of prices
                    getListOfPrices(oc, card, setName, stringBuilder, priceList)
                }
            }

            //get minimum price
            return if(priceList.isEmpty()) -0.01
            else priceList.min()
        }

        private fun getSetName(oc: Element): String {
            var setName = ""
            val setContainers = oc.getElementsByClass("col-icon small")
            for (setContainer in setContainers) {
                val set = setContainer.getElementsByTag("span")
                setName = set[0].text()
            }
            return setName
        }

        private fun getListOfPrices(
            oc: Element,
            card: Card,
            setName: String,
            stringBuilder: StringBuilder,
            priceList: MutableList<Double>
        ) {
            val price = oc.getElementsByClass("col-price pr-sm-2")
            if (!price.isEmpty() && price[0].text() != "N/A" && ((card.set.isNotEmpty() && card.set == setName) || card.set.isEmpty())) {
                stringBuilder.append(setName).append(" - ").append(price[0].text()).append("\n")
                try {
                    priceList.add(price[0].text().replace("€", "").trim().replace(".", "").replace(",", ".").toDouble())
                } catch(e: Exception) {
                    Log.e(Constants.getInstance().CARD_TRACKING_DEBUGGER, "Prezzo con format non corretto: " + price[0].text() + " Errore carta: " + card.name)
                }
            }
        }

        private fun constructUrl(name: String): String {
            val pre = "https://www.cardmarket.com/it/YuGiOh/Products/Search?idCategory=0&idExpansion=0&searchString=%5B"
            val url = name.trim().replace(" ", "+")
            val post = "%5D&exactMatch=on&idRarity=0&sortBy=price_asc&perSite=100"

            return pre + url + post
        }

        private fun callCardMarket(url: String): Elements? {

            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            var doc : Document? = null

            try {
                doc = Jsoup.connect(url).get()
            } catch(e : Exception) {
                e.message?.let { Log.e(Constants.getInstance().CARD_TRACKING_DEBUGGER,
                    "$it\n URL = $url"
                ) }
            }
            return doc?.getElementsByClass("row no-gutters")
        }

    }
}