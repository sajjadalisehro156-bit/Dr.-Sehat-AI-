package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.example.data.model.AppLanguage
import com.example.data.model.TriageResult
import com.example.data.model.UrgencyLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiHealthAiClient {

    private const val TAG = "GeminiHealthAiClient"
    private const val PREFS_NAME = "dr_sehat_prefs"
    private const val KEY_CUSTOM_API_KEY = "custom_gemini_api_key"

    private const val PRIMARY_MODEL = "gemini-3.5-flash"
    private const val FALLBACK_MODEL = "gemini-2.5-flash"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private const val SYSTEM_PROMPT = """
You are Dr. Sehat AI (ڈاکٹر صحت), an expert compassionate virtual physician and clinical triage specialist for Pakistan.
You fluently understand and communicate in English, Urdu (اردو), and Roman Urdu.
Your clinical diagnostic reasoning is grounded in evidence-based medicine and Kaggle clinical benchmark datasets:
- Kaggle Disease-Symptom Knowledge Graph (4,920 disease-symptom profiles)
- NIH ChestX-ray14 & Shenzhen TB dataset (pulmonary disease, pneumonia, cavity lesions)
- HAM10000 & DermNet (dermatology and skin lesions)
- EPFL COUGHVID acoustic respiratory corpus

When a user asks any health question, inquires about symptoms, or asks for advice:
1. Greet warmly and respectfully ("Assalam-o-Alaikum" / "السلام علیکم").
2. Provide a clear, accurate clinical explanation and differential diagnosis.
3. Classify clinical urgency clearly:
   - [EMERGENCY]: Immediate danger, call 1122 or rush to hospital ER (e.g. chest pain with sweating, coughing blood, breathing distress).
   - [SEE DOCTOR]: Requires formal clinical evaluation within 24-48 hours.
   - [HOME CARE]: Self-limiting mild illness.
4. Give authentic Pakistani nutritional & home care recommendations (e.g. Moong Daal Khichdi, Desi Chicken Yakhni, Joshanda, Ginger-Honey Kahwa, ORS Nimkol, Karela, Dahi/Lassi).
5. Highlight critical Red Flags to monitor.
6. If the user asks about medicines (e.g. Panadol, Disprin, Flagyl, ORS), provide safe OTC dosage guidelines and warn against self-prescribing antibiotics or taking Aspirin in suspected dengue.
7. Reply primarily in the user's selected language (Urdu, Roman Urdu, or English).
8. Always include the clinical safety reminder: "Supports a doctor, does not replace one."
"""

    fun getCustomApiKey(context: Context?): String {
        if (context == null) return ""
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_CUSTOM_API_KEY, "") ?: ""
    }

    fun setCustomApiKey(context: Context, key: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_CUSTOM_API_KEY, key.trim()).apply()
    }

    fun getEffectiveApiKey(context: Context?): String {
        val customKey = getCustomApiKey(context)
        if (customKey.isNotBlank()) return customKey

        val buildKey = BuildConfig.GEMINI_API_KEY
        if (!buildKey.isNullOrBlank() && buildKey != "MY_GEMINI_API_KEY") {
            return buildKey
        }
        return ""
    }

    fun isLiveAiConfigured(context: Context?): Boolean {
        return getEffectiveApiKey(context).isNotBlank()
    }

    suspend fun queryVirtualDoctor(
        userQuery: String,
        language: AppLanguage,
        conversationHistory: List<Pair<String, Boolean>> = emptyList(),
        context: Context? = null
    ): Pair<String, TriageResult?> = withContext(Dispatchers.IO) {
        val apiKey = getEffectiveApiKey(context)

        // If no API key configured, use our offline Kaggle clinical AI engine
        if (apiKey.isBlank()) {
            Log.d(TAG, "No live Gemini API key found, using local Kaggle clinical engine.")
            val triage = TriageClassifier.classifySymptomInput(userQuery)
            val reply = TriageClassifier.generateBotReply(triage, language, rawInput = userQuery)
            return@withContext Pair(reply, triage)
        }

        // Try primary model (gemini-3.5-flash) first, then fallback model (gemini-2.5-flash)
        val modelsToTry = listOf(PRIMARY_MODEL, FALLBACK_MODEL)
        for (modelName in modelsToTry) {
            try {
                val rootJson = JSONObject()

                // System Instruction
                val sysInstructionObj = JSONObject()
                val sysPartsArr = JSONArray()
                sysPartsArr.put(JSONObject().put("text", SYSTEM_PROMPT))
                sysInstructionObj.put("parts", sysPartsArr)
                rootJson.put("systemInstruction", sysInstructionObj)

                // Contents array
                val contentsArr = JSONArray()

                // Include recent turns for multi-turn conversational context
                val recentTurns = conversationHistory.takeLast(6)
                for ((text, isUser) in recentTurns) {
                    if (text.isNotBlank()) {
                        val turnObj = JSONObject()
                        turnObj.put("role", if (isUser) "user" else "model")
                        val partsArr = JSONArray()
                        partsArr.put(JSONObject().put("text", text))
                        turnObj.put("parts", partsArr)
                        contentsArr.put(turnObj)
                    }
                }

                // Current prompt
                val currentTurn = JSONObject()
                currentTurn.put("role", "user")
                val currentParts = JSONArray()
                val languagePrompt = when (language) {
                    AppLanguage.URDU -> "براہ کرم اردو زبان میں جواب دیں:\n$userQuery"
                    AppLanguage.ROMAN_URDU -> "Please respond in natural Roman Urdu:\n$userQuery"
                    AppLanguage.ENGLISH -> "Please respond in English:\n$userQuery"
                }
                currentParts.put(JSONObject().put("text", languagePrompt))
                currentTurn.put("parts", currentParts)
                contentsArr.put(currentTurn)

                rootJson.put("contents", contentsArr)

                // Generation Config
                val configObj = JSONObject()
                configObj.put("temperature", 0.4)
                configObj.put("topP", 0.95)
                rootJson.put("generationConfig", configObj)

                val url = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent?key=$apiKey"
                val requestBody = rootJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                val response = okHttpClient.newCall(request).execute()
                val responseString = response.body?.string() ?: ""

                if (response.isSuccessful && responseString.isNotBlank()) {
                    val respJson = JSONObject(responseString)
                    val candidates = respJson.optJSONArray("candidates")
                    val firstCand = candidates?.optJSONObject(0)
                    val content = firstCand?.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    val generatedText = parts?.optJSONObject(0)?.optString("text")

                    if (!generatedText.isNullOrBlank()) {
                        // Adjust triage based on AI output
                        val baseTriage = TriageClassifier.classifySymptomInput(userQuery)
                        val upper = generatedText.uppercase()
                        val adjustedUrgency = when {
                            upper.contains("[EMERGENCY]") || upper.contains("EMERGENCY ALERT") || upper.contains("1122") -> UrgencyLevel.EMERGENCY
                            upper.contains("[SEE DOCTOR]") -> UrgencyLevel.SEE_DOCTOR
                            upper.contains("[HOME CARE]") -> UrgencyLevel.HOME_CARE
                            else -> baseTriage.urgency
                        }
                        val structuredTriage = if (!TriageClassifier.isGreetingOrCasual(userQuery)) {
                            baseTriage.copy(urgency = adjustedUrgency)
                        } else null

                        return@withContext Pair(generatedText, structuredTriage)
                    }
                } else {
                    Log.w(TAG, "Model $modelName call failed with code ${response.code}: $responseString")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error querying model $modelName: ${e.message}")
            }
        }

        // Final fallback: Local Kaggle Clinical Knowledge Engine
        Log.w(TAG, "Gemini online call did not succeed, using local Kaggle Clinical Engine fallback.")
        val triage = TriageClassifier.classifySymptomInput(userQuery)
        val reply = TriageClassifier.generateBotReply(triage, language, rawInput = userQuery)
        Pair(reply, triage)
    }
}
