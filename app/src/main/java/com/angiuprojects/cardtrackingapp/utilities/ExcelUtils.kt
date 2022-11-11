package com.angiuprojects.cardtrackingapp.utilities

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.angiuprojects.cardtrackingapp.entities.Card
import com.angiuprojects.cardtrackingapp.queries.Queries
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.ss.usermodel.Workbook


class ExcelUtils  : ViewModel(){
    companion object {
        fun readFromExcelFile(context: Context) {
            val filename = "yugioh.xls"
            val assetManager = context.assets
            var workbook: Workbook? = null

            val inputStream = assetManager?.open(filename)
            val fileSystem = POIFSFileSystem(inputStream)
            workbook = HSSFWorkbook(fileSystem)

            var currentRow = 1
            val lastRow = 988
            var currentColumn = 0
            val lastColumn = 3

            val xlWs = workbook.getSheetAt(0)

            while (currentRow <= lastRow) {

                var name = ""
                var archetype = ""
                var duelist = ""
                var set = ""

                while (currentColumn <= lastColumn) {
                    Log.i(Constants.getInstance().CARD_TRACKING_DEBUGGER, "CurrentRow = " + currentRow + " - Current Column " + currentColumn)
                    when(currentColumn) {
                        0 -> name = xlWs.getRow(currentRow).getCell(currentColumn).stringCellValue.trim()

                        1 -> archetype = xlWs.getRow(currentRow).getCell(currentColumn).stringCellValue.trim()

                        2 -> duelist = xlWs.getRow(currentRow).getCell(currentColumn).stringCellValue.trim()

                        3 -> if(xlWs.getRow(currentRow).getCell(currentColumn).stringCellValue.trim() != "€")
                                set = xlWs.getRow(currentRow).getCell(currentColumn).stringCellValue.trim()
                    }
                    currentColumn++
                }

                if(name.trim()!= "") {
                    val card = Card(name, archetype, duelist, set,
                        inTransit = false,
                        minPrice = 0.0
                    )

                    Constants.getInstance().getInstanceCards()?.add(card)
                    Queries.getInstance().addUpdateCard(card, false)
                    Log.i(Constants.getInstance().CARD_TRACKING_DEBUGGER, card.toString())
                }

                currentColumn = 0
                currentRow++
            }

            Log.i(Constants.getInstance().CARD_TRACKING_DEBUGGER, "fine")

        }
    }

}