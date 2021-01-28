package org.tumba.kegel_app.domain.interactor

import org.amshove.kluent.`should be equal to`
import org.junit.Test

class ReminderDaysEncoderDecoderTest {

    @Test
    fun `should decode week day properly`() {
        val encodedDays = 0b1000001 // Set up Monday's and Sunday's bit
        val monday = ReminderDaysEncoderDecoder.decodeWeekDay(encodedDays, 0)
        val tuesday = ReminderDaysEncoderDecoder.decodeWeekDay(encodedDays, 1)
        val sunday = ReminderDaysEncoderDecoder.decodeWeekDay(encodedDays, 6)

        monday `should be equal to` true
        tuesday `should be equal to` false
        sunday `should be equal to` true
    }

    @Test
    fun `should encode week day properly`() {
        val allDays = listOf(true, true, true, true, true, true, true)
        val someDays = listOf(false, true, false, false, false, true, false) // Set up Tuesday's and Saturday's bit

        val allDaysResult = ReminderDaysEncoderDecoder.encodeDays(allDays)
        val someDaysResult = ReminderDaysEncoderDecoder.encodeDays(someDays)

        allDaysResult `should be equal to` 0b1111111
        someDaysResult `should be equal to` 0b0100010
    }
}