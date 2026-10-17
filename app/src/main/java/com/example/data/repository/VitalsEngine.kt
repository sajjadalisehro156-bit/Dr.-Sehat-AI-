package com.example.data.repository

import com.example.data.model.VitalsData
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

object VitalsEngine {

    /**
     * Generates a realistic photoplethysmography (PPG) pulse wave sample point.
     * Includes systolic peak, dicrotic notch, and diastolic runoff.
     */
    fun calculatePpgSample(timeSeconds: Float, heartRateBpm: Int = 74): Float {
        val frequency = heartRateBpm / 60.0f
        val phase = (timeSeconds * frequency) % 1.0f

        // Systolic sharp upstroke and peak
        val systolic = exp(-((phase - 0.18f) * (phase - 0.18f)) / 0.008f) * 0.95f
        // Dicrotic notch and secondary reflected peak
        val dicrotic = exp(-((phase - 0.42f) * (phase - 0.42f)) / 0.012f) * 0.35f
        // Baseline noise / gentle fluctuation
        val baseline = sin(timeSeconds * 0.4f * 2 * PI).toFloat() * 0.04f

        return (systolic + dicrotic + baseline).coerceIn(0.0f, 1.0f)
    }

    fun getSimulatedVitals(
        source: String = "Smartphone Camera rPPG",
        stressLevel: Float = 0.2f
    ): VitalsData {
        val baseBpm = 72 + (stressLevel * 20).toInt()
        val baseSpo2 = if (stressLevel > 0.8f) 94 else 98
        val baseTemp = 98.4f + (stressLevel * 1.4f)

        return VitalsData(
            heartRateBpm = baseBpm,
            systolicBp = 118 + (stressLevel * 14).toInt(),
            diastolicBp = 78 + (stressLevel * 8).toInt(),
            spo2Percent = baseSpo2,
            bodyTempF = (baseTemp * 10).toInt() / 10f,
            signalQuality = if (stressLevel > 0.7f) "Good (Motion Detected)" else "Optimal Signal (rPPG Locked)",
            source = source
        )
    }

    fun interpretHeartRate(bpm: Int): String {
        return when {
            bpm < 60 -> "Bradycardia (Slow Heart Rate - Normal in athletes or thyroid/cardiac check)"
            bpm <= 100 -> "Normal Sinus Rhythm (Healthy Resting Rate)"
            else -> "Tachycardia (Elevated Heart Rate - Fever, anxiety, or dehydration)"
        }
    }

    fun interpretSpo2(spo2: Int): Pair<String, Boolean> {
        return when {
            spo2 >= 95 -> "Optimal Blood Oxygenation (Normal)" to false
            spo2 >= 92 -> "Mild Hypoxia - Monitor closely with rest and ventilation" to false
            else -> "CRITICAL HYPOXIA (<92%) - Immediate Oxygen Therapy Needed!" to true
        }
    }
}
