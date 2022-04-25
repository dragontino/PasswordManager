package com.security.passwordmanager.ui.main

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.security.passwordmanager.*
import com.security.passwordmanager.settings.SettingsViewModel
import com.security.passwordmanager.ui.account.PasswordActivity
import com.security.passwordmanager.ui.bank.BankCardActivity

class PasswordListActivity : AppCompatActivity(), RecyclerCallback {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var settingsViewModel: SettingsViewModel

    private lateinit var navigationView: NavigationView
    private lateinit var navListener: NavController.OnDestinationChangedListener

    private lateinit var navController: NavController

    private lateinit var drawerListener: DrawerLayout.DrawerListener

    private lateinit var fab: FloatingActionButton

    companion object {
        fun getIntent(context: Context) =
            createIntent<PasswordListActivity>(context) {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_list)

        settingsViewModel = SettingsViewModel.getInstance(this)

//        val email = intent.getStringExtra(EMAIL_EXTRA)
        // TODO: 25.03.2022 сделать загрузку в бд

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
        navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_home, R.id.nav_settings), drawer)

        setupActionBarWithNavController(navController, appBarConfiguration)
        navigationView.setupWithNavController(navController)


        navListener = NavController.OnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.nav_settings)
                fab.hide()
            else {
                fab.show()
                fab.setBackground(settingsViewModel.headerColor)
            }

        }

        navigationView.isTopInsetScrimEnabled = true

        drawerListener = object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {
                settingsViewModel.updateThemeInScreen(window, supportActionBar, navigationView)

                val header = navigationView.findViewById<LinearLayout>(R.id.nav_header_main)
                if (settingsViewModel.isLightTheme())
                    header.setBackgroundResource(R.drawable.side_nav_bar)
                else
                    header.setBackgroundColor(settingsViewModel.headerColor)

                navController.currentDestination?.id?.let { navigationView.setCheckedItem(it) }
            }

            override fun onDrawerClosed(drawerView: View) {}

            override fun onDrawerStateChanged(newState: Int) {
                if (newState == DrawerLayout.STATE_DRAGGING) {
                    settingsViewModel.updateThemeInScreen(window, supportActionBar, navigationView)

                    val header = navigationView.findViewById<LinearLayout>(R.id.nav_header_main)
                    if (settingsViewModel.isLightTheme())
                        header.setBackgroundResource(R.drawable.side_nav_bar)
                    else
                        header.setBackgroundColor(settingsViewModel.headerColor)

                    navController.currentDestination?.id?.let { navigationView.setCheckedItem(it) }
                }
            }
        }

        val onBackPressedCallback = onBackPressedCallback(true) {
            val startMain = Intent(Intent.ACTION_MAIN)
            startMain.addCategory(Intent.CATEGORY_HOME)
            startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(startMain)
        }

        onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(navListener)

        settingsViewModel.updateThemeInScreen(window, supportActionBar)

        findViewById<DrawerLayout>(R.id.drawer_layout).addDrawerListener(drawerListener)

        fab.setBackground(settingsViewModel.headerColor)
        fab.backgroundTintList = ColorStateList.valueOf(settingsViewModel.headerColor)
    }

    override fun onPause() {
        super.onPause()
        navController.removeOnDestinationChangedListener(navListener)
        findViewById<DrawerLayout>(R.id.drawer_layout).removeDrawerListener(drawerListener)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }


    private fun FloatingActionButton.setBackground(@ColorInt color: Int) {
        backgroundTintList = ColorStateList.valueOf(color)
    }

    override fun onScroll(recyclerDirection: RecyclerDirection, currentState: RecyclerState) {
//        if (currentState != RecyclerState.STOPPED) {
//            if (recyclerDirection == RecyclerDirection.DOWN) {
////                supportActionBar?.hide()
//                window.statusBarColor = getColor(android.R.color.transparent)
//            } else if (recyclerDirection == RecyclerDirection.UP) {
////                supportActionBar?.show()
//                window.statusBarColor = settingsViewModel.headerColor
//            }
//        }
    }

    // TODO: 25.04.2022 попробовать сделать прозрачный status bar и скрыть action bar
    override fun onStateChanged(newState: RecyclerState) {
        if (newState == RecyclerState.STOPPED)
            fab.show()
        else
            fab.hide()
    }
}