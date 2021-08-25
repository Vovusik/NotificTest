package com.example.notifictest

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter


const val KEY_ITEM_TEXT = "com.example.notifictest.KEY_ITEM_TEXT"

class MutableCollectionFragmentActivity : MutableCollectionBaseActivity() {

    override fun createViewPagerAdapter(): RecyclerView.Adapter<*> {

        val items = items // дозволяє уникнути вирішення ViewModel кілька разів

        return object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int): PageFragment {
                val itemId = items.itemId(position)
                val itemText = items.getItemById(itemId)
                return PageFragment.newInstance(itemText)
            }

            override fun getItemCount(): Int = items.size
            override fun getItemId(position: Int): Long = items.itemId(position)
            override fun containsItem(itemId: Long): Boolean = items.contains(itemId)
        }
    }
}

class PageFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.item_mutable_collection, container, false)

        val buttonNotification: Button = view.findViewById(R.id.buttonNotification)
        buttonNotification.setText(R.string.create_new_notification)
        index = arguments?.getString(KEY_ITEM_TEXT) ?: throw IllegalStateException()

        buttonNotification.setOnClickListener {

            val toast = Toast.makeText(
                    activity,
                    "Notification $index", Toast.LENGTH_SHORT
            )
            toast.show()

            MNotification(context).notificationReminder()
        }
        return view
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        var index = ""
        fun newInstance(itemText: String) =
                PageFragment().apply {
                    arguments = Bundle(1).apply {
                        putString(KEY_ITEM_TEXT, itemText)
                    }
                }
    }
}