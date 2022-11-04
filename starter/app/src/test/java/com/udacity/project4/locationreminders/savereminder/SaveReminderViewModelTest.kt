package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.util.MainCoroutineRule
import com.udacity.project4.locationreminders.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@Config(sdk = [28])
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    private lateinit var saveReminderViewModel: SaveReminderViewModel
    private lateinit var fakeDataSource: FakeDataSource

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        stopKoin()
        fakeDataSource = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(), fakeDataSource
        )
    }

    @Test
    fun saveReminderViewModel_testSavingReminder() = runTest {
        //Given
        val reminder = ReminderDataItem("Title", "Description", "Location", 0.0, 0.0)

        //When
        saveReminderViewModel.saveReminder(reminder)
        val loaded = fakeDataSource.getReminders() as Result.Success
        val remindersCount = loaded.data.count()
        val loadedReminder = loaded.data[0]

        //Then
        assertThat(loaded, not(nullValue()))
        assertThat(remindersCount, IsEqual(1))
        assertThat(loadedReminder.title, IsEqual("Title"))
        assertThat(loadedReminder.description, IsEqual("Description"))
        assertThat(loadedReminder.location, IsEqual("Location"))
    }

    @Test
    fun saveReminderViewModel_testValidatingData() = runTest {
        //Given
        val reminderWithoutTitle = ReminderDataItem(null, "Description", "Location", 0.0, 0.0)

        //When
        saveReminderViewModel.validateAndSaveReminder(reminderWithoutTitle)

        //Then
        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), not(nullValue()))
    }
}