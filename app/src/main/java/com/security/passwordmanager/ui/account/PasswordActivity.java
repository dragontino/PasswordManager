package com.security.passwordmanager.ui.account;

import android.annotation.SuppressLint;
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

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.security.passwordmanager.BottomSheet;
import com.security.passwordmanager.R;
import com.security.passwordmanager.data.Data;
import com.security.passwordmanager.data.DataViewModel;
import com.security.passwordmanager.data.Website;
import com.security.passwordmanager.settings.Support;

import java.util.List;
import java.util.Objects;

public class PasswordActivity extends AppCompatActivity {

    private static final String EXTRA_ADDRESS = "extra_address";

    private RecyclerView recyclerView;
    private AccountAdapter adapter;

    private BottomSheet mBottomSheet;

    private Support mSupport;
    private DataViewModel mDataViewModel;

    private String address;
    private List<Data> accountList;
    private int startCount;

    private EditText url;
    private EditText name;

    public static Intent newIntent(Context context, String address) {
        Intent intent = new Intent(context, PasswordActivity.class);
        intent.putExtra(EXTRA_ADDRESS, address);
        return intent;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        mSupport = Support.getInstance(this);
        recyclerView = findViewById(R.id.account_recycler_view);
        mDataViewModel = new ViewModelProvider(this).get(DataViewModel.class);

        address = getIntent().getStringExtra(EXTRA_ADDRESS);
        accountList = mDataViewModel.getAccountList(address, Data.TYPE_WEBSITE);

        startCount = accountList.size();

        if (accountList.size() == 0)
            accountList.add(new Website());

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
                        Website data = getAccount(i);
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
                        Website data = getAccount(i);
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
            accountList.add(new Website());
            adapter.notifyDataSetChanged();
            Objects.requireNonNull(recyclerView.getLayoutManager())
                    .scrollToPosition(accountList.size() - 1);
        });


        name.setNextFocusDownId(R.id.edit_text_login);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateUI() {

        url.setText(getAccount(0).getAddress());
        name.setText(getAccount(0).getNameWebsite());

        if (adapter == null) {
            adapter = new AccountAdapter();
            recyclerView.setAdapter(adapter);
        }
        else adapter.notifyDataSetChanged();

        mSupport.updateThemeInScreen(getWindow(), Objects.requireNonNull(getSupportActionBar()));

        url.setBackgroundResource(mSupport.getBackgroundRes());
        name.setBackgroundResource(mSupport.getBackgroundRes());
        url.setTextColor(mSupport.getFontColor());
        name.setTextColor(mSupport.getFontColor());
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.password, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_item_save) {
            if (url.getText().toString().equals("")) {
                url.setError("Введите адрес сайта!");
                return true;
            }
            else if (name.getText().toString().equals("")) {
                name.setError("Введите название сайта!");
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

                if (login.getText().length() <= 0) {
                    login.setError(getString(R.string.required));
                    return true;
                }
                else if (password.getText().length() <= 0) {
                    password.setError(getString(R.string.required));
                    return true;
                }

                Website website = getAccount(position);
                website.setAddress(url.getText().toString());
                website.setNameWebsite(name.getText().toString());
                website.setNameAccount(name_account);
                website.setLogin(login.getText().toString());
                website.setPassword(password.getText().toString());
                website.setComment(comment.getText().toString());

                if (position >= startCount)
                    mDataViewModel.addData(website);
//                        mDataLab.addData(data);
                else
                    mDataViewModel.updateData(website);
//                        mDataLab.updateData(data);
            }
            finish();
            return true;
        }
        else if (item.getItemId() == R.id.menu_item_delete) {
            mDataViewModel.deleteData(getAccount(0).getAddress(), Data.TYPE_WEBSITE);
//                mDataLab.deleteData(getAccount(i).getAddress(), Data.TYPE_WEBSITE);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private String getNameAccountStart(int position) {
        return getString(R.string.account_start, position);
    }


    private Website getAccount(int index) {
        return (Website) accountList.get(index);
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
            Website data;
            if (accountList.size() == 0)
                data = new Website();
            else
                data = getAccount(position);

            holder.bindAccount(data, position + 1);
            accountList.set(position, data);
        }

        @Override
        public int getItemCount() {
            return accountList.size();
        }
    }





    private class AccountHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final EditText nameAccount;
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
            nameAccount = itemView.findViewById(R.id.name_of_account);
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

        private void bindAccount(Website data, int position) {

            this.data = data;
            this.position = position;

            login.setText(data.getLogin());
            password.setText(data.getPassword());
            comment.setText(data.getComment());
            if (data.getNameAccount().equals(""))
                nameAccount.setText(getNameAccountStart(position));
            else
                nameAccount.setText(data.getNameAccount());

            login.setBackgroundResource(mSupport.getBackgroundRes());
            password.setBackgroundResource(mSupport.getBackgroundRes());
            comment.setBackgroundResource(mSupport.getBackgroundRes());

            edit_name.setBackgroundTintList(ColorStateList.valueOf(mSupport.getFontColor()));

            buttonVisibility.setBackgroundColor(mSupport.getBackgroundColor());

            login.setTextColor(mSupport.getFontColor());
            password.setTextColor(mSupport.getFontColor());
            comment.setTextColor(mSupport.getFontColor());
            buttonVisibility.setImageTintList(ColorStateList.valueOf(mSupport.getFontColor()));

            nameAccount.setHintTextColor(mSupport.getDarkerGrayColor());
            nameAccount.setTextColor(mSupport.getDarkerGrayColor());

            itemView.setBackgroundColor(mSupport.getLayoutBackgroundColor());

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

            nameAccount.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    data.setNameAccount(nameAccount.getText().toString());
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


        @SuppressLint("NotifyDataSetChanged")
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
                    renamingText = nameAccount.isCursorVisible() ?
                            R.string.cancel_renaming_account :
                            R.string.rename_account;
                });

                mBottomSheet.setOnClickListener(BottomSheet.VIEW_COPY, view -> {
                    mDataViewModel.copyData(data);
//                    mDataLab.copyWebsite((Website) data);
                    mBottomSheet.stop();
                });

                mBottomSheet.setOnClickListener(BottomSheet.VIEW_DELETE, v1 -> {
                    mBottomSheet.stop();
                    mDataViewModel.deleteData(data);
                    accountList = mDataViewModel.getAccountList(address, Data.TYPE_WEBSITE);
//                    mDataLab.deleteData(data);
//                    accountList = mDataLab.getAccountList(address, Data.TYPE_WEBSITE);
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
            boolean blocking = nameAccount.isCursorVisible();
            //true - заблокирует, false - разблокирует
            String full = getNameAccountStart(position);
            String Null = "";

            nameAccount.setEnabled(!blocking);
            nameAccount.setCursorVisible(!blocking);

            if (blocking && nameAccount.getText().length() == 0)
                nameAccount.setText(full);
            else if (!blocking && nameAccount.getText().toString().equals(full))
                nameAccount.setText(Null);

            if (blocking) {
                nameAccount.setTextColor(mSupport.getDarkerGrayColor());
                nameAccount.setBackgroundTintList(ColorStateList.valueOf(mSupport.getDarkerGrayColor()));
            } else {
                nameAccount.setTextColor(mSupport.getFontColor());
                nameAccount.setBackgroundTintList(ColorStateList.valueOf(mSupport.getHeaderColor()));
            }
        }
    }
}