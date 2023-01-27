package com.security.passwordmanager.presentation.view.composablelements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.FabPosition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.security.passwordmanager.animate
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme
import me.onebone.toolbar.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolbarScaffold(
    state: CollapsingToolbarScaffoldState,
    scrollStrategy: ScrollStrategy,
    modifier: Modifier = Modifier,
    topBarModifier: Modifier = Modifier,
    topBar: @Composable CollapsingToolbarScope.() -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = MaterialTheme.colorScheme.background.animate(),
    contentColor: Color = contentColorFor(containerColor),
    body: @Composable (CollapsingToolbarScaffoldScope.() -> Unit)
) {
    Scaffold(
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        snackbarHost = snackbarHost,
        contentWindowInsets = WindowInsets
            .tappableElement
            .only(WindowInsetsSides.Bottom),
        containerColor = containerColor,
        contentColor = contentColor,
        modifier = modifier
    ) { contentPadding ->

        CollapsingToolbarScaffold(
            state = state,
            scrollStrategy = scrollStrategy,
            modifier = Modifier.padding(contentPadding),
            toolbarModifier = topBarModifier,
            toolbar = topBar,
        ) {
            body()
        }
    }
}





@ExperimentalMaterial3Api
@ExperimentalToolbarApi
@Preview
@Composable
private fun ToolbarScaffoldPreview() {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = Unit) {
        snackbarHostState.showSnackbar("Wrecked")
    }

    PasswordManagerTheme(isDarkTheme = false) {
        ToolbarScaffold(
            state = rememberCollapsingToolbarScaffoldState(),
            scrollStrategy = ScrollStrategy.EnterAlwaysCollapsed,
            topBar = {
                TopBar(title = "Imagine Dragons")
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) {
                    Snackbar(snackbarData = it)
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxSize(),
            ) {
                Text(
                    text = "Sharks",
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.background(MaterialTheme.colorScheme.background)
                )
            }

        }
    }

}