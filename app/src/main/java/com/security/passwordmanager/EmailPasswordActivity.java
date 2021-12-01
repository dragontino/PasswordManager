package com.security.passwordmanager;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.security.passwordmanager.ui.main.PasswordListActivity;

import java.util.Objects;

public class EmailPasswordActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";

    private ProgressBar progressBar;
    private Support mSupport;
    private EditText mEmailField, mPasswordField;
    private Button buttonSignIn, buttonSignUp;

    private TextView textViewLabel, textViewSubtitle;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mSupport = Support.get(this);

        textViewLabel = findViewById(R.id.text_view_main_label);
        textViewSubtitle = findViewById(R.id.text_view_main_subtitle);

        mEmailField = findViewById(R.id.username);
        mPasswordField = findViewById(R.id.password);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        buttonSignUp = findViewById(R.id.button_sign_up);

        buttonSignUp.setEnabled(true);
        progressBar = findViewById(R.id.loading);

        buttonSignUp.setOnClickListener(view -> {
            startActivity(PasswordListActivity.getIntent(this));
            progressBar.setVisibility(View.VISIBLE);
        });

        mAuthStateListener = firebaseAuth -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                //TODO Вход в приложение
                progressBar.setVisibility(View.VISIBLE);
            }
            else {
                //TODO выход из приложения
            }
            updateUI(user);
        };
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null)
            reload();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    private void reload() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem search = menu.findItem(R.id.menu_item_search);
        search.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_item_settings)
            startActivity(SettingsActivity.newIntent(this));
        return super.onOptionsItemSelected(item);
    }

    private void updateUI() {
        mSupport.updateThemeInScreen(getWindow(), Objects.requireNonNull(getSupportActionBar()));

        textViewLabel.setTextColor(mSupport.getFontColor());
        textViewSubtitle.setTextColor(mSupport.getFontColor());

        buttonSignIn.setBackgroundResource(mSupport.getButtonRes());

        mEmailField.setTextColor(mSupport.getFontColor());
        mPasswordField.setTextColor(mSupport.getFontColor());

        mEmailField.setHintTextColor(mSupport.getDarkerGrayColor());
        mPasswordField.setHintTextColor(mSupport.getDarkerGrayColor());

        mEmailField.setBackgroundTintList(ColorStateList.valueOf(mSupport.getFontColor()));
        mPasswordField.setBackgroundTintList(ColorStateList.valueOf(mSupport.getFontColor()));
    }


    private void updateUI(FirebaseUser user) {
        if (user != null) {

        }
    }


    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount: " + email);
        if (!validateForm())
            return;


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    }
                    else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(EmailPasswordActivity.this, "Authentication failed",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError(getString(R.string.required));
            valid = false;
        }
        else
            mEmailField.setError(null);

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError(getString(R.string.required));
            valid = false;
        }
        else
            mPasswordField.setError(null);

        return valid;
    }
}