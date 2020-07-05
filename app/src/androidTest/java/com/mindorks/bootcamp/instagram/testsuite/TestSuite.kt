package com.mindorks.bootcamp.instagram.testsuite

import com.mindorks.bootcamp.instagram.ui.home.HomeFragmentTest
import com.mindorks.bootcamp.instagram.ui.login.LoginActivityTest
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses


@RunWith(Suite::class)
@SuiteClasses(LoginActivityTest::class, HomeFragmentTest::class)
class AppTestSuite