package com.example.app.cbr

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.app.cbr.model.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var db: AppDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getAppDataBase(this@MainActivity)
        btSubmit.setOnClickListener {
            addKomponen()
            addGejala()
            getAll()
            all()
          //  addKasus()
        }
    }
/*
    private fun addKasus(){
        val testing = Kasus(idKasus = 1, namaKasus = "Kerjaan", kgId = 1 ,solusiId = 1)
        db?.KasusDao()?.insertKasus(testing)
        //Log.e("DB", db?.KasusDao()?.getKasus().toString())
    }*/

    private fun all() {
        /*val k1 = KomponenGejala(komponenId = 1, gejalaId = 1)
        val k2 = KomponenGejala(komponenId = 1, gejalaId = 2)
        val k3 = KomponenGejala(komponenId = 1, gejalaId = 3)
        db?.KomponenGejalaDao()?.addAll(k1)
        db?.KomponenGejalaDao()?.addAll(k2)
        db?.KomponenGejalaDao()?.addAll(k3)
        val hasil = db?.KomponenGejalaDao()?.addAllList()
        Log.e("DB", hasil.toString())
        */
    }

    private fun getAll() {
        //Log.e("DB", db?.GejalaDao()?.getGejala().toString())
        Log.e("DB", db?.KomponenDao()?.getKomponen().toString())
    }

    private fun addKomponen(){
        //db?.KomponenDao()?.insertKomponen(Komponen())
    }

    private fun addGejala() {
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Pola asuh ibu dan bapak bertentangan", bobot = 4))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Orang tua berhenti bekerja", bobot = 4))
    }
}
