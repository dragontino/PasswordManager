package com.security.passwordmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.UUID;

public class PasswordActivity extends AppCompatActivity {

    private static final String UUID = "uuid";

    private Support support;
    private RecyclerView recyclerView;
    private AccountAdapter adapter;

    private Data mData;

    public static Intent newIntent(Context context, UUID passwordId) {
        Intent intent = new Intent(context, PasswordActivity.class);
        intent.putExtra(UUID, passwordId);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        support = Support.get(this);
        recyclerView = findViewById(R.id.account_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        UUID uuid = (UUID) getIntent().getSerializableExtra(UUID);
        mData = new Data(uuid);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        if (adapter == null) {
            adapter = new AccountAdapter();
            recyclerView.setAdapter(adapter);
        }
        else adapter.notifyDataSetChanged();

        support.updateThemeInScreen(getWindow(), getSupportActionBar());
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

        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }





    private class AccountHolder extends RecyclerView.ViewHolder {

        private EditText login;
        private EditText password;
        private EditText comment;

        public AccountHolder(@NonNull View itemView) {
            super(itemView);
            login = itemView.findViewById(R.id.edit_text_login);
            password = itemView.findViewById(R.id.edit_text_password);
            comment = itemView.findViewById(R.id.edit_text_comment);
        }

        private void bindAccount() {

        }
    }
}