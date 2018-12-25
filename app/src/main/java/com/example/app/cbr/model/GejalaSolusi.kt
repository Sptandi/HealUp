package com.example.app.cbr.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.util.TableInfo

@Entity(tableName = "gejalasolusi",
    foreignKeys =
    [
        ForeignKey(
            entity = Solusi::class,
            parentColumns = ["idSolusi"],
            childColumns = ["solusiId"]),
        ForeignKey(
            entity = Gejala::class,
            parentColumns = ["idGejala"],
            childColumns = ["gejalaId"]),
        ForeignKey(
            entity = Konsultasi::class,
            parentColumns = ["idKonsultasi"],
            childColumns = ["idGejalaSolusi"])
    ]
)

data class GejalaSolusi(
    @PrimaryKey(autoGenerate = true) var idGS: Int = 0,
    var idGejalaSolusi: Int? = 0,
    var gejalaId: Int = 0,
    var solusiId: Int = 0
)