package com.example.app.cbr.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "gejala")

data class Gejala (
    @PrimaryKey(autoGenerate = true) var idGejala: Int = 0,
    var namaGejala: String = "",
    var bobot: Int = 0
) {
    var isSelected: Boolean = false
}