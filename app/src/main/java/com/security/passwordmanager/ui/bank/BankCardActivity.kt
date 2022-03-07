package com.security.passwordmanager.ui.bank

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.security.passwordmanager.R
import com.security.passwordmanager.data.BankCard
import com.security.passwordmanager.data.DataViewModel
import com.security.passwordmanager.data.Website
import com.security.passwordmanager.databinding.ActivityBankCardBinding
import com.security.passwordmanager.settings.SettingsViewModel
import com.security.passwordmanager.settings.getStringExtra
import com.security.passwordmanager.ui.account.AccountRecyclerView

class BankCardActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_NAME = "extra_bank_name"
        private const val EXTRA_POSITION = "extra_position"

        fun getIntent(context: Context, bankName : String) : Intent {
            val intent = Intent(context, BankCardActivity::class.java)
            intent.putExtra(bankName, EXTRA_NAME)
            return intent
        }

        fun getIntent(context: Context, bankName: String, startPosition: Int) : Intent {
            val intent = getIntent(context, bankName)
            intent.putExtra(EXTRA_POSITION, startPosition)
            return intent
        }
    }

    private lateinit var binding: ActivityBankCardBinding

    private lateinit var settings: SettingsViewModel
    private lateinit var dataViewModel: DataViewModel
    private lateinit var bankAccountRecyclerView: BankRecyclerView
    private lateinit var accountRecyclerView: AccountRecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityBankCardBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        settings = SettingsViewModel.getInstance(this)
        dataViewModel = ViewModelProvider(this)[DataViewModel::class.java]

        val bankName = intent.getStringExtra(EXTRA_NAME, "")
        val startPosition = intent.getIntExtra(EXTRA_POSITION, 1)

        bankAccountRecyclerView = BankRecyclerView(
            this,
            binding.bankCardRecyclerView,
            bankName
        )

        accountRecyclerView = AccountRecyclerView(
            this,
            binding.bankAccountRecyclerView,
            bankName)

        bankAccountRecyclerView.scrollToPosition(startPosition)

        binding.addAccount.setOnClickListener { accountRecyclerView.addData(Website()) }
        binding.addCard.setOnClickListener { bankAccountRecyclerView.addData(BankCard()) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.password, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId) {
        R.id.menu_item_save -> {

            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        accountRecyclerView.updateRecyclerView()
        bankAccountRecyclerView.updateRecyclerView()
    }
}