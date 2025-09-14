# BookLedger - Android Expense & Sales Tracking App

A comprehensive Android application built with Jetpack Compose for tracking business expenses and sales with detailed reporting capabilities.

## Features

### 📊 Dashboard
- Real-time overview of financial performance
- Total sales, expenses, and net profit display
- Recent transactions summary
- Monthly statistics and trends

### 💰 Expense Tracking
- Add and manage business expenses
- Categorize expenses (Office Supplies, Utilities, Rent, Marketing, Travel, Equipment)
- Add notes and descriptions
- View expense history with filtering

### 💵 Sales Tracking
- Record sales transactions
- Track customer information
- Categorize income sources (Book Sales, Consulting, Workshops, Royalties, Speaking, Other Income)
- Monitor sales performance

### 📈 Reporting
- Visual dashboard with key metrics
- Category-wise breakdowns
- Monthly and yearly comparisons
- Export capabilities (future enhancement)

## Technical Architecture

### 🏗️ Architecture Pattern
- **MVVM (Model-View-ViewModel)** with Repository pattern
- **Jetpack Compose** for modern UI
- **Room Database** for local data persistence
- **Hilt** for dependency injection
- **Navigation Component** for screen navigation

### 📱 Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Database**: Room (SQLite)
- **Dependency Injection**: Hilt
- **Navigation**: Navigation Compose
- **Architecture Components**: ViewModel, LiveData, Coroutines

### 🗂️ Project Structure
```
app/src/main/java/com/juan/bookledger/
├── data/
│   ├── database/          # Room database setup
│   ├── model/             # Data models and entities
│   └── repository/        # Repository pattern implementation
├── di/                    # Dependency injection modules
├── navigation/            # Navigation setup
├── ui/
│   ├── screens/          # Compose UI screens
│   ├── theme/            # App theming
│   └── viewmodel/        # ViewModels for each feature
└── MainActivity.kt       # Main activity entry point
```

## Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK 25+ (Android 7.0+)
- Kotlin 2.0.21+

### Installation
1. Clone the repository
2. Open the project in Android Studio
3. Sync the project with Gradle files
4. Run the app on an emulator or physical device

### Build Configuration
The app uses the following key dependencies:
- **Compose BOM**: 2024.09.00
- **Room**: 2.6.1
- **Hilt**: 2.48
- **Navigation**: 2.7.7
- **ViewModel**: 2.7.0

## Usage

### Adding Expenses
1. Navigate to the Expenses tab
2. Tap the "+" floating action button
3. Fill in amount, description, and select category
4. Add optional notes
5. Save the expense

### Recording Sales
1. Navigate to the Sales tab
2. Tap the "+" floating action button
3. Enter sale details including customer name
4. Select appropriate income category
5. Save the transaction

### Viewing Dashboard
- The dashboard automatically displays current month's data
- View total sales, expenses, and net profit
- Browse recent transactions
- Monitor category-wise breakdowns

## Database Schema

### Tables
- **Categories**: Expense and income categories
- **Expenses**: Business expense records
- **Sales**: Sales transaction records

### Key Features
- Foreign key relationships
- Automatic date handling
- Category-based organization
- Data integrity constraints

## Future Enhancements

- [ ] Data export to CSV/PDF
- [ ] Advanced reporting and charts
- [ ] Budget tracking and alerts
- [ ] Multi-currency support
- [ ] Cloud backup and sync
- [ ] Receipt photo capture
- [ ] Recurring transaction support

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support or questions, please open an issue in the repository or contact the development team.

---

**BookLedger** - Simplifying business financial tracking with modern Android development practices.
