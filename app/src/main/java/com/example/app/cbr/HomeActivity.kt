package com.example.app.cbr

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.app.cbr.model.AppDatabase
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private var db: AppDatabase? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        db = AppDatabase.getAppDataBase(this@HomeActivity)

        btCheclist.setOnClickListener() {
            startActivity(Intent(this@HomeActivity, KonsultasiActivity::class.java))
        }

        btLogout.setOnClickListener() {
            val sharePref: SharedPreferences = getSharedPreferences("file", Context.MODE_PRIVATE)
            sharePref.edit().remove("key-username").commit()
            startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
        }


        btRiwayat.setOnClickListener {
            startActivity(Intent(this@HomeActivity, RiwayatActivity::class.java))
        }
    }
}
