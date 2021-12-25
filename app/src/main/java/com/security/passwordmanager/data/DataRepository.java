package com.security.passwordmanager.data;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DataRepository {

    private final DataDao dataDao;

    public DataRepository(DataDao dataDao) {
        this.dataDao = dataDao;
    }

    public void addData(Data data) {
        if (data.isWebsite())
            dataDao.addWebsite((Website) data);
        else
            dataDao.addBankCard((BankCard) data);
    }


    public void updateData(Data data) {
        if (data.isWebsite())
            dataDao.updateWebsite((Website) data);
        else
            dataDao.updateBankCard((BankCard) data);
    }


    public List<Data> getDataList() {

        List<Website> websiteList = dataDao.getWebsiteList();
        List<BankCard> bankCardList = dataDao.getBankCardList();

        if (websiteList == null)
            websiteList = new ArrayList<>();
        if (bankCardList == null)
            bankCardList = new ArrayList<>();

        check(websiteList);
        check(bankCardList);

        return sortedConcat(websiteList, bankCardList);
    }

    @Nullable
    public List<Data> getAccountList(String key, @Data.DataType int type) {
        List<Website> websiteList = dataDao.getAccountList(key);

        if (type == Data.TYPE_BANK_CARD) {
            List<BankCard> bankCardList = dataDao.getBankAccountList(key);
            return sortedConcat(websiteList, bankCardList);
        }
        else if (websiteList == null)
            return null;
        else
            return new ArrayList<>(websiteList);
    }

    @Nullable
    public List<Data> searchData(String query) {
        if (query == null || query.length() == 0)
            return getDataList();

        List<Website> websiteList = dataDao.searchWebsite(query);
        List<BankCard> bankCardList = dataDao.searchBankCard(query);

        check(websiteList);
        check(bankCardList);

        return sortedConcat(websiteList, bankCardList);
    }


    //удаляет только 1 запись в бд
    public void deleteData(Data data) {
        if (data.isWebsite())
            dataDao.deleteWebsite((Website) data);
        else
            dataDao.deleteBankCard((BankCard) data);
    }

    //удаляет несколько записей в бд
    public void deleteData(String key, @Data.DataType int type) {
        if (type == Data.TYPE_BANK_CARD)
            dataDao.deleteBankCard(key);
        else
            dataDao.deleteWebsite(key);
    }





    private List<Data> sortedConcat(
            List<Website> websiteList, List<BankCard> bankCardList) {

        if (websiteList.size() == 0)
            return new ArrayList<>(bankCardList);
        else if (bankCardList.size() == 0)
            return new ArrayList<>(websiteList);

        List<Data> dataList = new ArrayList<>();

        int min = Math.min(websiteList.size(), bankCardList.size());
        int w = 0, b = 0;

        while (w < min && b < min) {
            Website website = websiteList.get(w);
            BankCard bankCard = bankCardList.get(b);

            if (website.compareTo(bankCard)) {
                checkAndAdd(dataList, website);
                w++;
            }
            else {
                checkAndAdd(dataList, bankCard);
                b++;
            }
        }

        if (w > b) for (; b < bankCardList.size(); b++)
            checkAndAdd(dataList, bankCardList.get(b));

        else if (w < b) for (; w < websiteList.size(); w++)
            checkAndAdd(dataList, websiteList.get(w));

        return dataList;
    }

    private void checkAndAdd(List<Data> list, Data value) {
        for (Data d : list)
            if (value.equals(d))
                return;

        list.add(value);
    }

    private void check(List<? extends Data> list) {
        int first = 0;

        while (first < list.size()) {
            Data data = list.get(first);

            for (int j = first + 1; j < list.size(); j++)
                if (data.equals(list.get(j))) {
                    list.remove(first);
                    break;
                }

            first++;
        }
    }
}
