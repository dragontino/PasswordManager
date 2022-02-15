package com.security.passwordmanager.ui.main

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.security.passwordmanager.*
import com.security.passwordmanager.data.BankCard
import com.security.passwordmanager.data.Data
import com.security.passwordmanager.data.DataViewModel
import com.security.passwordmanager.data.Website
import com.security.passwordmanager.settings.SettingsViewModel
import com.security.passwordmanager.ui.DataRecyclerView
import com.security.passwordmanager.ui.account.AccountRecyclerView
import com.security.passwordmanager.ui.account.PasswordActivity
import com.security.passwordmanager.ui.bank.BankCardActivity
import com.security.passwordmanager.ui.bank.BankRecyclerView

class PasswordListFragment : Fragment() {

    private fun Bundle?.getInt(key: String, defaultValue: Int) =
        this?.getInt(key) ?: defaultValue

    companion object {
        private const val OPENED_VIEW_KEY = "opened_view"
//        private const val VISIBILITIES_KEY = "visibilities"
    }

    private lateinit var mainRecyclerView: RecyclerView
    private var adapter: PasswordAdapter? = null
    private lateinit var searchView: SearchView

    private var openedView = -1

    private lateinit var settings: SettingsViewModel
    private lateinit var dataViewModel: DataViewModel
    private lateinit var dataList: List<Data>

    private fun openBottomSheet(bottomDialogFragment: ActionBottomDialogFragment) =
        activity?.supportFragmentManager?.let { bottomDialogFragment.show(it) }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_password_list, container, false)
        mainRecyclerView = root.findViewById(R.id.main_recycler_view)

        return root
    }

    // TODO: 07.02.2022 перенести меню в passwordListActivity
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main, menu)

        searchView = menu.findItem(R.id.menu_item_search).actionView as SearchView
        drawSearchView()

        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String) = false

                @SuppressLint("NotifyDataSetChanged")
                override fun onQueryTextChange(newText: String): Boolean {
                    dataList = dataViewModel.searchData(newText)
                    openedView =
                        if (dataList.size == 1) 0
                        else -1
                    adapter?.notifyDataSetChanged()
                    return true
                }
            },
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_item_settings)
            startActivity(SettingsActivity.getIntent(context))
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        activity?.let {
            settings = SettingsViewModel.getInstance(it)
            dataViewModel = ViewModelProvider(it)[DataViewModel::class.java]
        }

        dataList = dataViewModel.getDataList()

        openedView = savedInstanceState.getInt(OPENED_VIEW_KEY, -1)
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(OPENED_VIEW_KEY, openedView)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateUI() {
        // TODO: 09.02.2022 убрать!!!
//        activity?.let {
//            settings = ViewModelProvider(it)[SettingsViewModel::class.java]
//        }

        if (adapter == null) {
            adapter = PasswordAdapter()
            mainRecyclerView.adapter = adapter
        }
        else
            adapter?.notifyDataSetChanged()
    }

    private fun drawSearchView() {
        val searchText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        val searchImage = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_button)
        val clearView = searchView.findViewById<ImageView>(R.id.search_close_btn)

        searchText.setTextColor(Color.WHITE)
        searchText.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
        searchImage.imageTintList = ColorStateList.valueOf(Color.WHITE)
        clearView.imageTintList = ColorStateList.valueOf(Color.WHITE)
    }

    private fun getOpenedData() = if (openedView == -1)
        Website()
    else
        dataList[openedView]




    private inner class PasswordAdapter : RecyclerView.Adapter<PasswordHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasswordHolder {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_main_text_view, parent, false)

            val holder = PasswordHolder(view)

            holder.setOnClickListener {
                if (holder.adapterPosition == openedView)
                    openedView = -1
                else {
                    val oldOpen = openedView
                    openedView = holder.adapterPosition
                    notifyItemChanged(oldOpen)
                }
                notifyItemChanged(holder.adapterPosition)
            }

            return holder
        }

        override fun onBindViewHolder(holder: PasswordHolder, position: Int) {
            val data = dataList[position]
            holder.bindPassword(data, position == openedView)
        }

        override fun getItemCount() = dataList.size

    }

    private fun LinearLayout.getTextFields() = Pair(
        first = findViewById<TextView>(R.id.list_item_text_view_name),
        second = findViewById<TextView>(R.id.list_item_text_view_subtitle)
    )


    private fun LinearLayout.setText(headingText: String, subtitleText: String) {
        val fields = getTextFields()
        fields[0].text = headingText
        fields[1].text = subtitleText
        fields.setAll { it.setBackgroundColor(settings.backgroundColor) }
        fields[0].setTextColor(settings.fontColor)
    }

    private fun LinearLayout.getText() : Pair<String, String> {
        val fields = getTextFields()
        return getPair { fields[it].text.toString() }
    }





    private inner class PasswordHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private val mainInfoRecyclerView : DataRecyclerView
        private val accountRecyclerView = AccountRecyclerView(
            rootView = itemView,
            activity = activity as AppCompatActivity,
            recyclerIdRes = R.id.list_item_recycler_view_more_info,
            false
        )
        private var bankRecyclerView: BankRecyclerView? = null

        private val mainView = itemView.findViewById<LinearLayout>(R.id.list_item_main_view)
        private val imageView = itemView.findViewById<ImageView>(R.id.list_item_image_view)
        private val textView = itemView.findViewById<LinearLayout>(R.id.list_item_text_view)

        private val btnMore = itemView.findViewById<Button>(R.id.list_item_button_more)
        private val motionLayout = itemView.findViewById<MotionLayout>(R.id.motion_container)

        private lateinit var data: Data

        init {
            if (openedView != -1 && getOpenedData() is BankCard)
                bankRecyclerView = BankRecyclerView(
                    rootView = itemView,
                    activity = activity as AppCompatActivity,
                    recyclerIdRes = R.id.list_item_recycler_view_more_info_bank,
                    false
                )

            mainInfoRecyclerView = bankRecyclerView ?: accountRecyclerView

            btnMore.setOnClickListener(this)
        }

        fun bindPassword(data: Data, isShown: Boolean) {
            this.data = data
            accountRecyclerView.key = data.getKey()

            if (data is BankCard) {
                bankRecyclerView?.key = data.getKey()

                if (accountRecyclerView.isEmpty())
                    accountRecyclerView.setVisibility(View.GONE)
            }

            updateArrow(isShown)
            setVisibility(isShown)

            when (data) {
                is Website -> textView.setText(data.nameWebsite, data.address)
                is BankCard -> textView.setText(data.bankName, data.cardNumber)
            }

            imageView.imageTintList = ColorStateList.valueOf(settings.headerColor)
            btnMore.backgroundTintList = ColorStateList.valueOf(settings.fontColor)
        }

        fun setVisibility(visible: Boolean) {
            val visibility = if (visible) View.VISIBLE else View.GONE
            accountRecyclerView.setVisibility(visibility)
            bankRecyclerView?.setVisibility(visibility)
        }

        override fun onClick(v: View?) {
            //v = btnMore
            // TODO: 07.02.2022 сделать currentData()
            val text = textView.getText()

            val bottomDialogFragment = ActionBottom.newInstance(activity as AppCompatActivity)
            bottomDialogFragment.setHeading(text[0], text[1])

            bottomDialogFragment.addView(R.drawable.edit, R.string.edit) {
                when(data) {
                    is Website -> startActivity(
                        PasswordActivity.getIntent(requireContext(), data.getKey()))
                    is BankCard -> startActivity(
                        BankCardActivity.getIntent(requireContext(), data.getKey()))
                }
                bottomDialogFragment.dismiss()
            }

            bottomDialogFragment.addView(R.drawable.copy, R.string.copy_info) {
                mainInfoRecyclerView.copyAccountList()
                bottomDialogFragment.dismiss()
            }

            bottomDialogFragment.addView(R.drawable.delete, R.string.delete_password) {
                dataViewModel.deleteRecords(data)
                bottomDialogFragment.dismiss()
                updateUI()
            }
            openBottomSheet(bottomDialogFragment)
        }

        fun setOnClickListener(l: View.OnClickListener) =
            mainView.setOnClickListener(l)

        //true - стрелка вниз, false - вправо
        private fun updateArrow(isShown: Boolean) = if (isShown) {
            motionLayout.transitionToEnd()
        }
        else {
            motionLayout.transitionToStart()
        }
    }
}