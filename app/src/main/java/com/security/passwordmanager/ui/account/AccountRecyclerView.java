//package com.security.passwordmanager.ui.account;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.Intent;
//import android.content.res.ColorStateList;
//import android.net.Uri;
//import android.text.InputType;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.TextView;
//
//import androidx.annotation.IdRes;
//import androidx.annotation.LayoutRes;
//import androidx.annotation.NonNull;
//import androidx.annotation.StringRes;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.constraintlayout.widget.ConstraintLayout;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.security.passwordmanager.ActionBottom;
//import com.security.passwordmanager.R;
//import com.security.passwordmanager.data.DataType;
//import com.security.passwordmanager.data.DataViewModel;
//import com.security.passwordmanager.data.Website;
//import com.security.passwordmanager.ui.DataRecyclerView;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class AccountRecyclerView extends DataRecyclerView {
//
//    private AccountAdapter mAdapter;
//    private List<Boolean> visibilities;
//
//    public AccountRecyclerView(
//            @NonNull View root,
//            @NonNull Context context,
//            @NonNull DataViewModel viewModel,
//            @IdRes int resId,
//            boolean horizontal) {
//
//        super(root, context, viewModel, resId, horizontal);
//        createVisibilities();
//    }
//
//
//    public AccountRecyclerView(
//            @NonNull AppCompatActivity activity, @IdRes int resId, boolean horizontal, String address) {
//        super(activity, resId, horizontal, address);
//
//        createVisibilities();
//    }
//
//
//    @NonNull
//    @Override
//    public DataType getType() {
//        return DataType.WEBSITE;
//    }
//
//    @SuppressLint("NotifyDataSetChanged")
//    @Override
//    public void updateRecyclerView() {
//        if (mAdapter == null) {
//            mAdapter = new AccountAdapter();
//            mRecyclerView.setAdapter(mAdapter);
//        }
//        else
////            mAdapter.notifyItemChanged(getCurrentPosition());
//        mAdapter.notifyDataSetChanged();
//    }
//
//    private String getNameAccountStart(int position) {
//        return context.getString(R.string.account_start, position);
//    }
//
//    public Website getAccount(int index) {
//        return (Website) getData(index);
//    }
//
//
//    private void createVisibilities() {
//        visibilities = new ArrayList<>();
//
//        for (int i = 0; i < mAccountList.size(); i++)
//            visibilities.add(false);
//    }
//
//
//    @SuppressLint("NotifyDataSetChanged")
//    public void setVisibilitiesForPassword(boolean[] visibilities) {
//        for (int i = 0; i < this.visibilities.size(); i++)
//            setVisibilityForPassword(i, visibilities[i]);
//
//        mAdapter.notifyDataSetChanged();
//    }
//
//    public void setVisibilityForPassword(int position, boolean visibility) {
//        visibilities.set(position, visibility);
//        mAdapter.notifyItemChanged(position);
//    }
//
//    public boolean[] getVisibilitiesForPassword() {
//        boolean[] array = new boolean[visibilities.size()];
//
//        for (int i = 0; i < visibilities.size(); i++)
//            array[i] = getVisibilityForPassword(i);
//
//        return array;
//    }
//
//    public boolean getVisibilityForPassword(int position) {
//        return visibilities.get(position);
//    }
//
//
//    private class AccountAdapter extends RecyclerView.Adapter<AccountHolder> {
//
//        @NonNull
//        @Override
//        public AccountHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//
//            @LayoutRes int layout = R.layout.list_item_more_website;
//
//            if (editable)
//                layout = R.layout.list_item_new_account;
//
//            View view = LayoutInflater.from(context)
//                    .inflate(layout, parent, false);
//
//
//            return editable ?
//                    new AccountEditableHolder(view) :
//                    new NotEditableHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull AccountHolder holder, int position) {
//            Website data;
//            if (mAccountList.size() == 0)
//                data = new Website();
//            else
//                data = getAccount(position);
//
//            boolean visibility = false;
//            if (position < visibilities.size())
//                visibility = visibilities.get(position);
//
//            holder.bindAccount(
//                    data,
//                    position + 1,
//                    visibility);
//
//            updateData(position, data);
//        }
//
//        @Override
//        public int getItemCount() {
//            return mAccountList.size();
//        }
//    }
//
//
//
//
//
//    private abstract class AccountHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//
//        protected final EditText nameAccount;
//
//        protected TextView login, password, comment;
//
//        protected Website data;
//
//        private final ImageButton buttonVisibility;
//        private boolean isPasswordVisible;
//        protected int position;
//
//        protected abstract ImageButton getButtonVisibility();
//
//        protected abstract void clickToEditName();
//
//        public AccountHolder(@NonNull View itemView) {
//            super(itemView);
//
//            nameAccount = itemView.findViewById(R.id.list_item_name_of_account);
//
//            buttonVisibility = getButtonVisibility();
//            buttonVisibility.setOnClickListener(this);
//        }
//
//        protected void bindAccount(Website data, int position, boolean isPasswordVisible) {
//
//            this.data = data;
//            this.position = position;
//            this.isPasswordVisible = isPasswordVisible;
//
//            login.setText(data.getLogin());
//            password.setText(data.getPassword());
//
//            updatePasswordText(isPasswordVisible);
//
//            if (data.getNameAccount().length() == 0)
//                if (editable)
//                    nameAccount.setText(getNameAccountStart(position));
//                else
//                    nameAccount.setVisibility(View.GONE);
//            else
//                nameAccount.setText(data.getNameAccount());
//
////            if (!editable && data.getComment().length() == 0) {
////                comment.setVisibility(View.GONE);
////                comment_head.setVisibility(View.GONE);
////            }
//
//
//
////            if (!editable) {
////                setCopyOnClickListener(login);
////                setCopyOnClickListener(password);
////                setCopyOnClickListener(comment);
////            }
//
//            //colors:
//
//            itemView.setBackgroundColor(support.getLayoutBackgroundColor());
//
////            if (editable) {
////                login.setBackgroundResource(support.getBackgroundRes());
////                password.setBackgroundResource(support.getBackgroundRes());
////                comment.setBackgroundResource(support.getBackgroundRes());
//
////                nameAccount.setHintTextColor(support.getDarkerGrayColor());
////            }
////            else {
////                login.setBackgroundColor(support.getLayoutBackgroundColor());
////                password.setBackgroundColor(support.getLayoutBackgroundColor());
////                comment.setBackgroundColor(support.getLayoutBackgroundColor());
////
////                nameAccount.setBackgroundColor(support.getLayoutBackgroundColor());
////
////                button_open_url.setBackgroundResource(support.getButtonRes());
////
////                login_head.setTextColor(support.getFontColor());
////                password_head.setTextColor(support.getFontColor());
////                comment_head.setTextColor(support.getFontColor());
////            }
//
//            login.setTextColor(support.getFontColor());
//            password.setTextColor(support.getFontColor());
//            comment.setTextColor(support.getFontColor());
//
//            buttonVisibility.setBackgroundColor(support.getBackgroundColor());
//            buttonVisibility.setImageTintList(ColorStateList.valueOf(support.getFontColor()));
//
//            nameAccount.setTextColor(support.getDarkerGrayColor());
//
//
////            login.addTextChangedListener(new TextWatcher() {
////                @Override
////                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
////
////                @Override
////                public void onTextChanged(CharSequence s, int start, int before, int count) {}
////
////                @Override
////                public void afterTextChanged(Editable s) {
////                    if (s.length() != 0)
////                        data.setLogin(s.toString());
////                }
////            });
////
////            password.addTextChangedListener(new TextWatcher() {
////                @Override
////                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
////
////                @Override
////                public void onTextChanged(CharSequence s, int start, int before, int count) {}
////
////                @Override
////                public void afterTextChanged(Editable s) {
////                    if (s.length() != 0)
////                        data.setPassword(s.toString());
////                }
////            });
////
////            comment.addTextChangedListener(new TextWatcher() {
////                @Override
////                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
////
////                @Override
////                public void onTextChanged(CharSequence s, int start, int before, int count) {}
////
////                @Override
////                public void afterTextChanged(Editable s) {
////                    if (s.length() != 0)
////                        data.setComment(s.toString());
////                }
////            });
//
////            if (editable)
////                nameAccount.setOnKeyListener((v, keyCode, event) -> {
////                    if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
////                        data.setNameAccount(nameAccount.getText().toString());
////                        changeHeading();
////                        renamingText = R.string.rename_account;
////                        bottomDialogFragment.updateImageAndText(BottomSheet.VIEW_EDIT, renamingText, null);
////                        return true;
////                    }
////                    return false;
////                });
//
////            else
////                blockView(nameAccount, true);
//        }
//
//
//        private void updatePasswordText(boolean visibility) {
//
//            if (visibility) {
//                password.setInputType(InputType.TYPE_CLASS_TEXT);
//                buttonVisibility.setImageResource(R.drawable.visibility_off);
//                buttonVisibility.setContentDescription(context.getString(R.string.hide_password));
//            }
//            else {
//                password.setInputType(129);
//                buttonVisibility.setImageResource(R.drawable.visibility_on);
//                buttonVisibility.setContentDescription(context.getString(R.string.show_password));
//            }
//        }
//
//        private void updatePasswordText() {
//            isPasswordVisible = !isPasswordVisible;
////            visibilities.set(position - 1, isPasswordVisible);
//            updatePasswordText(isPasswordVisible);
//        }
//
////        //используется, если поле - это constraintLayout
////        private EditText getEditTextFromLayout(ConstraintLayout layout) {
////            return layout.findViewById(R.id.field_item_text_view);
////        }
//
////        private ImageButton getButtonVisibility(EditText editText) {
////            View parent = editText.getRootView();
////            return parent.findViewById(R.id.field_item_button_visibility);
////        }
////
////        private void hideButtonVisibility(EditText editText) {
////            getButtonVisibility(editText).setVisibility(View.GONE);
////        }
////
////        private ImageButton getButtonCopy(EditText editText) {
////            View parent = editText.getRootView();
////            return parent.findViewById(R.id.field_item_button_copy);
////        }
//
//
////        private void setCopyOnClickListener(EditText editText) {
////            getButtonCopy(editText).setOnClickListener(v ->
////                    mDataViewModel.copyText(editText.getText().toString()));
////
////            getButtonCopy(editText).setBackgroundColor(support.getLayoutBackgroundColor());
////            getButtonCopy(editText)
////                    .setImageTintList(ColorStateList.valueOf(support.getFontColor()));
////        }
//
//
//
//        @SuppressLint("NonConstantResourceId")
//        @Override
//        public void onClick(View v) {
//            switch (v.getId()) {
//                case R.id.edit_name_of_account:
//                    clickToEditName();
//                    break;
//
//                case R.id.field_item_button_visibility:
//                    updatePasswordText();
//                    break;
//
//                case R.id.list_item_button_open_url:
//                    String address;
//                    if (data.getAddress().contains("www."))
//                        address = "https://" + data.getAddress();
//                    else if (data.getAddress().contains("https://www.") || data.getAddress().contains("http://www."))
//                        address = data.getAddress();
//                    else
//                        address = "https://www." + data.getAddress();
//
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
//                    context.startActivity(intent);
//                    break;
//            }
//        }
//
//        protected void changeHeading() {
//            boolean blocking = nameAccount.isCursorVisible();
//            //true - заблокирует, false - разблокирует
//            String full = getNameAccountStart(position);
//            String Null = "";
//
//            blockView(nameAccount, blocking);
//
//            if (blocking && nameAccount.getText().length() == 0)
//                nameAccount.setText(full);
//            else if (!blocking && nameAccount.getText().toString().equals(full))
//                nameAccount.setText(Null);
//
//            if (blocking) {
//                nameAccount.setTextColor(support.getDarkerGrayColor());
//                nameAccount.setBackgroundTintList(ColorStateList.valueOf(support.getDarkerGrayColor()));
//            } else {
//                nameAccount.setTextColor(support.getFontColor());
//                nameAccount.setBackgroundTintList(ColorStateList.valueOf(support.getHeaderColor()));
//            }
//        }
//
//        //true - заблокирует, false - разблокирует
//        private void blockView(EditText editText, boolean block) {
//            editText.setEnabled(!block);
//            editText.setCursorVisible(!block);
//        }
//    }
//
//
//    private class AccountEditableHolder extends AccountHolder {
//
//        private @StringRes int renamingText;
//        private final Button edit_name;
//
//        @Override
//        protected ImageButton getButtonVisibility() {
//            return itemView.findViewById(R.id.field_item_button_visibility);
//        }
//
//        @Override
//        protected void clickToEditName() {
//            bottomDialogFragment = ActionBottom.Companion.newInstance(mActivity);
//
//            bottomDialogFragment.setHeading(data.getNameAccount(), null);
//
//            bottomDialogFragment.addView(R.drawable.edit, renamingText, v -> {
//                changeHeading();
//                bottomDialogFragment.dismiss();
//
//                renamingText = nameAccount.isCursorVisible() ?
//                        R.string.cancel_renaming_account :
//                        R.string.rename_account;
//            });
//
//            bottomDialogFragment.addView(R.drawable.copy, R.string.copy_info, v -> {
//                mDataViewModel.copyData(data);
//                bottomDialogFragment.dismiss();
//            });
//
//            bottomDialogFragment.addView(R.drawable.delete, R.string.delete_account, v -> {
//                bottomDialogFragment.dismiss();
//                mDataViewModel.deleteData(data);
//                updateAccountList();
//
//                if (mAccountList.size() == 0 && mActivity != null)
//                    mActivity.finish();
//
//                mAdapter.notifyItemRemoved(position);
//            });
//
//            bottomDialogFragment.show(mActivity.getSupportFragmentManager(), ActionBottom.TAG);
//        }
//
//
//
//
//        public AccountEditableHolder(@NonNull View itemView) {
//            super(itemView);
//
//            itemView.setOnFocusChangeListener((v, hasFocus) -> {
//                if (hasFocus)
//                    scrollToPosition(position + 1);
//            });
//
//            edit_name = itemView.findViewById(R.id.edit_name_of_account);
//            edit_name.setOnClickListener(this);
//
//            login = itemView.findViewById(R.id.list_item_login);
//            password = itemView.findViewById(R.id.list_item_password);
//            comment = itemView.findViewById(R.id.list_item_comment);
//
//            renamingText = R.string.rename_account;
//        }
//
//        @Override
//        protected void bindAccount(Website data, int position, boolean isPasswordVisible) {
//            super.bindAccount(data, position, isPasswordVisible);
//
//            comment.setText(data.getComment());
//
//            edit_name.setBackgroundTintList(ColorStateList.valueOf(support.getFontColor()));
//
//            login.setBackgroundResource(support.getBackgroundRes());
//            password.setBackgroundResource(support.getBackgroundRes());
//            comment.setBackgroundResource(support.getBackgroundRes());
//
//            nameAccount.setHintTextColor(support.getDarkerGrayColor());
//
//            nameAccount.setOnKeyListener((v, keyCode, event) -> {
//                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
//                    data.setNameAccount(nameAccount.getText().toString());
//                    changeHeading();
//                    renamingText = R.string.rename_account;
//                    bottomDialogFragment.editView(0, renamingText);
//                    return true;
//                }
//                return false;
//            });
//        }
//    }
//
//
//    private class NotEditableHolder extends AccountHolder {
//
//        private final TextView login_head, password_head, comment_head;
//        private final Button button_open_url;
//
//        private final ConstraintLayout loginLayout, passwordLayout, commentLayout;
//
//        @Override
//        protected ImageButton getButtonVisibility() {
//            return itemView.findViewById(R.id.list_item_password)
//                    .findViewById(R.id.field_item_button_visibility);
//        }
//
//        @Override
//        protected void clickToEditName() {
//
//            String heading = nameAccount.getText().toString();
//            String subtitle = null;
//
//            if (heading.length() == 0) {
//                heading = data.getNameWebsite();
//                subtitle = data.getLogin();
//            }
//            bottomDialogFragment.setHeading(heading, subtitle);
//
//            bottomDialogFragment.addView(R.drawable.edit, R.string.edit, v -> {
//                bottomDialogFragment.dismiss();
//                context.startActivity(
//                        PasswordActivity.Companion.getIntent(context, data.getAddress(), position + 1));
//            });
//
////            bottomDialogFragment.updateImageAndText(
////                    new int[]{ R.string.edit },
////                    new int[]{ R.drawable.edit}
////            );
//
//            bottomDialogFragment.show(mActivity.getSupportFragmentManager(), ActionBottom.TAG);
//        }
//
//        public NotEditableHolder(@NonNull View itemView) {
//            super(itemView);
//
//            login_head = itemView.findViewById(R.id.list_item_login_head);
//            password_head = itemView.findViewById(R.id.list_item_password_head);
//            comment_head = itemView.findViewById(R.id.list_item_comment_head);
//
//            loginLayout = itemView.findViewById(R.id.list_item_login);
//            passwordLayout = itemView.findViewById(R.id.list_item_password);
//            commentLayout = itemView.findViewById(R.id.list_item_comment);
//
//            login = getEditTextFromLayout(loginLayout);
//            password = getEditTextFromLayout(passwordLayout);
//            comment = getEditTextFromLayout(commentLayout);
//
//            itemView.setOnClickListener(v -> clickToEditName());
//
//            button_open_url = itemView.findViewById(R.id.list_item_button_open_url);
//            button_open_url.setOnClickListener(this);
//        }
//
//        @Override
//        protected void bindAccount(Website data, int position, boolean isPasswordVisible) {
//            super.bindAccount(data, position, isPasswordVisible);
//
//            updateView(loginLayout, data.getLogin(), false);
//            updateView(passwordLayout, data.getPassword(), true);
//            updateView(commentLayout, data.getComment(), false);
//
//            if (data.getComment().length() == 0) {
//                commentLayout.setVisibility(View.GONE);
//                comment_head.setVisibility(View.GONE);
//            }
//            else
//                comment.setText(data.getComment());
//
//            login.setBackgroundColor(support.getLayoutBackgroundColor());
//            password.setBackgroundColor(support.getLayoutBackgroundColor());
//            comment.setBackgroundColor(support.getLayoutBackgroundColor());
//
//            nameAccount.setBackgroundColor(support.getLayoutBackgroundColor());
//
//            button_open_url.setBackgroundResource(support.getButtonRes());
//
//            login_head.setTextColor(support.getFontColor());
//            password_head.setTextColor(support.getFontColor());
//            comment_head.setTextColor(support.getFontColor());
//        }
//
//        //используется, если поле - это constraintLayout
//        private TextView getEditTextFromLayout(ConstraintLayout layout) {
//            return layout.findViewById(R.id.field_item_text_view);
//        }
//
//
//        private void updateView(ConstraintLayout layout, String text, boolean visibility) {
//            ImageButton buttonVisibility = layout.findViewById(R.id.field_item_button_visibility);
//            ImageButton buttonCopy = layout.findViewById(R.id.field_item_button_copy);
//            TextView textView = getEditTextFromLayout(layout);
//
//            textView.setText(text);
//
//            buttonCopy.setOnClickListener(v ->
//                    mDataViewModel.copyText(text));
//
//            buttonVisibility.setVisibility(visibility ? View.VISIBLE : View.GONE);
//
//            textView.setTextColor(support.getFontColor());
//            buttonCopy.setImageTintList(ColorStateList.valueOf(support.getFontColor()));
//            buttonVisibility.setImageTintList(ColorStateList.valueOf(support.getFontColor()));
//
//            textView.setBackgroundColor(support.getLayoutBackgroundColor());
//            buttonCopy.setBackgroundColor(support.getLayoutBackgroundColor());
//            buttonVisibility.setBackgroundColor(support.getLayoutBackgroundColor());
//        }
//    }
//}