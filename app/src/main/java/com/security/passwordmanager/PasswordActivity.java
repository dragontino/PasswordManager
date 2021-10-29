package com.security.passwordmanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PasswordActivity extends AppCompatActivity {

    private static final String EXTRA_ADDRESS = "extra_address";

    private Support support;
    private RecyclerView recyclerView;
    private AccountAdapter adapter;
    private DataLab mDataLab;

    private EditText url;
    private EditText name;

    private int countAccounts;

    private String address;
    private List<Data> accountList;

    public static Intent newIntent(Context context, String address) {
        Intent intent = new Intent(context, PasswordActivity.class);
        intent.putExtra(EXTRA_ADDRESS, address);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        support = Support.get(this);
        recyclerView = findViewById(R.id.account_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDataLab = DataLab.get(this);

        countAccounts = 1;

        url = findViewById(R.id.url);
        name = findViewById(R.id.name);
        Button add = findViewById(R.id.add_account);
        add.setOnClickListener(v -> {
            countAccounts++;
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        });

        address = getIntent().getStringExtra(EXTRA_ADDRESS);
        accountList = mDataLab.getAccountList(address);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        String urlString = "", nameString = "";

        if (accountList.size() > 0) {
            urlString = accountList.get(0).getAddress();
            nameString = accountList.get(0).getName();
        }
        url.setText(urlString);
        name.setText(nameString);

        if (adapter == null) {
            adapter = new AccountAdapter(accountList);
            recyclerView.setAdapter(adapter);
        }
        else adapter.notifyDataSetChanged();

        support.updateThemeInScreen(getWindow(), Objects.requireNonNull(getSupportActionBar()));

        url.setBackgroundResource(support.getBackgroundRes());
        name.setBackgroundResource(support.getBackgroundRes());
        url.setHintTextColor(getColor(android.R.color.darker_gray));
        name.setHintTextColor(getColor(android.R.color.darker_gray));
        url.setTextColor(support.getFontColor());
        name.setTextColor(support.getFontColor());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.password, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_item_save) {
            if (url.getText().toString().equals("") || url.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), "Введите адрес сайта и название!",
                        Toast.LENGTH_SHORT).show();
                return true;
            }

            for (int position = 0; position < adapter.getItemCount(); position++) {
                View v = recyclerView.getChildAt(position);
                TextView login = v.findViewById(R.id.edit_text_login);
                TextView password = v.findViewById(R.id.edit_text_password);
                TextView comment = v.findViewById(R.id.edit_text_comment);

                if (position >= accountList.size()) {
                    Data data = new Data(
                            UUID.randomUUID(),
                            url.getText().toString(),
                            name.getText().toString(),
                            login.getText().toString(),
                            password.getText().toString(),
                            comment.getText().toString()
                    );

                    mDataLab.addData(data);
                }
                else {
                    Data data = accountList.get(position);
                    data.setAddress(url.getText().toString());
                    data.setName(name.getText().toString());
                    data.setLogin(login.getText().toString());
                    data.setPassword(password.getText().toString());
                    data.setComment(comment.getText().toString());

                    mDataLab.updateData(data);
                }
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }





    private class AccountAdapter extends RecyclerView.Adapter<AccountHolder> {

        private final List<Data> accountList;

        public AccountAdapter(List<Data> accountList) {
            this.accountList = accountList;
        }

        @NonNull
        @Override
        public AccountHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.list_item_new_account, parent, false);

            return new AccountHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AccountHolder holder, int position) {
            Data data;
            if (accountList.size() == 0)
                data = new Data();
            else
                data = accountList.get(position);

            holder.bindAccount(data, position + 1);
        }

        @Override
        public int getItemCount() {
            if (accountList.size() == 0) return countAccounts;
            return accountList.size();
        }
    }





    private class AccountHolder extends RecyclerView.ViewHolder {

        private final TextView name_of_account;
        private final EditText login;
        private final EditText password;
        private final EditText comment;

        public AccountHolder(@NonNull View itemView) {
            super(itemView);
            name_of_account = itemView.findViewById(R.id.name_of_account);
            login = itemView.findViewById(R.id.edit_text_login);
            password = itemView.findViewById(R.id.edit_text_password);
            comment = itemView.findViewById(R.id.edit_text_comment);

            if (support.isLightTheme()) {
                itemView.setBackgroundColor(getResources()
                        .getColor(R.color.light_gray, getTheme()));
                name_of_account.setTextColor(getColor(android.R.color.darker_gray));
            } else {
                itemView.setBackgroundColor(getColor(R.color.gray));
                name_of_account.setTextColor(Color.WHITE);
            }
        }

        private void bindAccount(Data data, int position) {
            login.setText(data.getLogin());
            password.setText(data.getPassword());
            comment.setText(data.getComment());
            name_of_account.setText(getString(R.string.account, position));

            login.setBackgroundResource(support.getBackgroundRes());
            password.setBackgroundResource(support.getBackgroundRes());
            comment.setBackgroundResource(support.getBackgroundRes());

            login.setHintTextColor(getColor(android.R.color.darker_gray));
            password.setHintTextColor(getColor(android.R.color.darker_gray));
            comment.setHintTextColor(getColor(android.R.color.darker_gray));

            login.setTextColor(support.getFontColor());
            password.setTextColor(support.getFontColor());
            comment.setTextColor(support.getFontColor());
        }
    }
}