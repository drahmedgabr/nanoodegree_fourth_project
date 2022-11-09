package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

class FakeDataSource : ReminderDataSource {

    private val linkedHashMap: LinkedHashMap<String, ReminderDTO> = LinkedHashMap()
    private var errorFound = false

    fun shouldReturnError(e: Boolean) {
        errorFound = e
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (errorFound) {
            return Result.Error("Error while testing: getReminders()")
        } else {
            return Result.Success(linkedHashMap.values.toList())
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        linkedHashMap[reminder.id] = reminder
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (errorFound) {
            return Result.Error("Error while testing: getReminders(id: $String)")
        } else {
            linkedHashMap[id]?.let {
                return Result.Success(it)
            }
            return Result.Error("Error while testing: Reminder Not found")
        }
    }

    override suspend fun deleteAllReminders() {
        linkedHashMap.clear()
    }
}