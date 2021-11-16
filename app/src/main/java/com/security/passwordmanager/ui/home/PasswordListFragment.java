package com.security.passwordmanager.ui.home;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.security.passwordmanager.Cryptographer;
import com.security.passwordmanager.Data;
import com.security.passwordmanager.DataLab;
import com.security.passwordmanager.PasswordInfoActivity;
import com.security.passwordmanager.R;
import com.security.passwordmanager.Support;

import java.util.List;

public class PasswordListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private PasswordAdapter mAdapter;

    private Support mSupport;
    private DataLab mDataLab;
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
        mDataLab = DataLab.get(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        dataList = mDataLab.getDataList();

        if (mAdapter == null) {
            mAdapter = new PasswordAdapter();
            mRecyclerView.setAdapter(mAdapter);
        }
        else mAdapter.notifyDataSetChanged();
    }



    private class PasswordAdapter extends RecyclerView.Adapter<PasswordHolder> {

        private static final int OPENED_VIEW = 0xAAA;
        private int openedView = -1;

        @Override
        public int getItemViewType(int position) {
            if (position == openedView)
                return OPENED_VIEW;
            return super.getItemViewType(position);
        }

        @NonNull
        @Override
        public PasswordHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater
                    .inflate(R.layout.list_item_main_text_view, parent, false);

            RecyclerView infoRecyclerView = view.findViewById(R.id.list_item_recycler_view_more_info);

            if (viewType == OPENED_VIEW)
                infoRecyclerView.setVisibility(View.VISIBLE);
            else
                infoRecyclerView.setVisibility(View.GONE);

            return new PasswordHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PasswordHolder holder, int position) {
            Data data = dataList.get(position);
            holder.bindPassword(mDataLab.getAccountList(data.getAddress()));

            holder.setOnClickListener(v -> {
                if (openedView == position)
                    openedView = -1;
                else {
                    int oldOpen = openedView;
                    openedView = position;
                    notifyItemChanged(oldOpen);
                }
                notifyItemChanged(position);
            });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }



    private class PasswordHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final RecyclerView recyclerView;
        private final LinearLayout text_view;
        private final TextView textView_name, textView_url;
        private final Button button_more;

        private List<Data> mAccountList;

        public PasswordHolder(@NonNull View itemView) {
            super(itemView);
            text_view = itemView.findViewById(R.id.list_item_text_view);
            textView_name = itemView.findViewById(R.id.list_item_text_view_name);
            textView_url = itemView.findViewById(R.id.list_item_text_view_url);
            button_more = itemView.findViewById(R.id.list_item_button_more);

            recyclerView = itemView.findViewById(R.id.list_item_recycler_view_more_info);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


            button_more.setOnClickListener(this);
        }

        public void bindPassword(List<Data> accountList) {
            this.mAccountList = accountList;

            recyclerView.setAdapter(new MoreInfoAdapter(mAccountList));

            textView_name.setText(accountList.get(0).getNameWebsite());
            textView_url.setText(accountList.get(0).getAddress());

            textView_name.setBackgroundColor(mSupport.getBackgroundColor());
            textView_url.setBackgroundColor(mSupport.getBackgroundColor());
            button_more.setBackgroundTintList(ColorStateList.valueOf(mSupport.getFontColor()));

            textView_name.setTextColor(mSupport.getFontColor());
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            startActivity(PasswordInfoActivity.newIntent(getContext(), mAccountList.get(0), PasswordInfoActivity.TYPE_DATA));
        }

        public void setOnClickListener(View.OnClickListener listener) {
            text_view.setOnClickListener(listener);
        }
    }



    private class MoreInfoAdapter extends RecyclerView.Adapter<MoreInfoHolder> {

        private final List<Data> accountList;

        public MoreInfoAdapter(List<Data> accountList) {
            this.accountList = accountList;
        }

        @NonNull
        @Override
        public MoreInfoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity())
                    .inflate(R.layout.list_item_more_info, parent, false);
            return new MoreInfoHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MoreInfoHolder holder, int position) {
            Data data = accountList.get(position);
            holder.bindInfo(data);
        }

        @Override
        public int getItemCount() {
            return accountList.size();
        }
    }



    private class MoreInfoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final LinearLayout moreInfo;
        private final TextView accountName, login, password, comment;
        private final TextView head_login, head_password, head_comment;
        private final Button button_open_url;
        private final ImageButton copy;
        private Data data;

        public MoreInfoHolder(@NonNull View itemView) {
            super(itemView);
            moreInfo = itemView.findViewById(R.id.list_item_linear_layout_more);

            accountName = itemView.findViewById(R.id.list_item_text_view_name_of_account);

            login = itemView.findViewById(R.id.list_item_text_view_login);
            password = itemView.findViewById(R.id.list_item_text_view_password);
            comment = itemView.findViewById(R.id.list_item_text_view_comment);
            button_open_url = itemView.findViewById(R.id.list_item_button_open_url);

            head_login = itemView.findViewById(R.id.list_item_text_view_login_head);
            head_password = itemView.findViewById(R.id.list_item_text_view_password_head);
            head_comment = itemView.findViewById(R.id.list_item_text_view_comment_head);

            copy = itemView.findViewById(R.id.list_item_button_copy_login);
            copy.setOnClickListener(this);

            button_open_url.setTextColor(Color.WHITE);
        }

        public void bindInfo(Data data) {
            this.data = data;

            if (data.getComment().length() == 0) {
                comment.setVisibility(View.GONE);
                head_comment.setVisibility(View.GONE);
            }

            accountName.setText(data.getNameAccount());
            login.setText(data.getLogin());
            password.setText(data.getPassword());
            comment.setText(data.getComment());

            moreInfo.setBackgroundColor(mSupport.getLayoutBackgroundColor());
            button_open_url.setBackgroundResource(mSupport.getButtonRes());

            accountName.setBackgroundColor(mSupport.getLayoutBackgroundColor());
            login.setBackgroundColor(mSupport.getLayoutBackgroundColor());
            password.setBackgroundColor(mSupport.getLayoutBackgroundColor());
            comment.setBackgroundColor(mSupport.getLayoutBackgroundColor());
            copy.setBackgroundColor(mSupport.getLayoutBackgroundColor());
            copy.setImageTintList(ColorStateList.valueOf(mSupport.getFontColor()));

            accountName.setTextColor(mSupport.getDarkerGrayColor());
            login.setTextColor(mSupport.getFontColor());
            password.setTextColor(mSupport.getFontColor());
            comment.setTextColor(mSupport.getFontColor());

            head_login.setTextColor(mSupport.getFontColor());
            head_password.setTextColor(mSupport.getFontColor());
            head_comment.setTextColor(mSupport.getFontColor());
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.list_item_button_open_url:
                    String address;
                    if (data.getAddress().contains("www."))
                        address = "https://" + data.getAddress();
                    else if (data.getAddress().contains("https://www.") || data.getAddress().contains("http://www."))
                        address = data.getAddress();
                    else
                        address = "https://www." + data.getAddress();

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
                    startActivity(intent);
                    break;

                case R.id.list_item_button_copy_login:
                    ClipboardManager clipboard = (ClipboardManager)
                            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("", data.getLogin());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getActivity(), "Скопированно!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}