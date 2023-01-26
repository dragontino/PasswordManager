package com.security.passwordmanager.presentation.view.composablelements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme
import me.onebone.toolbar.*
import me.onebone.toolbar.FabPosition


@OptIn(ExperimentalToolbarApi::class)
@Composable
fun ToolbarScaffold(
    state: CollapsingToolbarScaffoldState,
    scrollStrategy: ScrollStrategy,
    modifier: Modifier = Modifier,
    topBarModifier: Modifier = Modifier,
    topBar: @Composable CollapsingToolbarScope.() -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FloatingActionButtonPosition = FloatingActionButtonPosition.End,
    body: @Composable (CollapsingToolbarScaffoldScope.() -> Unit)
) {
    ToolbarWithFabScaffold(
        state = state,
        scrollStrategy = scrollStrategy,
        modifier = modifier,
        toolbarModifier = topBarModifier,
        toolbar = topBar,
        fab = floatingActionButton,
        fabPosition = floatingActionButtonPosition.toOneBoneFabPosition()
    ) {
        body()
        Box(
            modifier = Modifier
                .padding(horizontal = 1.dp)
                .padding(
                    WindowInsets
                        .tappableElement
                        .only(WindowInsetsSides.Vertical)
                        .asPaddingValues()
                )
                .align(Alignment.BottomCenter)
        ) {
            snackbarHost()
        }
    }
}


enum class FloatingActionButtonPosition {
    Center,
    End;


    fun toOneBoneFabPosition() = when (this) {
        Center -> FabPosition.Center
        End -> FabPosition.End
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