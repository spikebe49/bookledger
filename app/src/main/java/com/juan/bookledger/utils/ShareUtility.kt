package com.juan.bookledger.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object ShareUtility {
    
    fun shareFile(
        context: Context,
        filePath: String,
        mimeType: String,
        subject: String = "BookLedger Export",
        message: String = "Please find attached the BookLedger export file."
    ): Intent {
        val file = File(filePath)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        return shareIntent
    }
    
    fun shareViaEmail(
        context: Context,
        filePath: String,
        mimeType: String,
        subject: String = "BookLedger Export",
        message: String = "Please find attached the BookLedger export file."
    ): Intent {
        val shareIntent = shareFile(context, filePath, mimeType, subject, message)
        shareIntent.setType("message/rfc822")
        return shareIntent
    }
    
    fun shareViaSMS(
        context: Context,
        filePath: String,
        message: String = "BookLedger export file: $filePath"
    ): Intent {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        return shareIntent
    }
    
    fun shareViaGmail(
        context: Context,
        filePath: String,
        mimeType: String,
        subject: String = "BookLedger Export",
        message: String = "Please find attached the BookLedger export file."
    ): Intent {
        val shareIntent = shareFile(context, filePath, mimeType, subject, message)
        shareIntent.setPackage("com.google.android.gm")
        return shareIntent
    }
    
    fun shareViaQuickShare(
        context: Context,
        filePath: String,
        mimeType: String,
        subject: String = "BookLedger Export",
        message: String = "Please find attached the BookLedger export file."
    ): Intent {
        val shareIntent = shareFile(context, filePath, mimeType, subject, message)
        shareIntent.setPackage("com.samsung.android.quickconnect")
        return shareIntent
    }
    
    fun printFile(
        context: Context,
        filePath: String,
        mimeType: String
    ): Intent {
        val file = File(filePath)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        
        val printIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        return printIntent
    }
    
    fun getShareChooserIntent(
        context: Context,
        filePath: String,
        mimeType: String,
        subject: String = "BookLedger Export",
        message: String = "Please find attached the BookLedger export file."
    ): Intent {
        val shareIntent = shareFile(context, filePath, mimeType, subject, message)
        return Intent.createChooser(shareIntent, "Share BookLedger Export")
    }
    
    fun getAvailableShareMethods(context: Context): List<ShareMethod> {
        val methods = mutableListOf<ShareMethod>()
        
        // Check for email
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
        }
        if (emailIntent.resolveActivity(context.packageManager) != null) {
            methods.add(ShareMethod.EMAIL)
        }
        
        // Check for SMS
        val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("sms:")
        }
        if (smsIntent.resolveActivity(context.packageManager) != null) {
            methods.add(ShareMethod.SMS)
        }
        
        // Check for Gmail
        val gmailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            setPackage("com.google.android.gm")
        }
        if (gmailIntent.resolveActivity(context.packageManager) != null) {
            methods.add(ShareMethod.GMAIL)
        }
        
        // Check for QuickShare (Samsung)
        val quickShareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            setPackage("com.samsung.android.quickconnect")
        }
        if (quickShareIntent.resolveActivity(context.packageManager) != null) {
            methods.add(ShareMethod.QUICKSHARE)
        }
        
        // Check for printing
        val printIntent = Intent(Intent.ACTION_VIEW).apply {
            type = "application/pdf"
        }
        if (printIntent.resolveActivity(context.packageManager) != null) {
            methods.add(ShareMethod.PRINTER)
        }
        
        // Always add "Other" option
        methods.add(ShareMethod.OTHER)
        
        return methods
    }
}

enum class ShareMethod(val displayName: String, val icon: String) {
    EMAIL("Email", "📧"),
    SMS("SMS", "💬"),
    QUICKSHARE("QuickShare", "📱"),
    GMAIL("Gmail", "📨"),
    PRINTER("Print", "🖨️"),
    OTHER("Other Apps", "📤")
}
