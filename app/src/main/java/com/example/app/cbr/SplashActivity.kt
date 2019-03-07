package com.example.app.cbr

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.example.app.cbr.slider.SliderActivity

class SplashActivity : AppCompatActivity() {

    private var mDelayhandler: Handler? = null
    private val SPLASH_DELAY: Long = 2000

    internal val mRunnable: Runnable = Runnable {
        if(!isFinishing) {
            val intent = Intent(applicationContext, SliderActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = getColor(R.color.colorBackround)
        }

        mDelayhandler = Handler()
        mDelayhandler!!.postDelayed(mRunnable, SPLASH_DELAY)
    }

    override fun onDestroy() {
        if (mDelayhandler != null) {
            mDelayhandler!!.removeCallbacks(mRunnable)
        }
        super.onDestroy()
    }
}
