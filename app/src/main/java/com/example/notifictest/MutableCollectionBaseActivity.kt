package com.example.notifictest

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.*
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton

abstract class MutableCollectionBaseActivity : FragmentActivity() {

    private lateinit var buttonAdd: FloatingActionButton
    private lateinit var buttonRemove: FloatingActionButton
    private lateinit var viewPager: ViewPager2
    private lateinit var counterText: TextView

    private val TAG = "MutableCollectionBaseActivity"

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

                Log.d(TAG, "###Сторінка PageSelected ${position + 1}")

                displayMetaInfo(position)
            }
        })

        @SuppressLint("NotifyDataSetChanged")
        fun changeDataSet(performChanges: () -> Unit) {

            val idsOld = items.createIdSnapshot()
            performChanges()

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
            // Видаляємо останню, переходимо на передостанню
//            changeDataSet { items.removeAt(items.size - 1) }
//            viewPager.setCurrentItem(items.size, true)

            // При видаленні поточної, переходимо на попередню
            changeDataSet { items.removeAt(viewPager.currentItem) }
            viewPager.setCurrentItem(viewPager.currentItem - 1, true)


        }

        buttonAdd.setOnClickListener {
            changeDataSet { items.addNewAt(items.size) }
            viewPager.setCurrentItem(items.size, true)
        }
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


/** Колекція предметів. Оптимізовано для простоти (тобто не продуктивності). */
class ItemsViewModel : ViewModel() {
    private var nextValue = 1L

    private val items = (1..1).map { longToItem(nextValue++) }.toMutableList()

    fun getItemById(id: Long): String = items.first { itemToLong(it) == id }
    fun itemId(position: Int): Long = itemToLong(items[position])
    fun contains(itemId: Long): Boolean = items.any { itemToLong(it) == itemId }
    fun addNewAt(position: Int) = items.add(position, longToItem(nextValue++))
    fun removeAt(position: Int) = items.removeAt(position)
    fun createIdSnapshot(): List<Long> = (0 until size).map { position -> itemId(position) }
    val size: Int get() = items.size

    private fun longToItem(value: Long): String = "$value"
    private fun itemToLong(value: String): Long = value.toLong()
}