package com.ahr.storyapp.helper

import android.text.InputType
import androidx.annotation.StringRes
import androidx.core.widget.doOnTextChanged
import com.ahr.storyapp.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

fun TextInputEditText.validateEmail(grandParent: TextInputLayout) {
    if ((inputType-1) != InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS) return
    if (!isEmailFormatValid()) {
        grandParent.error = context.getString(R.string.email_not_valid)
    } else grandParent.isErrorEnabled = false
}

fun TextInputEditText.validateInput(@StringRes errorMessage: Int) {
    doOnTextChanged { _, _, _, count ->
        val grandParent = parent.parent
        if (grandParent is TextInputLayout) {
            if (count <= 0) {
                grandParent.error = context.getString(errorMessage)
                return@doOnTextChanged
            }
            grandParent.error = ""
            validateEmail(grandParent)
        }
    }
}

fun TextInputEditText.isEmailFormatValid(): Boolean {
    return text.toString().validateEmail()
}

fun TextInputEditText.isLengthPasswordGreaterThan6(): Boolean {
    return text.toString().length >= 6
}

fun TextInputEditText.isNotEmpty(): Boolean {
    return text?.isNotEmpty() == true
}
