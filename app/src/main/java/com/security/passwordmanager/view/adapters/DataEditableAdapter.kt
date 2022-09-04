package com.security.passwordmanager.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.security.passwordmanager.ColorStateList
import com.security.passwordmanager.R
import com.security.passwordmanager.data.Data
import com.security.passwordmanager.txt
import com.security.passwordmanager.view.BottomDialogFragment
import com.security.passwordmanager.view.customviews.BeautifulTextView
import com.security.passwordmanager.viewmodel.SettingsViewModel
import kotlin.reflect.KMutableProperty0

abstract class DataEditableAdapter<H: DataEditableHolder<D, VB>, D: Data, VB: ViewBinding>(
    protected val context: Context,
    protected val dataList: MutableList<D>,
    protected val settingsViewModel: SettingsViewModel
) : RecyclerView.Adapter<H>(), Iterable<Data> {

    abstract fun createViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        attachToRoot: Boolean
    ): H

    override fun getItemViewType(position: Int): Int =
        position

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): H {
        val inflater = LayoutInflater.from(parent.context)
        return createViewHolder(inflater, parent, false).apply {
            saveData = { data ->
                dataList[viewType] = data
            }
        }
    }

    override fun onBindViewHolder(holder: H, position: Int) {
        val data = this[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = dataList.size

    override fun iterator() = dataList.iterator()

    fun addData(data: D) {
        dataList += data
        notifyItemInserted(itemCount - 1)
    }

    fun removeData(position: Int) {
        dataList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getData(position: Int) = dataList[position]

    operator fun get(position: Int) = getData(position)

    fun isEmptyList() = dataList.isEmpty()


}

open class DataEditableHolder<D: Data, VB: ViewBinding>(
    protected val context: Context,
    protected val itemBinding: VB,
    protected val settingsViewModel: SettingsViewModel
) : RecyclerView.ViewHolder(itemBinding.root) {

    protected lateinit var bottomFragment: BottomDialogFragment

    @StringRes
    private var renamingTextRes = R.string.rename_data

    protected lateinit var data: D


    var saveData: (D) -> Unit = {}


    open fun bind(data: D) {
        this.data = data
    }

    protected open val defaultHeadingName: String = ""

    protected fun showBottomFragment() =
        bottomFragment.show(bottomFragment.parentFragmentManager)


    private fun changeHeadingAt(editText: EditText) = editText.run {
        //true - заблокирует, false - разблокирует
        val blocking = isCursorVisible

        isCursorVisible = !blocking
        isEnabled = !blocking

        // TODO: 06.08.2022 проверить работоспособность
        val full = defaultHeadingName
        val zero = ""

        if (blocking && text.isBlank())
            txt = full
        else if (!blocking && text.toString() == full)
            txt = zero
    }


    private fun repaintHeadingAt(editText: EditText) {
        editText.run {
            val isBlocked = !isCursorVisible
            backgroundTintList = if (isBlocked) {
                setTextColor(settingsViewModel.darkerGrayColor)
                ColorStateList(settingsViewModel.darkerGrayColor)
            } else {
                setTextColor(settingsViewModel.fontColor)
                ColorStateList(settingsViewModel.headerColor)
            }
        }
    }


    /**
     * param stringField: property, where new string will be right down
     */
    protected fun EditText.setTextWatcher(
        stringField: KMutableProperty0<String>,
        mustBeNotNull: Boolean = true
    ) =
        doAfterTextChanged {
            if (mustBeNotNull && it?.isEmpty() == true) {
                error = context.getString(R.string.required)
                return@doAfterTextChanged
            }

            stringField.set(it?.toString() ?: stringField.get())
            saveData(data)
        }


    // TODO: 20.08.2022 удалить
    fun BeautifulTextView.setTextWatcher(
        stringField: KMutableProperty0<String>,
        mustBeNotNull: Boolean = true,
    ) = doAfterTextChanged {
        if (mustBeNotNull && it.isEmpty()) {
            error = this@DataEditableHolder.context.getString(R.string.required)
            return@doAfterTextChanged
        }

        stringField.set(it)
    }



    /**
     * переименовывает / отменяет переименование названия аккаунта / банковской карты и т. д.
     */
    fun EditText.changeNameStatus(@StringRes newRenamingText: Int) {
        changeHeadingAt(this)
        repaintHeadingAt(this)
        renamingTextRes = newRenamingText
        bottomFragment.editView(0, renamingTextRes)
    }
}