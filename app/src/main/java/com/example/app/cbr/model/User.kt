package com.example.app.cbr.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "user")

data class User (
    @PrimaryKey(autoGenerate = true) var idUser: Int = 0,
    var nama: String = "",
    var emailUser: String = "",
    var passwordUser: String = "",
    var konsultasiId: Int = 0
)