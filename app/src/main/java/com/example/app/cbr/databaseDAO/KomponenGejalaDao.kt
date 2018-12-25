package com.example.app.cbr.databaseDAO

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.example.app.cbr.model.Gejala
import com.example.app.cbr.model.Kasus
import com.example.app.cbr.model.KomponenGejala

@Dao
interface KomponenGejalaDao {
    @Insert(onConflict =  OnConflictStrategy.REPLACE )
    fun add(komponenGejala: KomponenGejala)

    @Query("Select * from gejala INNER JOIN KasusGejala ON idGejala = gejalaId WHERE gejalaId = idGejala")
    fun getGejala(): List<Gejala>

    @Query("Select * from kasus INNER JOIN KasusGejala ON idKasus = kasusId WHERE idKasus = kasusId")
    fun getKasus(): List<Kasus>

    @Query("select * from KasusGejala WHERE kasusId = :id")
    fun getAllSameId(id: Int): List<KomponenGejala>
}