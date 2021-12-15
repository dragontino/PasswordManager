package com.security.passwordmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.util.PatternsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.security.passwordmanager.ui.main.PasswordListActivity;

import java.util.Objects;

public class EmailPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "EmailPassword";

    private ProgressBar progressBar;
    private Support mSupport;
    private EditText mEmailField, mPasswordField;
    private Button signIn;
    private CheckBox isPasswordRemember;

    private TextView label, subtitle;

    private FirebaseAuth mAuth;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, EmailPasswordActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mSupport = Support.getInstance(this);

        label = findViewById(R.id.text_view_main_label);
        subtitle = findViewById(R.id.text_view_main_subtitle);

        mEmailField = findViewById(R.id.username);
        mPasswordField = findViewById(R.id.password);
        signIn = findViewById(R.id.signIn);
        Button signUp = findViewById(R.id.signUp);

        isPasswordRemember = findViewById(R.id.remember_password);
        isPasswordRemember.setChecked(mSupport.isPasswordRemembered());

        if (isPasswordRemember.isChecked())
            startActivity(PasswordListActivity.getIntent(this));

        isPasswordRemember.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && (TextUtils.isEmpty(mPasswordField.getText()) ||
                            TextUtils.isEmpty(mEmailField.getText())))
                isPasswordRemember.setChecked(false);

            mSupport.setPasswordIsRemembered(isChecked);
        });

        progressBar = findViewById(R.id.loading);

        signIn.setOnClickListener(this);
        signUp.setOnClickListener(this);

        addTextChangeListener(mEmailField, true);
        addTextChangeListener(mPasswordField, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem search = menu.findItem(R.id.menu_item_search);
        search.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_item_settings)
            startActivity(SettingsActivity.newIntent(this, false));
        return super.onOptionsItemSelected(item);
    }



    private void addTextChangeListener(EditText view, boolean email) {
        view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (email)
                    validateForm(s.toString(), null);
                else
                    validateForm(null, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }



    private void updateUI() {
        mSupport.updateThemeInScreen(getWindow(), Objects.requireNonNull(getSupportActionBar()));

        label.setTextColor(mSupport.getFontColor());
        subtitle.setTextColor(mSupport.getFontColor());

        signIn.setBackgroundResource(mSupport.getButtonRes());

        mEmailField.setTextColor(mSupport.getFontColor());
        mPasswordField.setTextColor(mSupport.getFontColor());

        mEmailField.setHintTextColor(mSupport.getDarkerGrayColor());
        mPasswordField.setHintTextColor(mSupport.getDarkerGrayColor());

        mEmailField.setBackgroundTintList(ColorStateList.valueOf(mSupport.getFontColor()));
        mPasswordField.setBackgroundTintList(ColorStateList.valueOf(mSupport.getFontColor()));

        isPasswordRemember.setTextColor(mSupport.getFontColor());
        isPasswordRemember.setButtonTintList(ColorStateList.valueOf(mSupport.getHeaderColor()));
    }


    private void registerUser() {
        String email = mEmailField.getText().toString().trim();
        String password = mPasswordField.getText().toString().trim();
        if (!validateForm(email, password)) {
            progressBar.setVisibility(View.GONE);
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mAuth = FirebaseAuth.getInstance();
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                                .setValue(email)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(
                                                this,
                                                R.string.register_successful,
                                                Toast.LENGTH_LONG).show();

                                        startActivity(PasswordListActivity.getIntent(this));
                                    }
                                    else
                                        Toast.makeText(
                                                this,
                                                R.string.register_failed,
                                                Toast.LENGTH_LONG).show();

                                    progressBar.setVisibility(View.GONE);
                                });
                    }
                    else {
                        Toast.makeText(
                                this,
                                R.string.register_failed,
                                Toast.LENGTH_LONG).show();

                        progressBar.setVisibility(View.GONE);
                    }
                });
    }



    private void loginUser() {
        String email = mEmailField.getText().toString().trim();
        String password = mPasswordField.getText().toString().trim();

        if (!validateForm(email, password)) {
            progressBar.setVisibility(View.GONE);
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        startActivity(PasswordListActivity.getIntent(this));
                    else
                        Toast.makeText(
                                this,
                                R.string.login_failed,
                                Toast.LENGTH_LONG).show();
        });
    }


    private boolean validateForm(@Nullable String email, @Nullable String password) {

        if (email != null) {
            if (TextUtils.isEmpty(email)) {
                mEmailField.setError(getString(R.string.required));
                mEmailField.requestFocus();
                return false;
            } else if (!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
                mEmailField.setError(getString(R.string.valid_email));
                mEmailField.requestFocus();
                return false;
            } else
                mEmailField.setError(null);
        }


        if (password != null) {
            if (TextUtils.isEmpty(password)) {
                mPasswordField.setError(getString(R.string.required));
                mPasswordField.requestFocus();
                return false;
            }
            else if (password.length() < 6) {
                mPasswordField.setError(getString(R.string.min_password_length));
                mPasswordField.requestFocus();
                return false;
            }
            else
                mPasswordField.setError(null);
        }

        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        progressBar.setVisibility(View.VISIBLE);

        switch (v.getId()) {
            case R.id.signUp:
                registerUser();
                break;
            case R.id.signIn:
//                startActivity(PasswordListActivity.getIntent(this));
                loginUser();
                break;
        }
    }
}