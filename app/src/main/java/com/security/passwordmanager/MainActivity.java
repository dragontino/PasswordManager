package com.security.passwordmanager;

<<<<<<< HEAD
<<<<<<< HEAD
=======
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
>>>>>>> 30ee146 (версия 2.2.1 от 23.11.2021)
=======
>>>>>>> ea46599 (версия 2.2.1 от 23.11.2021)
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.security.passwordmanager.ui.main.PasswordListActivity;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private Support mSupport;
    private EditText username, password;
    Button buttonSignIn, buttonSignUp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSupport = Support.get(this);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        buttonSignUp = findViewById(R.id.buttonSignUp);

        buttonSignUp.setEnabled(true);
        progressBar = findViewById(R.id.loading);

        buttonSignUp.setOnClickListener(view -> {
            startActivity(PasswordListActivity.getIntent(this));
            progressBar.setVisibility(View.VISIBLE);
        });
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_item_settings)
            startActivity(SettingsActivity.newIntent(this));
        return super.onOptionsItemSelected(item);
    }

    private void updateUI() {
        mSupport.updateThemeInScreen(getWindow(), getSupportActionBar());

        username.setBackgroundResource(mSupport.getBackgroundRes());
        password.setBackgroundResource(mSupport.getBackgroundRes());
        buttonSignIn.setBackgroundResource(mSupport.getButtonRes());
        buttonSignUp.setTextColor(mSupport.getHeaderColor());

        username.setTextColor(mSupport.getFontColor());
        password.setTextColor(mSupport.getFontColor());

        username.setHintTextColor(mSupport.getDarkerGrayColor());
        password.setHintTextColor(mSupport.getDarkerGrayColor());
    }
}