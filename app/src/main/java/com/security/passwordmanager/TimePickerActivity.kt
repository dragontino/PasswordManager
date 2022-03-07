package com.security.passwordmanager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TimePicker
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.security.passwordmanager.settings.SettingsViewModel
import java.util.*

class TimePickerActivity : AppCompatActivity() {

    companion object {
        private const val ARG_TIME = "time"
        private const val ARG_TITLE = "title"
        private const val EXTRA_TIME = "extra_time"

        fun newIntent(context: Context, calendar: Calendar, @StringRes title : Int) : Intent {
            val intent = Intent(context, TimePickerActivity::class.java)
            intent.putExtra(ARG_TIME, calendar)
            intent.putExtra(ARG_TITLE, title)

            return intent
        }

        fun parseIntent(intent: Intent) =
            intent.getSerializableExtra(EXTRA_TIME) as Calendar



        class TimePickerActivityContract(@StringRes private val title: Int) :
            ActivityResultContract<Calendar, Calendar?>() {

            override fun createIntent(context: Context, input: Calendar) =
                newIntent(context, input, title)

            override fun parseResult(resultCode: Int, intent: Intent?) = when {
                resultCode != RESULT_OK || intent == null -> null
                else -> parseIntent(intent)
            }
        }

    }

    private var timePicker : TimePicker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_time)

        val settings = SettingsViewModel.getInstance(this)
        val calendar = intent.getSerializableExtra(ARG_TIME) as Calendar
        val title = intent.getIntExtra(ARG_TITLE, R.string.start_time)

        setTitle(title)

        val hours = calendar[Calendar.HOUR_OF_DAY]
        val minutes = calendar[Calendar.MINUTE]

        timePicker = layoutInflater.inflate(
            if (settings.isLightTheme()) R.layout.time_picker_light
            else R.layout.time_picker_dark,
            null
        ) as TimePicker

        timePicker?.setIs24HourView(true)
        timePicker?.hour = hours
        timePicker?.minute = minutes

        (findViewById<View>(R.id.dialog_time_picker) as LinearLayout).addView(timePicker)

        val buttonCancel = findViewById<Button>(R.id.dialog_button_cancel)
        val buttonOk = findViewById<Button>(R.id.dialog_button_ok)

        findViewById<View>(R.id.constraint_layout_buttons).setBackgroundColor(settings.backgroundColor)

        buttonCancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        buttonOk.setOnClickListener {
            calendar[Calendar.HOUR_OF_DAY] = timePicker?.hour ?: 0
            calendar[Calendar.MINUTE] = timePicker?.minute ?: 0

            val intent = Intent()
            intent.putExtra(EXTRA_TIME, calendar)
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}