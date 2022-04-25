package com.security.passwordmanager.ui

import android.content.res.ColorStateList
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.iterator
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.security.passwordmanager.*
import com.security.passwordmanager.data.*
import com.security.passwordmanager.databinding.NewAccountBinding
import com.security.passwordmanager.databinding.NewBankCardBinding
import com.security.passwordmanager.settings.SettingsViewModel
import kotlin.reflect.KMutableProperty0

class DataEditableRecyclerView(
    private val activity: AppCompatActivity,
    private val recyclerView: RecyclerView,
    key: String,
    type: DataType,
): Iterable<View> {

    private val adapter: EditableAdapter

    private val settings = SettingsViewModel.getInstance(activity)
    private val dataViewModel = ViewModelProvider(activity)[DataViewModel::class.java]

    private val accountList = dataViewModel.getAccountList(key, type) as ArrayList<Data>

    //стартовое количество элементов
    val startCount = accountList.size


    override fun iterator() = recyclerView.iterator()


    init {
        if (isEmpty()) {
            accountList.add(
                if (type == DataType.WEBSITE) Website()
                else BankCard()
            )
        }

        adapter = EditableAdapter()
        recyclerView.adapter = adapter
    }

    fun isEmpty() = accountList.isEmpty()

    // FIXME: 07.03.2022 не работает scroll
    fun scrollToPosition(position : Int) = recyclerView.post {
        Log.d(ActionBottom.TAG, "test")
        recyclerView.smoothScrollToPosition(position)
    }

    private fun scrollToEnd() = scrollToPosition(itemCount - 1)

    operator fun get(index : Int): View = recyclerView.getChildAt(index)

//    private val currentPosition : Int get() {
//        val position = layoutManager.findFirstVisibleItemPosition()
//
//        return if (position == -1) 0
//        else position
//    }

    fun addData(data: Data) {
        accountList.add(data)
        adapter.notifyItemInserted(itemCount - 1)
        scrollToEnd()
    }

    fun removeData(position: Int) {
        accountList.removeAt(position)
        adapter.notifyItemRemoved(position)
    }

    fun getData(position: Int) = accountList[position]

    fun forData(action: (Data) -> Unit) = accountList.forEach(action)

    //текущее количество элементов
    private val itemCount get() = accountList.size



    private inner class EditableAdapter: RecyclerView.Adapter<EditableHolder>() {

        override fun getItemViewType(position: Int) =
            getData(position).type.number

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditableHolder {
            val layoutInflater = LayoutInflater.from(activity)

            val binding = when(viewType) {
                DataType.BANK_CARD.number -> NewBankCardBinding
                    .inflate(layoutInflater, parent, false)

                else -> NewAccountBinding
                    .inflate(layoutInflater, parent, false)
            }

            return EditableHolder(binding)
        }

        override fun onBindViewHolder(holder: EditableHolder, position: Int) {
            val data = getData(position)
            holder.bindData(data, position)
        }

        override fun getItemCount() = accountList.size
    }



    private inner class EditableHolder(private val itemBinding: ViewBinding)
        : RecyclerView.ViewHolder(itemBinding.root) {

        private val bottomDialogFragment = ActionBottom.newInstance(activity)

        private lateinit var data: Data
        private var pos = 0

        private var isPasswordVisible = false
        private var renamingText = R.string.rename_account

        init {
            when (itemBinding) {
                is NewAccountBinding -> {
                    itemBinding.editNameOfAccount.setOnClickListener {
                        bottomDialogFragment.show(activity.supportFragmentManager)
                    }

                    itemBinding.passwordVisibility.setOnClickListener {
                        updatePasswordView()
                    }

                    updatePasswordView(isPasswordVisible)
                }
                is NewBankCardBinding -> {
                    // TODO: 07.03.2022 сделать bank card binding
                }
            }
        }

        val nameAccountStart get() =
            activity.getString(R.string.account_start, pos + 1)

        fun TextView.setTextColor() =
            setTextColor(settings.fontColor)


        fun EditText.changeHeading() {
            //true - заблокирует, false - разблокирует
            val blocking = isCursorVisible

            isCursorVisible = !blocking
            isEnabled = !blocking

            val full = nameAccountStart
            val zero = ""

            if (blocking && text.isBlank())
                txt = full
            else if (!blocking && text.toString() == full)
                txt = zero

            if (blocking && text.toString() != full) {
                (data as Website).nameAccount = text.toString()
                accountList[pos] = data
            }

            backgroundTintList = if (blocking) {
                setTextColor(settings.darkerGrayColor)
                ColorStateList.valueOf(settings.darkerGrayColor)
            } else {
                setTextColor(settings.fontColor)
                ColorStateList.valueOf(settings.headerColor)
            }
        }


        fun bindData(data: Data, position: Int) {
            this.data = data
            this.pos = position

            when (itemBinding) {
                is NewAccountBinding -> {
                    if (data !is Website)
                        return

                    itemBinding.run {
                        login.txt = data.login
                        password.txt = data.password
                        comment.txt = data.comment

                        login.setTextWatcher(data::login)
                        password.setTextWatcher(data::password)
                        comment.setTextWatcher(data::comment, false)

                        nameAccount.txt =
                            if (data.nameAccount.isEmpty())
                                nameAccountStart
                            else
                                data.nameAccount

                        nameAccount.setOnKeyListener { _, keyCode, event ->
                            if (event.action == KeyEvent.ACTION_DOWN &&
                                keyCode == KeyEvent.KEYCODE_ENTER
                            ) {
                                data.nameAccount = nameAccount.text.toString()
                                nameAccount.changeHeading()
                                renamingText = R.string.rename_account
                                bottomDialogFragment.editView(0, renamingText)
                                return@setOnKeyListener true
                            }
                            false
                        }

                        createBottomSheet()

                        //colors
                        root.backgroundTintList =
                            ColorStateList.valueOf(settings.layoutBackgroundColor)

                        for (item in arrayOf(login, password, comment)) {
                            item.setTextColor()
                            item.setBackgroundResource(settings.backgroundRes)
                        }

                        editNameOfAccount.imageTintList =
                            ColorStateList.valueOf(settings.fontColor)
                        editNameOfAccount.setBackgroundColor(settings.layoutBackgroundColor)

                        passwordVisibility.setBackgroundColor(settings.backgroundColor)
                        passwordVisibility.imageTintList = ColorStateList.valueOf(settings.fontColor)

                        editNameOfAccount.setOnClickListener {
                            bottomDialogFragment.show(activity.supportFragmentManager)
                        }
                    }
                }

                is NewBankCardBinding -> {
                    if (data !is BankCard)
                        return
                }
            }
        }

        /**
         * param stringField: property, where new string will be right down
         */
        fun EditText.setTextWatcher(stringField: KMutableProperty0<String>, mustBeNotNull: Boolean = true) =
            doAfterTextChanged {
                if (mustBeNotNull && it?.isEmpty() == true) {
                    error = activity.getString(R.string.required)
                    return@doAfterTextChanged
                }

                stringField.set(it?.toString() ?: stringField.get())
                accountList[pos] = data
            }


        fun updatePasswordView() {
            isPasswordVisible = !isPasswordVisible
            updatePasswordView(isPasswordVisible)
        }

        fun updatePasswordView(visibility: Boolean) = (itemBinding as NewAccountBinding).run {
            if (visibility) {
                password.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                passwordVisibility.setImageResource(R.drawable.visibility_off)
                passwordVisibility.contentDescription = activity.getString(R.string.hide_password)
            } else {
                password.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                passwordVisibility.setImageResource(R.drawable.visibility_on)
                passwordVisibility.contentDescription = activity.getString(R.string.show_password)
            }
        }


        // TODO: 07.03.2022 сделать для bank card
        fun createBottomSheet() {
            val heading = (data as Website).nameAccount
            itemBinding as NewAccountBinding

            bottomDialogFragment.setHeading(heading, null, true)

            bottomDialogFragment.addView(R.drawable.edit, renamingText) {
                itemBinding.nameAccount.changeHeading()

                renamingText = if (itemBinding.nameAccount.isCursorVisible)
                    R.string.cancel_renaming_account
                else
                    R.string.rename_account

                bottomDialogFragment.editView(0, renamingText)
                bottomDialogFragment.dismiss()
            }

            bottomDialogFragment.addView(R.drawable.copy, R.string.copy_info) {
                dataViewModel.copyData(data)
                bottomDialogFragment.dismiss()
            }

            bottomDialogFragment.addView(R.drawable.delete, R.string.delete_account) {
                dataViewModel.deleteData(data)
                removeData(pos)
                bottomDialogFragment.dismiss()

                if (isEmpty())
                    activity.finish()
            }
        }
    }
}