//package com.security.passwordmanager.ui.bank;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.IdRes;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.security.passwordmanager.data.BankCard;
//import com.security.passwordmanager.data.DataType;
//import com.security.passwordmanager.data.DataViewModel;
//import com.security.passwordmanager.ui.DataRecyclerView;
//
//public class BankRecyclerView extends DataRecyclerView {
//
//    private BankCardAdapter mAdapter;
//
//    public BankRecyclerView(
//            @NonNull Context context,
//            @NonNull DataViewModel viewModel,
//            @IdRes int resId,
//            boolean horizontal) {
//
//        super(root, context, viewModel, resId, horizontal);
//    }
//
//    public BankRecyclerView(@NonNull AppCompatActivity activity, int resId, boolean horizontal, String bankName) {
//        super(activity, resId, horizontal, bankName);
//    }
//
//
//    @Override
//    public DataType getType() {
//        return DataType.BANK_CARD;
//    }
//
//    @SuppressLint("NotifyDataSetChanged")
//    @Override
//    public void updateRecyclerView() {
//        if (mAdapter == null) {
//            mAdapter = new BankCardAdapter();
//            getRecyclerView().setAdapter(mAdapter);
//        }
//        else
//            mAdapter.notifyDataSetChanged();
//    }
//
//    public BankCard getBankCard(int position) {
//        return (BankCard) getData(position);
//    }
//
//
//
//    private class BankCardAdapter extends RecyclerView.Adapter<BankCardHolder> {
//
//        @NonNull
//        @Override
//        public BankCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            return null;
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull BankCardHolder holder, int position) {
//
//        }
//
//        @Override
//        public int getItemCount() {
//            return 0;
//        }
//    }
//
//
//    private class BankCardHolder extends RecyclerView.ViewHolder {
//
//        public BankCardHolder(@NonNull View itemView) {
//            super(itemView);
//        }
//    }
//}
