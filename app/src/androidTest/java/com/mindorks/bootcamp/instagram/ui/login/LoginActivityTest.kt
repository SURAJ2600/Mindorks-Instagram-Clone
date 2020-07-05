package com.mindorks.bootcamp.instagram.ui.login

import android.content.Intent
import androidx.test.core.app.ActivityScenario

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*


import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.TestComponentRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain

class LoginActivityTest {

    private val component = TestComponentRule(InstrumentationRegistry.getInstrumentation().targetContext)


    @get:Rule
    val chain = RuleChain.outerRule(component)

    @Before
    fun setup() {

    }

    @Test
    fun testCheckViewsDisplay() {

        ActivityScenario.launch(LoginActivity::class.java)
        onView(withId(R.id.layout_email))
            .check(matches(isDisplayed()))
        onView(withId(R.id.layout_password))
            .check(matches(isDisplayed()))
        onView(withId(R.id.bt_login))
            .check(matches(isDisplayed()))
    }

    @Test
    fun givenValidEmailAndValidPassword_whenLogin_ShouldOpneMainActivity(){
        ActivityScenario.launch(LoginActivity::class.java)
        onView(withId(R.id.et_email)).perform(typeText("test@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText("1234560"), closeSoftKeyboard())
        onView(withId(R.id.bt_login)).perform(click())
        onView(withId(R.id.bottomNavigation)).check(matches(isDisplayed()))


    }

}