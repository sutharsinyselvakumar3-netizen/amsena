package com.example.service

import com.example.model.EventLogItem
import com.example.model.LogSeverity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventLogRepository {
    private val maxCapacity = 100
    private val _logs = MutableStateFlow<List<EventLogItem>>(emptyList())
    val logs: StateFlow<List<EventLogItem>> = _logs.asStateFlow()

    init {
        // Initial system startup log
        log(
            event = "SYSTEM INIT",
            severity = LogSeverity.INFO,
            description = "AI Companion Smart Onion Garden Robot initialized in SAFE MANUAL mode."
        )
    }

    fun log(event: String, severity: LogSeverity, description: String) {
        val newItem = EventLogItem(
            timestamp = System.currentTimeMillis(),
            event = event,
            severity = severity,
            description = description
        )
        val current = _logs.value.toMutableList()
        current.add(0, newItem)
        if (current.size > maxCapacity) {
            current.removeAt(current.lastIndex)
        }
        _logs.value = current
    }

    fun clear() {
        _logs.value = emptyList()
        log("LOG CLEARED", LogSeverity.INFO, "Event log buffer cleared by user.")
    }

    companion object {
        fun formatTimestamp(timestamp: Long): String {
            val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }

        fun formatFullDate(timestamp: Long): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }
}
