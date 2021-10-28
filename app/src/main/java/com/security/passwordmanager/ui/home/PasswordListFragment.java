package com.security.passwordmanager.ui.home;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.security.passwordmanager.R;
import com.security.passwordmanager.Support;

public class PasswordListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private PasswordAdapter mAdapter;

    private Support mSupport;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_password_list, container, false);
        mRecyclerView = root.findViewById(R.id.main_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        updateUI();

        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mSupport = Support.get(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        if (mAdapter == null) {
            mAdapter = new PasswordAdapter();
            mRecyclerView.setAdapter(mAdapter);
        }
        else mAdapter.notifyDataSetChanged();
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



    private class PasswordHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView textView;
        private Button button;

        public PasswordHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.list_item_text_view);
            button = itemView.findViewById(R.id.list_item_button_more);
            button.setVisibility(View.GONE);
            textView.setOnClickListener(this);
        }

        public void bindPassword(String text) {
            textView.setText(text);
            textView.setTextColor(mSupport.getFontColor());
            textView.setBackgroundColor(mSupport.getBackgroundColor());
            button.setBackgroundTintList(ColorStateList.valueOf(mSupport.getFontColor()));
        }

        @Override
        public void onClick(View v) {

        }
    }
}