package com.security.passwordmanager.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.iterator
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.security.passwordmanager.*
import com.security.passwordmanager.data.*
import com.security.passwordmanager.databinding.NewBankCardBinding
import com.security.passwordmanager.databinding.NewWebsiteBinding
import com.security.passwordmanager.view.BottomDialogFragment
import com.security.passwordmanager.view.customviews.BeautifulTextView
import com.security.passwordmanager.viewmodel.DataViewModel
import com.security.passwordmanager.viewmodel.SettingsViewModel
import kotlin.reflect.KMutableProperty0

class DataEditableRecyclerView(
    private val activity: AppCompatActivity,
    private val recyclerView: RecyclerView,
    key: String,
    type: DataType,
): Iterable<View> {

    private val adapter: EditableAdapter

    private val settings = SettingsViewModel.getInstance(activity)
    private val dataViewModel = DataViewModel.getInstance(activity)

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
        Log.d(BottomDialogFragment.TAG, "test")
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

            return when(viewType) {
                DataType.BANK_CARD.number -> {
                    val binding = NewBankCardBinding
                        .inflate(layoutInflater, parent, false)
                    EditableHolder(binding)
                }
                else -> {
                    val binding = NewWebsiteBinding
                        .inflate(layoutInflater, parent, false)
                    EditableHolder(binding)
                }
            }
        }

        override fun onBindViewHolder(holder: EditableHolder, position: Int) {
            val data = getData(position)
            holder.bindData(data, position)
        }

        override fun getItemCount() = accountList.size
    }



    private abstract inner class DataEditableHolder<in T: Data>(viewBinding: ViewBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        protected val bottomDialogFragment = BottomDialogFragment(settings)

        abstract fun bindData(data: T)

        protected abstract fun getDefaultHeadingName(position: Int): String

        protected fun EditText.changeHeading() {
            //true - заблокирует, false - разблокирует
            val blocking = isCursorVisible

            isCursorVisible = !blocking
            isEnabled = !blocking

            // TODO: 06.08.2022 проверить работоспособность
            val full = getDefaultHeadingName(adapterPosition)
            val zero = ""

            if (blocking && text.isBlank())
                txt = full
            else if (!blocking && text.toString() == full)
                txt = zero

            backgroundTintList = if (blocking) {
                setTextColor(settings.darkerGrayColor)
                ColorStateList(settings.darkerGrayColor)
            } else {
                setTextColor(settings.fontColor)
                ColorStateList(settings.headerColor)
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
                    error = activity.getString(R.string.required)
                    return@doAfterTextChanged
                }

                stringField.set(it?.toString() ?: stringField.get())
            }


        protected fun BeautifulTextView.setTextWatcher(
            stringField: KMutableProperty0<String>,
            mustBeNotNull: Boolean = true,
        ) =
            doAfterTextChanged {
            if (mustBeNotNull && it.isEmpty()) {
                error = activity.getString(R.string.required)
                return@doAfterTextChanged
            }
            stringField.set(it)
        }
    }



    private inner class WebsiteEditableHolder(private val websiteBinding: NewWebsiteBinding) :
        DataEditableHolder<Website>(websiteBinding) {

        private var renamingTextRes = R.string.rename_data


        init {
            websiteBinding.editNameOfAccount.setOnClickListener {
                bottomDialogFragment.show(activity.supportFragmentManager)
            }

            websiteBinding.password.setOnVisibilityChangeListener { imageButton, isVisible ->
                if (isVisible) {
                    imageButton.setImageResource(R.drawable.visibility_off)
                    imageButton.contentDescription = activity.getString(R.string.hide_password)
                } else {
                    imageButton.setImageResource(R.drawable.visibility_on)
                    imageButton.contentDescription = activity.getString(R.string.show_password)
                }
            }

            // TODO: 13.06.2022 сделать сохранение видимости пароля
//            binding.password.isPasswordVisible = false
        }

        override fun bindData(data: Website) {
            websiteBinding.run {
                login.txt = data.login
                password.text = data.password
                comment.txt = data.comment

                login.setTextWatcher(data::login)
                password.textView.setTextWatcher(data::password)
                comment.setTextWatcher(data::comment, false)

                nameAccount.txt =
                    if (data.nameAccount.isEmpty())
                        getDefaultHeadingName(adapterPosition)
                    else
                        data.nameAccount

                nameAccount.setOnEnterListener {
                    nameAccount.changeNameStatus(R.string.rename_data)
                }

                val heading = data.nameAccount

                bottomDialogFragment.apply {
                    setHeading(heading, beautifulDesign = true)
                    addView(R.drawable.edit, heading) {
                        val newText =
                            if (bottomDialogFragment[0].text == getString(R.string.rename_data))
                                R.string.cancel_renaming_data
                            else
                                R.string.rename_data

                        nameAccount.changeNameStatus(newText)
                    }
                    addView(R.drawable.copy, activity!!, R.string.copy_info) {
                        dataViewModel.copyData(data)
                    }

                    addView(R.drawable.delete, activity!!, R.string.delete_account) {
                        dataViewModel.deleteData(data)
                        removeData(adapterPosition)

                        if (isEmpty())
                            this@DataEditableRecyclerView.activity.finish()
                    }
                }

//                bottomDialogFragment = createBottomSheet(heading, nameAccount)

                //colors
                root.backgroundTintList =
                    ColorStateList(settings.layoutBackgroundColor)

                for (item in arrayOf(login, password.textView, comment)) {
//                item.setTextColor()
                    item.setBackgroundResource(settings.backgroundRes)
                    //item.backgroundStyle = ...
                }

                editNameOfAccount.imageTintList =
                    ColorStateList(settings.fontColor)
                editNameOfAccount.setBackgroundColor(settings.layoutBackgroundColor)

                password.textView.backgroundStyle = settings.beautifulBackgroundStyle
                password.imageColor = settings.fontColor

                editNameOfAccount.setOnClickListener {
                    bottomDialogFragment.show(activity.supportFragmentManager)
                }
            }
        }


        /**
         * переименовывает / отменяет переименование названия аккаунта / банковской карты и т. д.
         */
        fun EditText.changeNameStatus(@StringRes newRenamingText: Int) {
            changeHeading()
            renamingTextRes = newRenamingText
            bottomDialogFragment.editView(0, renamingTextRes)
        }


        override fun getDefaultHeadingName(position: Int) =
            activity.getString(R.string.account_start, position + 1)
    }



    private inner class EditableHolder(private val itemBinding: ViewBinding)
        : RecyclerView.ViewHolder(itemBinding.root) {

        private lateinit var bottomDialogFragment: BottomDialogFragment

        private lateinit var data: Data
        private var pos = 0

        private var renamingText = R.string.rename_data

        init {
            when (itemBinding) {
                is NewWebsiteBinding -> initNewWebsite(itemBinding)
                is NewBankCardBinding -> initNewBankCard(itemBinding)
            }
        }


        private fun initNewBankCard(binding: NewBankCardBinding) = binding.run {

            editNameBankCard.setOnClickListener {
                bottomDialogFragment.show(activity.supportFragmentManager)
            }

            cardNumber.doAfterTextChanged { text ->
                when (text?.length) {
                    4, 9, 14 -> cardNumber.append(" ")
//                    5, 10, 15 -> cardNumber.deleteLast()
                }
            }

            validityPeriod.doAfterTextChanged { text ->
                when (text?.length) {
                    2 -> {
                        validityPeriod.append("/")

                        val month =
                            text[0].toString().toInt() * 10 + text[1].toString().toInt()
                        if (month !in 1..12)
                            validityPeriod.error = activity.getString(R.string.wrong_month)
                    }
                    3 -> validityPeriod.deleteLast()
                }
            }
        }



        private fun initNewWebsite(binding: NewWebsiteBinding) {

            binding.editNameOfAccount.setOnClickListener {
                bottomDialogFragment.show(activity.supportFragmentManager)
            }

            binding.password.setOnVisibilityChangeListener { imageButton, isVisible ->
                if (isVisible) {
                    imageButton.setImageResource(R.drawable.visibility_off)
                    imageButton.contentDescription = activity.getString(R.string.hide_password)
                } else {
                    imageButton.setImageResource(R.drawable.visibility_on)
                    imageButton.contentDescription = activity.getString(R.string.show_password)
                }
            }

            // TODO: 13.06.2022 сделать сохранение видимости пароля
//            binding.password.isPasswordVisible = false
        }

        fun getDefaultHeadingName() = when (data.type) {
            DataType.WEBSITE -> activity.getString(R.string.account_start, pos + 1)
            DataType.BANK_CARD -> activity.getString(R.string.bank_card_hint, pos + 1)
            // TODO: 13.06.2022 поменять номер позиции для банковской карты
        }


//        fun BeautifulTextView.setTextColor() {
//            textColor = settings.fontColor
//        }


        fun EditText.changeHeading() {
            //true - заблокирует, false - разблокирует
            val blocking = isCursorVisible

            isCursorVisible = !blocking
            isEnabled = !blocking

            val full = getDefaultHeadingName()
            val zero = ""

            if (blocking && text.isBlank())
                txt = full
            else if (!blocking && text.toString() == full)
                txt = zero

            if (blocking && txt != full) when (data) {
                is Website -> (data as Website).nameAccount = txt
                is BankCard -> (data as BankCard).bankCardName = txt
            }
            accountList[pos] = data

            backgroundTintList = if (blocking) {
                setTextColor(settings.darkerGrayColor)
                ColorStateList(settings.darkerGrayColor)
            } else {
                setTextColor(settings.fontColor)
                ColorStateList(settings.headerColor)
            }
        }


        fun bindData(data: Data, position: Int) {
            this.data = data
            this.pos = position

            when (itemBinding) {
                is NewWebsiteBinding -> {
                    if (data !is Website)
                        return

                    bindNewWebsite(itemBinding, data)
                }

                is NewBankCardBinding -> {
                    if (data !is BankCard)
                        return

                    bindNewBankCard(itemBinding, data)
                }
            }
        }


        fun createBottomSheet(heading: String, namingView: EditText): BottomDialogFragment {
            return BottomDialogFragment(settings).apply {
                setHeading(heading, beautifulDesign = true)
                addView(R.drawable.edit, activity!!, renamingText) {
                    val newText =
                        if (bottomDialogFragment[0].text == getString(R.string.rename_data))
                            R.string.cancel_renaming_data
                        else
                            R.string.rename_data

                    namingView.changeNameStatus(newText)
                }
                addView(R.drawable.copy, activity!!, R.string.copy_info) {
                        dataViewModel.copyData(this@EditableHolder.data)
                }
                addView(R.drawable.delete, activity!!, R.string.delete_account) {
                    dataViewModel.deleteData(this@EditableHolder.data)
                    removeData(pos)

                    if (isEmpty())
                        this@DataEditableRecyclerView.activity.finish()
                }
            }
        }


        private fun bindNewWebsite(binding: NewWebsiteBinding, website: Website) = binding.run {
            login.txt = website.login
            password.text = website.password
            comment.txt = website.comment

            login.setTextWatcher(website::login)
            password.textView.setTextWatcher(website::password)
            comment.setTextWatcher(website::comment, false)

            nameAccount.txt =
                if (website.nameAccount.isEmpty())
                    getDefaultHeadingName()
                else
                    website.nameAccount

            nameAccount.setOnEnterListener {
                nameAccount.changeNameStatus(R.string.rename_data)
            }

            val heading = website.nameAccount
            bottomDialogFragment = createBottomSheet(heading, nameAccount)

            //colors
            root.backgroundTintList =
                ColorStateList(settings.layoutBackgroundColor)

            for (item in arrayOf(login, password.textView, comment)) {
//                item.setTextColor()
                item.setBackgroundResource(settings.backgroundRes)
                //item.backgroundStyle = ...
            }

            editNameOfAccount.imageTintList =
                ColorStateList(settings.fontColor)
            editNameOfAccount.setBackgroundColor(settings.layoutBackgroundColor)

            password.textView.backgroundStyle = settings.beautifulBackgroundStyle
            password.imageColor = settings.fontColor

            editNameOfAccount.setOnClickListener {
                bottomDialogFragment.show(activity.supportFragmentManager)
            }
        }


        private fun bindNewBankCard(binding: NewBankCardBinding, bankCard: BankCard) = binding.run {
            cardNumber.txt = bankCard.cardNumber
            cardHolder.txt = bankCard.cardHolder
            validityPeriod.txt = bankCard.validity
            cardCvv.text = bankCard.cvvString
            pinCode.text = bankCard.pinString
            comment.txt = bankCard.comment

            cardNumber.setTextWatcher(bankCard::cardNumber)
            cardHolder.setTextWatcher(bankCard::cardHolder)
            validityPeriod.setTextWatcher(bankCard::validity)
            cardCvv.textView.setTextWatcher(bankCard::cvvString)
            pinCode.textView.setTextWatcher(bankCard::pinString)
            comment.setTextWatcher(bankCard::comment, false)

            nameBankCard.txt =
                if (bankCard.bankCardName.isEmpty())
                    getDefaultHeadingName()
                else
                    bankCard.bankCardName

            nameBankCard.setOnEnterListener {
                nameBankCard.changeNameStatus(R.string.cancel_renaming_data)
            }

            bottomDialogFragment = createBottomSheet(bankCard.bankCardName, nameBankCard)

            //colors
            root.backgroundTintList = ColorStateList(settings.layoutBackgroundColor)

            for (v in arrayOf(cardNumber, cardHolder, validityPeriod,
                cardCvv.textView, pinCode.textView, comment)) {
//                v.setTextColor()
                v.setBackgroundResource(settings.backgroundRes)
                //v.backgroundStyle = ...
            }

            editNameBankCard.imageTintList =
                ColorStateList(settings.fontColor)
            editNameBankCard.setBackgroundColor(settings.layoutBackgroundColor)

            for (view in arrayOf(cardCvv, pinCode)) {
                view.backgroundStyle = settings.beautifulBackgroundStyle
                view.imageColor = settings.fontColor
            }

            bankMainInfo.setTextColor(settings.fontColor)
            bankOptionallyInfo.setTextColor(settings.fontColor)

            //listeners
            editNameBankCard.setOnClickListener {
                bottomDialogFragment.show(activity.supportFragmentManager)
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

        fun BeautifulTextView.setTextWatcher(
            stringField: KMutableProperty0<String>,
            mustBeNotNull: Boolean = true,
        ) = doAfterTextChanged {
            if (mustBeNotNull && it.isEmpty()) {
                error = activity.getString(R.string.required)
                return@doAfterTextChanged
            }

            stringField.set(it)
            accountList[pos] = data
        }

//        fun PasswordFieldBinding.changeVisibility(newVisibility: Boolean = !textView.isPasswordVisible()) {
//            if (newVisibility) {
//                textView.showPassword()
//                visibility.setImageResource(R.drawable.visibility_off)
//                visibility.contentDescription = activity.getString(R.string.hide_password)
//            } else {
//                textView.hidePassword()
//                visibility.setImageResource(R.drawable.visibility_on)
//                visibility.contentDescription = activity.getString(R.string.show_password)
//            }
//        }

        /**
         * переименовывает / отменяет переименование названия аккаунта / банковской карты и т. д.
         */
        fun EditText.changeNameStatus(@StringRes newRenamingText: Int) {
            changeHeading()
            renamingText = newRenamingText
            bottomDialogFragment.editView(0, renamingText)
        }
    }
}