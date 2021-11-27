package com.security.passwordmanager.ui.account;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.security.passwordmanager.BottomSheet;
import com.security.passwordmanager.Data;
import com.security.passwordmanager.DataLab;
import com.security.passwordmanager.R;
import com.security.passwordmanager.Support;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PasswordActivity extends AppCompatActivity {

    private static final String EXTRA_ADDRESS = "extra_address";

    private Support support;
    private RecyclerView recyclerView;
    private AccountAdapter adapter;
    private DataLab mDataLab;
    private BottomSheet mBottomSheet;

    private String address;

    private List<Data> accountList;

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

        mBottomSheet = new BottomSheet(this);

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

        name.setNextFocusDownId(R.id.edit_text_login);
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

                if (name_account.equals(getNameAccountStart(position + 1)))
                    name_account = "";

                if (login.getText().length() > 0 && password.getText().length() > 0)
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
                    } else {
                        Data data = accountList.get(position)
                                .setAddress(url.getText().toString())
                                .setNameWebsite(name.getText().toString())
                                .setNameAccount(name_account)
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
            for (int i = 0; i < accountList.size(); i++)
                mDataLab.deleteData(accountList.get(i).getAddress());
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private String getNameAccountStart(int position) {
        return getString(R.string.account_start, position);
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

        private final ImageButton buttonVisibility;
        private boolean isPasswordVisible;
        private int position;

        private @StringRes int renamingText;

        public AccountHolder(@NonNull View itemView) {
            super(itemView);
            name_of_account = itemView.findViewById(R.id.name_of_account);
            edit_name = itemView.findViewById(R.id.edit_name_of_account);
            login = itemView.findViewById(R.id.edit_text_login);
            password = itemView.findViewById(R.id.edit_text_password);
            buttonVisibility = itemView.findViewById(R.id.field_item_button_visibility);
            comment = itemView.findViewById(R.id.edit_text_comment);
            isPasswordVisible = false;
            renamingText = R.string.rename_account;

            edit_name.setOnClickListener(this);
            buttonVisibility.setOnClickListener(this);
        }

        private void bindAccount(Data data, int position) {

            this.data = data;
            this.position = position;

            login.setText(data.getLogin());
            password.setText(data.getPassword());
            comment.setText(data.getComment());
            if (data.getNameAccount().equals(""))
                name_of_account.setText(getNameAccountStart(position));
            else
                name_of_account.setText(data.getNameAccount());

            login.setBackgroundResource(support.getBackgroundRes());
            password.setBackgroundResource(support.getBackgroundRes());
            comment.setBackgroundResource(support.getBackgroundRes());

            edit_name.setBackgroundTintList(ColorStateList.valueOf(support.getFontColor()));

            buttonVisibility.setBackgroundColor(support.getBackgroundColor());

            login.setHintTextColor(support.getDarkerGrayColor());
            password.setHintTextColor(support.getDarkerGrayColor());
            comment.setHintTextColor(support.getDarkerGrayColor());

            login.setTextColor(support.getFontColor());
            password.setTextColor(support.getFontColor());
            comment.setTextColor(support.getFontColor());
            buttonVisibility.setImageTintList(ColorStateList.valueOf(support.getFontColor()));

            name_of_account.setHintTextColor(support.getDarkerGrayColor());
            name_of_account.setTextColor(support.getDarkerGrayColor());

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
                    if (s.length() != 0)
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

            name_of_account.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    data.setNameAccount(name_of_account.getText().toString());
                    changeHeading();
                    renamingText = R.string.rename_account;
                    mBottomSheet.updateImageAndText(BottomSheet.VIEW_EDIT, renamingText, null);
                    return true;
                }
                return false;
            });
        }


        private void updatePasswordText() {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                password.setInputType(InputType.TYPE_CLASS_TEXT);
                buttonVisibility.setImageResource(R.drawable.ic_outline_visibility_off_24);
                buttonVisibility.setContentDescription(getString(R.string.hide_password));
            }
            else {
                password.setInputType(129);
                buttonVisibility.setImageResource(R.drawable.ic_visibility_24);
                buttonVisibility.setContentDescription(getString(R.string.show_password));
            }
        }


        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.edit_name_of_account) {
                mBottomSheet.updateImageAndText(
                        new int[] {
                                renamingText,
                                R.string.copy_info,
                                R.string.delete_account
                        },
                        new int[]{
                                R.drawable.ic_outline_edit_24,
                                R.drawable.ic_baseline_content_copy_24,
                                R.drawable.ic_baseline_delete_24
                        }
                );

                mBottomSheet.setOnClickListener(BottomSheet.VIEW_EDIT, v1 -> {
                    changeHeading();
                    mBottomSheet.stop();
                    renamingText = name_of_account.isCursorVisible() ?
                            R.string.cancel_renaming_account :
                            R.string.rename_account;
                });

                mBottomSheet.setOnClickListener(BottomSheet.VIEW_COPY, view -> {
                    mDataLab.copyData(data);
                    mBottomSheet.stop();
                });

                mBottomSheet.setOnClickListener(BottomSheet.VIEW_DELETE, v1 -> {
                    mBottomSheet.stop();
                    mDataLab.deleteData(data);
                    accountList = mDataLab.getAccountList(address);
                    if (accountList.size() == 0) finish();

                    adapter.notifyDataSetChanged();
                });
                mBottomSheet.start();
            }
            else if (v.getId() == R.id.field_item_button_visibility) {
                updatePasswordText();
            }
        }

        private void changeHeading() {
            boolean blocking = name_of_account.isCursorVisible();
            //true - заблокирует, false - разблокирует
            String full = getNameAccountStart(position);
            String Null = "";

            name_of_account.setEnabled(!blocking);
            name_of_account.setCursorVisible(!blocking);

            if (blocking && name_of_account.getText().length() == 0)
                name_of_account.setText(full);
            else if (!blocking && name_of_account.getText().toString().equals(full))
                name_of_account.setText(Null);

            if (blocking)
                name_of_account.setTextColor(support.getDarkerGrayColor());
            else
                name_of_account.setTextColor(support.getFontColor());
        }
    }
}