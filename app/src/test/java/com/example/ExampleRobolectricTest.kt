package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.AppLanguage
import com.example.data.model.UrgencyLevel
import com.example.data.repository.ComputerVisionEngine
import com.example.data.repository.TriageClassifier
import com.example.data.repository.VitalsEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read app name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Dr. Sehat AI", appName)
    }

    @Test
    fun `triage classifier flags emergency for acute chest pain and breathlessness`() {
        val result = TriageClassifier.classifySymptomInput("Seenay mein shadeed dard aur saans lene mein takleef")
        assertEquals(UrgencyLevel.EMERGENCY, result.urgency)
        assertTrue(result.probability > 0.85f)
    }

    @Test
    fun `triage classifier identifies home care for mild common cold`() {
        val result = TriageClassifier.classifySymptomInput("Halka nazla zukam aur gale mein kharash")
        assertEquals(UrgencyLevel.HOME_CARE, result.urgency)
    }

    @Test
    fun `triage classifier detects stomach pain gastroenteritis`() {
        val result = TriageClassifier.classifySymptomInput("Mere pait mein dard aur ulti hai")
        assertTrue(result.conditionEn.contains("Gastroenteritis"))
    }

    @Test
    fun `greeting detection recognizes casual chat`() {
        assertTrue(TriageClassifier.isGreetingOrCasual("Salam Dr sahab"))
        val reply = TriageClassifier.generateBotReply(
            TriageClassifier.classifySymptomInput("general"),
            AppLanguage.ROMAN_URDU,
            rawInput = "Salam Dr sahab"
        )
        assertTrue(reply.contains("Walaikum Assalam"))
    }

    @Test
    fun `computer vision engine generates grad cam heatmap`() {
        val heatmap = ComputerVisionEngine.generateGradCamHeatmap(100, 100, 0.5f, 0.5f, 0.35f)
        assertNotNull(heatmap)
        assertEquals(100, heatmap.width)
        assertEquals(100, heatmap.height)
    }

    @Test
    fun `vitals engine calculates ppg pulse wave sample`() {
        val sample = VitalsEngine.calculatePpgSample(0.5f, 75)
        assertTrue(sample in 0.0f..1.0f)
    }
}
