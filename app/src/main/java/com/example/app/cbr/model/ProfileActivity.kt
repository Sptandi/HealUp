package com.example.app.cbr.model

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.app.cbr.LoginActivity
import com.example.app.cbr.R
import kotlinx.android.synthetic.main.activity_profile.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast

class ProfileActivity : AppCompatActivity() {

    var name: String = ""
    var email: String = ""
    var userSession: String = ""
    private var db: AppDatabase? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        db = AppDatabase.getAppDataBase(this@ProfileActivity)
        getUserSession()
        getUserField()
        btUpdate.setOnClickListener() {
            update()
        }

        btLogout.setOnClickListener() {
            val sharePref: SharedPreferences = getSharedPreferences("file", Context.MODE_PRIVATE)
            sharePref.edit().remove("key-username").commit()
            startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
        }
    }

    fun getUserField(){
        val idUser = db?.UserDao()?.userIdSession(userSession)
        val getDataUser = db?.UserDao()?.getUserData(idUser)
        val nama: String? = getDataUser?.nama
        val email: String? = getDataUser?.emailUser
        etNama.setText(nama)
        etEmail.setText(email)
    }

    fun getUserSession(){
        val sharedPref: SharedPreferences = getSharedPreferences("file", Context.MODE_PRIVATE)
        userSession = sharedPref.getString("key-username", "tidak ada")
    }

    fun update() {
        val id = db?.UserDao()?.userIdSession(userSession)
        val nama = etNama.text.toString()
        val email = etEmail.text.toString()
        db?.UserDao()?.updateUser(nama, email, id)
        toast("Berhasil update")
    }
}
