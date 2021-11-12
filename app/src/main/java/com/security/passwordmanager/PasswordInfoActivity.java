package com.security.passwordmanager;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class PasswordInfoActivity extends AppCompatActivity {

    public static final int TYPE_DATA = 0;
    public static final int TYPE_ACCOUNT = 1;

    private static final String EXTRA_DATA = "extra_data";
    private static final String EXTRA_TYPE = "extra_type";

    private Support support;
    private DataLab dataLab;

    private int typeId;

    private static final @DrawableRes int[] images = new int[]
            {R.drawable.ic_outline_edit_24, R.drawable.ic_action_delete};
    private static final @StringRes int[] names = new int[]
            {R.string.edit_password, R.string.delete_password};
    private static final @StringRes int[] alt_names = new int[]
            {R.string.rename_account, R.string.delete_account};

    private Data data;

    public static Intent newIntent(Context context, Data data, int typeId) {
        Intent intent = new Intent(context, PasswordInfoActivity.class);
        intent.putExtra(EXTRA_DATA, data);
        intent.putExtra(EXTRA_TYPE, typeId);
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
        RecyclerView recyclerView = findViewById(R.id.info_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new InfoAdapter());

        typeId = getIntent().getIntExtra(EXTRA_TYPE, TYPE_DATA);

        getWindow().getDecorView().setBackgroundColor(support.getBackgroundColor());

        SpannableString title = new SpannableString(data.getNameWebsite());
        title.setSpan(
                new ForegroundColorSpan(support.getFontColor()),
                0,
                title.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        setTitle(title);
    }

    private class InfoAdapter extends RecyclerView.Adapter<InfoHolder> {

        @NonNull
        @Override
        public InfoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.list_item_password_info, parent, false);
            return new InfoHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull InfoHolder holder, int position) {
            holder.bindInfo(position);
        }

        @Override
        public int getItemCount() {
            return images.length;
        }
    }


    private class InfoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView imageView;
        private final Button button;
        private int position;

        public InfoHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.list_item_password_info_image_view);
            button = itemView.findViewById(R.id.list_item_password_info_button);
        }

        public void bindInfo(int position) {
            this.position = position;

            imageView.setImageDrawable(ContextCompat
                    .getDrawable(getApplicationContext(), images[position]));
            imageView.setImageTintList(ColorStateList.valueOf(support.getFontColor()));

            if (typeId == TYPE_DATA)
                button.setText(names[position]);
            else
                button.setText(alt_names[position]);

            button.setTextColor(support.getFontColor());
            button.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (position) {
                case 0:
                    if (typeId == TYPE_DATA) {
                        startActivity(PasswordActivity
                                .newIntent(getApplicationContext(), data.getAddress()));
                        finish();
                    }
                    break;
                case 1:
                    if (typeId == TYPE_DATA)
                        dataLab.deleteData(data);
                    else
                        dataLab.deleteData(data.getAddress(), data.getLogin());
                    finish();
                    break;
            }
        }
    }
}