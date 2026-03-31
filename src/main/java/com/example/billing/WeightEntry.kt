package com.example.billing

data class WeightEntry(
    val id: Long = System.currentTimeMillis(),
    val weight: Double
)