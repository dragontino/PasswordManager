package com.security.passwordmanager.view.adapters

import android.content.Intent
import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.security.passwordmanager.*
import com.security.passwordmanager.activities.WebsiteActivity
import com.security.passwordmanager.data.Data
import com.security.passwordmanager.data.DataType
import com.security.passwordmanager.data.Website
import com.security.passwordmanager.databinding.MoreBankCardBinding
import com.security.passwordmanager.databinding.MoreWebsiteBinding
import com.security.passwordmanager.databinding.WebsiteFieldBinding
import com.security.passwordmanager.view.BottomDialogFragment
import com.security.passwordmanager.viewmodel.DataViewModel
import com.security.passwordmanager.viewmodel.SettingsViewModel

class PasswordDescriptionAdapter(private val activity: AppCompatActivity, private val accountList: List<Data>) :
    RecyclerView.Adapter<PasswordDescriptionAdapter.PasswordDescriptionHolder>() {

    private val settings = SettingsViewModel.getInstance(activity)
    private val dataViewModel = DataViewModel.getInstance(activity)

    override fun getItemViewType(position: Int) =
        accountList[position].type.number

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasswordDescriptionHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val descriptionBinding = when(viewType) {
            DataType.BANK_CARD.number -> MoreBankCardBinding
                .inflate(layoutInflater, parent, false)

            else -> MoreWebsiteBinding
                .inflate(layoutInflater, parent, false)
        }

        return PasswordDescriptionHolder(descriptionBinding)
    }

    override fun onBindViewHolder(holder: PasswordDescriptionHolder, position: Int) {
        val account = accountList[position]
        holder.bind(account, position)
    }

    override fun getItemCount() = accountList.size




    inner class PasswordDescriptionHolder(private val descriptionBinding: ViewBinding) :
        RecyclerView.ViewHolder(descriptionBinding.root) {

        private lateinit var data: Data
        private var pos = 0
        private var isPasswordVisible = false

        fun TextView.setTextColor() =
            setTextColor(settings.fontColor)

        private fun ImageButton.setOnCopyListener(text: String) {
            setOnClickListener { dataViewModel.copyText(text) }
            setBackgroundColor(settings.layoutBackgroundColor)
            imageTintList = ColorStateList(settings.fontColor)
        }

        private fun WebsiteFieldBinding.setText(text: String) {
            textView.text = text
        }

        init {
            when (descriptionBinding) {
                is MoreWebsiteBinding -> {
                    descriptionBinding.buttonOpenUrl.setOnClickListener {
                        openUrl()
                    }

                    descriptionBinding.password.buttonVisibility.show()
                    descriptionBinding.password.buttonVisibility.setOnClickListener {
                        updatePasswordView()
                    }

                    updatePasswordView(isPasswordVisible)
                }
                is MoreBankCardBinding -> {
                    //todo доделать bankCard binding
                }
            }
        }

        fun bind(data: Data, position: Int) {
            this.data = data
            this.pos = position

            when (descriptionBinding) {
                is MoreWebsiteBinding -> {
                    if (data !is Website) return

                    descriptionBinding.run {
                        login.setText(data.login)
                        password.setText(data.password)
                        comment.setText(data.comment)

                        if (data.nameAccount.isEmpty())
                            nameAccount.hide()
                        else
                            nameAccount.txt = data.nameAccount

                        if (data.comment.isEmpty()) {
                            comment.textView.hide()
                            comment.fieldItemButtonCopy.hide()
                            commentHead.hide()
                        }

                        //colors
                        root.backgroundTintList =
                            ColorStateList(settings.layoutBackgroundColor)

                        for (item in arrayOf(login, password, comment)) {
                            item.textView.setBackgroundColor(settings.layoutBackgroundColor)
                            item.textView.setTextColor()
                        }

                        login.fieldItemButtonCopy.setOnCopyListener(data.login)
                        password.fieldItemButtonCopy.setOnCopyListener(data.password)
                        comment.fieldItemButtonCopy.setOnCopyListener(data.comment)

                        loginHead.setTextColor()
                        passwordHead.setTextColor()
                        commentHead.setTextColor()

                        nameAccount.setBackgroundColor(settings.layoutBackgroundColor)

                        password.buttonVisibility.setBackgroundColor(settings.layoutBackgroundColor)

                        password.buttonVisibility.imageTintList =
                            ColorStateList(settings.fontColor)

                        buttonOpenUrl.setBackgroundResource(settings.buttonRes)

                        root.setOnClickListener {
                            if (settings.baseSettings.isUsingBottomView)
                                showBottomView()
                        }
                    }
                }
            }
        }

        // TODO: 20.08.2022 переделать
        private fun showBottomView() {
            val bottomDialogFragment = when(descriptionBinding) {
                is MoreWebsiteBinding -> {
                    val heading = if (descriptionBinding.nameAccount.isEmpty()) {
                        (data as Website).login
                    } else
                        descriptionBinding.nameAccount.text.toString()

                    BottomDialogFragment(settings).apply {
                        setHeading(heading, beautifulDesign = true)
                        addView(R.drawable.edit, this@PasswordDescriptionAdapter.activity, R.string.edit) {
                            this@PasswordDescriptionAdapter.activity.startActivity(
                                WebsiteActivity
                                    .getIntent(activity, (data as Website).address, pos)
                            )
                        }
                    }
                }
                else -> {
                    // TODO: 10.05.2022 сделать для Bank Card
                    descriptionBinding as MoreBankCardBinding
//                    val heading = descriptionBinding.cardNumber
                    BottomDialogFragment(settings)
                }
            }

            bottomDialogFragment.show(activity.supportFragmentManager)
        }

        private fun updatePasswordView() {
            isPasswordVisible = !isPasswordVisible
            updatePasswordView(isPasswordVisible)
        }

        private fun updatePasswordView(visibility: Boolean) =
            (descriptionBinding as MoreWebsiteBinding).apply {
                if (visibility) {
                    password.textView.inputType = InputType.TYPE_CLASS_TEXT
                    password.buttonVisibility.setImageResource(R.drawable.visibility_off)
                    password.buttonVisibility.contentDescription =
                        activity.getString(R.string.hide_password)
                } else {
                    password.textView.inputType = 129
                    password.buttonVisibility.setImageResource(R.drawable.visibility_on)
                    password.buttonVisibility.contentDescription =
                        activity.getString(R.string.show_password)
                }
            }

        private fun openUrl() {
            val address = (data as Website).address.createUrlString()

            if (URLUtil.isValidUrl(address)) {
                val intent = Intent(Intent.ACTION_VIEW, address.toUri())
                activity.startActivity(intent)
            } else
                showToast(activity, "Неправильный адрес!")
        }


        private fun String.createUrlString() = when {
            "www." in this -> "https://$this"
            "https://www." in this || "http://www." in this -> this
            else -> "https://www.$this"
        }
    }
}