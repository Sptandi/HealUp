package com.example.app.cbr.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "kasus",
    foreignKeys =
    [/*
        ForeignKey(
            entity = Gejala::class,
            parentColumns = ["idGejala"],
            childColumns = ["kgId"]),*/
        ForeignKey(
            entity = Solusi::class,
            parentColumns = ["idSolusi"],
            childColumns = ["solusiId"])
    ])
data class Kasus (
    @PrimaryKey var idKasus: Int = 0,
    var namaKasus: String = "",
    //var kgId: Int = 0,
    var value: Int = 0,
    var solusiId: Int = 0
)