package com.security.passwordmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    private TextView switchTheme;
    private Support support;
    private ThemeBottomSheet bottomSheet;

    public static Intent newIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        support = Support.getInstance(this);
        switchTheme = findViewById(R.id.switchTheme);
        bottomSheet = new ThemeBottomSheet(this);

        switchTheme.setOnClickListener(v ->
                bottomSheet.start());

        updateTheme();

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0)
                setTitle(R.string.title_activity_settings);
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void updateTheme() {
        support.updateThemeInScreen(getWindow(), Objects.requireNonNull(getSupportActionBar()));
        switchTheme.setText(getCurrentThemeText());
        switchTheme.setTextColor(support.getFontColor());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private String getCurrentThemeText() {
        String[] themes = getResources().getStringArray(R.array.themes);
        int position = support.getIndexTheme();

        return getString(R.string.switchThemeText, themes[position].toLowerCase());
    }



    private class ThemeBottomSheet {
        private final BottomSheetDialog bottomSheetDialog;
        private final View bottomSheetView;
        private final RecyclerView mRecyclerView;
        private ThemeAdapter mAdapter;

        private final String[] themes;

        private ThemeBottomSheet(Context context) {
            bottomSheetDialog = new BottomSheetDialog(context);
            themes = getResources().getStringArray(R.array.themes);

            bottomSheetView = LayoutInflater.from(context).inflate(
                    R.layout.switch_theme_layout,
                    findViewById(R.id.theme_bottom_sheet_container)
            );

            mRecyclerView = bottomSheetView.findViewById(R.id.theme_bottom_sheet_container);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        }

        @SuppressLint("NotifyDataSetChanged")
        private void start() {
            bottomSheetView.setBackgroundColor(support.getBackgroundColor());
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();

            if (mAdapter == null) {
                mAdapter = new ThemeAdapter();
                mRecyclerView.setAdapter(mAdapter);
            }
            else
                mAdapter.notifyDataSetChanged();
        }



        private class ThemeAdapter extends RecyclerView.Adapter<ThemeHolder> {

            @NonNull
            @Override
            public ThemeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = getLayoutInflater().inflate(R.layout.list_item_theme, parent, false);
                return new ThemeHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull ThemeHolder holder, int position) {
                String text = themes[position];
                boolean isChecked = position == support.getIndexTheme();
                holder.bindTheme(text, isChecked);

                holder.setOnClickListener(view -> {
                    switch (position) {
                        case 0:
                            support.setTheme(Support.LIGHT_THEME);
                            break;
                        case 1:
                            support.setTheme(Support.DARK_THEME);
                            break;
                        case 2:
                            support.setTheme(Support.SYSTEM_THEME);
                            break;
                        case 3:
                            support.setTheme(Support.AUTO_THEME);
                            break;
                    }
                    updateTheme();
                    bottomSheetDialog.dismiss();
                });
            }

            @Override
            public int getItemCount() {
                return themes.length;
            }
        }


        private class ThemeHolder extends RecyclerView.ViewHolder {

            private final TextView textViewTheme;
            private final View itemView;
            private final LinearLayout times;
            private final View.OnClickListener listener;
            private Pair<Calendar, Calendar> calendarPair;

            public ThemeHolder(@NonNull View itemView) {
                super(itemView);
                this.itemView = itemView;
                textViewTheme = itemView.findViewById(R.id.list_item_theme_text_view);
                times = itemView.findViewById(R.id.list_item_theme_layout_time);
                calendarPair = support.getTimesForAutoTheme();

                listener = v -> {
                    if (v.getId() == R.id.them_layout_start_time)
                        startLauncher.launch(calendarPair.first());
                    else
                        endLauncher.launch(calendarPair.second());
                };
            }

            public void bindTheme(String text, boolean isChecked) {
                textViewTheme.setText(text);
                textViewTheme.setTextColor(support.getFontColor());
                textViewTheme.setBackgroundColor(support.getBackgroundColor());

                itemView.setBackgroundColor(support.getBackgroundColor());

                int color = getApplicationContext().getColor(R.color.raspberry);
                if (!isChecked)
                    color = support.getBackgroundColor();

                TextViewCompat.setCompoundDrawableTintList(
                        textViewTheme,
                        ColorStateList.valueOf(color)
                );

                if (isChecked && support.getTheme().equals(Support.AUTO_THEME)) {
                    times.setVisibility(View.VISIBLE);
                    updateTimes();
                }
                else
                    times.setVisibility(View.GONE);
            }

            public void setOnClickListener(View.OnClickListener listener) {
                textViewTheme.setOnClickListener(listener);
            }


            private void updateTimes() {
                calendarPair = support.getTimesForAutoTheme();

                String startTime = getStringFromCalendar(calendarPair.first());
                String endTime = getStringFromCalendar(calendarPair.second());

                TextView start = times.findViewById(R.id.them_layout_start_time);
                TextView end = times.findViewById(R.id.them_layout_end_time);

                start.setOnClickListener(listener);
                end.setOnClickListener(listener);

                start.setText(startTime);
                end.setText(endTime);

                start.setTextColor(support.getFontColor());
                end.setTextColor(support.getFontColor());

                start.setBackgroundResource(support.getBackgroundRes());
                end.setBackgroundResource(support.getBackgroundRes());

                times.setBackgroundColor(support.getBackgroundColor());
            }

            private String getStringFromCalendar(Calendar calendar) {
                DateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
                return format.format(calendar.getTime());
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    ActivityResultLauncher<Calendar> startLauncher = registerForActivityResult(
            new TimePickerActivity.TimePickerActivityContract(R.string.start_time),
            result -> {
                if (result != null) {
                    support.setStartTimeForAutoTheme(result);
                    bottomSheet.mAdapter.notifyDataSetChanged();
                    updateTheme();
                }
            });

    @SuppressLint("NotifyDataSetChanged")
    ActivityResultLauncher<Calendar> endLauncher = registerForActivityResult(
            new TimePickerActivity.TimePickerActivityContract(R.string.end_time),
            result -> {
                if (result != null) {
                    support.setEndTimeForAutoTheme(result);
                    bottomSheet.mAdapter.notifyDataSetChanged();
                    updateTheme();
                }
            });
}