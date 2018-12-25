package com.example.app.cbr.databaseDAO

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.example.app.cbr.model.Solusi

@Dao
interface SolusiDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSolusi(solusi: Solusi)

    @Query("Select nama from solusi where idSolusi = :id")
    fun getNamaById(id: Int): String
}