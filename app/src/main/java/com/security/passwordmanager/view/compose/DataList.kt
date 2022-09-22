package com.security.passwordmanager.view.compose

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.security.passwordmanager.DataCardShape
import com.security.passwordmanager.R
import com.security.passwordmanager.model.BankCard
import com.security.passwordmanager.model.Data
import com.security.passwordmanager.model.Website
import com.security.passwordmanager.showToast
import kotlin.reflect.KMutableProperty0

@Composable
fun DataList(
    accountList: List<Data>,
    modifier: Modifier = Modifier,
    startPosition: Int = 0,
    itemsBefore: @Composable ColumnScope.() -> Unit = {},
    itemsAfter: @Composable ColumnScope.() -> Unit = {},
    onClickToMore: (position: Int) -> Unit
) {
    val listState = rememberLazyListState(startPosition)

    // TODO: 25.08.2022 сделать скролл
//    val coroutineScope = rememberCoroutineScope()
    if (accountList.isEmpty()) return

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

        itemsIndexed(accountList) { index, data ->
            when (data) {
                is Website -> Website(index, data, onClickToMore)
                is BankCard -> BankCard(index, data)
            }
        }

        item {
            Column {
                itemsAfter()
            }
        }
    }

//    Button(
//        onClick = {
//            coroutineScope.launch {
//                listState.animateScrollToItem(index = startPosition)
//            }
//        }
//    ) { Text("Scroll") }

}


@Composable
private fun Website(position: Int, website: Website, onClickToMore: (position: Int) -> Unit) {
    Card(
        shape = DataCardShape,
        backgroundColor = MaterialTheme.colors.surface,
        modifier = Modifier
            .padding(dimensionResource(R.dimen.activity_vertical_margin))
            .border(1.dp, MaterialTheme.colors.primary, DataCardShape)
    ) {
        Column(Modifier.fillMaxHeight()) {
            Header(position, website.nameAccount, onClickToMore)
            OutlinedDataTextField(website::login, stringResource(R.string.login))
            OutlinedDataTextField(website::password, stringResource(R.string.password))
            OutlinedDataTextField(website::comment, stringResource(R.string.comment))
        }
    }
}


@Composable
fun BankCard(position: Int, bankCard: BankCard) {
    // TODO: 25.08.2022 нарисовать bankCard
}






@Composable
private fun Header(position: Int, nameAccount: String, onClickToMore: (position: Int) -> Unit) {
    Row(Modifier.fillMaxWidth()) {
        val defaultAccountName = stringResource(R.string.account_start, position + 1)
        // TODO: 22.08.2022 подумать над сохранением в бд
        val accountName = remember {
            mutableStateOf(
                nameAccount.ifEmpty { defaultAccountName }
            )
        }

        val focusManager = LocalFocusManager.current

        TextField(value = accountName.value,
            onValueChange = { newText -> accountName.value = newText },
            placeholder = {
                Text(
                    text = stringResource(R.string.account_name_placeholder),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            textStyle = LocalTextStyle.current.copy(
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            ),
//            enabled = false,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier
                .padding(10.dp)
                .weight(4f)
                .fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                textColor = MaterialTheme.colors.onBackground,
                placeholderColor = colorResource(android.R.color.darker_gray),
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = MaterialTheme.colors.primary
            )
        )

        IconButton(
            onClick = { onClickToMore(position) },
            modifier = Modifier
                .padding(16.dp)
                .background(Color.Transparent)
                .align(Alignment.CenterVertically)
                .weight(1f)
                .fillMaxHeight()
        ) {
            Icon(
                Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.rename_data),
                tint = MaterialTheme.colors.onBackground,
                modifier = Modifier
                    .scale(1.4f)
                    .align(Alignment.CenterVertically)
                    .fillMaxHeight()
            )
        }
    }
}


@Composable
fun OutlinedDataTextField(
    text: KMutableProperty0<String>,
    @StringRes hintRes: Int,
    whenFocused: () -> Unit = {}
) {
    OutlinedDataTextField(
        textField = text,
        hint = stringResource(hintRes),
        whenFocused = whenFocused
    )
}


@Composable
fun OutlinedDataTextField(
    textField: KMutableProperty0<String>,
    hint: String,
    whenFocused: () -> Unit = {}
) {
    val rememberedText = remember { mutableStateOf(textField.get()) }
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = rememberedText.value,
        onValueChange = { newText ->
            rememberedText.value = newText
            textField.set(newText)
        },
        textStyle = TextStyle(
            fontSize = 16.sp,
            fontFamily = MaterialTheme.typography.caption.fontFamily
        ),
        label = { Text(
            hint,
            fontFamily = MaterialTheme.typography.caption.fontFamily,
            color = if (rememberedText.value.isNotEmpty())
                colorResource(R.color.raspberry)
            else
                colorResource(android.R.color.darker_gray)
        ) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        shape = RoundedCornerShape(dimensionResource(R.dimen.text_view_corner)),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = MaterialTheme.colors.onBackground,
            placeholderColor = colorResource(android.R.color.darker_gray),
            backgroundColor = MaterialTheme.colors.background,
            focusedBorderColor = colorResource(R.color.raspberry),
            unfocusedBorderColor = colorResource(R.color.raspberry),
            focusedLabelColor = colorResource(R.color.raspberry),
            disabledLabelColor = colorResource(R.color.raspberry)
        ),
        modifier = Modifier
            .padding(dimensionResource(R.dimen.activity_vertical_margin))
            .onFocusChanged {
                when {
                    it.isFocused -> whenFocused()
                }
            }
            .fillMaxWidth()
            .padding()
    )
}



@Composable
fun TestWebsite() {
    DataList(listOf(
        Website(login = "petrovsd2002@yandex.ru"),
        Website(login = "sirpetrov19@yandex.ru"),
        Website(),
        Website()
    ), startPosition = 1) {
        showToast(context = null, "Hello!")
    }
}


@Preview
@Composable
fun PreviewWebsite() {
    TestWebsite()
}
