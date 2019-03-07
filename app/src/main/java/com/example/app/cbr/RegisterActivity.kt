package com.example.app.cbr

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.app.cbr.model.AppDatabase
import com.example.app.cbr.model.User
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private var db: AppDatabase? = null
    var email: String =""
    var password: String = ""
    var nama: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        db = AppDatabase.getAppDataBase(this@RegisterActivity)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = getColor(R.color.colorBackround)
        }

        val bar: android.support.v7.app.ActionBar? = supportActionBar
        bar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorBackround)))
        bar?.elevation = 0F

        btRegister.setOnClickListener {
            nama = etNama.text.toString()
            email = etEmail.text .toString()
            password = etKataSandi.text.toString()
            isUserExist()
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        }
    }

    fun cannotEmpty(): Boolean {
        if(etNama.text.isEmpty() || etEmail.text.isEmpty() || etKataSandi.text.isEmpty()) {
            Toast.makeText(this, "Semua harus diisi", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    fun isUserExist(): Boolean{
        cannotEmpty()
        if (db?.UserDao()?.checkIsUserExist(email = email, password = password, nama = nama) == true)  {
            Toast.makeText(this, "Akun sudah terdaftar", Toast.LENGTH_SHORT).show()
            return false
        } else {
            doRegister()
            return true
        }
    }

    fun doRegister(){
        db?.UserDao()?.insertUser(User(emailUser = email, nama = nama, passwordUser = password, konsultasiId = 0))
        //Log.e("DB", db?.UserDao()?.getAllUser().toString())
        startActivity(Intent(this@RegisterActivity, HomeActivity::class.java))
    }
}
