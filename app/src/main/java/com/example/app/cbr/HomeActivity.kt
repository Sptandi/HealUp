package com.example.app.cbr

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.WindowManager
import com.example.app.cbr.model.AppDatabase
import com.example.app.cbr.model.ProfileActivity
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private var db: AppDatabase? = null
    var userSession: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        db = AppDatabase.getAppDataBase(this@HomeActivity)
        getUserSession()
        val id = db?.UserDao()?.userIdSession(userSession)
        val getName = db?.UserDao()?.getUserData(id)

        tvWelcome.text = "Selamat datang, ${getName?.nama}"

        val bar: android.support.v7.app.ActionBar? = supportActionBar
        bar?.elevation = 0F

        val window = getWindow()
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)

        btKonsultasi.setOnClickListener() {

            startActivity(Intent(this@HomeActivity, KonsultasiActivity::class.java))
        }

        btProfile.setOnClickListener {
            startActivity(Intent(this@HomeActivity, ProfileActivity::class.java))
        }


        btRiwayat.setOnClickListener {
            startActivity(Intent(this@HomeActivity, RiwayatActivity::class.java))
        }

        btAbout.setOnClickListener() {
            startActivity(Intent(this@HomeActivity, AboutActivity::class.java))
        }
    }

    fun getUserSession(){
        val sharedPref: SharedPreferences = getSharedPreferences("file", Context.MODE_PRIVATE)
        userSession = sharedPref.getString("key-username", "tidak ada")
    }
}
