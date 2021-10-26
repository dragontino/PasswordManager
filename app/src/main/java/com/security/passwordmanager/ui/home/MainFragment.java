package com.security.passwordmanager.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.security.passwordmanager.R;

public class MainFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.main_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new PasswordAdapter());

        return root;
    }



    private class PasswordAdapter extends RecyclerView.Adapter<PasswordHolder> {

        @NonNull
        @Override
        public PasswordHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater
                    .inflate(R.layout.list_item_main_text_view, parent, false);
            return new PasswordHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PasswordHolder holder, int position) {
            holder.bindPassword("Элемент №" + (position + 1));
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }



    private class PasswordHolder extends RecyclerView.ViewHolder {

        private TextView textView;

        public PasswordHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.list_item_text_view);
            textView.setTextColor(Color.BLACK);
        }

        public void bindPassword(String text) {
            textView.setText(text);
        }
    }
}