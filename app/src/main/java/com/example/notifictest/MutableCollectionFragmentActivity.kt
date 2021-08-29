package com.example.notifictest

import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter


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

