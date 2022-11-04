package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class RemindersDaoTest {

    private lateinit var remindersDatabase: RemindersDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDB() {
        remindersDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDB() {
        remindersDatabase.close()
    }

    @Test
    fun RemindersDatabase_testSavingReminder() = runTest {
        //Given
        val reminder = ReminderDTO("Title", "Description", "Location", 0.0, 0.0, "ABC")

        //When
        remindersDatabase.reminderDao().saveReminder(reminder)
        val loaded = remindersDatabase.reminderDao().getReminderById("ABC")

        //Then
        assertThat(loaded, notNullValue())
        assertThat(loaded?.id, IsEqual(reminder.id))
        assertThat(loaded?.title, IsEqual(reminder.title))
        assertThat(loaded?.description, IsEqual(reminder.description))
        assertThat(loaded?.location, IsEqual(reminder.location))
    }

    @Test
    fun RemindersDatabase_testGetReminders() = runTest {
        //Given
        val reminder = ReminderDTO("Title", "Description", "Location", 0.0, 0.0, "ABC")
        val reminder2 = ReminderDTO("Title", "Description", "Location", 0.0, 0.0, "ABCD")

        //When
        remindersDatabase.reminderDao().saveReminder(reminder)
        remindersDatabase.reminderDao().saveReminder(reminder2)
        val loaded = remindersDatabase.reminderDao().getReminders()

        //Then
        assertThat(loaded, notNullValue())
        assertThat(loaded.size, IsEqual(2))
    }

    @Test
    fun RemindersDatabase_deleteReminders() = runTest {
        //Given
        val reminder = ReminderDTO("Title", "Description", "Location", 0.0, 0.0, "ABC")

        //When
        remindersDatabase.reminderDao().saveReminder(reminder)
        var loaded = remindersDatabase.reminderDao().getReminders()

        //Then
        assertThat(loaded, notNullValue())
        assertThat(loaded.size, IsEqual(1))

        remindersDatabase.reminderDao().deleteAllReminders()
        loaded = remindersDatabase.reminderDao().getReminders()

        //Then
        assertThat(loaded.size, IsEqual(0))
    }
}