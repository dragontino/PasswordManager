package com.security.passwordmanager.presentation.view.composablelements

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.security.passwordmanager.R
import com.security.passwordmanager.animate
import com.security.passwordmanager.presentation.view.theme.DarkerGray
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme
import kotlinx.coroutines.delay


@ExperimentalMaterial3Api
@Composable
internal fun SearchBar(
    onQueryChange: (query: String) -> Unit,
    onCloseSearchBar: () -> Unit,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        delay(200)
        focusRequester.requestFocus()
    }

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary.animate(),
            scrolledContainerColor = Color.Transparent,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary.animate(),
            titleContentColor = MaterialTheme.colorScheme.onPrimary.animate(),
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary.animate()
        ),
        navigationIcon = {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "search",
                modifier = Modifier.padding(start = 10.dp),
            )
        },
        actions = {
            IconButton(
                onClick = {
                    focusManager.clearFocus()
                    onCloseSearchBar()
                },
            ) {
                Icon(Icons.Rounded.Close, contentDescription = "close search bar")
            }
        },
        title = {
            TextField(
                value = query,
                onValueChange = {
                    query = it
                    onQueryChange(it)
                },
                singleLine = true,
                placeholder = {
                    Text(
                        stringResource(R.string.search_label),
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f).animate(),
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() },
                ),
                textStyle = MaterialTheme.typography.labelMedium,
                colors = TextFieldDefaults.textFieldColors(
                    textColor = MaterialTheme.colorScheme.onPrimary.animate(),
                    containerColor = MaterialTheme.colorScheme.primary.animate(),
                    focusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.onPrimary.animate(),
                    placeholderColor = DarkerGray
                ),
                modifier = Modifier.focusRequester(focusRequester)
            )
        },
        modifier = modifier
    )
}


@ExperimentalMaterial3Api
@Preview
@Composable
fun SearchBarPreview() {
    PasswordManagerTheme(isDarkTheme = false) {
        SearchBar(onQueryChange = {}, onCloseSearchBar = {})
    }
}