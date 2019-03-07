package com.example.app.cbr.slider

import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.text.Html
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_slider.*
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import com.example.app.cbr.R


class SliderActivity : AppCompatActivity() {

    private var dotsLayout: LinearLayout? = null
    private var layouts: IntArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slider)

        dotsLayout = layoutDots
        layouts = intArrayOf(R.layout.fragment_slider_one, R.layout.fragment_slider_two, com.example.app.cbr.R.layout.fragment_slider_three)
        addBottomDots(0)
        changeStatusBarColor()

        val vpSplashScreen = vpSplashScreen
        val pagerAdapter = AdapterSlider(supportFragmentManager, 3)
        vpSplashScreen.adapter = pagerAdapter
        vpSplashScreen.addOnPageChangeListener(viewPagerChangeListener)
    }

    fun changeStatusBarColor() {
        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val windows = window
            windows.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            windows.statusBarColor = Color.TRANSPARENT
        }*/
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    fun addBottomDots(currentPage: Int) {
        val dots: Array<TextView?> = arrayOfNulls(layouts?.size!!)

        dotsLayout?.removeAllViews()
        for (i in dots.indices) {
            val html = "&#8226;"
            dots[i] = TextView(this)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dots[i]?.text = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
            }  else {
                dots[i]?.text = Html.fromHtml(html)
            }
            dots[i]?.textSize = 35f
            dots[i]?.setTextColor(resources.getIntArray(R.array.array_dot_inactive)[currentPage])
            dotsLayout?.addView(dots[i])
        }

        if (dots.isNotEmpty()) {
            dots[currentPage]?.setTextColor(ContextCompat.getColor(this, R.color.dot_active))
        }
    }

    private var viewPagerChangeListener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            addBottomDots(position)
        }

        override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
        }

        override fun onPageScrollStateChanged(p0: Int) {
        }
    }
}
