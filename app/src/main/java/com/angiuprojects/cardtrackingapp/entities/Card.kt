package com.angiuprojects.cardtrackingapp.entities

import java.io.Serializable

class Card : Serializable {
    var name: String = ""
    var archetype: String = ""
    var duelist: String = ""
    var set: String = ""
    var inTransit = false
    var minPrice: Double = 0.0

    constructor(name: String, archetype: String, duelist: String, set: String, inTransit: Boolean, minPrice: Double) {
        this.name = name
        this.archetype = archetype
        this.duelist = duelist
        this.set = set
        this.inTransit = inTransit
        this.minPrice = minPrice
    }



    constructor() {}

    override fun toString(): String {
        return "Card(name='$name', archetype='$archetype', duelist='$duelist', set='$set', inTransit=$inTransit, minPrice=$minPrice)"
    }
}