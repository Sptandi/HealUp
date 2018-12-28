package com.example.app.cbr

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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

        btRegister.setOnClickListener {
            nama = etNama.text.toString()
            email = etEmailRegister.text .toString()
            password = etPasswordRegister.text.toString()
            isUserExist()
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        }
    }

    fun cannotEmpty(): Boolean {
        if(etNama.text.isEmpty() || etEmailRegister.text.isEmpty() || etPasswordRegister.text.isEmpty()) {
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
