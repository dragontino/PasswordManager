package com.security.passwordmanager.presentation.view

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.security.passwordmanager.*
import com.security.passwordmanager.R
import com.security.passwordmanager.data.model.Website
import com.security.passwordmanager.presentation.model.DataUI
import com.security.passwordmanager.presentation.view.compose.DataList
import com.security.passwordmanager.presentation.view.navigation.BottomSheetContent
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.CopyItem
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.DeleteItem
import com.security.passwordmanager.presentation.view.navigation.ModalSheetItems.EditItem
import com.security.passwordmanager.presentation.view.theme.BottomSheetShape
import com.security.passwordmanager.presentation.view.theme.PasswordManagerTheme
import com.security.passwordmanager.presentation.view.theme.RaspberryLight
import com.security.passwordmanager.presentation.view.theme.sheetCornerRadius
import com.security.passwordmanager.presentation.viewmodel.DataViewModel
import com.security.passwordmanager.presentation.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
@ExperimentalMaterial3Api
class WebsiteActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_DATA_UI = "extra_data_ui"
        private const val EXTRA_START_POSITION = "extra_position"

        fun getIntent(context: Context?, dataUI: DataUI?, startPosition: Int = 0) =
            createIntent<WebsiteActivity>(context) {
                putExtra(EXTRA_DATA_UI, dataUI)
                putExtra(EXTRA_START_POSITION, startPosition)
            }
    }

    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsViewModel = SettingsViewModel.getInstance(
            this,
            (application as PasswordManagerApplication).viewModelFactory
        )
        val dataViewModel = DataViewModel.getInstance(
            this,
            (application as PasswordManagerApplication).viewModelFactory
        )

//        if (dataUI.title !is Website) throw ExceptionInInitializerError(
//            "Неверный тип Data. Ожидался ${DataType.Website}, но пришел ${dataUI.title.type}!"
//        )

        val startPosition = intent.getIntExtra(EXTRA_START_POSITION, 0)

        setContent {
            PasswordManagerTheme {
                WebsiteActivityScreen(settingsViewModel, DataUI.DefaultWebsite, dataViewModel, startPosition)
            }
        }

//        binding = ActivityWebsiteBinding.inflate(layoutInflater)

        // TODO: 07.03.2022 улучшить
//        if (settings.baseSettings.isShowingDataHints)
//            binding.websiteName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
//                if (hasFocus && binding.websiteName.text.isEmpty() && binding.url.text.isNotEmpty()) {
//
//                    val urlAddress = binding.url.txt
//                    binding.websiteName.txt = buildString(urlAddress) {
//                        if ("www." in urlAddress)
//                            deleteRange(0, 4)
//
//                        if (".com" in urlAddress || ".org" in urlAddress)
//                            deleteLast(4)
//
//                        if (".ru" in urlAddress)
//                            deleteLast(3)
//
//                        this[0] = this[0].uppercaseChar()
//                    }
//                }
//            }
//
//        binding.addAccount.setOnClickListener {
//            recyclerView.addData(Website())
//        }

//        binding.websiteName.nextFocusDownId = R.id.login
//
//        binding.url.doAfterTextChanged { text ->
//            recyclerView.forData { (it as Website).address = text.toString() }
//        }
//
//        binding.websiteName.doAfterTextChanged { text ->
//            recyclerView.forData { (it as Website).nameWebsite = text.toString() }
//        }
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
//        val website = recyclerView.getData(0) as Website
//
//        binding.url.txt = website.address
//        binding.websiteName.txt = website.nameWebsite
//
//        settings.updateThemeInScreen(window, supportActionBar)
//
//        settings.backgroundRes.let {
//            binding.url.setBackgroundResource(it)
//            binding.websiteName.setBackgroundResource(it)
//        }
//
//        settings.fontColor.let {
//            binding.url.setTextColor(it)
//            binding.websiteName.setTextColor(it)
//        }
    }

    //    private fun deleteInfo() =
//        dataViewModel.deleteRecords(recyclerView.getData(0))
}


@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@Composable
private fun WebsiteActivityScreen(
    settingsViewModel: SettingsViewModel?,
    dataUI: DataUI,
    dataViewModel: DataViewModel?,
    startPosition: Int = 0
) {
    val context = LocalContext.current

    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        animationSpec = BottomAnimationSpec
    )
    if (dataUI.title !is Website)
        return

    val scope = rememberCoroutineScope()
    var currentData by rememberSaveable { mutableStateOf(dataUI.title) }

    val openBottomSheet = {
        scope.launch { bottomSheetState.show() }
    }

    val rename = stringResource(R.string.rename_data)
    val saveNewName = stringResource(R.string.cancel_renaming_data)

    var editNameText by rememberSaveable { mutableStateOf(rename) }

    var isRenamingName by rememberSaveable { mutableStateOf(false) }
    var isShowingAlertDialog by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = isShowingAlertDialog,
        enter = slideInVertically(
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
        ) + scaleIn(
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
        ),
        exit = slideOutVertically(
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
        ) + scaleOut(
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
        )
    ) {
        AlertDialog(
            onDismissRequest = { isShowingAlertDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        isShowingAlertDialog = false
                        context
                            .getActivity()
                            ?.startActivity(NavigationActivity.getIntent(context))
                    },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = RaspberryLight,
                        containerColor = MaterialTheme.colorScheme.background.animate()
                    ),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(stringResource(R.string.button_save))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        isShowingAlertDialog = false
                        context
                            .getActivity()
                            ?.startActivity(NavigationActivity.getIntent(context))
                    },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = RaspberryLight,
                        containerColor = MaterialTheme.colorScheme.background.animate()
                    ),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(stringResource(R.string.button_cancel))
                }
            },
            title = {
                Text(
                    "Подтверждение действия",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "Сохранить изменения в этой записи?",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                )
            },
            shape = RoundedCornerShape(sheetCornerRadius),
            containerColor = MaterialTheme.colorScheme.background.animate(),
            textContentColor = MaterialTheme.colorScheme.onBackground.animate(),
            titleContentColor = MaterialTheme.colorScheme.onBackground.animate(),
            modifier = Modifier.border(
                width = 1.dp,
                color = RaspberryLight,
                shape = RoundedCornerShape(sheetCornerRadius)
            )
        )
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetShape = BottomSheetShape,
        sheetBackgroundColor = MaterialTheme.colorScheme.background.animate(),
        modifier = Modifier.background(MaterialTheme.colorScheme.background.animate()),
        sheetContent = {
            BottomSheetContent(title = currentData.nameAccount) {
                EditItem(text = editNameText) {
                    isRenamingName = !isRenamingName
                    editNameText =
                        if (isRenamingName) saveNewName
                        else rename
                }

                CopyItem(text = stringResource(R.string.copy_info)) {
                    dataViewModel?.copyData(context, currentData)
                }

                DeleteItem(text = stringResource(R.string.delete_account)) {
                    dataViewModel?.deleteData(currentData)
                }
            }
//            BottomSheetContent(
//                title = currentData.value.nameAccount,
//                drawBeautifulDesign = true,
//                bottomItems = arrayOf(
//                    BottomSheetItems.edit(stringResource(R.string.rename_data)),
//                    BottomSheetItems.copy(stringResource(R.string.copy_info)),
//                    BottomSheetItems.delete(stringResource(R.string.delete_account))
//                ),
//                onClickToBottomItem = {
//                    closeBottomSheet()
//                    when (it.id) {
//                        BottomSheetItems.editItemId -> {
//                            isRenamingName.value = !isRenamingName.value
//                            it.text =
//                                if (isRenamingName.value) saveNewName
//                                else rename
//                        }
//                        BottomSheetItems.copyItemId -> dataViewModel?.copyData(context, currentData.value)
//                        BottomSheetItems.deleteItemId -> dataViewModel?.deleteData(currentData.value)
//                    }
//                }
//            )
        }
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    title = stringResource(R.string.website_label),
                    navigationIcon = Icons.Default.ArrowBack,
                    onNavigate = {
                        isShowingAlertDialog = true
                    }
                ) {
                    TopBarAction(
                        icon = Icons.Filled.Delete,
                        contentDescription = stringResource(R.string.delete_password),
                        onClick = {
                            dataViewModel?.deleteRecords(dataUI.title)
                        }
                    )
                    TopBarAction(
                        icon = Icons.Filled.Save,
                        contentDescription = stringResource(R.string.button_save),
                        onClick = { }
                    )
                }
            }
        ) { contentPadding ->
            DataList(
                accountList = dataUI.accountList,
                startPosition = startPosition,
                onClickToMore = {
                    currentData = dataUI.accountList[it] as Website
                    openBottomSheet()
                },
                copyText = { dataViewModel?.copyText(context, it) },
                itemsBefore = {
                    EditableDataTextField(
                        text = dataUI.title.address,
                        onTextChange = { dataUI.title.address = it },
                        hint = stringResource(R.string.url_address)
                    )
                    EditableDataTextField(
                        text = dataUI.title.nameWebsite,
                        onTextChange = { dataUI.title.nameWebsite = it },
                        hint = stringResource(R.string.name_website)
                    ) {
                        val isShowingDataHints = true

                        if (
                            isShowingDataHints &&
                            dataUI.title.nameWebsite.isEmpty() &&
                            dataUI.title.address.isNotEmpty()
                        ) {
                            dataUI.title.nameWebsite = buildString(dataUI.title.address) {
                                if ("www." in dataUI.title.address)
                                    deleteRange(0, 4)
                                if (".com" in dataUI.title.address || ".org" in dataUI.title.address)
                                    deleteFromLast(4)
                                if (".ru" in dataUI.title.address)
                                    deleteFromLast(3)

                                this[0] = this[0].uppercaseChar()
                            }
                        }
                    }
                },
                itemsAfter = {
                    OutlinedButton(
                        onClick = { dataUI.accountList += Website() },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.primary.animate()
                        ),
                        border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.animate()),
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.add_account),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                modifier = Modifier.padding(contentPadding)
            )
        }
    }
}



@ExperimentalMaterial3Api
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
fun WebsiteActivityScreenPreview() {
    PasswordManagerTheme {
        WebsiteActivityScreen(
            dataUI = DataUI.DefaultWebsite,
            dataViewModel = null,
            settingsViewModel = null
        )
    }
}