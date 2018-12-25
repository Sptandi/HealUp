package com.example.app.cbr.databaseDAO

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.example.app.cbr.model.Konsultasi
import java.util.*

@Dao
interface KonsultasiDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertKonsultasi(konsultasi: Konsultasi)

    @Query("Select * from konsultasi where userId = 1")
    fun getUserKonsultasi(): List<Konsultasi>

    @Query("select tanggal from KONSULTASI")
    fun getDate(): Date

    @Query("select * from konsultasi ")
    fun getAll(): List<Konsultasi>

    @Query("Select distinct idKonsultasi,namaAnak,tanggal from konsultasi where userId = :id")
    fun getAllOne(id: Int): List<Konsultasi>

    @Query("Select idKonsultasi from konsultasi ORDER BY idKonsultasi DESC LIMIT 1")
    fun getLastRow(): Int

}