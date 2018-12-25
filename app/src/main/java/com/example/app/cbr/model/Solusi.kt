package com.example.app.cbr.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "solusi")
data class Solusi (
    @PrimaryKey(autoGenerate = true)
    var idSolusi: Int = 0,
    var nama: String = ""
)