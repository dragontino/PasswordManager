package com.security.passwordmanager.ui.bank;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.security.passwordmanager.R;
import com.security.passwordmanager.data.BankCard;
import com.security.passwordmanager.data.DataViewModel;
import com.security.passwordmanager.data.Website;
import com.security.passwordmanager.settings.SettingsViewModel;

import java.util.Objects;

public class BankCardActivity_old extends AppCompatActivity {

    private static final String EXTRA_NAME = "extra_bank_name";

    private SettingsViewModel settings;
    private DataViewModel mDataViewModel;

    private BankRecyclerView mRecyclerView;

    private EditText name, number, cardHolder, period, pin, cvv;
    private TextView head;
    private Button add_account, add_card;

    private String bankName;
//    private List<Data> mBankCardList;
    private int startCount;

//    private BottomSheet bottomDialogFragment;

    public static Intent getIntent(Context context, String bankName) {
        Intent intent = new Intent(context, BankCardActivity_old.class);
        intent.putExtra(EXTRA_NAME, bankName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card);

        settings = new ViewModelProvider(this).get(SettingsViewModel.class);
        mDataViewModel = new ViewModelProvider(this).get(DataViewModel.class);

        bankName = getIntent().getStringExtra(EXTRA_NAME);

        startCount = mRecyclerView.getItemCount();

        name = findViewById(R.id.bank_name);
        number = findViewById(R.id.card_number);
        cardHolder = findViewById(R.id.card_holder);
        period = findViewById(R.id.validity_period);
        cvv = findViewById(R.id.card_cvv);
        pin = findViewById(R.id.pin_code);

//        bottomDialogFragment = new BottomSheet(this);

        head = findViewById(R.id.bank_head);

        add_account = findViewById(R.id.add_account);
        add_card = findViewById(R.id.add_card);

        add_account.setOnClickListener(v -> mRecyclerView.addData(new Website()));

        add_card.setOnClickListener(v -> mRecyclerView.addData(new BankCard()));

        number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() % 4 == s.length() / 4 - 1 && count >= before)
                    number.append(" ");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        cardHolder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.password, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_item_save) {

            //TODO сделать проверку

            for (int i = 0; i < mRecyclerView.getItemCount(); i++) {
                View view = mRecyclerView.get(i);

            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {

        mRecyclerView.updateRecyclerView();

        settings.updateThemeInScreen(getWindow(), Objects.requireNonNull(getSupportActionBar()));
        name.setBackgroundResource(settings.getBackgroundRes());
        number.setBackgroundResource(settings.getBackgroundRes());
        cardHolder.setBackgroundResource(settings.getBackgroundRes());
        period.setBackgroundResource(settings.getBackgroundRes());
        cvv.setBackgroundResource(settings.getBackgroundRes());
        pin.setBackgroundResource(settings.getBackgroundRes());

        name.setTextColor(settings.getFontColor());
        head.setTextColor(settings.getFontColor());
        number.setTextColor(settings.getFontColor());
        cardHolder.setTextColor(settings.getFontColor());
        period.setTextColor(settings.getFontColor());
        cvv.setTextColor(settings.getFontColor());
        pin.setTextColor(settings.getFontColor());
    }
}