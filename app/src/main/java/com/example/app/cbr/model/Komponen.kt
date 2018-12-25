package com.example.app.cbr.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "komponen")
data class Komponen (
    @PrimaryKey(autoGenerate = true)
    var idKomponen: Int = 0
)