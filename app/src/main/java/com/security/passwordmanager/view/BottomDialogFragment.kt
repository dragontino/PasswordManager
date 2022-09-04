package com.security.passwordmanager.view

import android.content.Context
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.security.passwordmanager.*
import com.security.passwordmanager.databinding.BottomSheetBinding
import com.security.passwordmanager.viewmodel.SettingsViewModel

open class BottomDialogFragment(protected val settingsViewModel: SettingsViewModel)
    : BottomSheetDialogFragment() {

    private val headingBuffer = Array(2) { String() }
    private val viewBuffer = ArrayList<BottomView>()
    private var beautifulDesign = false

    private lateinit var binding: BottomSheetBinding

    companion object {
        const val TAG = "ActionBottomDialog"

//        class Builder(
//            private val context: Context,
//            private val settingsViewModel: SettingsViewModel
//        ) {
//            internal constructor(
//                context: Context,
//                settingsViewModel: SettingsViewModel,
//                fragment: BottomDialogFragment
//            ) : this(context, settingsViewModel) {
//                this.fragment = fragment
//            }
//
//            private var fragment: BottomDialogFragment? = null
//
//            private var headText = ""
//            private var subtitleText: String? = null
//            private var beautifulDesign = false
//
//            private val bottomViewBuffer = ArrayList<BottomView>()
//
//
//            fun setHeading(
//                headText: String,
//                subtitleText: String? = null,
//                beautifulDesign: Boolean = false
//            ): Builder {
//                this.headText = headText
//                this.subtitleText = subtitleText
//                this.beautifulDesign = beautifulDesign
//                return this
//            }
//
//            fun addView(
//                @DrawableRes image: Int,
//                @StringRes text: Int,
//                listener: (View) -> Unit,
//            ) =
//                addView(image, context.getString(text), listener)
//
//
//            fun addView(
//                @DrawableRes image: Int,
//                text: String,
//                listener: (View) -> Unit
//            ): Builder {
//                val view = BottomView(image, text, listener)
//
//                if (view !in bottomViewBuffer)
//                    bottomViewBuffer += view
//
//                return this
//            }
//
//            fun addView(
//                @DrawableRes image: Int,
//                imageBound: ImageBounds,
//                text: String,
//                listener: (View) -> Unit
//            ): Builder {
//                val view = BottomView(image = image, text = text, listener = listener)
//                view.imageBound = imageBound
//
//                if (view !in bottomViewBuffer)
//                    bottomViewBuffer += view
//
//                return this
//            }
//
//            fun addViews(count: Int, init: (Int) -> BottomView): Builder {
//                for (index in 0 until count)
//                    bottomViewBuffer += init(index)
//                return this
//            }
//
//
//            fun build(): BottomDialogFragment {
//                val currentFragment = fragment ?: BottomDialogFragment(settingsViewModel)
//
//                return currentFragment.apply {
//                    headingBuffer[0] = headText
//                    headingBuffer[1] = subtitleText ?: ""
//                    beautifulDesign = this@Builder.beautifulDesign
//
//                    viewBuffer.updateAll(bottomViewBuffer)
//                }
//            }
//        }
    }

    override fun getTheme() = R.style.AppBottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = BottomSheetBinding.inflate(inflater, container, false)
        binding.root.backgroundTintList = ColorStateList(settingsViewModel.backgroundColor)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHeading()
        viewBuffer.forEachIndexed { position, bottomView ->
            bottomView.id = position
            addView(bottomView)
        }
    }



//    val builder get() = Builder(requireContext(), settingsViewModel, this)


    fun setHeading(
        headText: String,
        subtitleText: String? = null,
        beautifulDesign: Boolean = false
    ) {
        headingBuffer[0] = headText
        headingBuffer[1] = subtitleText ?: ""
        this.beautifulDesign = beautifulDesign
    }


    fun addView(
        @DrawableRes image: Int,
        context: Context,
        @StringRes text: Int,
        listener: (View) -> Unit,
    ) =
        addView(image, context.getString(text), listener)


    fun addView(
        @DrawableRes image: Int,
        text: String,
        listener: (View) -> Unit
    ) {
        val view = BottomView(image, text, listener)

        if (view !in viewBuffer)
            viewBuffer += view
    }


    fun addView(
        @DrawableRes image: Int,
        imageBound: ImageBounds,
        text: String,
        listener: (View) -> Unit
    ) {
        val view = BottomView(image = image, text = text, listener = listener)
        view.imageBound = imageBound

        if (view !in viewBuffer)
            viewBuffer += view
    }


    fun addViews(count: Int, init: (Int) -> BottomView) {
        for (index in 0 until count)
            viewBuffer += init(index)
    }




    private fun setHeading() {
        binding.bottomSheetHeading.root.show()
        binding.headerDivider.show()

        binding.bottomSheetHeading.run {
            heading.text = headingBuffer[0]
            heading.setTextColor(settingsViewModel.fontColor)

            if (beautifulDesign && settingsViewModel.baseSettings.isUsingBeautifulFont) {
                heading.textSize = 24F
                heading.setFont("fonts/beautiful_font.otf", Typeface.BOLD)
            } else {
                heading.textSize = 20F
            }

            if (headingBuffer[1].isEmpty())
                subtitle.hide()
            else {
                subtitle.text = headingBuffer[1]
                subtitle.setTextColor(settingsViewModel.fontColor)
            }
        }
    }

    protected fun addViewToBuffer(bottomView: BottomView) {
        viewBuffer += bottomView
    }

    //использовать только в паре с removeView(view: View)
    protected fun addView(view: View) {
        binding.root.addView(view)
    }

    protected fun removeView(view: View) {
        binding.root.removeView(view)
    }

    private fun addView(view: BottomView) {
        val child = layoutInflater
            .inflate(view.itemViewId, null, false) as Button

        child.setDrawables(view)
        child.updateColors()

        child.text = view.text
        child.setOnClickListener {
            view.listener(it)
            dismiss()
        }
        child.id = view.id

        binding.root.addView(child)
    }

    fun editView(id: Int, @StringRes text: Int) {
        viewBuffer[id].text = getString(text)
    }

    fun getView(id: Int): Button =
        binding.root.findViewById(id)

    operator fun get(id: Int) = getView(id)


    private fun TextView.updateColors() {
        setTextColor(settingsViewModel.fontColor)
        setCompoundDrawableColor(context.getColor(R.color.raspberry))
    }

    open fun updateColors() {
        binding.root.backgroundTintList = ColorStateList(settingsViewModel.backgroundColor)

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
        TextViewCompat.setCompoundDrawableTintList(this, ColorStateList(color))


    fun show(fragmentManager: FragmentManager) =
        show(fragmentManager, TAG)
}


data class BottomView(
    @DrawableRes val image: Int,
    var text: String,
    val listener: (View) -> Unit
) {
    constructor(@DrawableRes image: Int, text: String, listener: View.OnClickListener)
            : this(image, text, listener = listener::onClick)

    @LayoutRes val itemViewId = R.layout.bottom_sheet_field
    var id = -1
    var imageBound = ImageBounds.LEFT
}

enum class ImageBounds {
    LEFT,
    RIGHT
}


//fun BottomDialogFragment.edit(block: BottomDialogFragment.Companion.Builder.() -> Unit) {
//    builder.block()
//    builder.build()
//}


//fun buildBottomDialogFragment(
//    context: Context,
//
//    block: (BottomDialogFragment.Companion.Builder.() -> Unit)? = null
//) =
//    BottomDialogFragment.Companion.Builder().apply {
//        block?.invoke(this)
//    }.build()