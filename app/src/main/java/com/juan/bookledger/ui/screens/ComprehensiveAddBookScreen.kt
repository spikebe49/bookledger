package com.juan.bookledger.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.juan.bookledger.ui.viewmodel.BookViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComprehensiveAddBookScreen(
    navController: NavController,
    viewModel: BookViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    val errorMessage by viewModel.errorMessage.observeAsState(initial = null)
    val isSuccess by viewModel.isSuccess.observeAsState(initial = false)
    
    // Handle successful save
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            navController.popBackStack()
            viewModel.clearSuccess() // Clear the success state
        }
    }
    
    // Form state
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(Date()) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    // Author Information
    var authorName by remember { mutableStateOf("") }
    var authorEmail by remember { mutableStateOf("") }
    var authorPhone by remember { mutableStateOf("") }
    var authorWebsite by remember { mutableStateOf("") }
    
    // Publisher Information
    var publisherName by remember { mutableStateOf("") }
    var publisherEmail by remember { mutableStateOf("") }
    var publisherPhone by remember { mutableStateOf("") }
    var publisherWebsite by remember { mutableStateOf("") }
    var publisherAddress by remember { mutableStateOf("") }
    
    // Illustrator Information
    var illustratorName by remember { mutableStateOf("") }
    var illustratorEmail by remember { mutableStateOf("") }
    var illustratorPhone by remember { mutableStateOf("") }
    var illustratorWebsite by remember { mutableStateOf("") }
    
    // Additional Book Details
    var isbn by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var targetAudience by remember { mutableStateOf("") }
    var pageCount by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Add Book Details",
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
            // Basic Book Information
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Basic Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Book Title *") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = title.isBlank()
                    )
                    
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    
                    OutlinedTextField(
                        value = formatDate(selectedDate),
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Launch Date *") },
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            // Author Information
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Author Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    OutlinedTextField(
                        value = authorName,
                        onValueChange = { authorName = it },
                        label = { Text("Author Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = authorEmail,
                            onValueChange = { authorEmail = it },
                            label = { Text("Email") },
                            modifier = Modifier.weight(1f)
                        )
                        
                        OutlinedTextField(
                            value = authorPhone,
                            onValueChange = { authorPhone = it },
                            label = { Text("Phone") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    OutlinedTextField(
                        value = authorWebsite,
                        onValueChange = { authorWebsite = it },
                        label = { Text("Website") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            // Publisher Information
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Publisher Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    OutlinedTextField(
                        value = publisherName,
                        onValueChange = { publisherName = it },
                        label = { Text("Publisher Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = publisherEmail,
                            onValueChange = { publisherEmail = it },
                            label = { Text("Email") },
                            modifier = Modifier.weight(1f)
                        )
                        
                        OutlinedTextField(
                            value = publisherPhone,
                            onValueChange = { publisherPhone = it },
                            label = { Text("Phone") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    OutlinedTextField(
                        value = publisherWebsite,
                        onValueChange = { publisherWebsite = it },
                        label = { Text("Website") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    OutlinedTextField(
                        value = publisherAddress,
                        onValueChange = { publisherAddress = it },
                        label = { Text("Address") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            }
            
            // Illustrator Information
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Illustrator Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    OutlinedTextField(
                        value = illustratorName,
                        onValueChange = { illustratorName = it },
                        label = { Text("Illustrator Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = illustratorEmail,
                            onValueChange = { illustratorEmail = it },
                            label = { Text("Email") },
                            modifier = Modifier.weight(1f)
                        )
                        
                        OutlinedTextField(
                            value = illustratorPhone,
                            onValueChange = { illustratorPhone = it },
                            label = { Text("Phone") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    OutlinedTextField(
                        value = illustratorWebsite,
                        onValueChange = { illustratorWebsite = it },
                        label = { Text("Website") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            // Additional Book Details
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Additional Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = isbn,
                            onValueChange = { isbn = it },
                            label = { Text("ISBN") },
                            modifier = Modifier.weight(1f)
                        )
                        
                        OutlinedTextField(
                            value = genre,
                            onValueChange = { genre = it },
                            label = { Text("Genre") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = targetAudience,
                            onValueChange = { targetAudience = it },
                            label = { Text("Target Audience") },
                            modifier = Modifier.weight(1f)
                        )
                        
                        OutlinedTextField(
                            value = language,
                            onValueChange = { language = it },
                            label = { Text("Language") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    OutlinedTextField(
                        value = pageCount,
                        onValueChange = { pageCount = it },
                        label = { Text("Page Count") },
                        modifier = Modifier.fillMaxWidth()
                    )
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
                            Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            // Save button
            Button(
                onClick = {
                    if (validateForm(title)) {
                        viewModel.addBook(
                            title = title,
                            description = description.ifBlank { null },
                            launchDate = selectedDate,
                            authorName = authorName.ifBlank { null },
                            authorEmail = authorEmail.ifBlank { null },
                            authorPhone = authorPhone.ifBlank { null },
                            authorWebsite = authorWebsite.ifBlank { null },
                            publisherName = publisherName.ifBlank { null },
                            publisherEmail = publisherEmail.ifBlank { null },
                            publisherPhone = publisherPhone.ifBlank { null },
                            publisherWebsite = publisherWebsite.ifBlank { null },
                            publisherAddress = publisherAddress.ifBlank { null },
                            illustratorName = illustratorName.ifBlank { null },
                            illustratorEmail = illustratorEmail.ifBlank { null },
                            illustratorPhone = illustratorPhone.ifBlank { null },
                            illustratorWebsite = illustratorWebsite.ifBlank { null },
                            isbn = isbn.ifBlank { null },
                            genre = genre.ifBlank { null },
                            targetAudience = targetAudience.ifBlank { null },
                            pageCount = pageCount.toIntOrNull(),
                            language = language.ifBlank { null }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && validateForm(title)
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
                    Text("Save Book")
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
        title = { Text("Select Launch Date") },
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

private fun validateForm(title: String): Boolean {
    return title.isNotBlank()
}

private fun formatDate(date: Date): String {
    val formatter = java.text.SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return formatter.format(date)
}
