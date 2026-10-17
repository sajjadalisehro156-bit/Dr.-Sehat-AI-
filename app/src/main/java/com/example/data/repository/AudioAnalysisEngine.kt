package com.example.data.repository

import com.example.data.model.CoughAnalysisResult
import com.example.data.model.UrgencyLevel
import kotlin.random.Random

object AudioAnalysisEngine {

    fun analyzeCoughRecording(presetType: String = "Productive"): CoughAnalysisResult {
        return when (presetType) {
            "Productive" -> CoughAnalysisResult(
                coughType = "Wet / Productive Bronchial Cough",
                coughTypeUrdu = "بلغم والی تر کھانسی (چھاتی کی جکڑن)",
                confidence = 0.91f,
                urgency = UrgencyLevel.SEE_DOCTOR,
                acousticDuration = "0.78s (Extended dampening phase)",
                spectralPitch = "Lower resonance (280 - 450 Hz) with low-frequency rumble",
                explosivePhases = 3,
                potentialCauses = listOf(
                    "Acute Bronchitis",
                    "Early Bronchopneumonia",
                    "Chronic Obstructive Pulmonary Disease (COPD)",
                    "Post-Nasal Drip"
                ),
                recommendationEn = "Indicates lower respiratory secretions. Maintain hydration, use steam inhalation with eucalyptus oil, and consult a physician if accompanied by colored phlegm or fever.",
                recommendationUrdu = "پھیپھڑوں اور سانس کی نالیوں میں بلغم کی علامت ہے۔ پانی کا استعمال بڑھائیں، بھاپ لیں اور بخار کی صورت میں ڈاکٹر کو دکھائیں۔"
            )
            "Dry" -> CoughAnalysisResult(
                coughType = "Dry / Irritant Paroxysmal Cough",
                coughTypeUrdu = "خشک کھانسی (گلے میں خراش اور سوزش)",
                confidence = 0.93f,
                urgency = UrgencyLevel.HOME_CARE,
                acousticDuration = "0.42s (Sharp crisp explosive burst)",
                spectralPitch = "High spectral crest (650 - 1200 Hz)",
                explosivePhases = 2,
                potentialCauses = listOf(
                    "Viral Upper Respiratory Tract Infection (Common Cold)",
                    "Dust / Smog Environmental Bronchospasm (Common in Lahore/Karachi)",
                    "Gastroesophageal Reflux (Acid Reflux)",
                    "Early Stage COVID-19 / Flu"
                ),
                recommendationEn = "Typically self-limiting. Soothe vocal cords with warm Joshanda or honey-ginger tea. Avoid cold beverages and air pollutants (wear a mask in traffic/smog).",
                recommendationUrdu = "عام طور پر موسمی دھول، اسموگ یا وائرل نزلہ کی وجہ سے ہوتی ہے۔ گرم قہوہ، شہد اور جوشاندہ لیں۔ اسموگ میں ماسک پہنیں۔"
            )
            "Asthmatic" -> CoughAnalysisResult(
                coughType = "Asthmatic Wheezing / Spasmodic Cough",
                coughTypeUrdu = "دمہ یا سانس کی تنگی والی کھانسی (سیٹی کی آواز)",
                confidence = 0.88f,
                urgency = UrgencyLevel.SEE_DOCTOR,
                acousticDuration = "1.15s (Prolonged expiratory wheeze phase)",
                spectralPitch = "Monophonic high pitch harmonic overtone (> 1400 Hz)",
                explosivePhases = 4,
                potentialCauses = listOf(
                    "Bronchial Asthma Flare-up",
                    "Reactive Airway Disease",
                    "Allergic Bronchoconstriction"
                ),
                recommendationEn = "Acoustic spectrogram reveals end-expiratory high-frequency wheezing indicative of narrowed small airways. Use prescribed bronchodilator inhaler (e.g. Salbutamol) and seek urgent medical care if peak flow decreases.",
                recommendationUrdu = "سانس کی نالیوں میں تنگی اور سیٹی جیسی آواز موجود ہے۔ انہیلر (Inhaler) کا استعمال کریں اور فوری ڈاکٹر سے چیک اپ کروائیں۔"
            )
            else -> CoughAnalysisResult(
                coughType = "Normal Physiological Throat Clear",
                coughTypeUrdu = "عام گلا صاف کرنا (نارمل آواز)",
                confidence = 0.95f,
                urgency = UrgencyLevel.HOME_CARE,
                acousticDuration = "0.22s (Brief acoustic clearance)",
                spectralPitch = "Broadband low energy (150 - 300 Hz)",
                explosivePhases = 1,
                potentialCauses = listOf(
                    "Physiological Mucus Clearance",
                    "Mild dryness or vocal fatigue"
                ),
                recommendationEn = "No pathological acoustic biomarkers detected. Drink room-temperature water.",
                recommendationUrdu = "کوئی بیماری کے اثرات نہیں پائے گئے۔ پانی پیئیں اور حلق تر رکھیں۔"
            )
        }
    }

    /**
     * Generates simulated FFT audio frequency spectrum values for dynamic audio visualizer.
     */
    fun generateFftBars(seed: Long, barCount: Int = 24): List<Float> {
        val rand = Random(seed)
        return (0 until barCount).map { i ->
            val bellFactor = 1.0f - ((i - barCount / 2f) / (barCount / 2f)).let { it * it }
            val noise = rand.nextFloat() * 0.4f
            (bellFactor * 0.7f + noise).coerceIn(0.1f, 1.0f)
        }
    }
}
