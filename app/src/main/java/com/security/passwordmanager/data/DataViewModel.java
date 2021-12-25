package com.security.passwordmanager.data;

import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.security.passwordmanager.Cryptographer;
import com.security.passwordmanager.R;

import java.util.ArrayList;
import java.util.List;

public class DataViewModel extends AndroidViewModel {

    private static final String COPY_LABEL = "DataViewModel_copy";

    private final DataRepository dataRepository;
    private final Application mApplication;
    private final Cryptographer mCryptographer;

    public DataViewModel(@NonNull Application application) {
        super(application);
        this.mApplication = application;
        mCryptographer = new Cryptographer(application);

        DataDao dataDao = MainDatabase.getDatabase(application).websiteDao();
        dataRepository = new DataRepository(dataDao);
    }

    public void addData(Data data) {
        dataRepository.addData(data.encrypt(mCryptographer));
    }

    public void updateData(Data data) {
        dataRepository.updateData(data.encrypt(mCryptographer));
    }

    public List<Data> getDataList() {
        List<Data> dataList = dataRepository.getDataList();
        decryptList(dataList);
        return dataList;
    }

    public List<Data> getAccountList(String key, @Data.DataType int type) {
        List<Data> accountList = dataRepository.getAccountList(key, type);
        if (accountList == null)
            return new ArrayList<>();
        else {
            decryptList(accountList);
            return accountList;
        }
    }

    public List<Data> searchData(String query) {
        return dataRepository.searchData(query);
    }

    public void deleteData(Data data) {
        dataRepository.deleteData(data.encrypt(mCryptographer));
    }

    public void deleteData(String key, @Data.DataType int type) {
        dataRepository.deleteData(key, type);
    }

    public void copyData(Data data) {
        String dataString = data.toString(mApplication, true);
        copyText(dataString);
    }

    public void copyAccountList(List<Data> accountList) {
        StringBuilder builder = new StringBuilder(
                accountList.get(0).toString(mApplication, true));

        for (int i = 1, accountListSize = accountList.size(); i < accountListSize; i++) {
            Data d = accountList.get(i);
            builder.append("\n").append(d.toString(mApplication, false));
        }

        copyText(builder.toString());
    }

    public void copyText(String text) {
        ClipboardManager clipboard = (ClipboardManager)
                mApplication.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(COPY_LABEL, text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(mApplication, R.string.clipText, Toast.LENGTH_SHORT).show();
    }




    private void decryptList(@NonNull List<Data> list) {
        for (int i = 0; i < list.size(); i++) {
            Data data = list.get(i);
            list.set(i, data.decrypt(mCryptographer));
        }
    }
}
