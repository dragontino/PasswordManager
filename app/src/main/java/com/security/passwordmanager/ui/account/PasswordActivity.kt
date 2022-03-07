package com.security.passwordmanager.ui.account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.security.passwordmanager.R
import com.security.passwordmanager.data.DataViewModel
import com.security.passwordmanager.data.Website
import com.security.passwordmanager.databinding.ActivityPasswordBinding
import com.security.passwordmanager.settings.SettingsViewModel
import com.security.passwordmanager.settings.getStringExtra

class PasswordActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_ADDRESS = "extra_address"
        private const val EXTRA_POSITION = "extra_position"

        fun getIntent(context: Context?, address : String) : Intent {
            val intent = Intent(context, PasswordActivity::class.java)
            intent.putExtra(EXTRA_ADDRESS, address)
            return intent
        }

        fun getIntent(context: Context?, address: String, startPosition : Int) : Intent {
            val intent: Intent = getIntent(context, address)
            intent.putExtra(EXTRA_POSITION, startPosition)
            return intent
        }
    }

    private lateinit var binding: ActivityPasswordBinding

    private lateinit var settings : SettingsViewModel
    private lateinit var dataViewModel : DataViewModel
    private lateinit var recyclerView : AccountRecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPasswordBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        settings = SettingsViewModel.getInstance(this)
        dataViewModel = ViewModelProvider(this)[DataViewModel::class.java]

        val address = intent.getStringExtra(EXTRA_ADDRESS, "")
        val startPosition = intent.getIntExtra(EXTRA_POSITION, 1)

        recyclerView = AccountRecyclerView(
            activity = this,
            recyclerView = binding.accountRecyclerView,
            address = address,
        )

        recyclerView.scrollToPosition(startPosition)

        binding.websiteName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.websiteName.text.isEmpty() && binding.url.text.isNotEmpty()) {
                val builder = StringBuilder(binding.url.text)

                if (builder.toString().contains("www."))
                    builder.delete(0, 5)

                if (builder.toString().contains(".com"))
                    builder.delete(builder.length - 4, builder.length)

                if (builder.toString().contains(".ru"))
                    builder.delete(builder.length - 3, builder.length)

                val first = Character.toUpperCase(builder[0])
                builder.setCharAt(0, first)

                binding.websiteName.setText(builder.toString())
            }
        }

        val buttonAdd = findViewById<Button>(R.id.add_account)
        buttonAdd.setOnClickListener {
            recyclerView.addData(Website())
            recyclerView.scrollToEnd()
        }

        binding.websiteName.nextFocusDownId = R.id.login
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        binding.url.setText(recyclerView.getData(0).address)
        binding.websiteName.setText(recyclerView.getData(0).nameWebsite)

        recyclerView.updateRecyclerView()

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

    override fun onCreateOptionsMenu(menu: Menu) : Boolean {
        menuInflater.inflate(R.menu.password, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_save -> {
                if (binding.url.text.toString() == "") {
                    binding.url.error = getString(R.string.required_url)
                    return true
                } else if (binding.websiteName.text.toString() == "") {
                    binding.websiteName.error = getString(R.string.required_website_name)
                    return true
                }

                for (position in 0 until recyclerView.itemCount) {
                    val view = recyclerView[position]
                    val textViewNameAccount =
                        view.findViewById<TextView>(R.id.name_account)

                    val login = view.findViewById<TextView>(R.id.login)
                    val password = view.findViewById<TextView>(R.id.password)
                    val comment = view.findViewById<TextView>(R.id.comment)

                    val startCount = recyclerView.startCount

                    var nameAccount = textViewNameAccount.text.toString()

                    if (nameAccount == getNameAccountStart(position + 1))
                        nameAccount = ""

                    if (login.text.isEmpty()) {
                        login.error = getString(R.string.required)
                        return true
                    } else if (password.text.isEmpty()) {
                        password.error = getString(R.string.required)
                        return true
                    }

                    val website = recyclerView.getData(position)
                    website.address = binding.url.text.toString()
                    website.nameWebsite = binding.websiteName.text.toString()
                    website.nameAccount = nameAccount
                    website.login = login.text.toString()
                    website.password = password.text.toString()
                    website.comment = comment.text.toString()

                    if (position >= startCount)
                        dataViewModel.addData(website)
                    else
                        dataViewModel.updateData(website)
                }
                finish()
                return true
            }

            R.id.menu_item_delete -> {
                dataViewModel.deleteRecords(recyclerView.getData(0))
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun getNameAccountStart(position : Int) =
        getString(R.string.account_start, position)
}