package com.example.notifictest

import android.annotation.SuppressLint
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mutable_collection)

        buttonAdd = findViewById(R.id.buttonAddAfter)
        buttonRemove = findViewById(R.id.buttonRemove)
        viewPager = findViewById(R.id.viewPager)
        counterText = findViewById(R.id.counter)

        viewPager.adapter = createViewPagerAdapter()
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

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
            changeDataSet { items.removeAt(viewPager.currentItem) }

        }

        buttonAdd.setOnClickListener {
            changeDataSet { items.addNewAt(items.size) }
        }
    }

    private fun displayMetaInfo(position: Int) {
        counterText.text = items.itemId(position).toString()
    }

    abstract fun createViewPagerAdapter(): RecyclerView.Adapter<*>

    val items: ItemsViewModel by viewModels()
}


/** Колекція предметів. Оптимізовано для простоти (тобто не продуктивності). */
class ItemsViewModel : ViewModel() {
    private var nextValue = 1

    // створюю колекцію з 1-го елемента
    private val items = (1..1).map { longToItem(nextValue++) }.toMutableList()

    fun getItemById(id: Long): String = items.first { itemToLong(it) == id }
    fun itemId(position: Int): Long = itemToLong(items[position])
    fun contains(itemId: Long): Boolean = items.any { itemToLong(it) == itemId }
    fun addNewAt(position: Int) = items.add(position, longToItem(nextValue++))
    fun removeAt(position: Int) = items.removeAt(position)
    fun createIdSnapshot(): List<Long> = (1 until size).map { position -> itemId(position) }
    val size: Int get() = items.size

    private fun longToItem(value: Int): String = "item#$value"
    private fun itemToLong(value: String): Long = value.split("#")[1].toLong()
}