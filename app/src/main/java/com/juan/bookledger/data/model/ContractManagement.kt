package com.juan.bookledger.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "publisher_contracts",
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["bookId"])]
)
data class PublisherContract(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: Long = 0,
    val publisherName: String,
    val contractNumber: String,
    val contractType: ContractType,
    val status: ContractStatus = ContractStatus.ACTIVE,
    val signDate: Date,
    val startDate: Date,
    val endDate: Date? = null,
    val renewalDate: Date? = null,
    val terminationDate: Date? = null,
    
    // Financial Terms
    val advanceAmount: Double = 0.0,
    val advanceReceived: Double = 0.0,
    val royaltyRateHardcover: Double = 0.0,
    val royaltyRatePaperback: Double = 0.0,
    val royaltyRateEbook: Double = 0.0,
    val royaltyRateAudiobook: Double = 0.0,
    val royaltyRateForeign: Double = 0.0,
    val royaltyRateRemainder: Double = 0.0,
    val reserveAgainstReturns: Double = 0.0,
    val paymentTerms: PaymentTerms = PaymentTerms.NET_90,
    val paymentFrequency: PaymentFrequency = PaymentFrequency.QUARTERLY,
    
    // Rights
    val printRights: Boolean = true,
    val digitalRights: Boolean = true,
    val audioRights: Boolean = false,
    val foreignRights: Boolean = false,
    val filmRights: Boolean = false,
    val merchandisingRights: Boolean = false,
    val territoryRights: String = "World English",
    
    // Important Dates
    val manuscriptDueDate: Date? = null,
    val publicationDate: Date? = null,
    val firstRoyaltyPayment: Date? = null,
    val contractReviewDate: Date? = null,
    
    // Additional Terms
    val marketingCommitment: String? = null,
    val authorCopies: Int = 0,
    val discountRate: Double = 0.0,
    val returnPolicy: String? = null,
    val outOfPrintClause: String? = null,
    
    // Contact Information
    val editorName: String? = null,
    val editorEmail: String? = null,
    val editorPhone: String? = null,
    val accountingContact: String? = null,
    val accountingEmail: String? = null,
    
    // Legal
    val lawFirm: String? = null,
    val lawyerName: String? = null,
    val lawyerEmail: String? = null,
    val contractFile: String? = null, // Path to contract PDF
    
    // Notes
    val notes: String? = null,
    val warnings: String? = null, // Important warnings or red flags
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class ContractType {
    TRADITIONAL_PUBLISHING,    // Traditional publisher contract
    HYBRID_PUBLISHING,         // Hybrid/partnership publishing
    SELF_PUBLISHING,           // Self-publishing platform
    LICENSING,                 // Rights licensing
    WORK_FOR_HIRE,             // Work-for-hire agreement
    CO_PUBLISHING,             // Co-publishing arrangement
    OTHER                       // Other contract types
}

enum class ContractStatus {
    DRAFT,                     // Contract in draft
    NEGOTIATING,               // Under negotiation
    PENDING_SIGNATURE,         // Waiting for signatures
    ACTIVE,                    // Active contract
    SUSPENDED,                 // Temporarily suspended
    TERMINATED,                // Contract terminated
    EXPIRED,                   // Contract expired
    RENEWED                    // Contract renewed
}

enum class PaymentTerms {
    NET_30,                    // Payment due in 30 days
    NET_60,                    // Payment due in 60 days
    NET_90,                    // Payment due in 90 days
    UPON_PUBLICATION,          // Payment upon publication
    UPON_SIGNATURE,            // Payment upon contract signing
    QUARTERLY,                 // Quarterly payments
    SEMI_ANNUAL,               // Semi-annual payments
    ANNUAL,                    // Annual payments
    OTHER                      // Other payment terms
}

enum class PaymentFrequency {
    MONTHLY,                   // Monthly payments
    QUARTERLY,                 // Quarterly payments
    SEMI_ANNUAL,               // Semi-annual payments
    ANNUAL,                    // Annual payments
    UPON_SALE,                 // Payment upon each sale
    OTHER                      // Other frequency
}

@Entity(tableName = "contract_alerts")
data class ContractAlert(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val contractId: Long,
    val alertType: AlertType,
    val alertDate: Date,
    val message: String,
    val isRead: Boolean = false,
    val isResolved: Boolean = false,
    val priority: AlertPriority = AlertPriority.MEDIUM
)

enum class AlertType {
    PAYMENT_DUE,               // Payment due
    CONTRACT_EXPIRY,           // Contract expiring soon
    RENEWAL_DUE,               // Contract renewal due
    MANUSCRIPT_DUE,            // Manuscript deadline
    PUBLICATION_DATE,          // Publication date approaching
    ROYALTY_PAYMENT,           // Royalty payment expected
    CONTRACT_REVIEW,           // Contract review due
    RIGHTS_REVERSION,          // Rights reversion date
    OTHER                      // Other alerts
}

enum class AlertPriority {
    LOW,                       // Low priority
    MEDIUM,                    // Medium priority
    HIGH,                      // High priority
    URGENT                     // Urgent priority
}

data class ContractFinancialSummary(
    val contractId: Long,
    val totalAdvance: Double,
    val advanceReceived: Double,
    val advanceOutstanding: Double,
    val totalRoyaltiesEarned: Double,
    val totalRoyaltiesPaid: Double,
    val royaltiesOutstanding: Double,
    val averageRoyaltyRate: Double,
    val totalRevenue: Double,
    val contractValue: Double, // Total value of contract
    val daysSinceLastPayment: Int,
    val nextPaymentDue: Date?,
    val paymentHistory: List<PaymentRecord>
)

@Entity(tableName = "payment_records")
data class PaymentRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val contractId: Long,
    val paymentType: PaymentType,
    val amount: Double,
    val paymentDate: Date,
    val description: String? = null,
    val referenceNumber: String? = null,
    val isAdvance: Boolean = false,
    val isRoyalty: Boolean = false
)

enum class PaymentType {
    ADVANCE,                   // Advance payment
    ROYALTY,                   // Royalty payment
    BONUS,                     // Bonus payment
    PENALTY,                   // Penalty payment
    REFUND,                    // Refund
    OTHER                      // Other payment type
}
