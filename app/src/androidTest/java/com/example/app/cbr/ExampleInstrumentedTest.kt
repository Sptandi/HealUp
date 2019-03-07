package com.example.app.cbr

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.ViewAction
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.action.ViewActions.closeSoftKeyboard
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.jetbrains.anko.matchParent

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @Rule
    @JvmField
    val rule = ActivityTestRule(LoginActivity::class.java)

    private val email: String = "sptandi@gmail.com"
    private val password: String = "mantapbanget"
    /*
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("com.example.app.cbr", appContext.packageName)
    }*/

    @Test
    fun login_succes() {
        Espresso.onView(withId(R.id.etEmail))
            .perform(ViewActions.typeText(email), closeSoftKeyboard())

        Espresso.onView((withId(R.id.etKataSandi)))
            .perform(ViewActions.typeText(password), closeSoftKeyboard())

        Espresso.onView((withId(R.id.btLogin)))
            .perform(ViewActions.click())
            .check(matches(isDisplayed()))
    }
}
