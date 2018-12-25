package com.example.app.cbr

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.LinearLayout
import com.example.app.cbr.adapter.RiwayatAdapter
import com.example.app.cbr.model.AppDatabase
import com.example.app.cbr.model.Konsultasi
import kotlinx.android.synthetic.main.activity_riwayat.*

class RiwayatActivity : AppCompatActivity() {

    private var db: AppDatabase? = null
    var list: List<Konsultasi>? = null
    var userSession: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat)

        db = AppDatabase.getAppDataBase(this@RiwayatActivity)


        val sharedPref: SharedPreferences = getSharedPreferences("file", Context.MODE_PRIVATE)
        userSession = sharedPref.getString("key-username", "tidak ada")

        val recyclerView = recyclerViewRiwayat
        recyclerView.layoutManager= LinearLayoutManager(this@RiwayatActivity, LinearLayout.VERTICAL, false)

        val user = db?.UserDao()?.userIdSession(userSession)
        list = db?.KonsultasiDao()?.getAllOne(user!!)
        val adapater = RiwayatAdapter(list, this)
        recyclerView.adapter = adapater
    }
}
/*

    private fun getUserKonsultasi() {
        list = db?.KonsultasiDao()?.getUserKonsultasi()
    }
        getUserKonsultasi()

val getSolusi = db?.GejalaSolusiDao()?.getSolusi(db?.KonsultasiDao()?.getLastRow()!!)
if (getSolusi != null) {
    for (i in getSolusi) {
        Log.e("DB", db?.SolusiDao()?.getNamaById(i).toString())
    }
}

val getGejala = db?.GejalaSolusiDao()?.getGejala(db?.KonsultasiDao()?.getLastRow()!!)
if (getGejala != null) {
    for ( i in getGejala) {
        Log.e("DB", db?.GejalaDao()?.getGejalaName(i))
    }
}

*/

