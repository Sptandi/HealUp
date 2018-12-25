package com.example.app.cbr.databaseDAO

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.example.app.cbr.model.Gejala
import com.example.app.cbr.model.GejalaSolusi
import com.example.app.cbr.model.Solusi

@Dao
interface GejalaSolusiDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGejalaSolusi(gs: GejalaSolusi)

    @Query("Select distinct gejalaId from gejalasolusi join gejala on gejalaId  = idGejala where idGejalaSolusi = :id")
    fun getGejala(id: Int): List<Int>

    @Query("Select distinct solusiId from gejalasolusi  join solusi on solusiId   = idSolusi where idGejalaSolusi = :id")
    fun getSolusi(id: Int): List<Int>
}