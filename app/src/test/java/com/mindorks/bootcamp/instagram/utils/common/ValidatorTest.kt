package com.mindorks.bootcamp.instagram.utils.common

import com.mindorks.bootcamp.instagram.R
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.hasSize
import org.junit.Test
import java.util.regex.Matcher


class ValidatorTest {

    @Test
    fun givenValidEmailAndPassword_whenValidate_shouldReturnSuccess() {
        val email = "suraj260@gmail.com"
        val password = "1234566"
        val validation = Validator.validateLoginFields(email, password)
        assertThat(validation, hasSize(2))
        assertThat(
            validation, contains(
                Validation(
                    Validation.Field.EMAIL,
                    Resource.success()
                ),
                Validation(Validation.Field.PASSWORD, Resource.success())
            )
        )
    }

    @Test
    fun givenInvalidEmailAndValidPassowrd_whenValidate_shoulReturnEmailError() {
        val email = "surajgmail.com"
        val password = "123456"
        val validator = Validator.validateLoginFields(email, password)
        assertThat(validator, hasSize(2))
        assertThat(
            validator, contains(
                Validation(Validation.Field.EMAIL, Resource.error(R.string.email_field_invalid)),
                Validation(Validation.Field.PASSWORD, Resource.success())
            )
        )
    }

    @Test
    fun givenInvalidPasswordAndValidEmail_whenValidate_shoulReturnPasswordError() {
        val email = "suraj260@gmail.com"
        val password = "156"
        val validator = Validator.validateLoginFields(email, password)
        assertThat(validator, hasSize(2))
        assertThat(
            validator, contains(
                Validation(Validation.Field.EMAIL, Resource.success()),
                Validation(Validation.Field.PASSWORD, Resource.error(R.string.password_field_small_length)
            )
        ))
    }


}