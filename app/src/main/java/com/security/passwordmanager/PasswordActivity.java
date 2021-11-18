package com.security.passwordmanager;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PasswordActivity extends AppCompatActivity {

    private static final String EXTRA_ADDRESS = "extra_address";

    private String login_saved;

    private Support support;
    private RecyclerView recyclerView;
    private AccountAdapter adapter;
    private DataLab mDataLab;
    private String address;

    private List<Data> accountList;

    ActivityResultLauncher<Data> launcher = registerForActivityResult(
            new PasswordInfoActivity.InfoContract(
                    PasswordInfoActivity.TYPE_ACCOUNT),
            result -> {
                if (result == null) return;

                if (result.equals(PasswordInfoActivity.ACTION_RENAME))
                    login_saved = result;
                else if (result.equals(PasswordInfoActivity.ACTION_DELETE)) {
                    accountList = mDataLab.getAccountList(address);
                    if (accountList.size() == 0) finish();
                }

                adapter.notifyDataSetChanged();
            });

    private EditText url;
    private EditText name;

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

        url = findViewById(R.id.url);
        name = findViewById(R.id.name);

        url.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0)
                    for (int i = 0; i < accountList.size(); i++) {
                        Data data = accountList.get(i);
                        data.setAddress(s.toString());
                        accountList.set(i, data);
                    }
            }
        });
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0)
                    for (int i = 0; i < accountList.size(); i++) {
                        Data data = accountList.get(i);
                        data.setNameWebsite(s.toString());
                        accountList.set(i, data);
                    }
            }
        });

        name.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if (name.getText().length() == 0 && url.getText().length() != 0) {
                    StringBuilder builder = new StringBuilder(url.getText());

                    if (builder.toString().contains("www."))
                        builder.delete(0, 5);
                    if (builder.toString().contains(".com"))
                        builder.delete(builder.length() - 4, builder.length());
                    if (builder.toString().contains(".ru"))
                        builder.delete(builder.length() - 3, builder.length());

                    char first = Character.toUpperCase(builder.charAt(0));
                    builder.setCharAt(0, first);

                    name.setText(builder.toString());
                }
            }
        });

        Button add = findViewById(R.id.add_account);
        add.setOnClickListener(v -> {
            accountList.add(new Data());
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(accountList.size() - 1);
        });

        address = getIntent().getStringExtra(EXTRA_ADDRESS);
        accountList = mDataLab.getAccountList(address);
        if (accountList.size() == 0)
            accountList.add(new Data());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {

        url.setText(accountList.get(0).getAddress());
        name.setText(accountList.get(0).getNameWebsite());

        if (adapter == null) {
            adapter = new AccountAdapter();
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
                TextView nameAccount = v.findViewById(R.id.name_of_account);
                TextView login = v.findViewById(R.id.edit_text_login);
                TextView password = v.findViewById(R.id.edit_text_password);
                TextView comment = v.findViewById(R.id.edit_text_comment);

                String name_account = nameAccount.getText().toString();

//                if (name_account.equals(getString(R.string.account, position)))
//                    name_account = "";

                if (position >= accountList.size()) {
                    Data data = new Data(
                            UUID.randomUUID(),
                            url.getText().toString(),
                            name.getText().toString(),
                            name_account,
                            login.getText().toString(),
                            password.getText().toString(),
                            comment.getText().toString()
                    );

                    mDataLab.addData(data);
                }
                else {
                    Data data = accountList.get(position)
                    .setAddress(url.getText().toString())
                    .setNameWebsite(name.getText().toString())
                    .setNameAccount(nameAccount.getText().toString())
                    .setLogin(login.getText().toString())
                    .setPassword(password.getText().toString())
                    .setComment(comment.getText().toString());

                    mDataLab.updateData(data);
                }
            }
            finish();
            return true;
        }
        else if (item.getItemId() == R.id.menu_item_delete) {
            for (int i = 0; i < accountList.size(); i++) {
                mDataLab.deleteData(accountList.get(i));
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }





    private class AccountAdapter extends RecyclerView.Adapter<AccountHolder> {

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
            accountList.set(position, data);
        }

        @Override
        public int getItemCount() {
            return accountList.size();
        }
    }





    private class AccountHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final EditText name_of_account;
        private final Button edit_name;
        private final EditText login;
        private final EditText password;
        private final EditText comment;

        private Data data;

        private final Button subtitle;
        private boolean isPasswordVisible;
        private int position;

        public AccountHolder(@NonNull View itemView) {
            super(itemView);
            name_of_account = itemView.findViewById(R.id.name_of_account);
            edit_name = itemView.findViewById(R.id.edit_name_of_account);
            login = itemView.findViewById(R.id.edit_text_login);
            password = itemView.findViewById(R.id.edit_text_password);
            subtitle = itemView.findViewById(R.id.subtitle);
            comment = itemView.findViewById(R.id.edit_text_comment);
            isPasswordVisible = false;

//            launcher = registerForActivityResult(
//                    new PasswordInfoActivity.InfoContract(
//                            PasswordInfoActivity.TYPE_ACCOUNT),
//                    result -> {
//                        switch (result) {
//                            case PasswordInfoActivity.ACTION_RENAME:
//                                changeBlockHead();
//                                break;
//                            case PasswordInfoActivity.ACTION_DELETE:
//                                accountList.remove(position);
//                                adapter.notifyDataSetChanged();
//                                break;
//                        }
//                    });

//            subtitle.setOnClickListener(this);
            edit_name.setOnClickListener(this);
        }

        private void bindAccount(Data data, int position) {

            this.data = data;
            this.position = position;

            login.setText(data.getLogin());
            password.setText(data.getPassword());
            comment.setText(data.getComment());
            if (data.getNameAccount().equals(""))
                name_of_account.setText(getString(R.string.account, position));
            else
                name_of_account.setText(data.getNameAccount());

            if (data.getLogin().equals(login_saved)) {
                changeBlockHead();
                login_saved = null;
            }

            login.setBackgroundResource(support.getBackgroundRes());
            password.setBackgroundResource(support.getBackgroundRes());
            comment.setBackgroundResource(support.getBackgroundRes());

            edit_name.setBackgroundTintList(ColorStateList.valueOf(support.getFontColor()));

            login.setHintTextColor(getColor(android.R.color.darker_gray));
            password.setHintTextColor(getColor(android.R.color.darker_gray));
            comment.setHintTextColor(getColor(android.R.color.darker_gray));

            login.setTextColor(support.getFontColor());
            password.setTextColor(support.getFontColor());
            comment.setTextColor(support.getFontColor());
            subtitle.setTextColor(support.getFontColor());

            if (support.isLightTheme())
                name_of_account.setTextColor(getColor(android.R.color.darker_gray));
            else name_of_account.setTextColor(Color.WHITE);

            itemView.setBackgroundColor(support.getLayoutBackgroundColor());

            login.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() != 0)
                        data.setLogin(s.toString());
                }
            });

            password.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() != 0 && !s.toString().equals(getString(R.string.hidden_password)))
                        data.setPassword(s.toString());
                }
            });

            comment.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() != 0)
                        data.setComment(s.toString());
                }
            });
        }


        private void updatePasswordText() {
            password.setTextIsSelectable(isPasswordVisible);
            if (isPasswordVisible) {
                password.setInputType(InputType.TYPE_CLASS_TEXT);
                password.setText(data.getPassword());
                subtitle.setText(R.string.hide_password);
            }
            else {
                password.setInputType(InputType.TYPE_NULL);
                password.setText(R.string.hidden_password);
                subtitle.setText(R.string.show_password);
            }
        }


        @Override
        public void onClick(View v) {
//            isPasswordVisible = !isPasswordVisible;
//            updatePasswordText();
            if (v.getId() == R.id.edit_name_of_account)
                launcher.launch(data);
        }

        private void changeBlockHead() {
            boolean block = name_of_account.isCursorVisible();
            //true - заблокирует, false - разблокирует
            String full = getString(R.string.account, position);
            String Null = "";

            name_of_account.setClickable(!block);
            name_of_account.setEnabled(!block);
            name_of_account.setCursorVisible(!block);
            name_of_account.setFocusable(!block);

            if (block && name_of_account.getText().length() == 0) {
                name_of_account.setText(full);
                name_of_account.setInputType(InputType.TYPE_NULL);
            } else if (!block && name_of_account.getText().toString().equals(full)) {
                name_of_account.setText(Null);
                name_of_account.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            }
        }
    }
}