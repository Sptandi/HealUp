package com.example.app.cbr.databaseDAO

import android.arch.persistence.room.*
import com.example.app.cbr.model.Gejala
import com.example.app.cbr.model.Kasus
import com.example.app.cbr.model.KomponenGejala

@Dao
interface KasusDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertKasus(kasus: Kasus)

    @Query("select * from kasus")
    fun getAll(): List<Kasus>

    @Query("Select count(idKasus) from kasus")
    fun getCountKasus(): Int

    /*@Query("Select kgId from kasus where idKasus = :id")
    fun getGejalaId(id: Int): List<Int>*/

/*

    @Query("select * from kasus join gejala on kgId=idGejala where idKasus = :id")
    fun getAllKomponenGejalaId(id: Int): List<KomponenGejala>
*/

    @Query("select * from kasus where value = :nilai")
    fun getKasus(nilai: Double): Kasus

    @Query("select solusiId from kasus where idKasus = :id")
    fun getSolusiById(id: Int): Int

    @Query("Select nama from kasus join solusi where idKasus = :id")
    fun getByValue(id: Int): String

    @Query("update kasus set value = :nilai where idKasus = :id")
    fun updateValue(nilai: Double, id: Int)
}