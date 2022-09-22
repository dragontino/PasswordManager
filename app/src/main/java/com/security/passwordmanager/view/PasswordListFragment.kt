package com.security.passwordmanager.view

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.security.passwordmanager.*
import com.security.passwordmanager.databinding.FragmentPasswordListBinding
import com.security.passwordmanager.model.Data
import com.security.passwordmanager.view.adapters.PasswordListAdapter
import com.security.passwordmanager.viewmodel.DataViewModel

class PasswordListFragment : Fragment() {

    companion object {
        private const val OPENED_VIEW_KEY = "opened_view"
//        private const val VISIBILITIES_KEY = "visibilities"
    }

    private lateinit var binding: FragmentPasswordListBinding

    private lateinit var adapter: PasswordListAdapter
    private var recyclerCallback: RecyclerCallback? = null

    private var openedView = -1
    set(value) {
        field = value
        adapter.openedView = value
    }

    private lateinit var dataViewModel: DataViewModel
    private val dataList: ArrayList<Data> = ArrayList()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        recyclerCallback = activity as RecyclerCallback
    }

    override fun onDetach() {
        super.onDetach()
        recyclerCallback = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.addMenuProvider (object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main, menu)

                val searchView = menu.findItem(R.id.menu_item_search).actionView as SearchView
                drawSearchView(searchView)

                searchView.doOnQueryTextChange {
                    val list = emptyList<Data>() //dataViewModel.searchData(it)
                    openedView = -1
                    adapter.dataList = list
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
                R.id.menu_item_search -> true
                else -> false
            }
        })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPasswordListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
            dataViewModel = DataViewModel.getInstance(this)
        }

        val openedView = savedInstanceState.getInt(OPENED_VIEW_KEY, -1)

        adapter = PasswordListAdapter(activity as AppCompatActivity)
        this.openedView = openedView

        binding.mainRecyclerView.adapter = adapter

        binding.mainRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                recyclerCallback?.onScroll(
                    when {
                        dy > 1 -> RecyclerDirection.DOWN
                        dy < 1 -> RecyclerDirection.UP
                        else -> RecyclerDirection.STOPPED
                    },
                    RecyclerState.getState(recyclerView.scrollState)
                )

                super.onScrolled(recyclerView, dx, dy)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                recyclerCallback?.onStateChanged(RecyclerState.getState(newState))

                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }


    override fun onResume() {
        super.onResume()
//        dataList.updateAll(dataViewModel.getDataList())
        adapter.dataList = dataList
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(OPENED_VIEW_KEY, openedView)
    }

    private fun drawSearchView(searchView: SearchView) {
        val searchText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        val searchImage = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_button)
        val clearView = searchView.findViewById<ImageView>(R.id.search_close_btn)

        searchText.setTextColor(Color.WHITE)
        searchText.backgroundTintList = ColorStateList(Color.WHITE)
        searchImage.imageTintList = ColorStateList(Color.WHITE)
        clearView.imageTintList = ColorStateList(Color.WHITE)
    }


//    private fun String.createUrlString() = when {
//        "www." in this -> "https://$this"
//        "https://www." in this || "http://www." in this -> this
//        else -> "https://www.$this"
//    }



//    private inner class PasswordAdapter : RecyclerView.Adapter<PasswordHolder>() {
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasswordHolder {
//            val itemBinding = MainTextViewBinding
//                .inflate(LayoutInflater.from(parent.context), parent, false)
//
//            val holder = PasswordHolder(itemBinding)
//
//            holder.setOnClickListener {
//                if (holder.adapterPosition == openedView)
//                    openedView = -1
//                else {
//                    val oldOpen = openedView
//                    openedView = holder.adapterPosition
//                    notifyItemChanged(oldOpen)
//                }
//                notifyItemChanged(holder.adapterPosition)
//            }
//
//            return holder
//        }
//
//        override fun onBindViewHolder(holder: PasswordHolder, position: Int) {
//            val data = dataList[position]
//            holder.bindPassword(data, position == openedView)
//        }
//
//        override fun getItemCount() = dataList.size
//    }
//
//
//
//
//    private inner class PasswordHolder(private val itemBinding: MainTextViewBinding):
//        RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {
//
//        private lateinit var accountList: ArrayList<Data>
//        private var moreInfoAdapter: MoreInfoAdapter? = null
//
//        private lateinit var data: Data
//
//        init {
//            itemBinding.showBottomFragment.setOnClickListener(this)
//        }
//
//
//        private fun LayoutMainHeaderBinding.getTextFields() = Pair(heading, subtitle)
//
//        private fun LayoutMainHeaderBinding.setText(headingText: String, subtitleText: String) {
//            val fields = getTextFields()
//            fields[0].text = headingText
//            fields[1].text = subtitleText
//            fields.setAll { it.setBackgroundColor(settings.backgroundColor) }
//            fields[0].setTextColor(settings.fontColor)
//        }
//
//        private fun LayoutMainHeaderBinding.getText() = Pair {
//            getTextFields()[it].text.toString()
//        }
//
//
//        fun bindPassword(data: Data, isShown: Boolean) {
//            this.data = data
//            accountList = dataViewModel.getAccountList(data) as ArrayList<Data>
//
//            moreInfoAdapter = MoreInfoAdapter(accountList)
//
//            itemBinding.run {
//                recyclerViewDescription.adapter = moreInfoAdapter
//
//                updateArrow(isShown)
//                setVisible(isShown)
//
//                when (data) {
//                    is Website -> {
//                        textView.setText(data.nameWebsite, data.address)
//
////                        Picasso.get()
////                            .load("https://yandex.ru")
////                            .error(R.drawable.website_image)
////                            .into(websiteImage)
//                    }
//                    is BankCard -> {
//                        textView.setText(data.bankName, data.cardNumber)
//                        websiteImage.imageTintList = ColorStateList.valueOf(settings.headerColor)
//                    }
//                }
//                imageView.imageTintList = ColorStateList.valueOf(settings.headerColor)
//                showBottomFragment.imageTintList = ColorStateList.valueOf(settings.fontColor)
//                showBottomFragment.setBackgroundColor(settings.backgroundColor)
//            }
//        }
//
//        private fun setVisible(visible: Boolean) {
//            val visibility = if (visible) View.VISIBLE else View.GONE
//            itemBinding.recyclerViewDescription.visibility = visibility
//        }
//
//        override fun onClick(v: View?) {
//            /** v = btnMore */
//            val text = itemBinding.textView.getText()
//
//            val bottomDialogFragment = ActionBottom.newInstance(activity as AppCompatActivity)
//            bottomDialogFragment.setHeading(text[0], text[1])
//
//            bottomDialogFragment.addView(R.drawable.edit, R.string.edit) {
//                when(data) {
//                    is Website -> startActivity(
//                        PasswordActivity.getIntent(context, data.key))
//                    is BankCard -> startActivity(
//                        BankCardActivity.getIntent(context, data.key))
//                }
//                bottomDialogFragment.dismiss()
//            }
//
//            bottomDialogFragment.addView(R.drawable.copy, R.string.copy_info) {
//                dataViewModel.copyAccountList(accountList)
//                bottomDialogFragment.dismiss()
//            }
//
//            bottomDialogFragment.addView(R.drawable.delete, R.string.delete_password) {
//                dataViewModel.deleteRecords(data)
//
//                bottomDialogFragment.dismiss()
//                adapter?.notifyItemRemoved(adapterPosition)
//            }
//            openBottomSheet(bottomDialogFragment)
//        }
//
//        fun setOnClickListener(l: View.OnClickListener) =
//            itemBinding.mainLayout.setOnClickListener(l)
//
//
//        //true - стрелка вниз, false - вправо
//        private fun updateArrow(isShown: Boolean) = if (isShown) {
//            itemBinding.motionContainer.transitionToEnd()
//        }
//        else {
//            itemBinding.motionContainer.transitionToStart()
//        }




//        private inner class MoreInfoAdapter(private val accountList: ArrayList<Data>):
//            RecyclerView.Adapter<MoreInfoHolder>() {
//
//            override fun getItemViewType(position: Int) =
//                accountList[position].type.number
//
//            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoreInfoHolder {
//                val layoutInflater = LayoutInflater.from(context)
//
//                val moreInfoBinding = when(viewType) {
//                    DataType.BANK_CARD.number -> MoreBankCardBinding
//                        .inflate(layoutInflater, parent, false)
//
//                    else -> MoreWebsiteBinding
//                        .inflate(layoutInflater, parent, false)
//                }
//
//                return MoreInfoHolder(moreInfoBinding)
//            }
//
//            override fun onBindViewHolder(holder: MoreInfoHolder, position: Int) {
//                val data = accountList[position]
//                holder.bindInfo(data, position)
//            }
//
//            override fun getItemCount() =
//                accountList.size
//        }
//
//
//
//        private inner class MoreInfoHolder(private val moreInfoBinding: ViewBinding):
//            RecyclerView.ViewHolder(moreInfoBinding.root) {
//
//            private lateinit var data: Data
//            private var pos = 0
//
//            private val bottomDialogFragment = ActionBottom.newInstance(activity as AppCompatActivity)
//
//            private var isPasswordVisible = false
//
//            fun TextView.setTextColor() =
//                setTextColor(settings.fontColor)
//
//            private fun ImageButton.setOnCopyListener(text: String) {
//                setOnClickListener { dataViewModel.copyText(text) }
//                setBackgroundColor(settings.layoutBackgroundColor)
//                imageTintList = ColorStateList.valueOf(settings.fontColor)
//            }
//
//            private fun TextViewFieldBinding.setText(text: String) {
//                textView.text = text
//            }
//
//
//            init {
//                when (moreInfoBinding) {
//                    is MoreWebsiteBinding -> {
//                        moreInfoBinding.buttonOpenUrl.setOnClickListener {
//                            openUrl()
//                        }
//
//                        moreInfoBinding.password.buttonVisibility.show()
//                        moreInfoBinding.password.buttonVisibility.setOnClickListener {
//                            updatePasswordView()
//                        }
//
//                        updatePasswordView(isPasswordVisible)
//                    }
//                    is MoreBankCardBinding -> {
//                        //todo доделать bankCard binding
//                    }
//                }
//            }
//
//            fun bindInfo(data: Data, position: Int) {
//                this.data = data
//                this.pos = position
//
//                when (moreInfoBinding) {
//                    is MoreWebsiteBinding -> {
//                        if (data !is Website) return
//
//                        moreInfoBinding.run {
//                            login.setText(data.login)
//                            password.setText(data.password)
//                            comment.setText(data.comment)
//
//                            if (data.nameAccount.isEmpty())
//                                nameAccount.hide()
//                            else
//                                nameAccount.txt = data.nameAccount
//
//                            if (data.comment.isEmpty()) {
//                                comment.textView.hide()
//                                comment.fieldItemButtonCopy.hide()
//                                commentHead.hide()
//                            }
//
//                            createBottomSheet()
//
//                            //colors
//                            root.backgroundTintList =
//                                ColorStateList.valueOf(settings.layoutBackgroundColor)
//
//                            for (item in arrayOf(login, password, comment)) {
//                                item.textView.setBackgroundColor(settings.layoutBackgroundColor)
//                                item.textView.setTextColor()
//                            }
//
//                            login.fieldItemButtonCopy.setOnCopyListener(data.login)
//                            password.fieldItemButtonCopy.setOnCopyListener(data.password)
//                            comment.fieldItemButtonCopy.setOnCopyListener(data.comment)
//
//                            loginHead.setTextColor()
//                            passwordHead.setTextColor()
//                            commentHead.setTextColor()
//
//                            nameAccount.setBackgroundColor(settings.layoutBackgroundColor)
//
//                            password.buttonVisibility.setBackgroundColor(settings.layoutBackgroundColor)
//
//                            password.buttonVisibility.imageTintList =
//                                ColorStateList.valueOf(settings.fontColor)
//
//                            buttonOpenUrl.setBackgroundResource(settings.buttonRes)
//
//                            root.setOnClickListener {
//                                if (settings.baseSettings.isUsingBottomView)
//                                    bottomDialogFragment.show(childFragmentManager)
//                            }
//                        }
//                    }
//                }
//            }
//
//
//            private fun updatePasswordView() {
//                isPasswordVisible = !isPasswordVisible
//                updatePasswordView(isPasswordVisible)
//            }
//
//            private fun updatePasswordView(visibility: Boolean) =
//                (moreInfoBinding as MoreWebsiteBinding).apply {
//                    if (visibility) {
//                        password.textView.inputType = InputType.TYPE_CLASS_TEXT
//                        password.buttonVisibility.setImageResource(R.drawable.visibility_off)
//                        password.buttonVisibility.contentDescription =
//                            getString(R.string.hide_password)
//                    } else {
//                        password.textView.inputType = 129
//                        password.buttonVisibility.setImageResource(R.drawable.visibility_on)
//                        password.buttonVisibility.contentDescription =
//                            getString(R.string.show_password)
//                    }
//                }
//
//            //todo нужно ли в bankCard?
//            fun createBottomSheet() {
//                moreInfoBinding as MoreWebsiteBinding
//
//                val heading = if (moreInfoBinding.nameAccount.isEmpty()) {
//                    (data as Website).login
//                }
//                else moreInfoBinding.nameAccount.text.toString()
//
//                bottomDialogFragment.setHeading(heading, beautifulDesign = true)
//
//                bottomDialogFragment.addView(R.drawable.edit, R.string.edit) {
//                    bottomDialogFragment.dismiss()
//                    startActivity(PasswordActivity
//                        .getIntent(context, (data as Website).address, pos))
//                }
//            }
//
//            fun openUrl() {
//                val address = (data as Website).address.createUrlString()
//
//                if (URLUtil.isValidUrl(address)) {
//                    val intent = Intent(Intent.ACTION_VIEW, address.toUri())
//                    startActivity(intent)
//                } else
//                    showToast(activity, "Неправильный адрес!")
//            }
//        }
//    }
}