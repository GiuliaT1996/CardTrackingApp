package com.angiuprojects.cardtrackingapp.entities

import java.io.Serializable

class Card : Serializable {
    var name: String = ""
    var archetype: String = ""
    var duelist: String = ""
    var set: String = ""
    var pricey = false
    var inTransit = false

    constructor(name: String, archetype: String, duelist: String, set: String, pricey: Boolean, inTransit: Boolean) {
        this.name = name
        this.archetype = archetype
        this.duelist = duelist
        this.set = set
        this.pricey = pricey
        this.inTransit = inTransit
    }

    constructor() {}
}