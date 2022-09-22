package com.security.passwordmanager.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.security.passwordmanager.*
import com.security.passwordmanager.R
import com.security.passwordmanager.model.DataType
import com.security.passwordmanager.model.ScreenType
import com.security.passwordmanager.view.compose.AppTheme
import com.security.passwordmanager.view.compose.BottomSheetContent
import com.security.passwordmanager.view.compose.BottomSheetItem
import com.security.passwordmanager.view.compose.BottomSheetItems
import com.security.passwordmanager.view.compose.navigation.DrawerContent
import com.security.passwordmanager.view.compose.navigation.MainScreen
import com.security.passwordmanager.view.compose.navigation.SettingsScreen
import com.security.passwordmanager.viewmodel.DataViewModel
import com.security.passwordmanager.viewmodel.MySettingsViewModel
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
class NavigationActivity : AppCompatActivity(), RecyclerCallback {

    companion object {
        fun getIntent(context: Context) =
            createIntent<NavigationActivity>(context) {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsViewModelFactory =
            (application as PasswordManagerApplication)
                .settingsViewModelFactory
        val settingsViewModel = MySettingsViewModel
            .getInstance(this, settingsViewModelFactory)
        val dataViewModel = DataViewModel.getInstance(this)

        setContent {
            AppTheme(assets, settingsViewModel) {
                NavigationScreen(
                    BottomSheetItems.screenType(ScreenType.Website),
                    BottomSheetItems.screenType(ScreenType.BankCard),
                    dataViewModel = dataViewModel,
                    settingsViewModel = settingsViewModel,
                    onClickToBottomItem = {
                        when (it) {
                            DataType.Website ->
                                startActivity(WebsiteActivity.getIntent(this, null))
                            DataType.BankCard ->
                                startActivity(BankCardActivity.getIntent(this, null))
                        }
                    },
                    openUrl = { address ->
                        val urlString = when {
                            "www." in address -> "https://$address"
                            "https://www." in address || "http://www." in address -> address
                            else -> "https://www.$address"
                        }

                        if (urlString.isValidUrl()) {
                            val intent = Intent(Intent.ACTION_VIEW, urlString.toUri())
                            startActivity(intent)
                        } else
                            showToast(this, "Адрес $address — некорректный!")
                    }
                )
            }
        }



//        bottomFragment = BottomDialogFragment(settingsViewModel)
//        bottomFragment.addView(
//            image = R.drawable.account_image,
//            context = this,
//            text = R.string.website_label
//        ) {
//            startActivity(WebsiteActivity.getIntent(this, ""))
//        }
//        bottomFragment.addView(
//            image = R.drawable.bank_card_image,
//            context = this,
//            text = R.string.bank_label
//        ) {
//            startActivity(BankCardActivity.getIntent(this, ""))
//        }

//        fab = findViewById(R.id.fab)
//        fab.setOnClickListener {
//            bottomFragment.show(supportFragmentManager)
//        }

//        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)

//        navigationView = findViewById(R.id.nav_view)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        navController = findNavController(R.id.nav_host_fragment)
//        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_home, R.id.nav_settings), drawer)

//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navigationView.setupWithNavController(navController)


//        navListener = NavController.OnDestinationChangedListener { _, destination, _ ->
//            if (destination.id == R.id.nav_settings)
//                fab.hide()
//            else {
//                fab.show()
//                fab.setBackground(settingsViewModel.headerColor)
//            }
//
//        }

//        navigationView.isTopInsetScrimEnabled = true
//
//        drawerListener = object : DrawerLayout.DrawerListener {
//            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
//
//            override fun onDrawerOpened(drawerView: View) {
//                settingsViewModel.updateThemeInScreen(window, supportActionBar, navigationView)
//
//                val header = navigationView.findViewById<LinearLayout>(R.id.nav_header_main)
//                if (settingsViewModel.isLightTheme)
//                    header.setBackgroundResource(R.drawable.side_nav_bar)
//                else
//                    header.setBackgroundColor(settingsViewModel.headerColor)
//
//                navController.currentDestination?.id?.let { navigationView.setCheckedItem(it) }
//            }
//
//            override fun onDrawerClosed(drawerView: View) {}
//
//            override fun onDrawerStateChanged(newState: Int) {
//                if (newState == DrawerLayout.STATE_DRAGGING) {
//                    settingsViewModel.updateThemeInScreen(window, supportActionBar, navigationView)
//
//                    val header = navigationView.findViewById<LinearLayout>(R.id.nav_header_main)
//                    if (settingsViewModel.isLightTheme)
//                        header.setBackgroundResource(R.drawable.side_nav_bar)
//                    else
//                        header.setBackgroundColor(settingsViewModel.headerColor)
//
//                    navController.currentDestination?.id?.let { navigationView.setCheckedItem(it) }
//                }
//            }
//        }

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
//        navController.addOnDestinationChangedListener(navListener)

//        settingsViewModel.updateThemeInScreen(window, supportActionBar)

//        findViewById<DrawerLayout>(R.id.drawer_layout).addDrawerListener(drawerListener)

//        fab.setBackground(settingsViewModel.headerColor)
//        fab.backgroundTintList = ColorStateList(settingsViewModel.headerColor)
    }

    //    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment)
//        return navController.navigateUp(appBarConfiguration)
//                || super.onSupportNavigateUp()
//    }

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
//        if (newState == RecyclerState.STOPPED)
//            fab.show()
//        else
//            fab.hide()
    }
}


@ExperimentalMaterialApi
@Composable
private fun NavigationScreen(
    vararg bottomSheetItems: BottomSheetItem,
    dataViewModel: DataViewModel = DataViewModel(PasswordManagerApplication()),
    settingsViewModel: MySettingsViewModel = PasswordManagerApplication()
        .settingsViewModelFactory
        .create(MySettingsViewModel::class.java),
    onClickToBottomItem: (DataType) -> Unit = {},
    openUrl: (address: String) -> Unit = {}
) {
    val bottomSheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,
        animationSpec = BottomAnimationSpec
    )
    val scaffoldState = rememberScaffoldState()

    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    val currentScreenType: MutableState<ScreenType?> = remember { mutableStateOf(null) }

    val openDrawer = {
        scope.launch { scaffoldState.drawerState.open() }
    }
    val onDrawerDestinationClicked = { route: String ->
        scope.launch { scaffoldState.drawerState.close() }

        navController.navigate(route) {
            popUpTo(navController.graph.startDestinationId)
            launchSingleTop = true
        }
    }
    val openBottomSheet = {
        scope.launch { bottomSheetState.show() }
    }

    val closeBottomSheet = {
        scope.launch { bottomSheetState.hide() }
    }

    val showFloatingActionButton = remember { mutableStateOf(true) }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            BottomSheetContent(
                bottomItems = bottomSheetItems
            ) { bottomItem ->
                closeBottomSheet()
                onClickToBottomItem(
                    ScreenType
                        .values()
                        .find { it.id == bottomItem.id }
                        ?.toDataType()
                        ?: DataType.Website
                )
            }
        },
        sheetShape = BottomSheetShape,
        sheetBackgroundColor = MaterialTheme.colors.background
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            floatingActionButton = {
                if (showFloatingActionButton.value)
                    FloatingActionButton(
                        onClick = { openBottomSheet() },
                        backgroundColor = MaterialTheme.colors.primary,
                        contentColor = Color.White,
                    ) {
                        Icon(Icons.Default.Add, stringResource(R.string.create_record))
                    }
            },
            drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
            drawerContent = { DrawerContent(onDrawerDestinationClicked) },
            drawerBackgroundColor = MaterialTheme.colors.background
        ) { contentPadding ->
            NavHost(
                navController = navController,
                startDestination = ScreenType.Home.route,
                modifier = Modifier.padding(contentPadding)
            ) {
                ScreenType.values().forEach { screenType ->

                    composable(screenType.route) {
                        if (screenType == ScreenType.Settings) {
                            showFloatingActionButton.value = false
                            SettingsScreen(navController, settingsViewModel)
                        } else {
                            showFloatingActionButton.value = true
                            MainScreen(
                                screenType = screenType,
                                dataViewModel = dataViewModel,
                                bottomSheetState = bottomSheetState,
                                openDrawer = { openDrawer() },
                                openUrl = openUrl
                            )
                        }
                    }
                }
            }
        }
    }
}


@ExperimentalMaterialApi
@Preview
@Composable
fun NavigationScreenPreview() {
    NavigationScreen()
}