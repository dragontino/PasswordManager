package com.security.passwordmanager.ui.account

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.text.InputType
import android.view.*
import android.widget.*
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.security.passwordmanager.R
import com.security.passwordmanager.data.DataType
import com.security.passwordmanager.data.Website
import com.security.passwordmanager.show
import com.security.passwordmanager.ui.DataRecyclerView




class AccountRecyclerView : DataRecyclerView {

    constructor(
        activity: AppCompatActivity,
        @IdRes recyclerIdRes: Int,
        address: String,
    ) : super(activity, recyclerIdRes, DataType.WEBSITE, address)

    constructor(
        rootView: View,
        activity: AppCompatActivity,
        @IdRes recyclerIdRes: Int,
        editable: Boolean
    ) : super(rootView, activity, recyclerIdRes, DataType.WEBSITE, editable)



    private var adapter : AccountAdapter? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun updateRecyclerView() {
        if (adapter == null) {
            adapter = AccountAdapter()
            recyclerView.adapter = adapter
        }
        else
        // TODO: 06.02.2022 сделать adapter?.notifyItemChanged(getCurrentPosition())
            adapter?.notifyDataSetChanged()
    }



    override fun getData(position : Int) =
        super.getData(position) as Website



    private inner class AccountAdapter : RecyclerView.Adapter<AccountHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountHolder {
            val layoutRes = if (editable)
                R.layout.list_item_new_account
            else
                R.layout.list_item_more_website

            val view = LayoutInflater.from(activity).inflate(layoutRes, parent, false)
            return AccountHolder(view)
        }

        override fun onBindViewHolder(holder: AccountHolder, position: Int) {
            val website = getData(position)
            holder.bindAccount(website, position)
        }

        override fun getItemCount() = accountList.size
    }



    private inner class AccountHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private val nameAccount = itemView.findViewById<EditText>(R.id.list_item_name_of_account)

        private val login: TextView
        private val password: TextView
        private val comment: TextView

        private var loginHead: TextView? = null
        private var passwordHead: TextView? = null
        private var commentHead: TextView? = null

        private var loginCopy: ImageButton? = null
        private var passwordCopy: ImageButton? = null
        private var commentCopy: ImageButton? = null

        private var editName : Button? = null

        private val passwordVisibility : ImageButton
        private var buttonOpenUrl : Button? = null

        private var website = Website()

        private var isPasswordVisible = false
        private var pos = 0

        private var renamingText = R.string.rename_account

        init {
            if (editable) {
                login = itemView.findViewById(R.id.list_item_login)
                password = itemView.findViewById(R.id.list_item_password)
                comment = itemView.findViewById(R.id.list_item_comment)

                passwordVisibility = itemView.findViewById(R.id.field_item_button_visibility)

                editName = itemView.findViewById(R.id.edit_name_of_account)
                editName?.setOnClickListener(this)
            }
            else {
                login = itemView.getTextViewFromLayout(R.id.list_item_login)
                password = itemView.getTextViewFromLayout(R.id.list_item_password)
                comment = itemView.getTextViewFromLayout(R.id.list_item_comment)

                loginHead = itemView.findViewById(R.id.list_item_login_head)
                passwordHead = itemView.findViewById(R.id.list_item_password_head)
                commentHead = itemView.findViewById(R.id.list_item_comment_head)

                loginCopy = itemView.getButtonCopyFromLayout(R.id.list_item_login)
                passwordCopy = itemView.getButtonCopyFromLayout(R.id.list_item_password)
                commentCopy = itemView.getButtonCopyFromLayout(R.id.list_item_comment)

                passwordVisibility = itemView.findViewById<LinearLayout>(R.id.list_item_password)
                    .findViewById(R.id.field_item_button_visibility)

                buttonOpenUrl = itemView.findViewById(R.id.list_item_button_open_url)
                buttonOpenUrl?.setOnClickListener(this)
            }

            passwordVisibility.visibility = View.VISIBLE
            passwordVisibility.setOnClickListener(this)

            updatePasswordView(isPasswordVisible)
        }

        private fun View.getTextViewFromLayout(@IdRes resId : Int) =
            findViewById<LinearLayout>(resId)
                .findViewById<TextView>(R.id.field_item_text_view)

        private fun View.getButtonCopyFromLayout(@IdRes resId: Int) =
            findViewById<LinearLayout>(resId)
                .findViewById<ImageButton>(R.id.field_item_button_copy)

        private fun TextView.setTextColor() {
            setTextColor(settings.fontColor)
        }

        fun TextView.hide(@IdRes headId: Int) {
            visibility = View.GONE
            itemView.findViewById<TextView>(headId).visibility = View.GONE
        }

        fun ImageButton.setOnCopyListener(text: String) {
            setOnClickListener { dataViewModel.copyText(text) }
            setBackgroundColor(settings.layoutBackgroundColor)
            imageTintList = ColorStateList.valueOf(settings.fontColor)
        }

        fun TextView.setBackground() = if (editable)
            setBackgroundResource(settings.backgroundRes)
        else
            setBackgroundColor(settings.layoutBackgroundColor)


        fun bindAccount(website: Website, position: Int) {
            this.website = website
            this.pos = position

            login.text = website.login
            password.text = website.password
            comment.text = website.comment

            createBottomSheet()

            if (website.nameAccount.isEmpty()) {
                if (editable)
                    nameAccount.setText(getNameAccountStart(position))
                else
                    nameAccount.visibility = View.GONE
            }
            else {
                nameAccount.setText(website.nameAccount)
            }

            if (!editable && website.comment.isEmpty()) {
                comment.hide(R.id.list_item_comment_head)
                commentCopy?.visibility = View.GONE
            }

            itemView.setBackgroundColor(settings.layoutBackgroundColor)

            login.setBackground()
            password.setBackground()
            comment.setBackground()

            login.setTextColor()
            password.setTextColor()
            comment.setTextColor()

            loginCopy?.setOnCopyListener(website.login)
            passwordCopy?.setOnCopyListener(website.password)
            commentCopy?.setOnCopyListener(website.comment)

            loginHead?.setTextColor()
            passwordHead?.setTextColor()
            commentHead?.setTextColor()

            editName?.backgroundTintList = ColorStateList.valueOf(settings.fontColor)

            if (editable) nameAccount.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    website.nameAccount = nameAccount.text.toString()
                    nameAccount.changeHeading()
                    renamingText = R.string.rename_account
                    bottomDialogFragment.editView(0, renamingText)
                    return@setOnKeyListener true
                }
                false
            }
            else
                nameAccount.setBackgroundColor(settings.layoutBackgroundColor)


            passwordVisibility.setBackgroundColor(
                if (editable) settings.backgroundColor
                else settings.layoutBackgroundColor
            )
            passwordVisibility.imageTintList = ColorStateList.valueOf(settings.fontColor)

            buttonOpenUrl?.setBackgroundResource(settings.buttonRes)

            if (editable)
                editName?.setOnClickListener {
                    bottomDialogFragment.show(activity.supportFragmentManager)
                }
            else
                itemView.setOnClickListener {
                    bottomDialogFragment.show(activity.supportFragmentManager)
                }
        }

        fun updatePasswordView() {
            isPasswordVisible = !isPasswordVisible
            updatePasswordView(isPasswordVisible)
        }

        fun updatePasswordView(visibility : Boolean) = if (visibility) {
            password.inputType = InputType.TYPE_CLASS_TEXT
            passwordVisibility.setImageResource(R.drawable.visibility_off)
            passwordVisibility.contentDescription = activity.getString(R.string.hide_password)
        }
        else {
            password.inputType = 129
            passwordVisibility.setImageResource(R.drawable.visibility_on)
            passwordVisibility.contentDescription = activity.getString(R.string.show_password)
        }


        fun createBottomSheet() {
            val heading = when {
                editable -> website.nameAccount
                nameAccount.text.isEmpty() -> website.nameWebsite
                else -> nameAccount.text.toString()
            }
            val subtitle = when {
                editable || heading.isNotEmpty() -> null
                else -> website.login
            }

            bottomDialogFragment.setHeading(heading, subtitle)

            if (editable) {
                bottomDialogFragment.addView(R.drawable.edit, renamingText) {
                    nameAccount.changeHeading()

                    renamingText = if (nameAccount.isCursorVisible)
                        R.string.cancel_renaming_account
                    else
                        R.string.rename_account

                    bottomDialogFragment.editView(0, renamingText)
                    bottomDialogFragment.dismiss()
                }

                bottomDialogFragment.addView(R.drawable.copy, R.string.copy_info) {
                    dataViewModel.copyData(website)
                    bottomDialogFragment.dismiss()
                }

                bottomDialogFragment.addView(R.drawable.delete, R.string.delete_account) {
                    dataViewModel.deleteData(website)
                    updateAccountList()
                    bottomDialogFragment.dismiss()

                    if (accountList.isEmpty())
                        activity.finish()

                    adapter?.notifyItemRemoved(pos)
                }
            }
            else bottomDialogFragment.addView(R.drawable.edit, R.string.edit) {
                bottomDialogFragment.dismiss()
                activity.startActivity(
                    PasswordActivity
                        .getIntent(activity, website.address, pos + 1)
                )
            }
        }


        fun openUrl() {
            val address = when {
                website.address.contains("www.") -> "https://${website.address}"
                website.address.contains("https://www.") ||
                        website.address.contains("http://www.") -> website.address
                else -> "https://www.${website.address}"
            }

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(address))
            activity.startActivity(intent)
        }

        fun getNameAccountStart(position: Int) =
            activity.getString(R.string.account_start, position + 1)


        fun EditText.changeHeading() {
            //true - заблокирует, false - разблокирует
            val blocking = isCursorVisible
            block()

            val full = getNameAccountStart(pos)
            val zero = ""

            if (blocking && text.isEmpty())
                setText(full)
            else if (!blocking && text.toString() == full)
                setText(zero)

            backgroundTintList = if (blocking) {
                setTextColor(settings.darkerGrayColor)
                ColorStateList.valueOf(settings.darkerGrayColor)
            } else {
                setTextColor(settings.fontColor)
                ColorStateList.valueOf(settings.headerColor)
            }
        }

        //true - блокировать, false - разблокировать
        fun TextView.block() {
            val blocking = isCursorVisible
            isEnabled = !blocking
            isCursorVisible = !blocking
        }

        override fun onClick(v: View?) {
            when(v?.id) {
                R.id.field_item_button_visibility -> updatePasswordView()
                R.id.list_item_button_open_url -> openUrl()
            }
        }
    }
}