package com.security.passwordmanager.domain.model.entity

import androidx.annotation.StringRes
import com.security.passwordmanager.domain.R

enum class EntityType(@StringRes val tableTitleRes: Int) {
    All(R.string.all_entries),
    Website(R.string.websites),
    Bank(R.string.bank_cards)
}