package com.security.passwordmanager.view.compose.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.security.passwordmanager.R


@Composable
internal fun SearchBar(onQueryChange: (query: String) -> Unit, onCloseSearchBar: () -> Unit) {
    val query = remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    TopAppBar(
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.onBackground
    ) {
        TextField(
            value = query.value,
            onValueChange = {
                query.value = it
                onQueryChange(it)
            },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "search") },
            trailingIcon = {
                IconButton(onClick = onCloseSearchBar) {
                    Icon(Icons.Filled.Close, contentDescription = "close search bar")
                }
            },
            singleLine = true,
            placeholder = {
                Text(
                    stringResource(R.string.search_label),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontFamily = MaterialTheme.typography.caption.fontFamily
                )
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 18.sp,
                fontFamily = FontFamily.Default
            ),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.primary,
                textColor = Color.White,
                leadingIconColor = Color.White,
                trailingIconColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.White,
                placeholderColor = colorResource(android.R.color.darker_gray)
            ),
            modifier = Modifier
                .focusRequester(focusRequester)
                .fillMaxWidth(),
        )
    }
}


@Preview
@Composable
fun SearchBarPreview() {
    SearchBar(onQueryChange = {}, onCloseSearchBar = {})
}