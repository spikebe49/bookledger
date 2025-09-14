package com.juan.bookledger.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.juan.bookledger.data.BookManager
import com.juan.bookledger.data.ExpenseManager
import com.juan.bookledger.data.model.Book
import com.juan.bookledger.data.model.ExpenseCategory
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    navController: NavController
) {
    val context = LocalContext.current
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(Date()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var selectedBook by remember { mutableStateOf<Book?>(null) }
    var showBookDropdown by remember { mutableStateOf(false) }
    var books by remember { mutableStateOf<List<Book>>(emptyList()) }
    
    val categories = ExpenseCategory.values().map { it.name }
    
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    LaunchedEffect(Unit) {
        BookManager.initialize(context)
        BookManager.getAllBooks().collect { bookList ->
            books = bookList
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Expense") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Category Dropdown
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Category *",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    ExposedDropdownMenuBox(
                        expanded = showCategoryDropdown,
                        onExpandedChange = { showCategoryDropdown = !showCategoryDropdown }
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Select Category") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = showCategoryDropdown
                                )
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = showCategoryDropdown,
                            onDismissRequest = { showCategoryDropdown = false }
                        ) {
                            categories.forEach { categoryOption ->
                                DropdownMenuItem(
                                    text = { Text(categoryOption) },
                                    onClick = {
                                        category = categoryOption
                                        showCategoryDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            // Book Selection Dropdown
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Book (Optional)",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    ExposedDropdownMenuBox(
                        expanded = showBookDropdown,
                        onExpandedChange = { showBookDropdown = !showBookDropdown }
                    ) {
                        OutlinedTextField(
                            value = selectedBook?.title ?: "",
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Select Book") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = showBookDropdown
                                )
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = showBookDropdown,
                            onDismissRequest = { showBookDropdown = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("No Book Selected") },
                                onClick = {
                                    selectedBook = null
                                    showBookDropdown = false
                                }
                            )
                            books.forEach { book ->
                                DropdownMenuItem(
                                    text = { Text(book.title) },
                                    onClick = {
                                        selectedBook = book
                                        showBookDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            // Description Field
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Description *",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Enter description") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            }
            
            // Amount Field
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Amount *",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { newValue ->
                            // Allow only numbers and decimal point
                            if (newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                                amount = newValue
                            }
                        },
                        label = { Text("Enter amount") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        prefix = { Text("$") }
                    )
                }
            }
            
            // Date Picker
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Date *",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = dateFormatter.format(selectedDate),
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Select Date") },
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Save Button
            Button(
                onClick = {
                    if (category.isNotEmpty() && description.isNotEmpty() && amount.isNotEmpty()) {
                        try {
                            val expenseAmount = amount.toDouble()
                        ExpenseManager.addExpense(
                            category = ExpenseCategory.valueOf(category),
                            description = description,
                            amount = expenseAmount,
                            date = selectedDate,
                            bookId = selectedBook?.id ?: 0,
                            onSuccess = {
                                navController.popBackStack()
                            },
                            onError = { error ->
                                // Handle error - could show a toast or error message
                                println("Error saving expense: $error")
                            }
                        )
                        } catch (e: NumberFormatException) {
                            // Handle invalid number format
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = category.isNotEmpty() && description.isNotEmpty() && amount.isNotEmpty()
            ) {
                Text(
                    text = "Save Expense",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
    
    // Simple date picker using AlertDialog
    if (showDatePicker) {
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            title = { Text("Select Date") },
            text = {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = selectedDate.time
                )
                DatePicker(state = datePickerState)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // For now, just close the dialog
                        // In a real app, you'd get the selected date from the picker
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
