package com.example.app.cbr

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.LinearLayout
import com.example.app.cbr.adapter.GejalaAdapter
import com.example.app.cbr.adapter.SolusiAdapter
import com.example.app.cbr.model.AppDatabase
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_solusi.*

class SolusiActivity : AppCompatActivity() {

    private var db: AppDatabase? = null
    var dataSolusi: MutableList<String>? = arrayListOf()
    var dataGejala: MutableList<String>? = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solusi)
        db = AppDatabase.getAppDataBase(this@SolusiActivity)
        val recyclerViewGejala = rcGejala
        val recyclerViewSolusi = rcSolusi
        recyclerViewGejala.layoutManager= LinearLayoutManager(this@SolusiActivity, LinearLayout.VERTICAL, false)
        recyclerViewSolusi.layoutManager= LinearLayoutManager(this@SolusiActivity, LinearLayout.VERTICAL, false)

        //get solusi from last konsultasi
        val getSolusi = db?.GejalaSolusiDao()?.getSolusi(db?.KonsultasiDao()?.getLastRow()!!)
        if (getSolusi != null) {
            for (i in getSolusi) {
                val getName = db?.SolusiDao()?.getNamaById(i)
                if (getName != null) {
                    dataSolusi?.add(getName)
                }
            }
        }

        //get gejala from last konsultasi
        val getGejala = db?.GejalaSolusiDao()?.getGejala(db?.KonsultasiDao()?.getLastRow()!!)
        if (getGejala != null) {
            for ( i in getGejala) {
                val getName: String? = db?.GejalaDao()?.getGejalaName(i)
                Log.e("d", getName)
                if (getName != null) {
                    dataGejala?.add(getName)
                }
            }
        }

        val adapterSolusi = SolusiAdapter(dataSolusi, this)
        val adapterGejala = GejalaAdapter(dataGejala, this)
        recyclerViewGejala.adapter = adapterGejala
        recyclerViewSolusi.adapter = adapterSolusi

        btSelesai.setOnClickListener() {
            startActivity(Intent(this@SolusiActivity, HomeActivity::class.java))
            finish()
        }
    }
}
