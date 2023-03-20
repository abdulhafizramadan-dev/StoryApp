package com.ahr.storyapp.common

import android.content.Context
import android.util.AttributeSet
import com.ahr.storyapp.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class PasswordEditText : TextInputEditText {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        if (text != null && text.isNotEmpty()) {
            val grandParent = parent.parent
            if (grandParent is TextInputLayout) {
                if (text.length < 6) {
                    grandParent.error = context.getString(R.string.password_smaller_6)
                } else {
                    grandParent.error = ""
                }
            }
        }
    }
}