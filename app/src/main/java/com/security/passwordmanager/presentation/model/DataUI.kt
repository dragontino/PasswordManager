package com.security.passwordmanager.presentation.model

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import com.security.passwordmanager.data.model.BankCard
import com.security.passwordmanager.data.model.Data
import com.security.passwordmanager.data.model.Website

data class DataUI(
    val title: Data,
    val accountList: MutableList<Data> = arrayListOf(title)
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

        val DefaultWebsite get() = DataUI(Website())
        val DefaultBankCard get() = DataUI(BankCard())


        @Suppress("DEPRECATION")
        private fun Parcel.readData(defaultValue: Data): Data =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                readParcelable(ClassLoader.getSystemClassLoader(), Data::class.java) ?: defaultValue
            } else {
                (readParcelable(ClassLoader.getSystemClassLoader()) as Data?) ?: defaultValue
            }

        private fun Parcel.createAccountList(data: Data): MutableList<Data> {
            val creator = when (data) {
                is BankCard -> BankCard.CREATOR
                is Website -> Website.CREATOR
            }
            return createTypedArrayList(creator)?.toMutableList() ?: mutableListOf()
        }


        private fun Parcel.readDataUI(defaultValue: DataUI): DataUI {
            val title = readData(defaultValue = defaultValue.title)
            val accountList = createAccountList(title)
            return DataUI(title, accountList)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(title, flags)
        parcel.writeParcelableList(accountList, flags)
    }

    override fun describeContents() = 0
}