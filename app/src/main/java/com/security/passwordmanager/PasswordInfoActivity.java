package com.security.passwordmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.Button;

import java.util.UUID;

public class PasswordInfoActivity extends AppCompatActivity {

    private static final String EXTRA_DATA = "extra_data";

    private Support support;
    private DataLab dataLab;

    private Data data;

    public static Intent newIntent(Context context, Data data) {
        Intent intent = new Intent(context, PasswordInfoActivity.class);
        intent.putExtra(EXTRA_DATA, data);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_info);

        Log.d("PasswordInfo", "проверка");

        dataLab = DataLab.get(this);
        support = Support.get(this);
        data = (Data) getIntent().getSerializableExtra(EXTRA_DATA);

        getWindow().getDecorView().setBackgroundColor(support.getBackgroundColor());

        SpannableString title = new SpannableString(data.getName());
        title.setSpan(
                new ForegroundColorSpan(support.getFontColor()),
                0,
                title.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        setTitle(title);

        Button edit = findViewById(R.id.button_edit_password);
        edit.setTextColor(support.getFontColor());
        edit.setOnClickListener(v -> {
            startActivity(PasswordActivity.newIntent(this, data.getAddress()));
            finish();
        });

        Button delete = findViewById(R.id.button_delete_password);
        delete.setTextColor(support.getFontColor());
        delete.setOnClickListener(v -> {
            dataLab.deleteData(data);
            finish();
        });
    }
}