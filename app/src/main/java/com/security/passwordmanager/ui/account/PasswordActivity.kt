package com.security.passwordmanager.ui.account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.security.passwordmanager.R
import com.security.passwordmanager.data.DataType
import com.security.passwordmanager.data.DataViewModel
import com.security.passwordmanager.data.Website
import com.security.passwordmanager.databinding.ActivityPasswordBinding
import com.security.passwordmanager.settings.SettingsViewModel
import com.security.passwordmanager.settings.getStringExtra
import com.security.passwordmanager.ui.DataEditableRecyclerView

class PasswordActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_ADDRESS = "extra_address"
        private const val EXTRA_POSITION = "extra_position"

        fun getIntent(context: Context?, address : String, startPosition: Int = 1) : Intent {
            val intent = Intent(context, PasswordActivity::class.java)
            intent.putExtra(EXTRA_ADDRESS, address)
            intent.putExtra(EXTRA_POSITION, startPosition)
            return intent
        }
    }

    private lateinit var binding: ActivityPasswordBinding

    private lateinit var settings : SettingsViewModel
    private lateinit var dataViewModel : DataViewModel
    private lateinit var recyclerView : DataEditableRecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPasswordBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        settings = SettingsViewModel.getInstance(this)
        dataViewModel = ViewModelProvider(this)[DataViewModel::class.java]

        val address = intent.getStringExtra(EXTRA_ADDRESS, "")
        val startPosition = intent.getIntExtra(EXTRA_POSITION, 1)

        recyclerView = DataEditableRecyclerView(
            activity = this,
            recyclerView = binding.accountRecyclerView,
            key = address,
            type = DataType.WEBSITE
        )

        recyclerView.scrollToPosition(startPosition)

        // TODO: 07.03.2022 улучшить
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
        }

        binding.websiteName.nextFocusDownId = R.id.login
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        val website = recyclerView.getData(0) as Website

        binding.url.setText(website.address)
        binding.websiteName.setText(website.nameWebsite)

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
                if (binding.url.text.isBlank()) {
                    binding.url.error = getString(R.string.required_url)
                    return true
                } else if (binding.websiteName.text.isBlank()) {
                    binding.websiteName.error = getString(R.string.required_website_name)
                    return true
                }

                for (position in 0 until recyclerView.itemCount) {
                    val website = recyclerView.getData(position) as Website
                    website.nameWebsite = binding.websiteName.text.toString()
                    website.address = binding.url.text.toString()

                    if (position >= recyclerView.startCount)
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
}