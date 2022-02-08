//package com.security.passwordmanager.ui;
//
//import android.content.Context;
//import android.view.View;
//
//import androidx.annotation.IdRes;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.lifecycle.ViewModelProvider;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.security.passwordmanager.ActionBottom;
//import com.security.passwordmanager.ActionBottomDialogFragment;
//import com.security.passwordmanager.data.BankCard;
//import com.security.passwordmanager.data.Data;
//import com.security.passwordmanager.data.DataType;
//import com.security.passwordmanager.data.DataViewModel;
//import com.security.passwordmanager.data.Website;
//import com.security.passwordmanager.settings.SettingsViewModel;
//
//import java.util.List;
//
//public abstract class DataRecyclerView {
//
//    protected SettingsViewModel support;
//    protected Context context;
//    protected DataViewModel mDataViewModel;
//
//    protected final RecyclerView mRecyclerView;
//    private final LinearLayoutManager mLayoutManager;
//    protected List<Data> mAccountList;
//
//    protected String key;
//    protected boolean editable;
//
//    private OnScroll onScroll, updateViewScroll;
//
//    protected AppCompatActivity mActivity;
//    public int startCount;
//
//    protected ActionBottomDialogFragment bottomDialogFragment;
//
//    public DataRecyclerView(
//            @NonNull View root,
//            @NonNull Context context,
//            @NonNull DataViewModel viewModel,
//            @IdRes int resId,
//            boolean horizontal) {
//
//        this(root, context, viewModel, resId, horizontal, "");
//    }
//
//    public DataRecyclerView(
//            @NonNull AppCompatActivity activity,
//            @IdRes int resId,
//            boolean horizontal,
//            String key) {
//
//        this(
//                activity.getWindow().getDecorView(),
//                activity.getApplicationContext(),
//                new ViewModelProvider(activity).get(DataViewModel.class),
//                resId,
//                horizontal,
//                key
//        );
//
//        this.mActivity = activity;
//    }
//
//
//    //Главный конструктор
//    public DataRecyclerView(
//            @NonNull View root,
//            @NonNull Context context,
//            @NonNull DataViewModel viewModel,
//            @IdRes int resId,
//            boolean horizontal,
//            String key) {
//
//        this.context = context;
//        this.mDataViewModel = viewModel;
//        this.key = key;
//
//        support = new ViewModelProvider(mActivity).get(SettingsViewModel.class);
//
//        mRecyclerView = root.findViewById(resId);
//        if (horizontal)
//            mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
//        else
//            mLayoutManager = new LinearLayoutManager(context);
//
//        mRecyclerView.setLayoutManager(mLayoutManager);
//
//        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//
//                if (newState == RecyclerView.SCROLL_STATE_DRAGGING && onScroll != null)
//                    onScroll.onScrollChanged();
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                if (updateViewScroll != null)
//                    updateViewScroll.onScrollChanged();
//
//                if (onScroll != null)
//                    onScroll.onScrollChanged();
//            }
//        });
//
//        editable = true;
//
//        bottomDialogFragment = ActionBottom.Companion.newInstance(mActivity);
//
//        updateAccountList();
//        startCount = mAccountList.size();
//
//        if (mAccountList.size() == 0)
//            if (getType() == DataType.WEBSITE)
//                mAccountList.add(new Website());
//            else
//                mAccountList.add(new BankCard());
//    }
//
//
//    public void setVisibility(int visibility) {
//        mRecyclerView.setVisibility(visibility);
//    }
//
//    public abstract DataType getType();
//
//
//    public void setOnScroll(OnScroll onScroll) {
//        this.onScroll = onScroll;
//    }
//
//    public void updateViewScroll(OnScroll updateViewScroll) {
//        this.updateViewScroll = updateViewScroll;
//    }
//
//    public void setKey(String key) {
//        this.key = key;
//        updateAccountList();
//    }
//
//    public void setAccountList(List<Data> accountList) {
//        this.mAccountList = accountList;
//        updateRecyclerView();
//    }
//
//    public void scrollToPosition(int position) {
//        mLayoutManager.scrollToPosition(position);
//    }
//
//    public void scrollToEnd() {
//        scrollToPosition(mAccountList.size());
//    }
//
//    public View getChildAt(int index) {
//        return mRecyclerView.getChildAt(index);
//    }
//
//    @Deprecated
//    public int getCurrentPosition() {
//        int position = mLayoutManager.findFirstVisibleItemPosition();
//
//        return position == -1 ? 0 : position;
//    }
//
//    public abstract void updateRecyclerView();
//
//
//    public void addData(Data data) {
//        mAccountList.add(data);
//    }
//
//    public void updateData(int position, Data data) {
//        mAccountList.set(position, data);
//    }
//
//    public Data getData(int position) {
//        return mAccountList.get(position);
//    }
//
//    public int getItemCount() {
//        return mAccountList.size();
//    }
//
//    @Deprecated
//    public Data getCurrentData() {
//        return mAccountList.get(getCurrentPosition());
//    }
//
//
//    public void setEditable(boolean editable) {
//        this.editable = editable;
//    }
//
//
//    protected void updateAccountList() {
//        mAccountList = mDataViewModel.getAccountList(key, getType());
//    }
//
//    public interface OnScroll {
//        void onScrollChanged();
//    }
//}