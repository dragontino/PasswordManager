package com.security.passwordmanager.ui.bank;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.security.passwordmanager.BottomSheet;
import com.security.passwordmanager.R;
import com.security.passwordmanager.data.BankCard;
import com.security.passwordmanager.data.Data;
import com.security.passwordmanager.data.DataViewModel;
import com.security.passwordmanager.data.Website;
import com.security.passwordmanager.settings.Support;

import java.util.List;
import java.util.Objects;

public class BankCardActivity extends AppCompatActivity {

    private static final String EXTRA_NAME = "extra_bank_name";

    private Support mSupport;
    private DataViewModel mDataViewModel;

    private RecyclerView mRecyclerView;
    private BankAdapter mBankAdapter;

    private EditText name, number, cardHolder, period, pin, cvv;
    private TextView head;
    private Button add_account, add_card;

    private String bankName;
    private List<Data> mBankCardList;
    private int startCount;

    private BottomSheet mBottomSheet;

    public static Intent getIntent(Context context, String bankName) {
        Intent intent = new Intent(context, BankCardActivity.class);
        intent.putExtra(EXTRA_NAME, bankName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card);
        mSupport = Support.getInstance(this);
        mDataViewModel = new ViewModelProvider(this).get(DataViewModel.class);

        mRecyclerView = findViewById(R.id.bank_card_recycler_view);

        name = findViewById(R.id.bank_name);
        number = findViewById(R.id.card_number);
        cardHolder = findViewById(R.id.card_holder);
        period = findViewById(R.id.validity_period);
        cvv = findViewById(R.id.card_cvv);
        pin = findViewById(R.id.pin_code);

        bankName = getIntent().getStringExtra(EXTRA_NAME);
        mBankCardList = mDataViewModel.getAccountList(bankName, Data.TYPE_BANK_CARD);
        startCount = mBankCardList.size();

        if (mBankCardList.size() == 0)
            mBankCardList.add(new BankCard());

        mBottomSheet = new BottomSheet(this);

        head = findViewById(R.id.bank_head);

        add_account = findViewById(R.id.add_account);
        add_card = findViewById(R.id.add_new_card);

        add_account.setOnClickListener(v -> {
            mBankCardList.add(new Website());

        });

        add_card.setOnClickListener(v -> {
            mBankCardList.add(new BankCard());

        });

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
            BankCard bankCard = new BankCard();
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

        if (mBankAdapter == null) {
            mBankAdapter = new BankAdapter();
            mRecyclerView.setAdapter(mBankAdapter);
        }
        //TODO обновление notify

        mSupport.updateThemeInScreen(getWindow(), Objects.requireNonNull(getSupportActionBar()));
        name.setBackgroundResource(mSupport.getBackgroundRes());
        number.setBackgroundResource(mSupport.getBackgroundRes());
        cardHolder.setBackgroundResource(mSupport.getBackgroundRes());
        period.setBackgroundResource(mSupport.getBackgroundRes());
        cvv.setBackgroundResource(mSupport.getBackgroundRes());
        pin.setBackgroundResource(mSupport.getBackgroundRes());

        name.setTextColor(mSupport.getFontColor());
        head.setTextColor(mSupport.getFontColor());
        number.setTextColor(mSupport.getFontColor());
        cardHolder.setTextColor(mSupport.getFontColor());
        period.setTextColor(mSupport.getFontColor());
        cvv.setTextColor(mSupport.getFontColor());
        pin.setTextColor(mSupport.getFontColor());
    }


    private class BankAdapter extends RecyclerView.Adapter<BankHolder> {

        @NonNull
        @Override
        public BankHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater()
                    .inflate(R.layout.list_item_new_bank_card, parent, false);

            return new BankHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BankHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return mBankCardList.size();
        }
    }


    private class BankHolder extends RecyclerView.ViewHolder {

        public BankHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}