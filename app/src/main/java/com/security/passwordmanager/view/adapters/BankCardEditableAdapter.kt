package com.security.passwordmanager.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import com.security.passwordmanager.R
import com.security.passwordmanager.data.BankCard
import com.security.passwordmanager.databinding.NewBankCardBinding
import com.security.passwordmanager.deleteLast
import com.security.passwordmanager.viewmodel.SettingsViewModel

class BankCardEditableAdapter(
    context: Context,
    dataList: MutableList<BankCard>,
    settingsViewModel: SettingsViewModel
) : DataEditableAdapter<BankCardEditableHolder, BankCard, NewBankCardBinding>(context, dataList, settingsViewModel) {

    override fun createViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        attachToRoot: Boolean
    ): BankCardEditableHolder {
        val binding = NewBankCardBinding.inflate(inflater, parent, attachToRoot)
        return BankCardEditableHolder(context, binding, settingsViewModel)
    }
}


class BankCardEditableHolder(
    context: Context,
    itemBinding: NewBankCardBinding,
    settingsViewModel: SettingsViewModel
) : DataEditableHolder<BankCard, NewBankCardBinding>(context, itemBinding, settingsViewModel) {

    init {
        itemBinding.run {
            editNameBankCard.setOnClickListener { showBottomFragment() }

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
                            validityPeriod.error = context.getString(R.string.wrong_month)
                    }
                    3 -> validityPeriod.deleteLast()
                }
            }
        }
    }

    override val defaultHeadingName: String
        get() = context.getString(R.string.bank_card_hint, adapterPosition + 1)
}