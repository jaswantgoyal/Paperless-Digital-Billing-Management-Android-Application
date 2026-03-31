package com.example.billing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WeightViewModel : ViewModel() {
    private val _entries = MutableLiveData<MutableList<WeightEntry>>(mutableListOf())
    val entries: LiveData<MutableList<WeightEntry>> = _entries

    private val _total = MutableLiveData(0.0)
    val total: LiveData<Double> = _total

    fun addWeight(weight: Double) {
        val currentList = _entries.value?.toMutableList() ?: mutableListOf()
        currentList.add(WeightEntry(weight = weight))
        _entries.value = currentList
        updateTotal()
    }

    fun removeWeight(entry: WeightEntry) {
        val currentList = _entries.value?.toMutableList() ?: return
        currentList.removeAll { it.id == entry.id }
        _entries.value = currentList
        updateTotal()
    }

    fun clearAll() {
        _entries.value = mutableListOf()
        updateTotal()
    }

    private fun updateTotal() {
        val sum = _entries.value?.sumOf { it.weight } ?: 0.0
        _total.value = sum
    }
}