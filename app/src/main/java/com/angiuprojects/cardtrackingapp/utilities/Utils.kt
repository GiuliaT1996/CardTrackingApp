package com.angiuprojects.cardtrackingapp.utilities

import android.R
import android.content.Context
import android.os.StrictMode
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.content.res.ResourcesCompat
import com.angiuprojects.cardtrackingapp.entities.Card
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.text.DecimalFormat
import kotlin.Exception

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
            Log.i(Constants.getInstance().CARD_TRACKING_DEBUGGER, "${if(priceList.isEmpty()) -0.01 else priceList.min()}")

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
            val url = name.trim().replace(" ", "+").replace("\"","")
            val post = "%5D&exactMatch=on&idRarity=0&sortBy=price_asc&perSite=100"

            return pre + url + post
        }

        private fun callCardMarket(url: String): Elements? {

            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            val doc : Document?

            try {
                doc = Jsoup.connect(url).userAgent("Personal card tracking application / Android").get()
            } catch(e : Exception) {
                e.message?.let { Log.e(Constants.getInstance().CARD_TRACKING_DEBUGGER,
                    "$it\n URL = $url"
                ) }

                throw Exception()
            }
            return doc?.getElementsByClass("row no-gutters")
        }

        fun setSpinnerInfo(fieldList : List<String>, spinner : AutoCompleteTextView, context: Context) {
            val adapter = ArrayAdapter(context, R.layout.simple_spinner_dropdown_item, fieldList)
            spinner.setAdapter(adapter)

            spinner.setDropDownBackgroundDrawable(
                ResourcesCompat.getDrawable(
                    context.resources,
                    com.angiuprojects.cardtrackingapp.R.drawable.filter_spinner_dropdown_bg,
                    null
                ))
        }

        fun filter(field : Int, text : String) : MutableList<Card> {

            var filteredList = mutableListOf<Card>()

            when(field) {
                0 -> filteredList =
                    Constants.getInstance().getInstanceCards()?.filter { c -> c.archetype.lowercase() == text.lowercase() }
                        ?.toMutableList() ?: mutableListOf()
                1  -> filteredList =
                    Constants.getInstance().getInstanceCards()?.filter { c ->  c.duelist.lowercase() == (text.lowercase())}
                        ?.toMutableList() ?: mutableListOf()
                2 -> filteredList =
                    Constants.getInstance().getInstanceCards()?.filter { c ->  c.set.lowercase() == (text.lowercase())}
                        ?.toMutableList() ?: mutableListOf()
                3 ->  filteredList = filterByPrize(text)
                4 -> filteredList =
                    Constants.getInstance().getInstanceCards()?.filter { c ->  c.name.lowercase().contains(text.lowercase())}
                        ?.toMutableList() ?: mutableListOf()
                else -> Log.e(Constants.getInstance().CARD_TRACKING_DEBUGGER, "Nessun campo selezionato")
            }

            return filteredList
        }

        private fun filterByPrize(text: String) : MutableList<Card> {

            var filteredList = mutableListOf<Card>()

            if(text == Constants.getInstance().priceRange[0]) {
                filteredList = Constants.getInstance().getInstanceCards()?.filter { c -> c.minPrice <= 0.0 }
                    ?.toMutableList() ?: mutableListOf()
            } else if(text == Constants.getInstance().priceRange[1]) {
                filteredList = Constants.getInstance().getInstanceCards()?.filter { c -> c.minPrice > 0 && c.minPrice <= 1.0 }
                    ?.toMutableList() ?: mutableListOf()
            } else if(text == Constants.getInstance().priceRange[2]) {
                filteredList = Constants.getInstance().getInstanceCards()?.filter { c -> c.minPrice > 1.0 && c.minPrice <= 2.5 }
                    ?.toMutableList() ?: mutableListOf()
            } else if(text == Constants.getInstance().priceRange[3]) {
                filteredList = Constants.getInstance().getInstanceCards()?.filter { c -> c.minPrice > 2.5 && c.minPrice <= 10.0 }
                    ?.toMutableList() ?: mutableListOf()
            } else if(text == Constants.getInstance().priceRange[4]) {
                filteredList = Constants.getInstance().getInstanceCards()?.filter { c -> c.minPrice > 10.0 }
                    ?.toMutableList() ?: mutableListOf()
            }

            return filteredList
        }

    }
}