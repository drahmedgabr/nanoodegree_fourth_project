package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class RemindersLocalRepositoryTest {

    private lateinit var remindersLocalRepository: RemindersLocalRepository
    private lateinit var remindersDao: RemindersDao
    private lateinit var remindersDatabase: RemindersDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDB() {
        remindersDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()

        remindersDao = remindersDatabase.reminderDao()
        remindersLocalRepository = RemindersLocalRepository(remindersDao)
    }

    @After
    fun closeDB() {
        remindersDatabase.close()
    }

    @Test
    fun RemindersLocalRepository_testSavingReminder() = runTest {
        //Given
        val reminder = ReminderDTO("Title", "Description", "Location", 0.0, 0.0, "ABC")

        //When
        remindersLocalRepository.saveReminder(reminder)
        val loaded = remindersLocalRepository.getReminder("ABC") as Result.Success

        //Then
        assertThat(loaded, notNullValue())
        assertThat(loaded.data.id, IsEqual(reminder.id))
        assertThat(loaded.data.title, IsEqual(reminder.title))
        assertThat(loaded.data.description, IsEqual(reminder.description))
        assertThat(loaded.data.location, IsEqual(reminder.location))
    }

    @Test
    fun RemindersLocalRepository_testGetReminders() = runTest {
        //Given
        val reminder = ReminderDTO("Title", "Description", "Location", 0.0, 0.0, "ABC")
        val reminder2 = ReminderDTO("Title", "Description", "Location", 0.0, 0.0, "ABCD")

        //When
        remindersLocalRepository.saveReminder(reminder)
        remindersLocalRepository.saveReminder(reminder2)
        val loaded = remindersLocalRepository.getReminders() as Result.Success

        //Then
        assertThat(loaded, notNullValue())
        assertThat(loaded.data.size, IsEqual(2))
    }

    @Test
    fun RemindersLocalRepository_deleteReminders() = runTest {
        //Given
        val reminder = ReminderDTO("Title", "Description", "Location", 0.0, 0.0, "ABC")

        //When
        remindersLocalRepository.saveReminder(reminder)
        var loaded = remindersLocalRepository.getReminders() as Result.Success

        //Then
        assertThat(loaded, notNullValue())
        assertThat(loaded.data.size, IsEqual(1))

        remindersLocalRepository.deleteAllReminders()
        loaded = remindersLocalRepository.getReminders() as Result.Success

        //Then
        assertThat(loaded.data.size, IsEqual(0))
    }
}