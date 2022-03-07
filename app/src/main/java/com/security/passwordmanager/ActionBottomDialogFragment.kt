package com.security.passwordmanager

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.security.passwordmanager.databinding.BottomSheetBinding
import com.security.passwordmanager.settings.SettingsViewModel

open class ActionBottomDialogFragment(protected val activity: AppCompatActivity) : BottomSheetDialogFragment() {

    protected lateinit var settings: SettingsViewModel
    private val headingBuffer = Array(2) { String() }
    private var beautifulDesign = false
    private val viewBuffer = ArrayList<BottomView>()

    private lateinit var binding: BottomSheetBinding

    private var isCreated = false

    override fun getTheme() = R.style.AppBottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = BottomSheetBinding.inflate(inflater, container, false)
        settings = SettingsViewModel.getInstance(activity)
        binding.root.backgroundTintList = ColorStateList.valueOf(settings.backgroundColor)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCreated = true
        if (headingBuffer[0] != "")
            setHeading(headingBuffer[0], headingBuffer[1], beautifulDesign)

        if (viewBuffer.isNotEmpty()) {
            viewBuffer.forEachIndexed { pos, bottomView ->
                bottomView.id = pos
                addView(bottomView)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isCreated = false
    }


    fun setHeading(
        headText: String,
        subtitleText: String? = null,
        beautifulDesign: Boolean = false
    ) {
        val empty = ""

        if (!isCreated) {
            headingBuffer[0] = headText
            headingBuffer[1] = subtitleText ?: empty
            this.beautifulDesign = beautifulDesign
            return
        }

        val heading = binding.root.findViewById<LinearLayout>(R.id.bottom_sheet_heading)
        heading.show()
        binding.headerDivider.show()

        val head = heading.findViewById<TextView>(R.id.text_view_name)
        val subtitle = heading.findViewById<TextView>(R.id.text_view_subtitle)

        head.text = headText
        head.setTextColor(settings.fontColor)

        if (beautifulDesign) {
            head.textSize = 24F
            head.typeface = Typeface.create(
                Typeface.createFromAsset(activity.assets, "fonts/beautiful_font.otf"),
                Typeface.BOLD
            )
        }

        if (subtitleText == empty)
            subtitle.hide()
        else {
            subtitle.text = subtitleText
            subtitle.setTextColor(settings.fontColor)
        }
    }


    fun addView(
        @DrawableRes image: Int,
        @StringRes text: Int,
        listener: View.OnClickListener
    ) {
        val view = BottomView(image, text, activity.resources, listener)

        if (view !in viewBuffer)
            viewBuffer += view
    }

    fun addView(
        @DrawableRes image: Int,
        imageBound: ImageBounds,
        text: String,
        listener: View.OnClickListener
    ) {
        val view = BottomView(image = image, text = text, listener = listener)
        view.imageBound = imageBound

        if (view !in viewBuffer)
            viewBuffer += view
    }

    fun addViews(images: IntArray, strings: IntArray, listener: View.OnClickListener) {
        if (images.size != strings.size)
            return

        for (index in images.indices) {
            addView(images[index], strings[index], listener)
        }
    }


    //использовать только в паре с removeView(view: View)
    protected fun addView(view: View) =
        binding.root.addView(view)

    protected fun removeView(view: View) =
        binding.root.removeView(view)


    @SuppressLint("InflateParams")
    private fun addView(view: BottomView) {

        val child = layoutInflater
            .inflate(view.itemViewId, null, false) as Button

        child.setDrawables(view)
        child.updateColors()

        child.text = view.text
        child.setOnClickListener(view.listener)
        child.id = view.id

        addView(child)
    }


    fun editView(id: Int, @StringRes text: Int) {
        viewBuffer[id].text = activity.getString(text)
    }

    fun getView(id: Int): Button =
        binding.root.findViewById(id)


    private fun TextView.updateColors() {
        setTextColor(settings.fontColor)
        setCompoundDrawableColor(activity.getColor(R.color.raspberry))
    }

    open fun updateColors() {
        binding.root.backgroundTintList = ColorStateList.valueOf(settings.backgroundColor)

        for (id in viewBuffer.indices)
            getView(id).updateColors()
    }


    private fun Button.setDrawables(
        left: Drawable?,
        right: Drawable?,
        top: Drawable? = null,
        bottom: Drawable? = null
    ) =
        setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)


    protected fun Button.setDrawables(view: BottomView? = null) {
        val v = if (view == null) {
            val index = id
            if (index >= viewBuffer.size)
                return

            viewBuffer[index]
        }
        else view

        val drawableImage = context?.let { ContextCompat.getDrawable(it, v.image) }
        drawableImage?.bounds = Rect(0, 0, 0, 0)

        when (v.imageBound) {
            ImageBounds.LEFT -> setDrawables(drawableImage, null)
            ImageBounds.RIGHT -> setDrawables(null, drawableImage)
        }
    }


    fun Button.removeDrawables() =
        setDrawables(null, null)


    private fun TextView.setCompoundDrawableColor(color: Int) =
        TextViewCompat.setCompoundDrawableTintList(this, ColorStateList.valueOf(color))
}






data class BottomView(
        @LayoutRes val itemViewId: Int = R.layout.bottom_sheet_field,
        @DrawableRes val image: Int,
        var text: String,
        val listener: View.OnClickListener
) {
    constructor(
        @DrawableRes image: Int,
        @StringRes text: Int,
        resources: Resources,
        listener: View.OnClickListener
    ) : this(
        image = image,
        text = resources.getString(text),
        listener = listener
    )

    var id = -1
    var imageBound = ImageBounds.LEFT
}

enum class ImageBounds {
    LEFT,
    RIGHT
}








