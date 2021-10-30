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

    private static final String EXTRA_URL = "extra_url";
    private static final String EXTRA_ID = "extra_id";

    private Support support;
    private DataLab dataLab;

    private String url;

    public static Intent newIntent(Context context, UUID id, String url) {
        Intent intent = new Intent(context, PasswordInfoActivity.class);
        intent.putExtra(EXTRA_ID, id);
        intent.putExtra(EXTRA_URL, url);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_info);

        Log.d("PasswordInfo", "проверка");

        dataLab = DataLab.get(this);
        support = Support.get(this);
        url = getIntent().getStringExtra(EXTRA_URL);
        UUID id = (UUID) getIntent().getSerializableExtra(EXTRA_ID);

        getWindow().getDecorView().setBackgroundColor(support.getBackgroundColor());

        SpannableString title = new SpannableString(dataLab.getData(id).getName());
        title.setSpan(
                new ForegroundColorSpan(support.getFontColor()),
                0,
                title.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        setTitle(title);

        Button delete = findViewById(R.id.button_delete_password);
        delete.setTextColor(support.getFontColor());
        delete.setOnClickListener(v -> {
            dataLab.deleteData(url);
            finish();
        });
    }
}