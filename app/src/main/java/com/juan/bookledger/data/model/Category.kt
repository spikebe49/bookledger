package com.juan.bookledger.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: CategoryType, // EXPENSE or INCOME
    val color: String = "#FF6200EE" // Default color
)

enum class CategoryType {
    EXPENSE,
    INCOME
}
