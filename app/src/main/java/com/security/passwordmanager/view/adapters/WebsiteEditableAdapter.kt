package com.security.passwordmanager.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.security.passwordmanager.ColorStateList
import com.security.passwordmanager.R
import com.security.passwordmanager.data.Website
import com.security.passwordmanager.databinding.NewWebsiteBinding
import com.security.passwordmanager.setOnEnterListener
import com.security.passwordmanager.txt
import com.security.passwordmanager.viewmodel.SettingsViewModel

class WebsiteEditableAdapter(
    context: Context,
    dataList: MutableList<Website>,
    settingsViewModel: SettingsViewModel
) : DataEditableAdapter<WebsiteEditableHolder, Website, NewWebsiteBinding>
    (context, dataList, settingsViewModel) {

    override fun createViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        attachToRoot: Boolean
    ): WebsiteEditableHolder {
        val binding = NewWebsiteBinding.inflate(inflater, parent, attachToRoot)
        return WebsiteEditableHolder(context, binding, settingsViewModel)
    }
}


class WebsiteEditableHolder(
    context: Context,
    itemBinding: NewWebsiteBinding,
    settingsViewModel: SettingsViewModel
) : DataEditableHolder<Website, NewWebsiteBinding>(context, itemBinding, settingsViewModel) {
    init {
        itemBinding.editNameOfAccount.setOnClickListener { showBottomFragment() }

        itemBinding.password.setOnVisibilityChangeListener { imageButton, isVisible ->
            if (isVisible) {
                imageButton.setImageResource(R.drawable.visibility_off)
                imageButton.contentDescription = context.getString(R.string.hide_password)
            } else {
                imageButton.setImageResource(R.drawable.visibility_on)
                imageButton.contentDescription = context.getString(R.string.show_password)
            }
        }



//    TODO: 13.06.2022 сделать сохранение видимости пароля
//     binding.password.isPasswordVisible = false
    }


    // TODO: 20.08.2022 проверить корректность работы
    override val defaultHeadingName: String
        get() = context.getString(R.string.account_start, adapterPosition + 1)


    override fun bind(data: Website) {
        super.bind(data)
        itemBinding.run {
            login.txt = data.login
            password.text = data.password
            comment.txt = data.comment

            login.setTextWatcher(data::login)
            password.textView.setTextWatcher(data::password)
            comment.setTextWatcher(data::comment, false)

            nameAccount.txt =
                if (data.nameAccount.isEmpty()) defaultHeadingName
                else data.nameAccount

            nameAccount.setOnEnterListener {
                nameAccount.changeNameStatus(R.string.rename_data)
            }

            val heading = data.nameAccount
//            bottomFragment = createBottomSheet(heading, nameAccount)

            //colors
            root.backgroundTintList =
                ColorStateList(settingsViewModel.layoutBackgroundColor)

            for (item in arrayOf(login, password.textView, comment)) {
//                item.setTextColor()
                item.setBackgroundResource(settingsViewModel.backgroundRes)
                //item.backgroundStyle = ...
            }

            editNameOfAccount.imageTintList =
                ColorStateList(settingsViewModel.fontColor)
            editNameOfAccount.setBackgroundColor(settingsViewModel.layoutBackgroundColor)

            password.textView.backgroundStyle = settingsViewModel.beautifulBackgroundStyle
            password.imageColor = settingsViewModel.fontColor

            editNameOfAccount.setOnClickListener { showBottomFragment() }
        }
    }
}