package com.security.passwordmanager.view

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.security.passwordmanager.*
import com.security.passwordmanager.R
import com.security.passwordmanager.model.DataType
import com.security.passwordmanager.model.DataUI
import com.security.passwordmanager.model.Website
import com.security.passwordmanager.view.compose.*
import com.security.passwordmanager.view.compose.navigation.TopBar
import com.security.passwordmanager.view.compose.navigation.TopBarAction
import com.security.passwordmanager.viewmodel.DataViewModel
import com.security.passwordmanager.viewmodel.MySettingsViewModel
import kotlinx.coroutines.launch

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

//    private lateinit var binding: ActivityWebsiteBinding

//    private lateinit var settings: SettingsViewModel
//    private lateinit var dataViewModel: DataViewModel
//    private lateinit var recyclerView: DataEditableRecyclerView

    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsViewModel = MySettingsViewModel.getInstance(
            this,
            (application as PasswordManagerApplication).settingsViewModelFactory
        )
        val dataViewModel = DataViewModel.getInstance(this)

        val dataUI = intent.getDataUIExtra(EXTRA_DATA_UI, DataUI.Website)

        if (dataUI.title !is Website) throw ExceptionInInitializerError(
            "Неверный тип Data. Ожидался ${DataType.Website}, но пришел ${dataUI.title.type}!"
        )

        val startPosition = intent.getIntExtra(EXTRA_START_POSITION, 0)

        setContent {
            AppTheme(assets, settingsViewModel) {
                WebsiteActivityScreen(dataUI, dataViewModel, settingsViewModel, startPosition)
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

    private fun saveInfo() {
//        if (binding.url.text.isBlank()) {
//            binding.url.error = getString(R.string.required_url)
//            return
//        } else if (binding.websiteName.text.isBlank()) {
//            binding.websiteName.error = getString(R.string.required_website_name)
//            return
//        }
//
//        recyclerView.forEachIndexed { index, _ ->
//            val website = recyclerView.getData(index) as Website
//            website.nameWebsite = binding.websiteName.text.toString()
//            website.address = binding.url.text.toString()
//
//            if (website.isEmpty()) {
//                Toast.makeText(this, R.string.blank_fields, Toast.LENGTH_SHORT).show()
//                return
//            }
//
//            if (index >= recyclerView.startCount)
//                dataViewModel.addData(website)
//            else
//                dataViewModel.updateData(website)
//        }
    }

//    private fun deleteInfo() =
//        dataViewModel.deleteRecords(recyclerView.getData(0))
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun WebsiteActivityScreen(
    dataUI: DataUI,
    dataViewModel: DataViewModel?,
    settingsViewModel: MySettingsViewModel?,
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
    val currentData = rememberSaveable { mutableStateOf(dataUI.title) }

    val openBottomSheet = {
        scope.launch { bottomSheetState.show() }
    }
    val closeBottomSheet = {
        scope.launch { bottomSheetState.hide() }
    }

    val rename = stringResource(R.string.rename_data)
    val saveNewName = stringResource(R.string.cancel_renaming_data)

    val isRenamingName = rememberSaveable { mutableStateOf(false) }
    val isShowingAlertDialog = remember { mutableStateOf(false) }

    if (isShowingAlertDialog.value) {
        AlertDialog(
            onDismissRequest = { isShowingAlertDialog.value = false },
            buttons = {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            isShowingAlertDialog.value = false
                            context
                                .getActivity()
                                ?.startActivity(NavigationActivity.getIntent(context))
                        },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = colorResource(R.color.raspberry),
                            backgroundColor = MaterialTheme.colors.background
                        ),
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(1f)
                    ) {
                        Text(stringResource(R.string.button_cancel))
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    TextButton(
                        onClick = {
                            // TODO: saveInfo()
                            isShowingAlertDialog.value = false
                            context
                                .getActivity()
                                ?.startActivity(NavigationActivity.getIntent(context))
                        },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = colorResource(R.color.raspberry),
                            backgroundColor = MaterialTheme.colors.background
                        ),
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(1f)
                    ) {
                        Text(stringResource(R.string.button_save))
                    }
                }
            },
            title = {
                Text(
                    "Подтверждение действия",
                    fontSize = 22.sp,
                    fontFamily = MaterialTheme.typography.caption.fontFamily,
                    textAlign = TextAlign.Center
                )
            },
            text = { Text(
                "Сохранить изменения в этой записи?",
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            ) },
            shape = RoundedCornerShape(bottomSheetCornerRadius),
            backgroundColor = MaterialTheme.colors.background,
            contentColor = MaterialTheme.colors.onBackground,
            modifier = Modifier.border(
                width = 1.dp,
                color = colorResource(R.color.raspberry),
                shape = RoundedCornerShape(bottomSheetCornerRadius)
            )
        )
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetShape = BottomSheetShape,
        sheetBackgroundColor = MaterialTheme.colors.background,
        modifier = Modifier.background(MaterialTheme.colors.background),
        sheetContent = {
            BottomSheetContent(
                titleWithSubtitle = TitleWithSubtitle(title = currentData.value.nameAccount),
                drawBeautifulDesign = true,
                BottomSheetItems.edit(stringResource(R.string.rename_data)),
                BottomSheetItems.copy(stringResource(R.string.copy_info)),
                BottomSheetItems.delete(stringResource(R.string.delete_account)),
                onClickToBottomItem = {
                    closeBottomSheet()
                    when (it.id) {
                        BottomSheetItems.editItemId -> {
                            isRenamingName.value = !isRenamingName.value
                            it.text =
                                if (isRenamingName.value) saveNewName
                                else rename
                        }
                        BottomSheetItems.copyItemId -> dataViewModel?.copyData(currentData.value)
                        BottomSheetItems.deleteItemId -> dataViewModel?.deleteData(currentData.value)
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    title = stringResource(R.string.website_label),
                    navigationIcon = Icons.Default.ArrowBack,
                    onNavigate = {
                        // TODO: сделать диалог подтверждения
                        isShowingAlertDialog.value = true
                    },
                    actions = listOf(
                        TopBarAction(
                            icon = Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.delete_password),
                            onClick = {
                                // TODO: сделать подтверждение действия
                                dataViewModel?.deleteRecords(dataUI.title)
                            }
                        ),
                        TopBarAction(
                            icon = Icons.Filled.Save,
                            contentDescription = stringResource(R.string.button_save),
                            onClick = { /* TODO: сделать save */  }
                        )
                    )
                )
            }
        ) { contentPadding ->
            DataList(
                accountList = dataUI.accountList,
                startPosition = startPosition,
                onClickToMore = {
                    currentData.value = dataUI.accountList[it] as Website
                    openBottomSheet()
                },
                itemsBefore = {
                    OutlinedDataTextField(
                        text = dataUI.title::address,
                        hintRes = R.string.url_address
                    )
                    OutlinedDataTextField(
                        text = dataUI.title::nameWebsite,
                        hintRes = R.string.name_website,
                        whenFocused = {
                            val isShowingDataHints = (
                                    settingsViewModel
                                        ?.state?.value as MySettingsViewModel.State.Data
                                    ).settings
                                .isShowingDataHints

                            if (
                                isShowingDataHints &&
                                dataUI.title.nameWebsite.isEmpty() &&
                                dataUI.title.address.isNotEmpty()
                            ) {
                                // TODO: переделать
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
                    )
                },
                itemsAfter = {
                    Button(
                        onClick = { dataUI.accountList += Website() },
                        colors = ButtonDefaults.textButtonColors(
                            backgroundColor = Color.Transparent,
                            contentColor = colorResource(R.color.raspberry)
                        ),
                        elevation = ButtonDefaults.elevation(0.dp),
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.add_account), fontSize = 15.sp)
                    }
                },
                modifier = Modifier.padding(contentPadding)
            )
        }
    }
}



@Preview
@Composable
fun WebsiteActivityScreenPreview() {
    WebsiteActivityScreen(
        dataUI = DataUI.Website,
        dataViewModel = null,
        settingsViewModel = null
    )
}