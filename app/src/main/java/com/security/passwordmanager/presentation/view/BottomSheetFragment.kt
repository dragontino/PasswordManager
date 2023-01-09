package com.security.passwordmanager.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.security.passwordmanager.R
import com.security.passwordmanager.presentation.view.navigation.BottomSheetContent
import com.security.passwordmanager.presentation.view.navigation.Header
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme

class BottomSheetFragment(
    private val header: Header = Header(),
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
        return ComposeView(inflater.context).apply {
            setContent {
                PasswordManagerTheme {
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

    fun show(fragmentManager: FragmentManager) = show(fragmentManager, TAG)
}