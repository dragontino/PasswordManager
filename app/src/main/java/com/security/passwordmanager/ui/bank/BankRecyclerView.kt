package com.security.passwordmanager.ui.bank

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.security.passwordmanager.data.BankCard
import com.security.passwordmanager.data.DataType
import com.security.passwordmanager.ui.DataRecyclerView

class BankRecyclerView : DataRecyclerView {

    constructor(
        activity: AppCompatActivity,
        recyclerView: RecyclerView,
        bankName: String,
    ) : super(activity, recyclerView, DataType.BANK_CARD, bankName)

    constructor(
        activity: AppCompatActivity,
        recyclerView: RecyclerView,
        editable: Boolean
    ) : super(activity, recyclerView, DataType.BANK_CARD, editable = editable)

    private var adapter : BankCardAdapter? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun updateRecyclerView() {
        if (adapter == null) {
            adapter = BankCardAdapter()
            recyclerView.adapter = adapter
        }
        else adapter?.notifyDataSetChanged()
    }

    override fun getData(position: Int) : BankCard =
        super.getData(position) as BankCard





    private inner class BankCardAdapter : RecyclerView.Adapter<BankCardHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankCardHolder {
            TODO("Not yet implemented")
        }

        override fun onBindViewHolder(holder: BankCardHolder, position: Int) {
            TODO("Not yet implemented")
        }

        override fun getItemCount(): Int {
            TODO("Not yet implemented")
        }

    }



    private inner class BankCardHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        fun bindBankCard(bankCard : BankCard, position : Int) {
            //TODO сделать holder
        }
    }
}