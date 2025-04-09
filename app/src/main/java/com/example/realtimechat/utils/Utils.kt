package com.example.realtimechat.utils

import android.content.Context
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun generateChatId(user1: String, user2: String): String {
    return if (user1 < user2) "${user1}_$user2" else "${user2}_$user1"
}

fun getBytesFromUri(context: Context, uri: Uri): ByteArray {
    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
    val byteBuffer = ByteArrayOutputStream()
    val buffer = ByteArray(1024)
    var len: Int

    while (inputStream?.read(buffer).also { len = it ?: -1 } != -1) {
        byteBuffer.write(buffer, 0, len)
    }

    return byteBuffer.toByteArray()
}

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

