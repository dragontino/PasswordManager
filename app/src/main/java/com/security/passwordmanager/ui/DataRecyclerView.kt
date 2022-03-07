package com.security.passwordmanager.ui

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.security.passwordmanager.data.*
import com.security.passwordmanager.settings.SettingsViewModel

abstract class DataRecyclerView(
    protected val activity: AppCompatActivity,
    protected val recyclerView: RecyclerView,
    private val type: DataType,
    key: String = "",
    val editable: Boolean = true
) {

    //todo попробовать имплементить iterable<View>

    var key = key
        set(value) {
            field = value
            updateAccountList()
        }

    private val layoutManager: LinearLayoutManager =
        recyclerView.layoutManager as LinearLayoutManager

    protected val settings = SettingsViewModel.getInstance(activity)
    protected val dataViewModel = ViewModelProvider(activity)[DataViewModel::class.java]

    protected lateinit var accountList : ArrayList<Data>
    val startCount : Int

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
        accountList = (if (key.isEmpty())
            ArrayList()
        else
            dataViewModel.getAccountList(key, type) as ArrayList<Data>)
        updateRecyclerView()
    }

    fun setVisibility(visibility : Int) {
        recyclerView.visibility = visibility
    }

    fun isEmpty() = accountList.isEmpty()

    fun scrollToPosition(position : Int) = recyclerView.post {
        recyclerView.smoothScrollToPosition(position)
    }

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