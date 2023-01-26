package com.security.passwordmanager.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.security.passwordmanager.PasswordManagerApplication
import com.security.passwordmanager.R
import com.security.passwordmanager.getActivity
import com.security.passwordmanager.presentation.view.navigation.BottomSheetContent
import com.security.passwordmanager.presentation.view.navigation.Header
import com.security.passwordmanager.presentation.view.navigation.HeadingInterface
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme
import com.security.passwordmanager.presentation.viewmodel.SettingsViewModel

class BottomSheetFragment(
    private val header: HeadingInterface? = null,
    private val bodyContent: @Composable ColumnScope.(BottomSheetFragment) -> Unit
) : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "BottomSheetComposeFragment"
    }

    constructor(
        title: String = "",
        subtitle: String = "",
        beautifulDesign: Boolean = false,
        body: @Composable ColumnScope.(BottomSheetFragment) -> Unit,
    ) : this(Header(title, subtitle, beautifulDesign), body)

    override fun getTheme() = R.style.AppBottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        val settingsViewModel by viewModels<SettingsViewModel> {
            (inflater.context.getActivity()?.application as PasswordManagerApplication).viewModelFactory
        }

        return ComposeView(inflater.context).apply {
            setContent {
                PasswordManagerTheme(
                    settings = settingsViewModel.settings,
                    times = settingsViewModel.times
                ) {
                    if (header == null) {
                        Column {
                            this.bodyContent(this@BottomSheetFragment)
                        }
                    } else {
                        BottomSheetContent(
                            header = header,
                            modifier = Modifier
                        ) {
                            this.bodyContent(this@BottomSheetFragment)
                        }
                    }
                }
            }
        }
    }

    fun show(fragmentManager: FragmentManager) = show(fragmentManager, TAG)
}