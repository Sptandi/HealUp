package com.example.app.cbr.slider

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

class AdapterSlider(val fm: FragmentManager, var mNumberofTabs: Int) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> SliderOneFragment()
            1 -> SliderTwoFragment()
            2 -> SliderThreeFragment()
            else -> {
                null
            }
        }
    }

    override fun getCount(): Int {
        return mNumberofTabs
    }

}