package com.security.passwordmanager

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.security.passwordmanager.settings.Theme
import com.security.passwordmanager.settings.ThemeBottomDialogFragment

class ActionBottom {

    companion object {

        const val TAG = "ActionBottomDialog"

        fun newInstance(activity: AppCompatActivity) = ActionBottomDialogFragment(activity)

        fun themeInstance(theme: Theme, activity: AppCompatActivity) =
            ThemeBottomDialogFragment(theme, activity)
    }
}

fun BottomSheetDialogFragment.show(fragmentManager: FragmentManager) =
    show(fragmentManager, ActionBottom.TAG)