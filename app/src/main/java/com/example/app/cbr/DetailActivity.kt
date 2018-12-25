package com.example.app.cbr

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

class DetailActivity : AppCompatActivity() {

    private var db: AppDatabase? = null
    var dataSolusi: MutableList<String>? = arrayListOf()
    var dataGejala: MutableList<String>? = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        db = AppDatabase.getAppDataBase(this@DetailActivity)
        val recyclerViewGejala = recyclerDetailGejala
        val recyclerViewSolusi = recyclerDetailSolusi
        recyclerViewGejala.layoutManager= LinearLayoutManager(this@DetailActivity, LinearLayout.VERTICAL, false)
        recyclerViewSolusi.layoutManager= LinearLayoutManager(this@DetailActivity, LinearLayout.VERTICAL, false)

        val intent = getIntent()
        val id = intent.getStringExtra("id")
        Log.e("DB", id)

        //get solusi from last konsultasi
        val getSolusi = db?.GejalaSolusiDao()?.getSolusi(id.toInt())
        if (getSolusi != null) {
            for (i in getSolusi) {
                val getName = db?.SolusiDao()?.getNamaById(i)
                if (getName != null) {
                    dataSolusi?.add(getName)
                }
            }
        }

        //get gejala from last konsultasi
        val getGejala = db?.GejalaSolusiDao()?.getGejala(id.toInt()) // get by
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
    }
}
