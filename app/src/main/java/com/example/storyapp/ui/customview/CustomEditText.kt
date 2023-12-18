package com.example.storyapp.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.storyapp.R

class CustomEditText : AppCompatEditText, View.OnTouchListener, View.OnFocusChangeListener {

    private lateinit var eyeOpenDrawable: Drawable
    private lateinit var eyeClosedDrawable: Drawable
    private lateinit var passwordDrawable: Drawable
    private var isPasswordVisible: Boolean = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = resources.getString(R.string.password)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun init() {
        transformationMethod = PasswordTransformationMethod.getInstance()

        eyeOpenDrawable = ContextCompat.getDrawable(context, R.drawable.ic_eye_open) as Drawable
        eyeClosedDrawable = ContextCompat.getDrawable(context, R.drawable.ic_horizontal) as Drawable
        passwordDrawable = ContextCompat.getDrawable(context, R.drawable.ic_lock) as Drawable
        setOnTouchListener(this)
        onFocusChangeListener = this

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) showClearButton() else hideClearButton()

                if (s.toString().length < 8) {
                    setError(resources.getString(R.string.weakPassword), null)
                } else {
                    error = null
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun showClearButton() {
        setButtonDrawables(endOfTheText = eyeClosedDrawable)
    }

    private fun hideClearButton() {
        setButtonDrawables()
    }

    private fun setButtonDrawables(startOfTheText: Drawable? = passwordDrawable, topOfTheText: Drawable? = null,
                                   endOfTheText: Drawable? = null, bottomOfTheText: Drawable? = null) {
        setCompoundDrawablesWithIntrinsicBounds(startOfTheText, topOfTheText, endOfTheText, bottomOfTheText)
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val clearButtonStart: Float
            val clearButtonEnd: Float
            var isClearButtonClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (eyeClosedDrawable.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < clearButtonEnd -> isClearButtonClicked = true
                }
            } else {
                clearButtonStart = (width - paddingEnd - eyeClosedDrawable.intrinsicWidth).toFloat()
                when {
                    event.x > clearButtonStart -> isClearButtonClicked = true
                }
            }
            return if (isClearButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        isPasswordVisible = !isPasswordVisible
                        togglePasswordVisibility()
                        true
                    }

                    else -> false
                }
            } else false
        }
        return false
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            eyeClosedDrawable = ContextCompat.getDrawable(context, R.drawable.ic_eye_open) as Drawable
            transformationMethod = null
        } else {
            eyeClosedDrawable = ContextCompat.getDrawable(context, R.drawable.ic_horizontal) as Drawable
            transformationMethod = PasswordTransformationMethod.getInstance()
        }
        setButtonDrawables(endOfTheText = eyeClosedDrawable)
        setSelection(length())
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (!hasFocus) {
            isPasswordVisible = false
            togglePasswordVisibility()
        }
    }
}
