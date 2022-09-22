package com.security.passwordmanager.view.customviews

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.text.InputFilter
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DrawableRes
import androidx.annotation.GravityInt
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doBeforeTextChanged
import androidx.core.widget.doOnTextChanged
import com.security.passwordmanager.*
import com.security.passwordmanager.databinding.BeautifulViewBinding

class BeautifulTextView(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    constructor(context: Context) : this(context, null)

    private val binding: BeautifulViewBinding
    private var isTouched = false

    init {
        val root = inflate(context, R.layout.beautiful_view, this)
        binding = BeautifulViewBinding.bind(root)
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.BeautifulTextView)
        setAttributes(attributes)
        attributes.recycle()

        binding.editText.setOnClickListener {
            isTouched = !isTouched
            when {
                binding.editText.text.isNotEmpty() -> return@setOnClickListener
                isTouched -> {
                    binding.editText.hint = ""
                    binding.hintView.show()
                    showKeyboard()
                }
                else -> {
                    binding.editText.hint = hint
                    binding.hintView.hide()
                }
            }
        }
    }

    private fun setAttributes(attributes: TypedArray) {
//        val text = attributes.getString(R.styleable.BeautifulTextView_android_text) ?: ""
        val hint = attributes.getString(R.styleable.BeautifulTextView_hint) ?: ""
        val textColor = attributes.getColor(R.styleable.BeautifulTextView_textColor, Color.BLACK)
        val hintColor = attributes.getColor(
            R.styleable.BeautifulTextView_hintColor,
            context.getColor(android.R.color.darker_gray)
        )
        val viewMargin =
            attributes.getDimensionPixelSize(R.styleable.BeautifulTextView_viewMargin, -1)
        val background = attributes.getInt(R.styleable.BeautifulTextView_backgroundStyle, 0)
        val textSize = attributes.getDimension(R.styleable.BeautifulTextView_textSize, 16f)
        val hintSize = attributes.getDimension(R.styleable.BeautifulTextView_hintSize, 11f)

        val textGravity = attributes.getInt(
            R.styleable.BeautifulTextView_textGravity,
            Gravity.CENTER_VERTICAL or Gravity.START
        )

//        val isSelectable = attributes
//            .getBoolean(R.styleable.BeautifulTextView_android_textIsSelectable, false)
//
//        val inputType = attributes
//            .getInt(R.styleable.BeautifulTextView_android_inputType, 0)


        binding.run {
            editText.txt = text
            hintView.text = hint
            editText.hint = hint
            editText.setTextColor(textColor)
            editText.setHintTextColor(hintColor)

            if (viewMargin > 0) root.setLayoutMargin(viewMargin)
            backgroundStyle =
                if (background == 0) BackgroundStyle.LIGHT
                else BackgroundStyle.DARK
            editText.setBackgroundResource(backgroundStyle.resId)


            if (text.isNotEmpty() && hint.isNotEmpty()) {
                hintView.show()
                hintBackgroundView.show()
            } else {
                hintView.hide()
                hintBackgroundView.hide()
            }

            editText.textSize = textSize
            hintView.textSize = hintSize

            hintView.measure(0, 0)

            hintBackgroundView.updateLayoutParams { width = hintView.measuredWidth }

            editText.gravity = textGravity

//            textIsSelectable = isSelectable
//            editText.setTextIsSelectable(isSelectable)

            editText.inputType = inputType
        }
    }


    private fun View.setLayoutMargin(margin: Int) {
        (layoutParams as MarginLayoutParams).apply {
            marginStart = margin
            marginEnd = margin
            bottomMargin = margin
            topMargin = margin - 4
        }
    }


    private fun showKeyboard() {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(binding.editText, 0)
    }


    private fun hideKeyboard() {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    }


    var text
        get() = binding.editText.text.toString()
        set(value) {
            binding.editText.txt = value

            if (value.isNotEmpty())
                binding.hintView.show()
            else
                binding.hintView.hide()
        }


    var hint
        get() = binding.hintView.text.toString()
        set(value) {
            binding.editText.hint = value
            binding.hintView.text = value
        }


    fun setHint(@StringRes stringRes: Int) {
        hint = context.getString(stringRes)
    }


    var textColor
        get() = binding.editText.currentTextColor
        set(value) = binding.editText.setTextColor(context.getColor(value))


    var hintColor
        get() = binding.editText.currentHintTextColor
        set(value) = binding.editText.setHintTextColor(context.getColor(value))


    var textSize
        get() = binding.editText.textSize
        set(value) {
            binding.editText.textSize = value
        }


    var hintSize
        get() = binding.hintView.textSize
        set(value) {
            binding.hintView.textSize = value
        }


    var backgroundStyle: BackgroundStyle = BackgroundStyle.LIGHT
        set(value) {
            field = value
            binding.editText.setBackgroundResource(value.resId)
            val backgroundColor = when (value) {
                BackgroundStyle.LIGHT -> Color.WHITE
                BackgroundStyle.DARK -> context.getColor(R.color.background_dark)
            }
            binding.hintBackgroundView.setBackgroundColor(backgroundColor)
        }


    var maxLength: Int
        get() {
            val filter = binding.editText.filters.findLast {
                it is InputFilter.LengthFilter
            }
            return if (filter == null) 30
            else (filter as InputFilter.LengthFilter).max
        }
        set(value) {
            binding.editText.filters += arrayOf(InputFilter.LengthFilter(value))
        }


    @GravityInt
    var textGravity = Gravity.CENTER_VERTICAL or Gravity.START
        set(value) {
            field = value
            binding.editText.gravity = textGravity
        }

    var textIsSelectable = false
        set(value) {
            field = value
            binding.editText.setTextIsSelectable(value)
        }


    override fun getBackground() = context.findDrawableById(backgroundStyle.resId)


    var inputType
        get() = binding.editText.inputType
        set(value) {
            binding.editText.inputType = value
        }



    fun doBeforeTextChanged(
        action: (
            text: String,
            start: Int,
            count: Int,
            after: Int
        ) -> Unit
    ) = binding.editText.doBeforeTextChanged { text, start, count, after ->
        if (text != null)
            action(text.toString(), start, count, after)
    }

    fun doOnTextChanged(
        action: (
            text: String,
            start: Int,
            count: Int,
            after: Int
        ) -> Unit
    ) = binding.editText.doOnTextChanged { text, start, count, after ->
        if (text != null)
            action(text.toString(), start, count, after)
    }

    fun doAfterTextChanged(action: (text: String) -> Unit) =
        binding.editText.doAfterTextChanged {
            if (it != null) action(it.toString())
        }


    var error: String
        get() = binding.editText.error.toString()
        set(value) {
            binding.editText.error = value
        }



    enum class BackgroundStyle(@DrawableRes internal val resId: Int) {
        LIGHT(R.drawable.text_view_style),
        DARK(R.drawable.text_view_dark_style)
    }
}