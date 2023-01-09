package com.security.passwordmanager.presentation.view.compose

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.security.passwordmanager.R
import com.security.passwordmanager.animate
import com.security.passwordmanager.data.model.Data
import com.security.passwordmanager.data.model.Website
import com.security.passwordmanager.presentation.view.EditableDataTextField
import com.security.passwordmanager.presentation.view.TrailingActions.CopyIconButton
import com.security.passwordmanager.presentation.view.TrailingActions.VisibilityIconButton
import com.security.passwordmanager.presentation.view.theme.DarkerGray
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
fun DataList(
    accountList: List<Data>,
//    fragmentManager: FragmentManager,
    modifier: Modifier = Modifier,
    startPosition: Int = 0,
    itemsBefore: @Composable ColumnScope.() -> Unit = {},
    itemsAfter: @Composable ColumnScope.() -> Unit = {},
    copyText: (String) -> Unit = {},
    onClickToMore: (position: Int) -> Unit
) {
    val listState = rememberLazyListState()

    // TODO: 25.08.2022 сделать скролл
    if (accountList.isEmpty()) return

    val coroutineScope = rememberCoroutineScope()

    val scrollToItem = { index: Int ->
        coroutineScope.launch { listState.scrollToItem(index) }
    }

    LazyColumn(state = listState, modifier = modifier) {
        item {
            Column {
                itemsBefore()
                Divider(
                    color = colorResource(android.R.color.darker_gray),
                    modifier = Modifier
                        .padding(vertical = dimensionResource(R.dimen.activity_vertical_margin))
                )
            }
        }

        item {
            Column {
                itemsAfter()
            }
        }
    }

    scrollToItem(startPosition)

//    Button(
//        onClick = {
//            coroutineScope.launch {
//                listState.animateScrollToItem(index = startPosition)
//            }
//        }
//    ) { Text("Scroll") }

}


@ExperimentalMaterial3Api
@Composable
private fun Website(
    website: Website,
    position: Int,
    copyText: (String) -> Unit,
    onClickToMore: (position: Int) -> Unit,
) {
    var passwordIsVisible by rememberSaveable { mutableStateOf(false) }

    ElevatedCard(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface.animate(),
            contentColor = MaterialTheme.colorScheme.onBackground.animate()
        ),
        modifier = Modifier.padding(16.dp)
    ) {
        Header(
            nameAccount = website.nameAccount,
            updateNameAccount = { website.nameAccount = it },
            onClickToMore = onClickToMore,
            position = position
        )

        EditableDataTextField(
            text = website.login,
            onTextChange = { website.login = it },
            hint = stringResource(R.string.login),
            trailingActions = {
                CopyIconButton {
                    copyText(website.login)
                }
            }
        )

        EditableDataTextField(
            text = website.password,
            onTextChange = { website.password = it },
            hint = stringResource(R.string.password),
            keyboardType = KeyboardType.Password,
            visualTransformation = if (passwordIsVisible)
                VisualTransformation.None
            else PasswordVisualTransformation(),
            trailingActions = {
                VisibilityIconButton(visible = passwordIsVisible) {
                    passwordIsVisible = it
                }
                Spacer(modifier = Modifier.width(8.dp))
                CopyIconButton {
                    copyText(website.password)
                }
                Spacer(Modifier.width(8.dp))
            }
        )

        EditableDataTextField(
            text = website.comment,
            onTextChange = { website.comment = it },
            hint = stringResource(R.string.comment),
            trailingActions = {
                CopyIconButton {
                    copyText(website.comment)
                }
            }
        )
    }
}






@ExperimentalMaterial3Api
@Composable
private fun Header(
    nameAccount: String,
    updateNameAccount: (String) -> Unit,
    onClickToMore: (position: Int) -> Unit,
    position: Int,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
    ) {
        val defaultAccountName = stringResource(R.string.account_start, position + 1)
        var headingText by rememberSaveable {
            mutableStateOf(nameAccount.ifBlank { defaultAccountName })
        }
        @StringRes
        var bottomSheetItemTextRes by rememberSaveable { mutableStateOf(R.string.rename_data) }
        var isHeadingEnabled by rememberSaveable { mutableStateOf(false) }
        val focusManager = LocalFocusManager.current


        TextField(
            value = headingText,
            onValueChange = {
                headingText = it
            },
            placeholder = {
                Text(
                    text = stringResource(R.string.account_name_placeholder),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                textAlign = TextAlign.Center
            ),
            enabled = isHeadingEnabled,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.moveFocus(FocusDirection.Down)
                    isHeadingEnabled = false
                    updateNameAccount(
                        if (headingText == defaultAccountName) "" else headingText
                    )
                },
            ),
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .weight(4f)
                .fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                textColor = MaterialTheme.colorScheme.onBackground.animate(),
                placeholderColor = DarkerGray,
                containerColor = Color.Transparent,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary.animate(),
                disabledTextColor = DarkerGray,
                disabledIndicatorColor = Color.Transparent
            ),
        )

        IconButton(
            onClick = { onClickToMore(position) },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.onBackground.animate(),
                containerColor = Color.Transparent
            ),
            modifier = Modifier
                .padding(end = 8.dp, start = 4.dp)
                .fillMaxHeight()
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.rename_data),
                modifier = Modifier.scale(1.3f)
            )
        }
    }
}




@ExperimentalMaterial3Api
@Preview
@Composable
private fun WebsitePreview() {
    PasswordManagerTheme {
        DataList(
            accountList = listOf(
                Website(login = "petrovsd2002@yandex.ru"),
                Website(login = "sirpetrov19@yandex.ru"),
                Website(),
                Website(),
            ),
            startPosition = 1,
//            fragmentManager = AppCompatActivity().supportFragmentManager
        ) {

        }
    }
}
