package com.example.notifictest

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.os.Bundle
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.notifictest.PageFragment.Companion.NOTIFICATION_ID
import com.google.android.material.floatingactionbutton.FloatingActionButton


abstract class MutableCollectionBaseActivity : FragmentActivity() {

    private lateinit var buttonAdd: FloatingActionButton
    private lateinit var buttonRemove: FloatingActionButton
    private lateinit var viewPager: ViewPager2
    private lateinit var counterText: TextView

    private val TAG = "MutableCollectionBaseActivity"

    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mutable_collection)

        buttonAdd = findViewById(R.id.buttonAddAfter)
        buttonRemove = findViewById(R.id.buttonRemove)
        viewPager = findViewById(R.id.viewPager)
        counterText = findViewById(R.id.counter)

        viewPager.adapter = createViewPagerAdapter()
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            @SuppressLint("LongLogTag")
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                Log.i(TAG, "Сторінка ${position + 1}")

                displayMetaInfo(position)
            }
        })

        @SuppressLint("NotifyDataSetChanged")
        fun changeDataSet(performChanges: () -> Unit) {

            val idsOld = items.createIdSnapshot()
            performChanges()
            notifyDataSetChanged()
            val idsNew = items.createIdSnapshot()
            DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int = idsOld.size
                override fun getNewListSize(): Int = idsNew.size

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                    idsOld[oldItemPosition] == idsNew[newItemPosition]

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                    areItemsTheSame(oldItemPosition, newItemPosition)
            }, true).dispatchUpdatesTo(viewPager.adapter!!)
        }

        buttonRemove.setOnClickListener {
            // Видаляємо останню
            changeDataSet { items.removeAt(items.size - 1) }
            viewPager.setCurrentItem(items.size, true) //переходимо на передостанню

            removeNotification()
        }

        buttonAdd.setOnClickListener {
            changeDataSet { items.addNewAt(items.size) }
            viewPager.setCurrentItem(items.size, true)
        }
    }

    open fun removeNotification() {
        val notificationManager =
            getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }

    private fun displayMetaInfo(position: Int) {
        counterText.text = items.itemId(position).toString()

        showHide(position)
    }

    private fun showHide(position: Int) {
        if (position == 0) {
            buttonRemove.visibility = INVISIBLE
        } else {
            buttonRemove.visibility = VISIBLE
        }
    }

    abstract fun createViewPagerAdapter(): RecyclerView.Adapter<*>

    val items: ItemsViewModel by viewModels()

    /**
     * Shows how to use notifyDataSetChanged with [ViewPager2]
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun notifyDataSetChanged() {
        viewPager.adapter!!.notifyDataSetChanged()
    }
}