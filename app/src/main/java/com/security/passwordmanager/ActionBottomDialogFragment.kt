package com.security.passwordmanager

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.security.passwordmanager.settings.SettingsViewModel

class ActionBottomDialogFragment(private val activity: AppCompatActivity) : BottomSheetDialogFragment() {

    private lateinit var rootView : View
    private lateinit var settings : SettingsViewModel
    private val headingBuffer = Array(2) { String() }
    private val viewBuffer = ArrayList<IntArray>()
    private val listenerBuffer = ArrayList<View.OnClickListener>()

    private var isCreated = false

    override fun getTheme() = R.style.AppBottomSheetDialogTheme

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?) : View {

        rootView = inflater.inflate(R.layout.bottom_sheet, container, false)
        settings = SettingsViewModel.getInstance(activity)
        rootView.backgroundTintList = ColorStateList.valueOf(settings.backgroundColor)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCreated = true
        if (headingBuffer[0] != "")
            setHeading(headingBuffer[0], headingBuffer[1])

        if (viewBuffer.isNotEmpty()) {
            for (pos in 0 until viewBuffer.size)
                addView(
                    id = pos,
                    image = viewBuffer[pos][0],
                    text = viewBuffer[pos][1],
                    listener = listenerBuffer[pos]
                )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isCreated = false
    }


    fun setHeading(headText : String, subtitleText : String?) {
        setHeading(headText, subtitleText, false)
    }


    fun setHeading(headText: String, subtitleText: String?, beautifulDesign: Boolean) {
        val empty = ""

        if (!isCreated) {
            headingBuffer[0] = headText
            headingBuffer[1] = subtitleText ?: empty
            return
        }

        val heading = rootView.findViewById<LinearLayout>(R.id.bottom_sheet_heading)
        heading.visibility = View.VISIBLE
        rootView.findViewById<View>(R.id.header_divider).visibility = View.VISIBLE

        val head = heading.findViewById<TextView>(R.id.list_item_text_view_name)
        val subtitle = heading.findViewById<TextView>(R.id.list_item_text_view_subtitle)

        head.text = headText
        head.setTextColor(settings.fontColor)

        if (beautifulDesign) {
            //TODO не работает, переделать
            head.textSize = 70F
            head.typeface = Typeface.createFromAsset(activity.assets, "fonts/beautiful_font.otf")
        }

        if (subtitleText == empty)
            subtitle.visibility = View.GONE
        else {
            subtitle.text = subtitleText
            subtitle.setTextColor(settings.fontColor)
        }
    }


    fun addView(@DrawableRes image : Int, @StringRes text : Int, listener: View.OnClickListener) {
        val array = intArrayOf(image, text)
        if (viewBuffer.inArray(array))
            return
        viewBuffer.add(intArrayOf(image, text))
        listenerBuffer.add(listener)
    }

    @SuppressLint("InflateParams")
    private fun addView(
        id: Int,
        @DrawableRes image: Int,
        @StringRes text: Int,
        listener: View.OnClickListener) {

        val child = layoutInflater
            .inflate(R.layout.bottom_sheet_field, null, false) as Button

        TextViewCompat.setCompoundDrawableTintList(
            child,
            ColorStateList.valueOf(SettingsViewModel.RASPBERRY)
        )

        val drawableImage = context?.let { ContextCompat.getDrawable(it, image) }
        drawableImage?.bounds = Rect(0, 0, 0, 0)
        child.setCompoundDrawablesWithIntrinsicBounds(drawableImage, null, null, null)

        child.setText(text)
        child.setTextColor(settings.fontColor)

        child.setOnClickListener(listener)

        child.id = id

        (rootView as ViewGroup).addView(child)
    }


    fun editView(id : Int, @StringRes text : Int) {
        viewBuffer[id][1] = text
    }

    //проверяет вхождение элемента в список (true - элемент уже есть в списке; false - нет)
    private fun ArrayList<IntArray>.inArray(element: IntArray) : Boolean {
        forEach {
            if (it.equal(element))
                return@inArray true
        }
        return false
    }

    private fun IntArray.equal(otherArray: IntArray) : Boolean {
        if (size != otherArray.size)
            return false

        for (index in 0 until size) {
            if (this[index] != otherArray[index])
                return false
        }
        return true
    }
}