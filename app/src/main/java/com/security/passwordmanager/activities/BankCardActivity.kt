package com.security.passwordmanager.activities

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import com.security.passwordmanager.R
import com.security.passwordmanager.createIntent
import com.security.passwordmanager.data.BankCard
import com.security.passwordmanager.data.DataType
import com.security.passwordmanager.data.Website
import com.security.passwordmanager.databinding.ActivityBankCardBinding
import com.security.passwordmanager.settings.getStringExtra
import com.security.passwordmanager.txt
import com.security.passwordmanager.ui.DataEditableRecyclerView
import com.security.passwordmanager.viewmodel.DataViewModel
import com.security.passwordmanager.viewmodel.SettingsViewModel

class BankCardActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_NAME = "extra_bank_name"
        private const val EXTRA_POSITION = "extra_position"

        fun getIntent(context: Context?, bankName: String, startPosition: Int = 0) =
            createIntent<BankCardActivity>(context) {
                putExtra(EXTRA_NAME, bankName)
                putExtra(EXTRA_POSITION, startPosition)
            }
    }

    private lateinit var binding: ActivityBankCardBinding

    private lateinit var settings: SettingsViewModel
    private lateinit var dataViewModel: DataViewModel

    private lateinit var recyclerView: DataEditableRecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityBankCardBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)


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


        settings = SettingsViewModel.getInstance(this)
        dataViewModel = ViewModelProvider(this)[DataViewModel::class.java]

        val bankName = intent.getStringExtra(EXTRA_NAME, "")
        val startPosition = intent.getIntExtra(EXTRA_POSITION, 0)

        recyclerView = DataEditableRecyclerView(
            this,
            binding.bankCardRecyclerView,
            bankName,
            DataType.BANK_CARD
        )

        recyclerView.scrollToPosition(startPosition)

        binding.addAccount.setOnClickListener { recyclerView.addData(Website()) }
        binding.addCard.setOnClickListener { recyclerView.addData(BankCard()) }

        binding.bankName.nextFocusDownId = R.id.card_number
        binding.bankName.doAfterTextChanged { text ->
            recyclerView.forData { (it as BankCard).bankName = text.toString() }
        }
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        binding.bankName.run {
            val bankCard = recyclerView.getData(0) as BankCard
            txt = bankCard.bankName
            setBackgroundResource(settings.backgroundRes)
            setTextColor(settings.fontColor)
        }

        settings.updateThemeInScreen(window, supportActionBar)
    }

    private fun saveInfo() {
        // TODO: 14.06.2022 сделать
    }

    private fun deleteInfo() {
        dataViewModel.deleteRecords(recyclerView.getData(0))
    }
}