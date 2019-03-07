package com.example.app.cbr

import android.app.ActionBar
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.example.app.cbr.model.AppDatabase
import kotlinx.android.synthetic.main.activity_login.*
import android.graphics.drawable.ColorDrawable



class LoginActivity : AppCompatActivity() {

    private var db: AppDatabase? = null
    var email: String = ""
    var password: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        db = AppDatabase.getAppDataBase(this@LoginActivity)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = getColor(R.color.colorBackround)
        }

        val bar: android.support.v7.app.ActionBar? = supportActionBar
        bar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorBackround)))
        bar?.elevation = 0F
        /*val window = getWindow()
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorWhite)
*/
        changeButtonState()

        btLogin.setOnClickListener {
            email = etEmail.text.toString()
            password = etKataSandi.text.toString()
            doLogin()
        }

        tvDaftar.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
    }

    private fun changeButtonState(){
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (etEmail.text.isNotEmpty() && etKataSandi.text.isNotEmpty()) {
                    btLogin.setBackgroundResource(R.drawable.bt_login_enable)
                    btLogin.setTextColor(resources.getColor(R.color.colorWhite))
                } else {
                    btLogin.setBackgroundResource(R.drawable.bt_login_disable)
                    btLogin.setTextColor(resources.getColor(R.color.colorPrimary))
                }
            }
        }
        etEmail.addTextChangedListener(textWatcher)
        etKataSandi.addTextChangedListener(textWatcher)
    }

    private fun doLogin() {
        if (tryLogin()) {
            if (db?.UserDao()?.checkUser(email = email, password = password) == true)  {
                saveSharedPref()
                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                intent.apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                //finish()
            } else {
                Toast.makeText(this, "Akun anda tidak terdaftar", Toast.LENGTH_SHORT).show()
            }
        }
        else {
            Log.d("intent", "HERE")
        }
    }

    //save to pref
    private fun saveSharedPref(){
        val sharedPref = getSharedPreferences("file", Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("key-username", email)
            putBoolean("isLoggedIn", true)
            apply()
        }
    }

    private fun tryLogin(): Boolean {
        if (email.isEmpty() || password.isEmpty()){
            Log.e("DB", email)
            Toast.makeText(this, "Username dan password harus diisi", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
