package com.juan.bookledger.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.juan.bookledger.data.model.Book
import com.juan.bookledger.data.model.SaleType
import com.juan.bookledger.ui.viewmodel.BookViewModel
import com.juan.bookledger.ui.viewmodel.SaleViewModel
import com.juan.bookledger.utils.CurrencyFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleAddSaleScreen(
    bookId: Long? = null,
    navController: NavController,
    bookViewModel: BookViewModel = hiltViewModel(),
    saleViewModel: SaleViewModel = hiltViewModel()
) {
    val books by bookViewModel.books.observeAsState(initial = emptyList<Book>())
    val isLoading by saleViewModel.isLoading.collectAsState(initial = false)
    val errorMessage by saleViewModel.errorMessage.collectAsState(initial = null)
    
    // Form state
    var selectedBook by remember { mutableStateOf<Book?>(null) }
    var saleType by remember { mutableStateOf("") }
    var bookTitle by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unitPrice by remember { mutableStateOf("") }
    var donationAmount by remember { mutableStateOf("") }
    var isGiveaway by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(Date()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTypeDropdown by remember { mutableStateOf(false) }
    var showBookDropdown by remember { mutableStateOf(false) }
    
    val currencyFormatter = CurrencyFormatter
    
    // Predefined sale types
    val saleTypes = SaleType.values().map { it.name }
    
    // Calculate total amount
    val totalAmount = remember(quantity, unitPrice, donationAmount) {
        val qty = quantity.toDoubleOrNull() ?: 0.0
        val price = unitPrice.toDoubleOrNull() ?: 0.0
        val donation = donationAmount.toDoubleOrNull() ?: 0.0
        (qty * price) - donation
    }
    
    // Set selected book if bookId is provided
    LaunchedEffect(bookId, books) {
        if (bookId != null && books.isNotEmpty()) {
            selectedBook = books.find { it.id == bookId }
            bookTitle = selectedBook?.title ?: ""
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Add Sale",
                        style = MaterialTheme.typography.headlineMedium
                    ) 
                },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Book selection
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Book Selection",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
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
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showBookDropdown) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        
                        ExposedDropdownMenu(
                            expanded = showBookDropdown,
                            onDismissRequest = { showBookDropdown = false }
                        ) {
                            books.forEach { book: Book ->
                                DropdownMenuItem(
                                    text = { Text(book.title) },
                                    onClick = {
                                        selectedBook = book
                                        bookTitle = book.title
                                        showBookDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            // Sale details
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Sale Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Sale type selection
                    ExposedDropdownMenuBox(
                        expanded = showTypeDropdown,
                        onExpandedChange = { showTypeDropdown = !showTypeDropdown }
                    ) {
                        OutlinedTextField(
                            value = saleType,
                            onValueChange = { saleType = it },
                            readOnly = true,
                            label = { Text("Sale Type") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showTypeDropdown) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        
                        ExposedDropdownMenu(
                            expanded = showTypeDropdown,
                            onDismissRequest = { showTypeDropdown = false }
                        ) {
                            saleTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = {
                                        saleType = type
                                        showTypeDropdown = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Book title
                    OutlinedTextField(
                        value = bookTitle,
                        onValueChange = { bookTitle = it },
                        label = { Text("Book Title") },
                        placeholder = { Text("Enter book title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Quantity and Unit Price
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = quantity,
                            onValueChange = { quantity = it },
                            label = { Text("Quantity") },
                            placeholder = { Text("1") },
                            modifier = Modifier.weight(1f)
                        )
                        
                        OutlinedTextField(
                            value = unitPrice,
                            onValueChange = { unitPrice = it },
                            label = { Text("Unit Price") },
                            placeholder = { Text("0.00") },
                            leadingIcon = { Text("$") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Donation amount
                    OutlinedTextField(
                        value = donationAmount,
                        onValueChange = { donationAmount = it },
                        label = { Text("Donation Amount (Optional)") },
                        placeholder = { Text("0.00") },
                        leadingIcon = { Text("$") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Giveaway checkbox
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isGiveaway,
                            onCheckedChange = { isGiveaway = it }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "This is a giveaway",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Date selection
                    OutlinedTextField(
                        value = formatDate(selectedDate),
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Date") },
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            // Total calculation
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Total Calculation",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Quantity × Unit Price:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = currencyFormatter.formatCurrency(
                                (quantity.toDoubleOrNull() ?: 0.0) * (unitPrice.toDoubleOrNull() ?: 0.0)
                            ),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    
                    if (donationAmount.isNotBlank() && donationAmount.toDoubleOrNull() != null && donationAmount.toDouble() > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Donation:",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "-${currencyFormatter.formatCurrency(donationAmount.toDoubleOrNull() ?: 0.0)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Divider()
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total Amount:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = currencyFormatter.formatCurrency(totalAmount),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            // Error message
            errorMessage?.let { message ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
            
            // Save button
            Button(
                onClick = {
                    if (validateForm(selectedBook, saleType, bookTitle, quantity, unitPrice)) {
                        saleViewModel.addSale(
                            type = SaleType.valueOf(saleType),
                            platform = "Unknown", // Default platform
                            bookTitle = bookTitle,
                            quantity = quantity.toInt(),
                            unitPrice = unitPrice.toDouble(),
                            totalAmount = totalAmount,
                            date = selectedDate,
                            donationAmount = donationAmount.toDoubleOrNull() ?: 0.0,
                            isGiveaway = isGiveaway,
                            bookId = selectedBook?.id ?: 0L
                        )
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && validateForm(selectedBook, saleType, bookTitle, quantity, unitPrice)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Sale")
                }
            }
        }
    }
    
    // Date picker
    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = { date ->
                selectedDate = date
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Date") },
        text = {
            DatePicker(
                state = datePickerState,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onDateSelected(Date(millis))
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun validateForm(
    selectedBook: Book?,
    saleType: String,
    bookTitle: String,
    quantity: String,
    unitPrice: String
): Boolean {
    return selectedBook != null &&
            saleType.isNotBlank() &&
            bookTitle.isNotBlank() &&
            quantity.isNotBlank() &&
            quantity.toIntOrNull() != null &&
            quantity.toInt() > 0 &&
            unitPrice.isNotBlank() &&
            unitPrice.toDoubleOrNull() != null &&
            unitPrice.toDouble() > 0
}

private fun formatDate(date: Date): String {
    val formatter = java.text.SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return formatter.format(date)
}
