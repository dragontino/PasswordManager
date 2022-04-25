package com.security.passwordmanager.ui.entry

import android.os.Bundle
import androidx.fragment.app.Fragment

class LoginFragment: Fragment() {

    companion object {
        fun newInstance() = LoginFragment().apply {
            val bundle = Bundle()
            arguments = bundle
        }
    }
}