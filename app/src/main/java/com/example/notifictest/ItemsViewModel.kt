package com.example.notifictest

import androidx.lifecycle.ViewModel

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