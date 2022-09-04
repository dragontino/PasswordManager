package com.security.passwordmanager.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.security.passwordmanager.*
import com.security.passwordmanager.activities.BankCardActivity
import com.security.passwordmanager.activities.WebsiteActivity
import com.security.passwordmanager.data.BankCard
import com.security.passwordmanager.data.Data
import com.security.passwordmanager.data.Website
import com.security.passwordmanager.databinding.LayoutMainHeaderBinding
import com.security.passwordmanager.databinding.MainTextViewBinding
import com.security.passwordmanager.view.BottomDialogFragment
import com.security.passwordmanager.viewmodel.DataViewModel
import com.security.passwordmanager.viewmodel.SettingsViewModel

class PasswordListAdapter(private val activity: AppCompatActivity) :
    RecyclerView.Adapter<PasswordListAdapter.PasswordListHolder>() {

    var dataList = emptyList<Data>()
    set(newList) {
        DiffUtil.calculateDiff(RecyclerDiffCallback(field, newList)).dispatchUpdatesTo(this)
        field = newList
    }
    private val settings = SettingsViewModel.getInstance(activity)
    private val dataViewModel = DataViewModel.getInstance(activity)
    var openedView: Int = -1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasswordListHolder {
        val itemBinding = MainTextViewBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        val holder = PasswordListHolder(itemBinding)

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

    override fun onBindViewHolder(holder: PasswordListHolder, position: Int) {
        val data = dataList[position]
        holder.bind(data, position == openedView)
    }

    override fun getItemCount() = dataList.size


    inner class PasswordListHolder(private val itemBinding: MainTextViewBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        private lateinit var accountList: MutableList<Data>
        private var passwordDescriptionAdapter: PasswordDescriptionAdapter? = null
        private lateinit var data: Data


        private fun LayoutMainHeaderBinding.setText(headingText: String, subtitleText: String) {
            val fields = Pair(heading, subtitle)
            fields[0].text = headingText
            fields[1].text = subtitleText
            fields.setAll { it.setBackgroundColor(settings.backgroundColor) }
            fields[0].setTextColor(settings.fontColor)
        }

        private fun LayoutMainHeaderBinding.getText() = Pair {
            Pair(heading, subtitle)[it].text.toString()
        }

        init {
            val text = itemBinding.textView.getText()

            val bottomDialogFragment = BottomDialogFragment(settings).apply {
                setHeading(text[0], text[1])
                    addView(R.drawable.edit, this@PasswordListAdapter.activity, R.string.edit) {
                        when (data) {
                            is Website -> this@PasswordListAdapter.activity.startActivity(
                                WebsiteActivity.getIntent(activity, data.key)
                            )
                            is BankCard -> this@PasswordListAdapter.activity.startActivity(
                                BankCardActivity.getIntent(activity, data.key)
                            )
                        }
                    }
                    addView(R.drawable.copy, this@PasswordListAdapter.activity, R.string.copy_info) {
                        dataViewModel.copyAccountList(accountList)
                    }
                    addView(R.drawable.delete, this@PasswordListAdapter.activity, R.string.delete_password) {
                        dataViewModel.deleteRecords(data)
                        notifyItemRemoved(adapterPosition)
                    }
            }

            itemBinding.showBottomFragment.setOnClickListener {
                bottomDialogFragment.show(activity.supportFragmentManager)
            }
        }

        fun bind(data: Data, isShownDescription: Boolean) {
            this.data = data
            accountList = dataViewModel.getAccountList(data) as MutableList<Data>

            passwordDescriptionAdapter = PasswordDescriptionAdapter(activity, accountList)

            itemBinding.run {
                recyclerViewDescription.adapter = passwordDescriptionAdapter

                updateArrow(isShownDescription)
                setVisible(isShownDescription)

                when (data) {
                    is Website -> {
                        textView.setText(data.nameWebsite, data.address)
//                        Picasso.get()
//                            .load("https://yandex.ru")
//                            .error(R.drawable.website_image)
//                            .into(websiteImage)
                    }
                    is BankCard -> {
                        textView.setText(data.bankName, data.cardNumber)
                        websiteImage.imageTintList = ColorStateList(settings.headerColor)
                    }
                }
                imageView.imageTintList = ColorStateList(settings.headerColor)
                showBottomFragment.imageTintList = ColorStateList(settings.fontColor)
                showBottomFragment.setBackgroundColor(settings.backgroundColor)
            }
        }

        fun setOnClickListener(listener: View.OnClickListener) =
            itemBinding.mainLayout.setOnClickListener(listener)


        //true - стрелка вниз, false - вправо
        private fun updateArrow(isShown: Boolean) = if (isShown) {
            itemBinding.motionContainer.transitionToEnd()
        }
        else {
            itemBinding.motionContainer.transitionToStart()
        }

        private fun setVisible(visible: Boolean) {
            val visibility = if (visible) View.VISIBLE else View.GONE
            itemBinding.recyclerViewDescription.visibility = visibility
        }
    }



    private class RecyclerDiffCallback(
        private val oldList: List<Data>,
        private val newList: List<Data>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            newList[newItemPosition].id == oldList[oldItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            newList[newItemPosition] == oldList[oldItemPosition]
    }
}