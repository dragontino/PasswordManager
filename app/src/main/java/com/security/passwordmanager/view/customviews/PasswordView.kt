package com.security.passwordmanager.view.customviews

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.text.InputType
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageButton
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import com.security.passwordmanager.ColorStateList
import com.security.passwordmanager.R
import com.security.passwordmanager.databinding.PasswordFieldBinding

@SuppressLint("CustomViewStyleable")
class PasswordView(context: Context, attrs: AttributeSet?): ConstraintLayout(context, attrs) {

    private val binding: PasswordFieldBinding

    private var onVisibilityChangeListener: (visibility: ImageButton, isVisible: Boolean) -> Unit =
        { _, _ ->   }


    init {
        val root = inflate(context, R.layout.password_field, this)
        binding = PasswordFieldBinding.bind(root)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.PasswordView)
        setAttributes(attributes)
        attributes.recycle()

        val textViewAttributes = context.obtainStyledAttributes(attrs, R.styleable.BeautifulTextView)
        textViewAttributes.recycle()

        binding.visibility.setOnClickListener {
            changeVisibility()
            onVisibilityChangeListener(binding.visibility, this.isPasswordVisible)
        }
    }


    private fun setAttributes(attributes: TypedArray) {
        text = attributes.getString(R.styleable.PasswordView_text) ?: ""
        hint = attributes.getString(R.styleable.PasswordView_android_hint) ?: ""

        val inputTypeInt = attributes
            .getInt(R.styleable.PasswordView_inputType, 100)
        Log.d("TestPetrov", inputTypeInt.toString())
        inputType =
            PasswordType.values().find { it.code == inputTypeInt } ?: PasswordType.TEXT_PASSWORD

        isPasswordVisible = attributes
            .getBoolean(R.styleable.PasswordView_passwordVisible, false)

        val maxLength = attributes.getInt(
            R.styleable.PasswordView_android_maxLength,
            binding.textView.maxLength
        )

        val backgroundStyleInt = attributes.getInt(R.styleable.BeautifulTextView_backgroundStyle, 0)
        imageColor = attributes.getColor(R.styleable.PasswordView_imageColor, Color.BLACK)

        backgroundStyle = if (backgroundStyleInt == 0)
            BeautifulTextView.BackgroundStyle.LIGHT
        else
            BeautifulTextView.BackgroundStyle.DARK

        binding.textView.maxLength = maxLength
    }


    val textView get() = binding.textView


    var text
        get() = textView.text
        set(value) {
            textView.text = value
        }


    var hint
        get() = textView.hint
        set(value) {
            textView.hint = value
        }


    var inputType: PasswordType = PasswordType.TEXT_PASSWORD
        set(value) {
            field = value
            Log.d("TestPetrov", isPasswordVisible.toString())
            binding.textView.inputType = if (isPasswordVisible)
                value.visibleType
            else
                value.hiddenType
        }


    var isPasswordVisible = false
        set(value) {
            field = value
            binding.textView.inputType = if (value)
                inputType.visibleType
            else
                inputType.hiddenType
        }


    fun changeVisibility() {
        isPasswordVisible = !isPasswordVisible
    }


    var backgroundStyle = BeautifulTextView.BackgroundStyle.LIGHT
        set(value) {
            field = value
            binding.textView.backgroundStyle = value
        }


    @ColorInt
    var imageColor = Color.BLACK
        set(value) {
            field = value
            binding.visibility.imageTintList = ColorStateList(value)
        }


    fun setOnVisibilityChangeListener(listener: (imageButton: ImageButton, isVisible: Boolean) -> Unit) {
        onVisibilityChangeListener = listener
    }


    enum class PasswordType(
        internal val code: Int,
        internal val visibleType: Int,
        internal val hiddenType: Int,
    ) {
        TEXT_PASSWORD(
            100,
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD,
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        ),
        NUMBER_PASSWORD(
            200,
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL,
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        )
    }
}