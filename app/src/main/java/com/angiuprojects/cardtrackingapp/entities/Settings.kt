package com.angiuprojects.cardtrackingapp.entities

class Settings {

    var settingName = ""
    var enabled = false

    constructor(settingName: String, enabled: Boolean) {
        this.settingName = settingName
        this.enabled = enabled
    }

    constructor() {}
}