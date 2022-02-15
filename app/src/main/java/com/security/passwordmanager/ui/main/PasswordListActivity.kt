package com.security.passwordmanager.ui.main

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.security.passwordmanager.ActionBottom
import com.security.passwordmanager.R
import com.security.passwordmanager.settings.SettingsViewModel
import com.security.passwordmanager.show
import com.security.passwordmanager.ui.account.PasswordActivity
import com.security.passwordmanager.ui.bank.BankCardActivity

class PasswordListActivity : AppCompatActivity() {

    private lateinit var mAppBarConfiguration: AppBarConfiguration
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var navigationView: NavigationView
    private lateinit var fab: FloatingActionButton

    companion object {
        fun getIntent(context: Context) : Intent {
            val intent = Intent(context, PasswordListActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_list)

        settingsViewModel = SettingsViewModel.getInstance(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val bottomFragment = ActionBottom.newInstance(this)

        bottomFragment.addView(text = R.string.password_label, image = R.drawable.account_image) {
            startActivity(PasswordActivity.getIntent(this, ""))
            bottomFragment.dismiss()
        }

        bottomFragment.addView(text = R.string.bank_label, image = R.drawable.bank_card_image) {
            startActivity(BankCardActivity.getIntent(this, ""))
            bottomFragment.dismiss()
        }

        fab = findViewById(R.id.fab)
        fab.setOnClickListener {
            bottomFragment.show(supportFragmentManager)
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = AppBarConfiguration.Builder(R.id.nav_home)
            .setOpenableLayout(drawer)
            .build()

        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        setupActionBarWithNavController(this, navController, mAppBarConfiguration)
        setupWithNavController(navigationView, navController)
    }

    override fun onResume() {
        super.onResume()
        settingsViewModel.updateThemeInScreen(window, supportActionBar)
        fab.backgroundTintList = ColorStateList.valueOf(settingsViewModel.headerColor)
        navigationView.setBackgroundColor(settingsViewModel.backgroundColor)

        val headerLayout = findViewById<LinearLayout>(R.id.nav_header_main)

        if (headerLayout != null) {
            if (settingsViewModel.isLightTheme())
                headerLayout.setBackgroundResource(R.drawable.side_nav_bar)
            else
                headerLayout.setBackgroundColor(settingsViewModel.headerColor)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp()
    }
}