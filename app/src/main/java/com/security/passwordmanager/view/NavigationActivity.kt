package com.security.passwordmanager.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.security.passwordmanager.*
import com.security.passwordmanager.R
import com.security.passwordmanager.activities.BankCardActivity
import com.security.passwordmanager.activities.WebsiteActivity
import com.security.passwordmanager.view.compose.AppTheme
import com.security.passwordmanager.view.compose.BottomSheetContent
import com.security.passwordmanager.view.compose.navigation.*
import com.security.passwordmanager.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
class NavigationActivity : AppCompatActivity(), RecyclerCallback {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var settingsViewModel: SettingsViewModel

//    private lateinit var navigationView: NavigationView
//    private lateinit var navListener: NavController.OnDestinationChangedListener

//    private lateinit var navController: NavController

//    private lateinit var drawerListener: DrawerLayout.DrawerListener

//    private lateinit var fab: FloatingActionButton

    private lateinit var bottomFragment: BottomDialogFragment



    companion object {
        fun getIntent(context: Context) =
            createIntent<NavigationActivity>(context) {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_navigation)

        setContent {
            AppTheme(assets) {
                NavigationScreen {
                    bottomFragment.show(supportFragmentManager)
                }
            }
        }

        settingsViewModel = SettingsViewModel.getInstance(this)

//        val email = intent.getStringExtra(EMAIL_EXTRA)
        // TODO: 25.03.2022 сделать загрузку в бд

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        bottomFragment = BottomDialogFragment(settingsViewModel)
        bottomFragment.addView(
            image = R.drawable.account_image,
            context = this,
            text = R.string.website_label
        ) {
            startActivity(WebsiteActivity.getIntent(this, ""))
        }
        bottomFragment.addView(
            image = R.drawable.bank_card_image,
            context = this,
            text = R.string.bank_label
        ) {
            startActivity(BankCardActivity.getIntent(this, ""))
        }

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

        settingsViewModel.updateThemeInScreen(window, supportActionBar)

//        findViewById<DrawerLayout>(R.id.drawer_layout).addDrawerListener(drawerListener)

//        fab.setBackground(settingsViewModel.headerColor)
//        fab.backgroundTintList = ColorStateList(settingsViewModel.headerColor)
    }

    override fun onPause() {
        super.onPause()
//        navController.removeOnDestinationChangedListener(navListener)
//        findViewById<DrawerLayout>(R.id.drawer_layout).removeDrawerListener(drawerListener)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }


    private fun FloatingActionButton.setBackground(@ColorInt color: Int) {
        backgroundTintList = ColorStateList(color)
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
//        if (newState == RecyclerState.STOPPED)
//            fab.show()
//        else
//            fab.hide()
    }
}


@ExperimentalMaterialApi
@Composable
fun NavigationScreen(onFloatingActionButtonClick: () -> Unit) {
    val navController = rememberNavController()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val openDrawer = {
        scope.launch { bottomSheetScaffoldState.drawerState.open() }
    }

    val onDestinationClicked = { route: String ->
        scope.launch { bottomSheetScaffoldState.drawerState.close() }

        navController.navigate(route) {
            popUpTo(navController.graph.startDestinationId)
            launchSingleTop = true
        }
    }

//    val bottomItems = listOf(
//        BottomSheetItem()
//    )

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onFloatingActionButtonClick,
                backgroundColor = colorResource(R.color.header_color),
                contentColor = Color.White,
            ) {
                Icon(Icons.Default.Add, stringResource(R.string.create_record))
            }
        },
        drawerGesturesEnabled = bottomSheetScaffoldState.drawerState.isOpen,
        drawerContent = { DrawerContent(onDestinationClicked) },
        drawerBackgroundColor = MaterialTheme.colors.background,
        sheetContent = { BottomSheetContent() },
        sheetShape = RoundedCornerShape(
            topStart = dimensionResource(R.dimen.bottom_sheet_corner),
            topEnd = dimensionResource(R.dimen.bottom_sheet_corner)
        ),
        sheetBackgroundColor = MaterialTheme.colors.background,
        sheetGesturesEnabled = bottomSheetScaffoldState.bottomSheetState.isExpanded,
        sheetPeekHeight = 0.dp
    ) { contentPadding ->

        NavHost(
            navController = navController,
            startDestination = DrawerScreens.Home.route,
            modifier = Modifier.padding(contentPadding)
        ) {
            composable(DrawerScreens.Home.route) {
                HomeScreen(
                    openDrawer = { openDrawer() },
                    onSearch = { /* TODO: 02.09.2022 реализовать поиск */ }
                )
            }

            composable(DrawerScreens.Website.route) {
                WebsitesScreen(
                    openDrawer = { openDrawer() },
                    onSearch = { }
                )
            }

            composable(DrawerScreens.BankCard.route) {
                BankCardsScreen(
                    openDrawer = { openDrawer() },
                    onSearch = { }
                )
            }

            composable(DrawerScreens.Settings.route) {
                SettingsScreen(navController = navController)
            }
        }
    }
}


@Composable
private fun NavHeader() {
    Column(
        modifier = Modifier
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFB6B8),
                        colorResource(R.color.raspberry),
                        Color(0xFFF1C4DF)
                    )
                )
            )
            .padding(vertical = 16.dp, horizontal = 12.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {

        Icon(
            imageVector = Icons.Rounded.Lock,
            contentDescription = stringResource(R.string.nav_header_desc),
            tint = colorResource(android.R.color.holo_orange_dark),
            modifier = Modifier
                .padding(start = 4.dp)
                .padding(vertical = 12.dp)
                .scale(2.2f)
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            stringResource(R.string.app_label),
            color = Color.White,
            style = MaterialTheme.typography.body1
        )
        Spacer(Modifier.height(3.dp))
        Text(
            stringResource(R.string.app_version),
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}


@Composable
private fun DrawerItem(screen: DrawerScreens, onDestinationClicked: (route: String) -> Unit) {
    Row(modifier = Modifier
        .clickable {
            onDestinationClicked(screen.route)
        }
        .padding(vertical = 16.dp)
        .padding(start = 8.dp, end = 8.dp)
    ) {
        Icon(
            imageVector = screen.icon,
            contentDescription = stringResource(screen.titleRes),
            tint = colorResource(R.color.raspberry)
        )
        Spacer(modifier = Modifier.width(7.dp))
        Text(
            text = stringResource(screen.titleRes),
            color = colorResource(R.color.text_color),
            fontSize = 16.sp
        )
    }
}


@ExperimentalMaterialApi
@Preview
@Composable
fun NavigationScreenPreview() {
    NavigationScreen {  }
}