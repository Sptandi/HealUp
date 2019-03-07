package com.example.app.cbr

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Button
import com.example.app.cbr.adapter.ChecklistAdapter
import com.example.app.cbr.model.*
import kotlinx.android.synthetic.main.activity_konsultasi.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.text.SimpleDateFormat
import java.util.*

class KonsultasiActivity : AppCompatActivity() {

    private var db: AppDatabase? = null
    var hasilListKomponen: Double = 0.0
    var userSession: String? = ""
    var gejalaCalculation: MutableList<Gejala> = arrayListOf()
    val hasilPerhitungan: ArrayList<Int>? = arrayListOf()
    var gejalas = mutableListOf<Gejala>()
    lateinit var rv: RecyclerView
    lateinit var formatDate: String
    lateinit var btCheck: Button
    lateinit var adapter: ChecklistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_konsultasi)
        db = AppDatabase.getAppDataBase(this@KonsultasiActivity)

        queryDB()

        /*val nama: String = etNamaAnak.text.toString()
        val c: Date = Calendar.getInstance().time
        val df: SimpleDateFormat = SimpleDateFormat("dd-MMM-yyyy")
        formatDate = df.format(c)*/

        rv = findViewById(R.id.rv)
        btCheck = findViewById(R.id.btCheck)

        //initialize all checkbox with data gejala from db
        for (i in 1..62) {
            val gejala = db?.GejalaDao()?.getAllGejala(i)
            if (gejala != null) {
                gejalas.add(gejala)
            }
        }

        setAdapter()

        //getting checkbox that checked
        btCheck.setOnClickListener() {
            val data = adapter.getItem()
            data.map {
                    if(it.isSelected){
                        gejalaCalculation.add(it)
                }
            }
            getUserSession()
            checkKemiripan()
        }
    }

    fun queryDB() {
        doAsync {
            addSolusi()
            addGejala()
            addKasus()
            addKomponenGejala()
        }
    }

    fun setAdapter() {
        //set to adapter
        adapter = ChecklistAdapter(this, gejalas)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter
    }

    fun getUserSession(){
        val sharedPref: SharedPreferences = getSharedPreferences("file", Context.MODE_PRIVATE)
        userSession = sharedPref.getString("key-username", "tidak ada")
    }


    fun jumlahKasusSama() {

    }

    fun checkKemiripan() {
        doAsync {
            val jumlahKasus = db?.KasusDao()?.getCountKasus()
            Log.d("jumlah kasus", jumlahKasus.toString())
            if (jumlahKasus != null) {
                //mengulang sebanyak kasus
                for (l in 1..jumlahKasus) {
                    var bobotSemua: Double = 0.0
                    var bobotBaru: Double = 0.0
                    val listKasus = db?.KomponenGejalaDao()?.getAllSameId(l)
                    if (listKasus != null) {
                        for( j in listKasus) {
                            for ( i in gejalaCalculation) {
                                if (j.gejalaId == i.idGejala) {
                                    bobotBaru += i.bobot
                                    //Log.e("hasil", bobotBaru.toString())
                                }
                            }
                            val getBobot: Int? = db?.GejalaDao()?.getGejala(j.gejalaId)
                            bobotSemua += getBobot!!
                        }
                    }
                    hasilListKomponen = bobotBaru / bobotSemua
                    //Log.e("bobot", bobotBaru.toString())
                    //Log.e("bobot", bobotSemua.toString())
                    //Log.e("hasil", hasilListKomponen.toString())
                    if (hasilListKomponen >= 0.75) {
                        val hasil: Int? = db?.KasusDao()?.getSolusiById(l)
                        if (hasil != null) {
                            hasilPerhitungan?.add(hasil)
                        }
                    }
                }
                val c = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
                db?.KonsultasiDao()?.insertKonsultasi(Konsultasi(
                    namaAnak = etNamaAnak.text.toString() , userId = db?.UserDao()?.userIdSession(userSession), tanggal = c))
                val getLast = db?.KonsultasiDao()?.getLastRow()
                for (i in gejalaCalculation) {
                    if (hasilPerhitungan != null) {
                        for (j in hasilPerhitungan) {
                            db?.GejalaSolusiDao()?.insertGejalaSolusi(GejalaSolusi(idGejalaSolusi = getLast, gejalaId = i.idGejala, solusiId = j ))
                        }
                    }
                }
            }
            uiThread {
                val intent = Intent(this@KonsultasiActivity, SolusiActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun addKomponenGejala() {
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 1, gejalaId = 2))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 1, gejalaId = 37))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 1, gejalaId = 46))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 1, gejalaId = 47))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 2, gejalaId = 2))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 2, gejalaId = 45))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 2, gejalaId = 46))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 2, gejalaId = 49))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 3, gejalaId = 2))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 3, gejalaId = 14))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 3, gejalaId = 15))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 3, gejalaId = 16))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 4, gejalaId = 2))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 4, gejalaId = 7))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 4, gejalaId = 22))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 4, gejalaId = 39))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 4, gejalaId = 55))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 5, gejalaId = 2))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 5, gejalaId = 19))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 5, gejalaId = 37))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 5, gejalaId = 58))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 5, gejalaId = 59))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 6, gejalaId = 2))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 6, gejalaId = 47))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 6, gejalaId = 57))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 6, gejalaId = 60))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 7, gejalaId = 4))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 7, gejalaId = 13))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 7, gejalaId = 54))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 7, gejalaId = 59))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 8, gejalaId = 1))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 8, gejalaId = 6))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 8, gejalaId = 12))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 8, gejalaId = 17))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 8, gejalaId = 29))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 8, gejalaId = 30))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 9, gejalaId = 3))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 9, gejalaId = 6))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 9, gejalaId = 7))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 9, gejalaId = 17))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 9, gejalaId = 29))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 9, gejalaId = 30))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 10, gejalaId = 1))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 10, gejalaId = 13))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 10, gejalaId = 37))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 10, gejalaId = 19))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 10, gejalaId = 39))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 10, gejalaId = 47))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 11, gejalaId = 3))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 11, gejalaId = 10))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 11, gejalaId = 11))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 11, gejalaId = 14))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 12, gejalaId = 3))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 12, gejalaId = 12))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 12, gejalaId = 18))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 12, gejalaId = 19))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 12, gejalaId = 23))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 12, gejalaId = 27))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 13, gejalaId = 3))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 13, gejalaId = 11))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 13, gejalaId = 22))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 13, gejalaId = 26))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 13, gejalaId = 36))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 14, gejalaId = 8))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 14, gejalaId = 14))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 14, gejalaId = 15))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 14, gejalaId = 26))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 15, gejalaId = 8))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 15, gejalaId = 16))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 15, gejalaId = 25))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 15, gejalaId = 27))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 16, gejalaId = 9))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 16, gejalaId = 10))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 16, gejalaId = 11))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 16, gejalaId = 27))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 16, gejalaId = 7))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 16, gejalaId = 39))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 17, gejalaId = 12))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 17, gejalaId = 13))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 17, gejalaId = 17))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 17, gejalaId = 18))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 17, gejalaId = 23))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 17, gejalaId = 24))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 17, gejalaId = 28))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 18, gejalaId = 5))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 18, gejalaId = 7))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 18, gejalaId = 13))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 18, gejalaId = 30))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 18, gejalaId = 34))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 18, gejalaId = 36))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 19, gejalaId = 13))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 19, gejalaId = 18))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 19, gejalaId = 22))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 19, gejalaId = 31))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 19, gejalaId = 33))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 19, gejalaId = 38))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 19, gejalaId = 56))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 20, gejalaId = 17))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 20, gejalaId = 19))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 20, gejalaId = 16))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 20, gejalaId = 25))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 20, gejalaId = 29))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 20, gejalaId = 31))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 21, gejalaId = 19))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 21, gejalaId = 18))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 21, gejalaId = 33))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 21, gejalaId = 34))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 21, gejalaId = 36))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 21, gejalaId = 37))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 22, gejalaId = 19))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 22, gejalaId = 37))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 22, gejalaId = 39))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 22, gejalaId = 45))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 22, gejalaId = 46))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 23, gejalaId = 20))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 23, gejalaId = 22))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 23, gejalaId = 27))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 23, gejalaId = 43))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 23, gejalaId = 48))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 24, gejalaId = 23))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 24, gejalaId = 24))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 24, gejalaId = 33))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 24, gejalaId = 51))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 24, gejalaId = 57))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 25, gejalaId = 4))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 25, gejalaId = 6))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 25, gejalaId = 25))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 25, gejalaId = 31))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 25, gejalaId = 57))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 25, gejalaId = 60))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 26, gejalaId = 4))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 26, gejalaId = 28))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 26, gejalaId = 34))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 26, gejalaId = 45))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 26, gejalaId = 53))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 26, gejalaId = 54))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 26, gejalaId = 60))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 27, gejalaId = 4))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 27, gejalaId = 17))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 27, gejalaId = 28))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 27, gejalaId = 31))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 27, gejalaId = 56))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 27, gejalaId = 59))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 28, gejalaId = 6))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 28, gejalaId = 23))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 28, gejalaId = 24))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 28, gejalaId = 16))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 28, gejalaId = 25))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 28, gejalaId = 54))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 29, gejalaId = 23))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 29, gejalaId = 51))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 29, gejalaId = 53))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 30, gejalaId = 27))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 30, gejalaId = 54))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 30, gejalaId = 57))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 30, gejalaId = 59))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 31, gejalaId = 40))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 31, gejalaId = 39))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 31, gejalaId = 46))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 31, gejalaId = 45))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 31, gejalaId = 47))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 31, gejalaId = 48))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 31, gejalaId = 52))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 32, gejalaId = 40))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 32, gejalaId = 26))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 32, gejalaId = 27))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 32, gejalaId = 43))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 32, gejalaId = 47))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 33, gejalaId = 5))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 33, gejalaId = 6))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 33, gejalaId = 17))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 33, gejalaId = 41))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 34, gejalaId = 5))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 34, gejalaId = 6))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 34, gejalaId = 17))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 34, gejalaId = 54))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 35, gejalaId = 27))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 35, gejalaId = 39))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 35, gejalaId = 47))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 35, gejalaId = 50))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 35, gejalaId = 55))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 36, gejalaId = 22))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 36, gejalaId = 35))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 36, gejalaId = 47))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 36, gejalaId = 50))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 36, gejalaId = 52))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 37, gejalaId = 44))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 37, gejalaId = 45))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 37, gejalaId = 46))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 37, gejalaId = 49))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 38, gejalaId = 44))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 38, gejalaId = 46))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 38, gejalaId = 48))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 38, gejalaId = 52))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 39, gejalaId = 35))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 39, gejalaId = 36))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 39, gejalaId = 37))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 39, gejalaId = 43))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 39, gejalaId = 44))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 39, gejalaId = 52))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 40, gejalaId = 38))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 40, gejalaId = 39))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 40, gejalaId = 43))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 40, gejalaId = 51))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 40, gejalaId = 53))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 40, gejalaId = 54))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 41, gejalaId = 37))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 41, gejalaId = 52))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 41, gejalaId = 57))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 41, gejalaId = 58))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 42, gejalaId = 45))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 42, gejalaId = 48))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 42, gejalaId = 49))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 42, gejalaId = 50))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 42, gejalaId = 56))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 43, gejalaId = 40))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 43, gejalaId = 54))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 43, gejalaId = 58))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 43, gejalaId = 59))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 43, gejalaId = 60))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 44, gejalaId = 40))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 44, gejalaId = 47))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 44, gejalaId = 52))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 44, gejalaId = 55))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 44, gejalaId = 56))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 45, gejalaId = 29))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 45, gejalaId = 30))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 45, gejalaId = 31))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 45, gejalaId = 35))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 45, gejalaId = 41))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 45, gejalaId = 54))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 46, gejalaId = 43))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 46, gejalaId = 45))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 46, gejalaId = 51))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 47, gejalaId = 33))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 47, gejalaId = 36))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 47, gejalaId = 37))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 47, gejalaId = 51))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 47, gejalaId = 57))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 48, gejalaId = 32))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 48, gejalaId = 33))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 48, gejalaId = 39))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 48, gejalaId = 48))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 49, gejalaId = 32))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 49, gejalaId = 39))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 49, gejalaId = 43))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 49, gejalaId = 54))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 49, gejalaId = 55))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 50, gejalaId = 32))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 50, gejalaId = 41))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 50, gejalaId = 47))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 50, gejalaId = 52))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 50, gejalaId = 54))
        db?.KomponenGejalaDao()?.add(KomponenGejala( kasusId = 50, gejalaId = 59))
    }

    fun addGejala() {
        db?.GejalaDao()?.insertGejala(Gejala( namaGejala = "Pola asuh ibu dan bapak bertentangan", bobot = 4))
        db?.GejalaDao()?.insertGejala(Gejala( namaGejala = "Orang tua bercerai", bobot = 4))
        db?.GejalaDao()?.insertGejala(Gejala( namaGejala = "Orang tua sering bertengkar", bobot = 4))
        db?.GejalaDao()?.insertGejala(Gejala( namaGejala = "Orang tua terlalu posesif", bobot = 4))
        db?.GejalaDao()?.insertGejala(Gejala( namaGejala = "Suka berselisih dengan orang tua", bobot = 4))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Tidak pernah mendengarkan nasihat orang tua", bobot = 4))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Segala yang dia butuhkan selalu dituruti orang tua", bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Orang tua meninggal", bobot = 4))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Orang tua sakit", bobot = 4))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Orang tua berhenti bekerja", bobot = 4))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Orang tua mengalami kesulitan keuangan", bobot = 4))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Merasa tidak nyaman dengan orang tua", bobot = 4))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Orang tua tidak mendukung cita-citanya", bobot = 4))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Anggota keluarga terlalu banyak", bobot = 3))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Menjadi tulang punggung ekonomi keluarga", bobot = 3))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Kesulitan keuangan", bobot = 3))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Suka kabur dari rumah ketika keinginannya tidak dituruti", bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Suka berselisih dengan saudaranya", bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Tidak nyaman/betah tinggal di rumah", bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Ada anggota keluarga atau orang yang dekatnya meninggal", bobot = 3))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Ada anggota keluarga atau orang yang dekatnya sakit", bobot = 3))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Sulit bepisah dengan orang dekatnya", bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Suka berselisih dengan kawan", bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Bersifat agresif (misal: suka berkelahi)", bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Banyak hutang", bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Suka bolos sekolah/ kuliah", bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Tidak konsentrasi belajar", bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Bersikap tidak sopan dengan guru / orang yang lebih tua", bobot = 4))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Tidak bertanggug jawab", bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Kepercayaan disalahgunakan", bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Suka belanja hal yang tidak perlu", bobot = 1))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Terlalu menyesali kegagalan", bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Kurang percaya diri", bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Sering pergi dengan gang nya", bobot = 3))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Sulit berkawan terutama dengan orang baru", bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Suka menyalahkan orang lain", bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Mudah frustasi", bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Khawatir yang berlebihan", bobot = 3))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Selalu pesimis", bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Putus cinta", bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Sering pergi berduaan dengan kekasihnya", bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Boros dan tidak bisa menabung", bobot = 3))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Suka menyendiri", bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Sangat pemalu", bobot = 1))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Sangat tertutup", bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Merasa kesepian", bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Merasa tidak berguna", bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Merasa bersalah", bobot = 3))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Tidak mudah curhat", bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Ada masalah kesehatan", bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Marah jika dikritik", bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Tidak bersemangat dalam menjalani hidup", bobot = 3))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Tidak punya rasa empati", bobot = 1))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Malas beribadah", bobot = 4))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Murung dan bersedih", bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Galau dengan penampilan", bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Merokok berlebihan", bobot = 3))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Kecanduan narkoba", bobot = 4))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Kecanduan game online", bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Mengalami masalah kriminal", bobot = 4))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Mengalami masalah kejiwaan", bobot = 4))
    }

    fun addSolusi() {
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 1, nama = "Orangtua harus selalu disampingnya"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 2, nama = "Peran teman dekat sangat dibutuhka"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 3, nama = "Mencari pekerjaan yang dekat dari rumah agar tidak jauh dari keluarga"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 4, nama = "Butuh perhatian dari orangtua"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 5, nama = "Dijauhkan dari lingkungan yang rusak "))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 6, nama = "Rehabilitasi "))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 7, nama = "Orangtua seharusnya membiarkan apa yang diinginkan anaknya selagi itu hal positif "))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 8, nama = "Keluarga harus membuat suasana yang nyaman"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 9, nama = "Tidak boleh dimanjakan"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 10, nama = "Orangtua harus berdamai"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 11, nama = "Mencari pekerjaan untuk membantu keluarga"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 12, nama = "Lingkungan keluarga harus kondusif sehingga anak bisa belajar dengan baik"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 13, nama = "Mandiri dan harus berpikiran dewasa"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 14, nama = "Sekolah sambil bekerja"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 15, nama = "Mencari pekerjaan "))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 16, nama = "Bersekolah sambil bekerja dan tetap semangat "))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 17, nama = "Didikan orangtua harus diperbaiki karena akan berpengaruh ke lingkungan sekitar"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 18, nama = "Hilangkan sifat manja dan kekanakan pada anak "))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 19, nama = "Berdamai dengan saudara "))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 20, nama = "Kurangi sikap manja dan boros"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 21, nama = "Hilangkan trauma anak "))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 22, nama = "Pendekatan orangtua"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 23, nama = "Menerima kondisi yang ada sekarang dan berhenti menyalahkan diri sendiri"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 24, nama = "Kendalikan amarah dan selalu berpikir positif apa yang diberikan oleh orang lain"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 25, nama = "Orangtua seharusnya bisa memberikan kebebasan yang bertanggung jawab pada anaknya"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 26, nama = "Pendekatan psikolog"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 27, nama = "Berhenti dimanja dan dilatih untuk mandiri "))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 28, nama = "Pendekatan orangtua dan psikolog keluarga"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 29, nama = "Mengendalikan amarah dan perbanyak mendekatkan diri pada Tuhan"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 30, nama = "Ibadah yang rajin dan perbanyak kegiatan sosial"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 31, nama = "Perbanyak kegiatan sosial dan harus move on "))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 32, nama = "Move on dan perbanyak kegiatan positif"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 33, nama = "Mengutamakan keluarga dan memperbaiki cara belajar"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 34, nama = "Perbanyak ibadah dan jangan merepotkan orangtua"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 35, nama = "Optimis menjalani hidup "))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 36, nama = "Membuka diri dan hidup dengan optimis "))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 37, nama = "Pendekatan keluarga dan psikolog"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 38, nama = "Selalu optimis dan hentikan menyalahkan diri sendiri "))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 39, nama = "Butuh seseorang yang terdekat untuk membuka dirinya agar lebih berani"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 40, nama = "Perbanyak mendekatkan diri pada Tuhan"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 41, nama = "Perbanyak motivasi hidup dan ibadah "))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 42, nama = "Percaya pada diri sendiri"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 43, nama = "Periksa ke psikolog"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 44, nama = "Move on"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 45, nama = "Membuka diri dengan lingkungan sekitarnya"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 46, nama = "Membuka diri dan menerima apa adanya"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 47, nama = "Kurangi stres berlebih dan kendalikan diri sendiri "))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 48, nama = "Lupakan kenangan buruk dan jangan takut gagal saat belajar"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 49, nama = "Perbanyak ibadah dan curhat ke orang terdekat "))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 50, nama = "Dibutuhkan motivasi dari orang terdekat"))
    }

    fun addKasus() {
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 1,  solusiId = 1))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 2,  solusiId = 2))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 3,  solusiId = 3))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 4,  solusiId = 4))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 5,  solusiId = 5))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 6,  solusiId = 6))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 7,  solusiId = 7))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 8,  solusiId = 8))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 9,  solusiId = 9))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 10,  solusiId = 10))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 11,  solusiId = 11))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 12,  solusiId = 12))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 13,  solusiId = 13))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 14,  solusiId = 14))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 15,  solusiId = 15))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 16,  solusiId = 16))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 17,  solusiId = 17))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 18,  solusiId = 18))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 19,  solusiId = 19))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 20,  solusiId = 20))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 21,  solusiId = 21))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 22,  solusiId = 22))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 23,  solusiId = 23))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 24,  solusiId = 24))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 25,  solusiId = 25))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 26,  solusiId = 26))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 27,  solusiId = 27))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 28,  solusiId = 28))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 29,  solusiId = 29))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 30,  solusiId = 30))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 31,  solusiId = 31))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 32,  solusiId = 32))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 33,  solusiId = 33))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 34,  solusiId = 34))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 35,  solusiId = 35))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 36,  solusiId = 36))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 37,  solusiId = 37))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 38,  solusiId = 38))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 39,  solusiId = 39))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 40,  solusiId = 40))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 41,  solusiId = 41))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 42,  solusiId = 42))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 43,  solusiId = 43))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 44,  solusiId = 44))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 45,  solusiId = 45))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 46,  solusiId = 46))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 47,  solusiId = 47))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 48,  solusiId = 48))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 49,  solusiId = 49))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 50,  solusiId = 50))
    }

}