package com.example.app.cbr.databaseDAO

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.example.app.cbr.model.Komponen

@Dao
interface KomponenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertKomponen(komponen: Komponen)

    @Query("Select * from komponen")
    fun getKomponen(): List<Komponen>
}