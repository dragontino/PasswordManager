package com.security.passwordmanager.ui

import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.security.passwordmanager.ActionBottom
import com.security.passwordmanager.data.*
import com.security.passwordmanager.settings.SettingsViewModel

abstract class DataRecyclerView(
    rootView: View,
    protected val activity: AppCompatActivity,
    @IdRes recyclerIdRes: Int,
    private val type: DataType,
    key: String,
    val editable: Boolean) {

    constructor(
        activity: AppCompatActivity,
        @IdRes recyclerIdRes: Int,
        type: DataType,
        key: String
    ) : this(activity.window.decorView, activity, recyclerIdRes, type, key, true)

    constructor(
        rootView: View,
        activity: AppCompatActivity,
        @IdRes recyclerIdRes: Int,
        type: DataType,
        editable: Boolean
    ) : this(rootView, activity, recyclerIdRes, type, "", editable)

    var key = key
        set(value) {
            field = value
            updateAccountList()
        }

    protected val recyclerView: RecyclerView = rootView.findViewById(recyclerIdRes)
    private val layoutManager: LinearLayoutManager =
        recyclerView.layoutManager as LinearLayoutManager

    protected val settings = ViewModelProvider(activity)[SettingsViewModel::class.java]
    protected val dataViewModel = ViewModelProvider(activity)[DataViewModel::class.java]

    protected lateinit var accountList : ArrayList<Data>
    val startCount : Int

    protected val bottomDialogFragment = ActionBottom.newInstance(activity)

    abstract fun updateRecyclerView()

    init {
        updateAccountList()
        startCount = accountList.size

        if (isEmpty()) {
            accountList.add(
                if (type == DataType.WEBSITE) Website()
                else BankCard()
            )
        }

    }

    fun copyAccountList() =
        dataViewModel.copyAccountList(accountList)

    protected fun updateAccountList() {
        accountList = if (key.isEmpty())
            ArrayList()
        else
            dataViewModel.getAccountList(key, type) as ArrayList<Data>
        updateRecyclerView()
    }

    fun setVisibility(visibility : Int) {
        recyclerView.visibility = visibility
    }

    fun isEmpty() = accountList.isEmpty()

    fun scrollToPosition(position : Int) = recyclerView.scrollToPosition(position)

    fun scrollToEnd() = scrollToPosition(itemCount - 1)

    operator fun get(index : Int): View = recyclerView.getChildAt(index)

    private val currentPosition : Int get() {
        val position = layoutManager.findFirstVisibleItemPosition()

        return if (position == -1) 0
        else position
    }

    fun addData(data: Data) {
        accountList.add(data)
        updateRecyclerView()
    }

    open fun getData(position: Int) = accountList[position]

    val itemCount get() = accountList.size

    fun getCurrentData() = getData(currentPosition)
}