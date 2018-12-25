package com.example.app.cbr

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.app.cbr.model.AppDatabase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private var db: AppDatabase? = null
    var email: String = ""
    var password: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        db = AppDatabase.getAppDataBase(this@LoginActivity)

        btLogin.setOnClickListener {
            email = etEmail.text .toString()
            password = etPassword.text.toString()
            doLogin()
        }

        btSignup.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
    }

    private fun doLogin() {
        tryLogin()
        if (db?.UserDao()?.checkUser(email = email, password = password) == true)  {
            val sharedPref = getSharedPreferences("file", Context.MODE_PRIVATE) ?: return
            with(sharedPref.edit()) {
                putString("key-username", email)
                putBoolean("isLoggedIn", true)
                apply()
            }
            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Akun anda tidak terdaftar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun tryLogin(): Boolean {
        if (email.isEmpty() || password.isEmpty()){
            Log.e("DB", email)
            Toast.makeText(this, "Username dan password harus diisi", Toast.LENGTH_SHORT).show()
            return false
        } else {
            return true
        }
    }
}
