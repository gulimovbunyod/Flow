package com.gulimovbunyod.flow

import org.junit.Assert.assertEquals
import org.junit.Test

class AppIdentityTest {
    /** applicationId o'zgarsa yoki debug'ga suffix qo'shilsa, update zanjiri buziladi. */
    @Test
    fun applicationIdNeverChanges() {
        assertEquals("com.gulimovbunyod.flow", BuildConfig.APPLICATION_ID)
    }
}
