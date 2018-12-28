package com.example.app.cbr.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "KasusGejala",
    foreignKeys =
    [
        ForeignKey(
            entity = Kasus::class,
            parentColumns = ["idKasus"],
            childColumns = ["kasusId"]),
        ForeignKey(
            entity = Gejala::class,
            parentColumns = ["idGejala"],
            childColumns = ["gejalaId"])
    ]
)

data class KomponenGejala (
    @PrimaryKey(autoGenerate = true) var kasusGejala: Int = 0,
    var kasusId: Int = 0,
    var gejalaId: Int = 0
)