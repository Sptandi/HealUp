package com.example.app.cbr.databaseDAO

import android.arch.persistence.room.*
import com.example.app.cbr.model.Gejala

@Dao
interface GejalaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGejala(gejala: Gejala)

    @Query("Select * from gejala where idGejala = :id")
    fun getAllGejala(id: Int): Gejala

    @Query("select bobot from gejala where idGejala = :id")
    fun getGejala(id: Int): Int

    @Query("select namaGejala from gejala where idGejala = :id")
    fun getGejalaName(id: Int): String

/*
    @Query("Select idGejala,namaGejala from gejala")
    fun getAllname(): String*/
}