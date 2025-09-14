package com.juan.bookledger.data.model

enum class CanadianTaxCategory {
    // Business Expenses (Deductible)
    ADVERTISING,                    // Marketing and promotional expenses
    BANK_CHARGES,                   // Business banking fees
    BUSINESS_INSURANCE,             // Professional liability insurance
    BUSINESS_MEALS,                 // 50% deductible in Canada
    BUSINESS_SUPPLIES,              // Office supplies, stationery
    BUSINESS_USE_OF_HOME,           // Home office expenses
    COMPUTER_SOFTWARE,              // Writing and design software
    COPYRIGHT_REGISTRATION,         // Copyright registration fees
    DEPRECIATION,                   // Equipment depreciation
    EDITING_SERVICES,               // Professional editing
    EQUIPMENT,                      // Computers, tablets, writing tools
    INSURANCE,                      // Business insurance
    LEGAL_FEES,                     // Legal services for business
    MARKETING,                      // Book marketing campaigns
    OFFICE_RENT,                    // Office space rental
    PHONE_INTERNET,                 // Business phone and internet
    POSTAGE_SHIPPING,               // Shipping and postage
    PROFESSIONAL_DEVELOPMENT,       // Writing courses, conferences
    PUBLISHING_COSTS,               // ISBN, printing, distribution
    RESEARCH,                       // Research materials and travel
    SOFTWARE_SUBSCRIPTIONS,         // Monthly software subscriptions
    TRAVEL,                         // Business travel (conferences, book tours)
    UTILITIES,                      // Home office utilities (proportional)
    WEBSITE_HOSTING,                // Website and domain costs
    
    // Income Categories
    BOOK_SALES,                     // Revenue from book sales
    ROYALTIES,                      // Royalty income
    SPEAKING_FEES,                  // Speaking engagements
    TEACHING_INCOME,                // Writing workshops, courses
    CONSULTING,                     // Writing consulting services
    FREELANCE_WRITING,              // Other writing income
    
    // Non-Deductible (Personal)
    PERSONAL_EXPENSES,              // Personal, non-business expenses
    PERSONAL_MEALS,                 // Personal dining
    PERSONAL_TRAVEL,                // Personal travel
    PERSONAL_SUPPLIES,              // Personal items
    
    // Special Categories
    GST_HST,                        // GST/HST collected/paid
    PROVINCIAL_TAX,                 // Ontario provincial tax
    FEDERAL_TAX,                    // Federal income tax
    CPP_CONTRIBUTIONS,              // Canada Pension Plan
    EI_PREMIUMS,                    // Employment Insurance
    OTHER                           // Other miscellaneous
}

data class CanadianTaxInfo(
    val category: CanadianTaxCategory,
    val isDeductible: Boolean,
    val deductionRate: Double, // 1.0 = 100%, 0.5 = 50% (like meals)
    val requiresReceipt: Boolean,
    val description: String
)

object CanadianTaxCategories {
    val TAX_INFO = mapOf(
        CanadianTaxCategory.ADVERTISING to CanadianTaxInfo(
            category = CanadianTaxCategory.ADVERTISING,
            isDeductible = true,
            deductionRate = 1.0,
            requiresReceipt = true,
            description = "Marketing and promotional expenses"
        ),
        CanadianTaxCategory.BUSINESS_MEALS to CanadianTaxInfo(
            category = CanadianTaxCategory.BUSINESS_MEALS,
            isDeductible = true,
            deductionRate = 0.5, // 50% deductible in Canada
            requiresReceipt = true,
            description = "Business meals (50% deductible)"
        ),
        CanadianTaxCategory.BUSINESS_USE_OF_HOME to CanadianTaxInfo(
            category = CanadianTaxCategory.BUSINESS_USE_OF_HOME,
            isDeductible = true,
            deductionRate = 1.0,
            requiresReceipt = true,
            description = "Home office expenses (proportional)"
        ),
        CanadianTaxCategory.EDITING_SERVICES to CanadianTaxInfo(
            category = CanadianTaxCategory.EDITING_SERVICES,
            isDeductible = true,
            deductionRate = 1.0,
            requiresReceipt = true,
            description = "Professional editing services"
        ),
        CanadianTaxCategory.PUBLISHING_COSTS to CanadianTaxInfo(
            category = CanadianTaxCategory.PUBLISHING_COSTS,
            isDeductible = true,
            deductionRate = 1.0,
            requiresReceipt = true,
            description = "ISBN, printing, distribution costs"
        ),
        CanadianTaxCategory.TRAVEL to CanadianTaxInfo(
            category = CanadianTaxCategory.TRAVEL,
            isDeductible = true,
            deductionRate = 1.0,
            requiresReceipt = true,
            description = "Business travel for book promotion"
        ),
        CanadianTaxCategory.BOOK_SALES to CanadianTaxInfo(
            category = CanadianTaxCategory.BOOK_SALES,
            isDeductible = false,
            deductionRate = 0.0,
            requiresReceipt = false,
            description = "Revenue from book sales"
        ),
        CanadianTaxCategory.ROYALTIES to CanadianTaxInfo(
            category = CanadianTaxCategory.ROYALTIES,
            isDeductible = false,
            deductionRate = 0.0,
            requiresReceipt = false,
            description = "Royalty income"
        )
    )
}

data class OntarioTaxInfo(
    val gstRate: Double = 0.05, // 5% GST
    val hstRate: Double = 0.13, // 13% HST in Ontario
    val provincialTaxRate: Double = 0.05, // 5% provincial tax
    val federalTaxRate: Double = 0.15, // 15% federal tax (basic rate)
    val cppRate: Double = 0.0595, // 5.95% CPP
    val eiRate: Double = 0.0163 // 1.63% EI
)
