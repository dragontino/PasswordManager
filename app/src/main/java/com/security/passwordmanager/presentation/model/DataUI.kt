package com.security.passwordmanager.presentation.model

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import com.security.passwordmanager.data.model.BankCard
import com.security.passwordmanager.data.model.Data
import com.security.passwordmanager.data.model.Website

data class DataUI(
    val title: ObservableData,
    val accountList: MutableList<ObservableData> = arrayListOf(title)
) : Parcelable {
    private constructor(dataUI: DataUI) : this(
        title = dataUI.title,
        accountList = dataUI.accountList
    )

    constructor(parcel: Parcel) : this(dataUI = parcel.readDataUI(DefaultWebsite))

    companion object CREATOR : Parcelable.Creator<DataUI> {
        override fun createFromParcel(parcel: Parcel): DataUI {
            return DataUI(parcel)
        }

        override fun newArray(size: Int): Array<DataUI?> {
            return arrayOfNulls(size)
        }

        val DefaultWebsite get() = DataUI(ObservableWebsite())
        val DefaultBankCard get() = DataUI(ObservableBankCard())


        @Suppress("DEPRECATION")
        private fun Parcel.readObservableData(defaultValue: ObservableData): ObservableData {
            val defaultData = defaultValue.toData()

            val resultData = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                    readParcelable(ClassLoader.getSystemClassLoader(), Data::class.java) ?: defaultData
                }
                else -> {
                    (readParcelable(ClassLoader.getSystemClassLoader()) as? Data) ?: defaultData
                }
            }

            return resultData.observe()
        }

        private fun Parcel.createObservableAccountList(observableData: ObservableData): MutableList<ObservableData> {
            val creator = when (observableData.toData()) {
                is BankCard -> BankCard.CREATOR
                is Website -> Website.CREATOR
            }
            return createTypedArrayList(creator)
                ?.map { it.observe() }
                ?.toMutableList()
                ?: mutableListOf()
        }


        private fun Parcel.readDataUI(defaultValue: DataUI): DataUI {
            val title = readObservableData(defaultValue = defaultValue.title)
            val accountList = createObservableAccountList(title)
            return DataUI(title, accountList)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(title.toData(), flags)
        parcel.writeParcelableList(accountList.map { it.toData() }, flags)
    }

    override fun describeContents() = 0
}