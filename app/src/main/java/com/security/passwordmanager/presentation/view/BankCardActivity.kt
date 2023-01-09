package com.security.passwordmanager.presentation.view

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.view.MenuProvider
import androidx.lifecycle.ViewModelProvider
import com.security.passwordmanager.R
import com.security.passwordmanager.createIntent
import com.security.passwordmanager.databinding.ActivityBankCardBinding
import com.security.passwordmanager.presentation.model.DataUI
import com.security.passwordmanager.presentation.viewmodel.DataViewModel
import com.security.passwordmanager.presentation.viewmodel.OldSettingsViewModel

@ExperimentalMaterial3Api
class BankCardActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_DATA_UI = "extra_data_ui"
        private const val EXTRA_POSITION = "extra_position"

        fun getIntent(context: Context?, dataUI: DataUI?, startPosition: Int = 0) =
            createIntent<BankCardActivity>(context) {
                putExtra(EXTRA_DATA_UI, dataUI)
                putExtra(EXTRA_POSITION, startPosition)
            }
    }

    private lateinit var binding: ActivityBankCardBinding

    private lateinit var settings: OldSettingsViewModel
    private lateinit var dataViewModel: DataViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityBankCardBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        settings = OldSettingsViewModel.getInstance(this)
        dataViewModel = ViewModelProvider(this)[DataViewModel::class.java]

//        val dataUI = intent.getDataUIExtra(EXTRA_DATA_UI, DataUI.DefaultBankCard)
        val startPosition = intent.getIntExtra(EXTRA_POSITION, 0)

//        if (dataUI.title !is BankCard)
//            return

        setContent {
//            OutlinedDataTextField(
//                text = dataUI.title::bankName,
//                hintRes = R.string.bank_name
//            )
//
//            Divider(
//                color = colorResource(android.R.color.darker_gray),
//                modifier = Modifier
//                    .padding(vertical = dimensionResource(R.dimen.activity_vertical_margin))
//            )
//
//            DataList(dataUI.accountList, onClickToMore = {})
            // TODO: доделать BankCard
        }


        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) =
                menuInflater.inflate(R.menu.password, menu)

            override fun onMenuItemSelected(menuItem: MenuItem) = when(menuItem.itemId) {
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


        settings = OldSettingsViewModel.getInstance(this)
        dataViewModel = ViewModelProvider(this)[DataViewModel::class.java]

//        recyclerView = DataEditableRecyclerView(
//            this,
//            binding.bankCardRecyclerView,
//            dataUI,
//            DataType.BankCard
//        )

//        recyclerView.scrollToPosition(startPosition)

//        binding.addAccount.setOnClickListener { recyclerView.addData(Website()) }
//        binding.addCard.setOnClickListener { recyclerView.addData(BankCard()) }

        binding.bankName.nextFocusDownId = R.id.card_number
//        binding.bankName.doAfterTextChanged { text ->
//            recyclerView.forData { (it as BankCard).bankName = text.toString() }
//        }
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        binding.bankName.run {
//            val bankCard = recyclerView.getData(0) as BankCard
//            txt = bankCard.bankName
            setBackgroundResource(settings.backgroundRes)
            setTextColor(settings.fontColor)
        }

        settings.updateThemeInScreen(window, supportActionBar)
    }

    private fun saveInfo() {
        // TODO: 14.06.2022 сделать
    }

    private fun deleteInfo() {
//        dataViewModel.deleteRecords(recyclerView.getData(0))
    }
}