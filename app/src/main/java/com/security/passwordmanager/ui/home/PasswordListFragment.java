package com.security.passwordmanager.ui.home;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.security.passwordmanager.Data;
import com.security.passwordmanager.DataLab;
import com.security.passwordmanager.PasswordActivity;
import com.security.passwordmanager.PasswordInfoActivity;
import com.security.passwordmanager.R;
import com.security.passwordmanager.Support;

import java.util.List;

public class PasswordListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private PasswordAdapter mAdapter;

    private Support mSupport;
    private List<Data> dataList;

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

        DataLab mDataLab = DataLab.get(getActivity());
        dataList = mDataLab.getDataList();

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
            Data data = dataList.get(position);
            holder.bindPassword(data);
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }



    private class PasswordHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private LinearLayout text_view;
        private final TextView textView_name, textView_url;
        private final Button button;
        private Data mData;

        public PasswordHolder(@NonNull View itemView) {
            super(itemView);
            text_view = itemView.findViewById(R.id.list_item_text_view);
            textView_name = itemView.findViewById(R.id.list_item_text_view_name);
            textView_url = itemView.findViewById(R.id.list_item_text_view_url);
            button = itemView.findViewById(R.id.list_item_button_more);
            text_view.setOnClickListener(this);
            button.setOnClickListener(this);
        }

        public void bindPassword(Data data) {
            this.mData = data;
            textView_name.setText(data.getName());
            textView_url.setText(data.getAddress());
            textView_name.setTextColor(mSupport.getFontColor());
            textView_name.setBackgroundColor(mSupport.getBackgroundColor());
            textView_url.setBackgroundColor(mSupport.getBackgroundColor());
            button.setBackgroundTintList(ColorStateList.valueOf(mSupport.getFontColor()));
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.list_item_text_view:
                    startActivity(PasswordActivity.newIntent(getActivity(), mData.getAddress()));
                    break;
                case R.id.list_item_button_more:
                    startActivity(PasswordInfoActivity
                            .newIntent(getContext(), mData.getId(), mData.getAddress()));
                    break;
            }
        }
    }
}