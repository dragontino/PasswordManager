package com.security.passwordmanager.ui.bank

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.security.passwordmanager.R
import com.security.passwordmanager.data.BankCard
import com.security.passwordmanager.data.DataViewModel
import com.security.passwordmanager.data.Website
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

    private lateinit var settings: SettingsViewModel
    private lateinit var dataViewModel: DataViewModel
    private lateinit var bankAccountRecyclerView: BankRecyclerView
    private lateinit var accountRecyclerView: AccountRecyclerView

    private lateinit var name: EditText
    private lateinit var head: TextView
    private lateinit var addAccount: Button
    private lateinit var addCard: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_card)

        settings = ViewModelProvider(this)[SettingsViewModel::class.java]
        dataViewModel = ViewModelProvider(this)[DataViewModel::class.java]

        val bankName = intent.getStringExtra(EXTRA_NAME, "")
        val startPosition = intent.getIntExtra(EXTRA_POSITION, 1)

        bankAccountRecyclerView = BankRecyclerView(
            this,
            R.id.bank_card_recycler_view,
            bankName
        )

        accountRecyclerView = AccountRecyclerView(
            this,
            R.id.bank_account_recycler_view,
            bankName)

        bankAccountRecyclerView.scrollToPosition(startPosition)

        name = findViewById(R.id.bank_name)
        head = findViewById(R.id.bank_head)
        addAccount = findViewById(R.id.add_account)
        addCard = findViewById(R.id.add_new_card)

        addAccount.setOnClickListener { accountRecyclerView.addData(Website()) }
        addCard.setOnClickListener { bankAccountRecyclerView.addData(BankCard()) }
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