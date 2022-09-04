package com.security.passwordmanager.activities

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.ui.res.colorResource
import androidx.core.view.MenuProvider
import androidx.core.widget.doAfterTextChanged
import com.security.passwordmanager.*
import com.security.passwordmanager.data.Data
import com.security.passwordmanager.data.DataType
import com.security.passwordmanager.data.Website
import com.security.passwordmanager.databinding.ActivityWebsiteBinding
import com.security.passwordmanager.settings.getStringExtra
import com.security.passwordmanager.ui.DataEditableRecyclerView
import com.security.passwordmanager.ui.SaveInfoDialog
import com.security.passwordmanager.ui.website_compose.DataList
import com.security.passwordmanager.view.compose.AppTheme
import com.security.passwordmanager.view.compose.BottomSheetFragment
import com.security.passwordmanager.viewmodel.DataViewModel
import com.security.passwordmanager.viewmodel.SettingsViewModel
import java.net.URI

class WebsiteActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_ADDRESS = "extra_address"
        private const val EXTRA_START_POSITION = "extra_position"

        fun getIntent(context: Context?, address: String, startPosition: Int = 0) =
            createIntent<WebsiteActivity>(context) {
                putExtra(EXTRA_ADDRESS, address)
                putExtra(EXTRA_START_POSITION, startPosition)
            }
    }

    private lateinit var binding: ActivityWebsiteBinding

    private lateinit var settings: SettingsViewModel
    private lateinit var dataViewModel: DataViewModel
    private lateinit var recyclerView: DataEditableRecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settings = SettingsViewModel.getInstance(this)
        dataViewModel = DataViewModel.getInstance(this)

        AppCompatDelegate.setDefaultNightMode(settings.currentNightMode)

        val address = intent.getStringExtra(EXTRA_ADDRESS, "")
        val startPosition = intent.getIntExtra(EXTRA_START_POSITION, 0)

        setContent {
            AppTheme(assets) {
                val bottomSheetFragment = BottomSheetFragment(settings).apply {
                    setHeading("Test", beautifulDesign = true)
                    addView(
                        Icons.Outlined.Edit,
                        "Edit",
                        imageTintColor = colorResource(R.color.raspberry)
                    ) {}
                }

                val accountList = dataViewModel.getAccountList(address, DataType.WEBSITE) as ArrayList<Data>
                if (accountList.isEmpty())
                    accountList += Website()

                DataList(
                    accountList = accountList,
                    startPosition = startPosition
                ) {
                    bottomSheetFragment.show(supportFragmentManager)
                }
            }
        }

        binding = ActivityWebsiteBinding.inflate(layoutInflater)
//        setContentView(binding.root)



        binding.dialog123.setOnClickListener {
            SaveInfoDialog(this, R.string.menu_item_save).show()
//            startActivity(DialogSaveActivity.getIntent(this))
        }

//        binding.websitesListCompose.setContent {
//            val bottomSheetFragment = BottomSheetFragment().apply {
//                setHeading("Test", beautifulDesign = true)
//                addView(R.drawable.edit, "Edit") {}
//            }
//
//            DataList(dataViewModel.getAccountList(address, DataType.WEBSITE), startPosition) {
//                bottomSheetFragment.show(supportFragmentManager)
//            }
//        }

        recyclerView = DataEditableRecyclerView(
            activity = this,
            recyclerView = binding.accountRecyclerView,
            key = address,
            type = DataType.WEBSITE
        )

        recyclerView.scrollToPosition(startPosition)



        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) =
                menuInflater.inflate(R.menu.password, menu)

            override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
                R.id.menu_item_save -> {
                    saveInfo()
                    finish()
                    true

                }

                R.id.menu_item_delete -> {
                    deleteInfo()
                    finish()
                    true
                }
                else -> false
            }
        })


        // TODO: 19.08.2022 проверить
        val hostName = URI(binding.url.txt).host
        showToast(this, binding.url.txt)

        // TODO: 07.03.2022 улучшить
        if (settings.baseSettings.isShowingDataHints)
            binding.websiteName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus && binding.websiteName.text.isEmpty() && binding.url.text.isNotEmpty()) {

                    val urlAddress = binding.url.txt
                    binding.websiteName.txt = buildString(urlAddress) {
                        if ("www." in urlAddress)
                            deleteRange(0, 4)

                        if (".com" in urlAddress || ".org" in urlAddress)
                            deleteLast(4)

                        if (".ru" in urlAddress)
                            deleteLast(3)

                        this[0] = this[0].uppercaseChar()
                    }
                }
            }

        binding.addAccount.setOnClickListener {
            recyclerView.addData(Website())
        }

        binding.websiteName.nextFocusDownId = R.id.login

        binding.url.doAfterTextChanged { text ->
            recyclerView.forData { (it as Website).address = text.toString() }
        }

        binding.websiteName.doAfterTextChanged { text ->
            recyclerView.forData { (it as Website).nameWebsite = text.toString() }
        }
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        val website = recyclerView.getData(0) as Website

        binding.url.txt = website.address
        binding.websiteName.txt = website.nameWebsite

        settings.updateThemeInScreen(window, supportActionBar)

        settings.backgroundRes.let {
            binding.url.setBackgroundResource(it)
            binding.websiteName.setBackgroundResource(it)
        }

        settings.fontColor.let {
            binding.url.setTextColor(it)
            binding.websiteName.setTextColor(it)
        }
    }

    private fun saveInfo() {
        if (binding.url.text.isBlank()) {
            binding.url.error = getString(R.string.required_url)
            return
        } else if (binding.websiteName.text.isBlank()) {
            binding.websiteName.error = getString(R.string.required_website_name)
            return
        }

        recyclerView.forEachIndexed { index, _ ->
            val website = recyclerView.getData(index) as Website
            website.nameWebsite = binding.websiteName.text.toString()
            website.address = binding.url.text.toString()

            if (website.isEmpty()) {
                Toast.makeText(this, R.string.blank_fields, Toast.LENGTH_SHORT).show()
                return
            }

            if (index >= recyclerView.startCount)
                dataViewModel.addData(website)
            else
                dataViewModel.updateData(website)
        }
    }

    private fun deleteInfo() =
        dataViewModel.deleteRecords(recyclerView.getData(0))
}