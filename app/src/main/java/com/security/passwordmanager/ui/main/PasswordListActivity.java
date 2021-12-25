package com.security.passwordmanager.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.security.passwordmanager.BottomSheet;
import com.security.passwordmanager.R;
import com.security.passwordmanager.settings.Support;
import com.security.passwordmanager.ui.account.PasswordActivity;
import com.security.passwordmanager.ui.bank.BankCardActivity;

import java.util.Objects;

public class PasswordListActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private Support mSupport;
    private NavigationView navigationView;
    private FloatingActionButton fab;
    private LinearLayout headerLayout;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, PasswordListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_list);

        mSupport = Support.getInstance(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        BottomSheet bottomSheet = new BottomSheet(this);

//        bottomSheet
//                .addView(R.drawable.ic_baseline_account_circle_24, R.string.password_label, v -> {
//                    startActivity(PasswordActivity.newIntent(this, ""));
//                    bottomSheet.stop();
//                });
//
//        bottomSheet.addView(R.drawable.ic_baseline_credit_card_24, R.string.bank_label, v -> {
//            startActivity(BankCardActivity.getIntent(this));
//            bottomSheet.stop();
//        });

        bottomSheet.setOnClickListener(BottomSheet.VIEW_EDIT, v -> {
            startActivity(PasswordActivity.newIntent(this, ""));
            bottomSheet.stop();
        });

        bottomSheet.setOnClickListener(BottomSheet.VIEW_COPY, v -> {
            startActivity(BankCardActivity.getIntent(this, ""));
            bottomSheet.stop();
        });

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {

            bottomSheet.updateImageAndText(
                    new int[] {
                            R.string.password_label,
                            R.string.bank_label
                    },
                    new int[] {
                            R.drawable.ic_baseline_account_circle_24,
                            R.drawable.ic_baseline_credit_card_24
                    }
            );
            bottomSheet.start();
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        headerLayout = findViewById(R.id.nav_header_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSupport.updateThemeInScreen(getWindow(), Objects.requireNonNull(getSupportActionBar()));
        fab.setBackgroundTintList(ColorStateList.valueOf(mSupport.getHeaderColor()));
        navigationView.setBackgroundColor(mSupport.getBackgroundColor());

        headerLayout = findViewById(R.id.nav_header_main);

        if (headerLayout != null) {
            if (mSupport.isLightTheme())
                headerLayout.setBackgroundResource(R.drawable.side_nav_bar);
            else
                headerLayout.setBackgroundColor(mSupport.getHeaderColor());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}