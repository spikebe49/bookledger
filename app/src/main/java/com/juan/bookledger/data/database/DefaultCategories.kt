package com.juan.bookledger.data.database

import com.juan.bookledger.data.model.Category
import com.juan.bookledger.data.model.CategoryType

object DefaultCategories {
    val expenseCategories = listOf(
        Category(name = "Office Supplies", type = CategoryType.EXPENSE, color = "#FF5722"),
        Category(name = "Utilities", type = CategoryType.EXPENSE, color = "#2196F3"),
        Category(name = "Rent", type = CategoryType.EXPENSE, color = "#9C27B0"),
        Category(name = "Marketing", type = CategoryType.EXPENSE, color = "#FF9800"),
        Category(name = "Travel", type = CategoryType.EXPENSE, color = "#4CAF50"),
        Category(name = "Equipment", type = CategoryType.EXPENSE, color = "#795548")
    )

    val incomeCategories = listOf(
        Category(name = "Book Sales", type = CategoryType.INCOME, color = "#4CAF50"),
        Category(name = "Consulting", type = CategoryType.INCOME, color = "#2196F3"),
        Category(name = "Workshops", type = CategoryType.INCOME, color = "#FF9800"),
        Category(name = "Royalties", type = CategoryType.INCOME, color = "#9C27B0"),
        Category(name = "Speaking", type = CategoryType.INCOME, color = "#FF5722"),
        Category(name = "Other Income", type = CategoryType.INCOME, color = "#607D8B")
    )
}
