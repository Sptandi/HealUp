package com.example.app.cbr.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "konsultasi",
    foreignKeys =
    [
        ForeignKey(
            entity = User::class,
            parentColumns = ["idUser"],
            childColumns = ["userId"])
    ]
)

data class Konsultasi (
    @PrimaryKey(autoGenerate = true) var idKonsultasi: Int = 0,
    var namaAnak: String = "",
    var tanggal: String,
    var userId: Int? = 0
)