package com.security.passwordmanager.ui.passwords;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.security.passwordmanager.BottomSheet;
import com.security.passwordmanager.Data;
import com.security.passwordmanager.DataLab;
import com.security.passwordmanager.PasswordActivity;
import com.security.passwordmanager.R;
import com.security.passwordmanager.Support;

import java.util.List;

public class PasswordListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private PasswordAdapter mAdapter;

    private Support mSupport;
    private DataLab mDataLab;
    private List<Data> dataList;

    private BottomSheet mBottomSheet;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_password_list, container, false);
        mRecyclerView = root.findViewById(R.id.main_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        updateUI();

        mBottomSheet = new BottomSheet(requireContext(), root);

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
            holder.bindPassword(mDataLab.getAccountList(data.getAddress()), position == openedView);

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
        private final LinearLayout mainView;
        private final ImageView imageView;
        private final TextView textView_name, textView_url;
        private final Button button_more;
        private final MotionLayout motionLayout;

        private List<Data> mAccountList;

        public PasswordHolder(@NonNull View itemView) {
            super(itemView);
            mainView = itemView.findViewById(R.id.list_item_main_view);
            imageView = itemView.findViewById(R.id.list_item_image_view);
            textView_name = itemView.findViewById(R.id.list_item_text_view_name);
            textView_url = itemView.findViewById(R.id.list_item_text_view_url);
            button_more = itemView.findViewById(R.id.list_item_button_more);

            recyclerView = itemView.findViewById(R.id.list_item_recycler_view_more_info);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            button_more.setOnClickListener(this);

            motionLayout = itemView.findViewById(R.id.motion_container);
        }

        public void bindPassword(List<Data> accountList, boolean isShown) {
            this.mAccountList = accountList;

            recyclerView.setAdapter(new MoreInfoAdapter(mAccountList));

            updateArrow(isShown);
            textView_name.setText(accountList.get(0).getNameWebsite());
            textView_url.setText(accountList.get(0).getAddress());

            imageView.setImageTintList(ColorStateList.valueOf(mSupport.getHeaderColor()));
            textView_name.setBackgroundColor(mSupport.getBackgroundColor());
            textView_url.setBackgroundColor(mSupport.getBackgroundColor());
            button_more.setBackgroundTintList(ColorStateList.valueOf(mSupport.getFontColor()));

            textView_name.setTextColor(mSupport.getFontColor());
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {

            mBottomSheet.setHeading(
                    mAccountList.get(0).getNameWebsite(),
                    mAccountList.get(0).getAddress()
            );

            mBottomSheet.updateImageAndText(
                    new int[]{ R.string.edit_password, R.string.delete_password },
                    new int[]{ R.drawable.ic_outline_edit_24, R.drawable.ic_baseline_delete_24 }
            );

            mBottomSheet.setOnClickListener(BottomSheet.VIEW_EDIT, v1 -> {
                startActivity(PasswordActivity
                        .newIntent(requireContext(), mAccountList.get(0).getAddress()));
                mBottomSheet.stop();
            });

            mBottomSheet.setOnClickListener(BottomSheet.VIEW_DELETE, v1 -> {
                mDataLab.deleteData(mAccountList.get(0).getAddress());
                mBottomSheet.stop();
                updateUI();
            });

            mBottomSheet.start();
        }

        public void setOnClickListener(View.OnClickListener listener) {
            mainView.setOnClickListener(listener);
        }


        //true - стрелка вниз, false - вправо
        private void updateArrow(boolean isShown) {
            if (isShown)
                motionLayout.transitionToEnd();
            else
                motionLayout.transitionToStart();
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
        private final TextView accountName;
        private final ConstraintLayout login, password, comment;
        private final TextView head_login, head_password, head_comment;
        private final Button button_open_url;
        private Data data;

        private boolean isPasswordVisible;

        public MoreInfoHolder(@NonNull View itemView) {
            super(itemView);
            moreInfo = itemView.findViewById(R.id.list_item_linear_layout_more);

            accountName = itemView.findViewById(R.id.list_item_text_view_name_of_account);
            login = itemView.findViewById(R.id.list_item_text_view_login);
            password = itemView.findViewById(R.id.list_item_text_view_password);
            comment = itemView.findViewById(R.id.list_item_text_view_comment);
            button_open_url = itemView.findViewById(R.id.list_item_button_open_url);

            button_open_url.setOnClickListener(this);

            head_login = itemView.findViewById(R.id.list_item_text_view_login_head);
            head_password = itemView.findViewById(R.id.list_item_text_view_password_head);
            head_comment = itemView.findViewById(R.id.list_item_text_view_comment_head);

            button_open_url.setTextColor(Color.WHITE);

            isPasswordVisible = false;
        }

        public void bindInfo(Data data) {
            this.data = data;

            if (data.getComment().length() == 0) {
                comment.setVisibility(View.GONE);
                head_comment.setVisibility(View.GONE);
            }

            accountName.setText(data.getNameAccount());
            setTextToLayout(login, data.getLogin(), null);
            setTextToLayout(password, data.getPassword(), 129);
            setTextToLayout(comment, data.getComment(), null);

            setOnClickListener(login, data.getLogin(), false);
            setOnClickListener(password, data.getPassword(), true);
            setOnClickListener(comment, data.getComment(), false);

            moreInfo.setBackgroundColor(mSupport.getLayoutBackgroundColor());
            accountName.setTextColor(mSupport.getDarkerGrayColor());
            accountName.setBackgroundColor(mSupport.getLayoutBackgroundColor());
            setColor(login);
            setColor(password);
            setColor(comment);
            button_open_url.setBackgroundResource(mSupport.getButtonRes());

            head_login.setTextColor(mSupport.getFontColor());
            head_password.setTextColor(mSupport.getFontColor());
            head_comment.setTextColor(mSupport.getFontColor());
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.list_item_button_open_url) {
                String address;
                if (data.getAddress().contains("www."))
                    address = "https://" + data.getAddress();
                else if (data.getAddress().contains("https://www.") || data.getAddress().contains("http://www."))
                    address = data.getAddress();
                else
                    address = "https://www." + data.getAddress();

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
                startActivity(intent);
            }
        }

        private void setTextToLayout(ConstraintLayout layout, String text, Integer inputType) {
            TextView textView = layout.findViewById(R.id.field_item_text_view);
            textView.setText(text);
            if (inputType != null)
                textView.setInputType(inputType);
        }

        private void setOnClickListener(ConstraintLayout layout, String text, boolean needVisibility) {
            ImageButton copy = layout.findViewById(R.id.field_item_button_copy);
            ImageButton visibility = layout.findViewById(R.id.field_item_button_visibility);

            copy.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager)
                        requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", text);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity(), "Скопированно!", Toast.LENGTH_SHORT).show();
            });

            if (needVisibility) {
                visibility.setVisibility(View.VISIBLE);
                visibility.setOnClickListener(v -> {
                    TextView textView = layout.findViewById(R.id.field_item_text_view);

                    isPasswordVisible = !isPasswordVisible;

                    if (isPasswordVisible) {
                        textView.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        visibility.setImageResource(R.drawable.ic_outline_visibility_off_24);
                    }
                    else {
                        textView.setInputType(129);
                        visibility.setImageResource(R.drawable.ic_visibility_24);
                    }
                });
            }
            else visibility.setVisibility(View.GONE);
        }

        private void setColor(ConstraintLayout layout) {
            TextView textView = layout.findViewById(R.id.field_item_text_view);
            ImageButton copy = layout.findViewById(R.id.field_item_button_copy);
            ImageButton visibility = layout.findViewById(R.id.field_item_button_visibility);

            textView.setTextColor(mSupport.getFontColor());
            copy.setImageTintList(ColorStateList.valueOf(mSupport.getFontColor()));
            visibility.setImageTintList(ColorStateList.valueOf(mSupport.getFontColor()));

            textView.setBackgroundColor(mSupport.getLayoutBackgroundColor());
            copy.setBackgroundColor(mSupport.getLayoutBackgroundColor());
            visibility.setBackgroundColor(mSupport.getLayoutBackgroundColor());
        }
    }
}