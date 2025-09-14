package com.juan.bookledger.data.ai

import android.content.Context
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiAIService @Inject constructor(
    private val context: Context,
    private val aiConfig: AIConfig
) {
    private suspend fun getGenerativeModel(): GenerativeModel? {
        val apiKey = aiConfig.getGeminiApiKey()
        val isEnabled = aiConfig.isAIEnabled()
        return if (apiKey.isNotEmpty() && isEnabled) {
            GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = apiKey,
                generationConfig = generationConfig {
                    temperature = 0.7f
                    topK = 40
                    topP = 0.95f
                    maxOutputTokens = 1024
                }
            )
        } else {
            null
        }
    }
    
    private suspend fun executeAIRequest(prompt: String): String {
        val model = getGenerativeModel()
        return if (model == null) {
            "AI features are disabled. Please configure your Gemini API key in Settings."
        } else {
            try {
                withContext(Dispatchers.IO) {
                    model.generateContent(prompt).text ?: "Unable to process AI request at this time."
                }
            } catch (e: Exception) {
                "Error processing AI request: ${e.message}"
            }
        }
    }
    
    // Writing & Publishing Workflow AI
    suspend fun generateWritingPrompt(genre: String, currentProgress: String): String {
        return withContext(Dispatchers.IO) {
            val prompt = """
                As a writing coach, help an author who is writing a $genre book.
                Current progress: $currentProgress
                
                Provide:
                1. A motivational writing prompt to continue their work
                2. 3 specific writing tips for this genre
                3. A suggested next scene or chapter direction
                
                Keep it concise and actionable.
            """.trimIndent()
            
            executeAIRequest(prompt)
        }
    }
    
    suspend fun analyzeManuscriptStructure(content: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = """
                    Analyze this manuscript excerpt for structure and pacing:
                    
                    $content
                    
                    Provide:
                    1. Overall structure assessment
                    2. Pacing analysis
                    3. 3 specific improvement suggestions
                    4. Strengths to maintain
                    
                    Keep feedback constructive and actionable.
                """.trimIndent()
                
                executeAIRequest(prompt)
            } catch (e: Exception) {
                "Error analyzing manuscript: ${e.message}"
            }
        }
    }
    
    // Marketing & Promotion AI
    suspend fun generateMarketingIdeas(bookTitle: String, genre: String, targetAudience: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = """
                    Generate creative marketing ideas for a book titled "$bookTitle" in the $genre genre, targeting $targetAudience.
                    
                    Provide:
                    1. 5 unique marketing campaign ideas
                    2. 3 social media content strategies
                    3. 2 offline promotion ideas
                    4. Budget-friendly options for indie authors
                    
                    Focus on actionable, creative strategies.
                """.trimIndent()
                
                executeAIRequest(prompt)
            } catch (e: Exception) {
                "Error generating marketing ideas: ${e.message}"
            }
        }
    }
    
    suspend fun generateSocialMediaPost(bookTitle: String, genre: String, platform: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = """
                    Create an engaging social media post for $platform about the book "$bookTitle" ($genre).
                    
                    Requirements:
                    - Platform-appropriate length and style
                    - Include relevant hashtags
                    - Make it engaging and shareable
                    - Include a call-to-action
                    
                    Generate 3 different post options.
                """.trimIndent()
                
                executeAIRequest(prompt)
            } catch (e: Exception) {
                "Error generating social media post: ${e.message}"
            }
        }
    }
    
    // Financial Planning & Forecasting AI
    suspend fun generateFinancialInsights(salesData: String, expensesData: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = """
                    Analyze this author's financial data and provide insights:
                    
                    Sales Data: $salesData
                    Expenses Data: $expensesData
                    
                    Provide:
                    1. Key financial trends and patterns
                    2. Areas for cost optimization
                    3. Revenue growth opportunities
                    4. 3 specific financial recommendations
                    5. Break-even analysis insights
                    
                    Focus on actionable financial advice for authors.
                """.trimIndent()
                
                executeAIRequest(prompt)
            } catch (e: Exception) {
                "Error generating financial insights: ${e.message}"
            }
        }
    }
    
    suspend fun predictRoyaltyProjections(currentSales: String, marketTrends: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = """
                    Based on current sales data and market trends, predict royalty projections:
                    
                    Current Sales: $currentSales
                    Market Trends: $marketTrends
                    
                    Provide:
                    1. 3-month projection
                    2. 6-month projection
                    3. 12-month projection
                    4. Key factors affecting projections
                    5. Risk factors to monitor
                    
                    Include conservative, realistic, and optimistic scenarios.
                """.trimIndent()
                
                executeAIRequest(prompt)
            } catch (e: Exception) {
                "Error generating royalty projections: ${e.message}"
            }
        }
    }
    
    // Writing Analytics & Insights AI
    suspend fun analyzeWritingProductivity(writingData: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = """
                    Analyze this author's writing productivity data:
                    
                    $writingData
                    
                    Provide:
                    1. Productivity patterns and trends
                    2. Peak writing times identification
                    3. Goal achievement analysis
                    4. 3 specific productivity improvement suggestions
                    5. Motivation and consistency insights
                    
                    Focus on actionable productivity advice.
                """.trimIndent()
                
                executeAIRequest(prompt)
            } catch (e: Exception) {
                "Error analyzing writing productivity: ${e.message}"
            }
        }
    }
    
    // Content Creation & Distribution AI
    suspend fun suggestContentDistributionStrategy(bookDetails: String, currentPlatforms: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = """
                    Suggest a content distribution strategy for this book:
                    
                    Book Details: $bookDetails
                    Current Platforms: $currentPlatforms
                    
                    Provide:
                    1. Platform prioritization strategy
                    2. Format-specific recommendations (ebook, audiobook, etc.)
                    3. Pricing strategy suggestions
                    4. Launch sequence recommendations
                    5. Long-term distribution plan
                    
                    Focus on maximizing reach and revenue.
                """.trimIndent()
                
                executeAIRequest(prompt)
            } catch (e: Exception) {
                "Error generating distribution strategy: ${e.message}"
            }
        }
    }
    
    // Advanced Reporting & Analytics AI
    suspend fun generateAuthorDashboardInsights(comprehensiveData: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = """
                    Analyze this comprehensive author data and provide dashboard insights:
                    
                    $comprehensiveData
                    
                    Provide:
                    1. Top 3 performance highlights
                    2. Top 3 areas needing attention
                    3. Key opportunities for growth
                    4. Risk factors to monitor
                    5. Recommended next actions
                    
                    Format as a concise executive summary for an author dashboard.
                """.trimIndent()
                
                executeAIRequest(prompt)
            } catch (e: Exception) {
                "Error generating dashboard insights: ${e.message}"
            }
        }
    }
    
    // General AI Assistant
    suspend fun askAIQuestion(question: String, context: String = ""): String {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = if (context.isNotEmpty()) {
                    "Context: $context\n\nQuestion: $question\n\nProvide a helpful, actionable answer for an author."
                } else {
                    "Question: $question\n\nProvide a helpful, actionable answer for an author."
                }
                
                executeAIRequest(prompt)
            } catch (e: Exception) {
                "Error processing question: ${e.message}"
            }
        }
    }
}
