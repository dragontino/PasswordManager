package com.security.passwordmanager.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.AnnotatedString
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.security.passwordmanager.PasswordManagerApplication
import com.security.passwordmanager.R
import com.security.passwordmanager.presentation.view.navigation.AnnotatedHeader
import com.security.passwordmanager.presentation.view.navigation.BottomSheetContent
import com.security.passwordmanager.presentation.view.navigation.Header
import com.security.passwordmanager.presentation.view.navigation.HeadingInterface
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme
import com.security.passwordmanager.presentation.viewmodel.SettingsViewModel

class BottomSheetFragment(
    private val state: BottomSheetState = BottomSheetState()
) : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "BottomSheetComposeFragment"
    }


    private val settingsViewModel by viewModels<SettingsViewModel> {
        (activity?.application as PasswordManagerApplication).viewModelFactory
    }


    constructor(
        header: HeadingInterface = Header(),
        content: @Composable (ColumnScope.(BottomSheetFragment) -> Unit) = {}
    ) : this(BottomSheetState(header, content))


    constructor(
        title: String = "",
        subtitle: String = "",
        beautifulDesign: Boolean = false,
        body: @Composable (ColumnScope.(BottomSheetFragment) -> Unit),
    ) : this(Header(title, subtitle, beautifulDesign), body)


    constructor(
        title: AnnotatedString = AnnotatedString(""),
        subtitle: AnnotatedString = AnnotatedString(""),
        beautifulDesign: Boolean = false,
        content: @Composable (ColumnScope.(BottomSheetFragment) -> Unit)
    ) : this(AnnotatedHeader(title, subtitle, beautifulDesign), content)

    override fun getTheme() = R.style.AppBottomSheetDialogTheme


    fun copy(
        header: HeadingInterface = this.state.header,
        content: @Composable (ColumnScope.(BottomSheetFragment) -> Unit) = this.state.content
    ) = apply {
        state.copy(
            header = header,
            content = content
        )
    }


    fun copy(state: BottomSheetState) = copy(state.header, state.content)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        return ComposeView(inflater.context).apply {
            setContent {
                PasswordManagerTheme(settings = settingsViewModel.settings) {
                    BottomSheetContent(
                        header = state.header,
                        modifier = Modifier
                    ) {
                        state.content(this, this@BottomSheetFragment)
                    }
                }
            }
        }
    }

    fun show(fragmentManager: FragmentManager) = show(fragmentManager, TAG)
}



class BottomSheetState(
    var header: HeadingInterface = Header(),
    var content: @Composable (ColumnScope.(BottomSheetFragment) -> Unit) = {}
) {
    constructor(
        title: String = "",
        subtitle: String = "",
        beautifulDesign: Boolean = false,
        body: @Composable (ColumnScope.(BottomSheetFragment) -> Unit),
    ) : this(Header(title, subtitle, beautifulDesign), body)


    constructor(
        title: AnnotatedString = AnnotatedString(""),
        subtitle: AnnotatedString = AnnotatedString(""),
        beautifulDesign: Boolean = false,
        content: @Composable (ColumnScope.(BottomSheetFragment) -> Unit)
    ) : this(AnnotatedHeader(title, subtitle, beautifulDesign), content)


    fun copy(
        header: HeadingInterface = this.header,
        content: @Composable ColumnScope.(BottomSheetFragment) -> Unit = this.content
    ) = apply {
        this.header = header
        this.content = content
        return this
    }
}