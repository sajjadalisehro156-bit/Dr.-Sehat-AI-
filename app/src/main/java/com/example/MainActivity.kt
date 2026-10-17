package com.example

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.TriageResult
import com.example.ui.components.TopAppBarWithBadges
import com.example.ui.screens.ChestXrayScreen
import com.example.ui.screens.CoughAudioScreen
import com.example.ui.screens.DoctorHandoffScreen
import com.example.ui.screens.EyeAnemiaScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MedicineDietScreen
import com.example.ui.screens.ModelAccuracyScreen
import com.example.ui.screens.SkinScanScreen
import com.example.ui.screens.SymptomCheckerScreen
import com.example.ui.screens.VitalsSensorScreen
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.MyApplicationTheme
import java.util.Locale

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        tts = TextToSpeech(this, this)

        setContent {
            MyApplicationTheme {
                MainAppContainer(
                    onSpeakText = { text, lang ->
                        speakOut(text, lang)
                    }
                )
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val urduLocale = Locale("ur", "PK")
            val result = tts?.setLanguage(urduLocale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to English TTS with Pakistani accent or default
                tts?.setLanguage(Locale.ENGLISH)
            }
            isTtsReady = true
        }
    }

    private fun speakOut(text: String, lang: AppLanguage) {
        if (!isTtsReady || tts == null) {
            Toast.makeText(this, "Voice engine initializing...", Toast.LENGTH_SHORT).show()
            return
        }

        if (lang == AppLanguage.URDU) {
            val urResult = tts?.setLanguage(Locale("ur", "PK"))
            if (urResult == TextToSpeech.LANG_MISSING_DATA || urResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.ENGLISH)
            }
        } else {
            tts?.setLanguage(Locale.ENGLISH)
        }

        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "dr_sehat_tts")
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}

@Composable
fun MainAppContainer(
    onSpeakText: (String, AppLanguage) -> Unit
) {
    var currentScreen by remember { mutableStateOf("home") }
    var currentLanguage by remember { mutableStateOf(AppLanguage.ROMAN_URDU) }
    var isLowBandwidth by remember { mutableStateOf(false) }
    var activeTriageResult by remember { mutableStateOf<TriageResult?>(null) }

    fun speakCurrentContext() {
        val voiceMsg = when (currentLanguage) {
            AppLanguage.URDU -> "ڈاکٹر صحت آپ کا ورچوئل میڈیکل معاون ہے۔ ایمرجنسی میں 1122 کال کریں۔"
            AppLanguage.ROMAN_URDU -> "Dr. Sehat AI aap ka virtual medical companion hai. Emergency mein 1122 call karein."
            AppLanguage.ENGLISH -> "Dr. Sehat AI is your virtual triage companion. For emergencies, please call 1122."
        }
        onSpeakText(voiceMsg, currentLanguage)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (currentScreen == "home") {
                TopAppBarWithBadges(
                    currentLanguage = currentLanguage,
                    onLanguageChange = { currentLanguage = it },
                    isLowBandwidth = isLowBandwidth,
                    onLowBandwidthToggle = { isLowBandwidth = it },
                    onSpeakUrdu = { speakCurrentContext() }
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                NavigationBarItem(
                    selected = currentScreen == "home",
                    onClick = { currentScreen = "home" },
                    icon = {
                        Icon(
                            if (currentScreen == "home") Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldDark,
                        selectedTextColor = EmeraldPrimary,
                        indicatorColor = EmeraldLight.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.testTag("nav_home")
                )

                NavigationBarItem(
                    selected = currentScreen == "symptom_checker",
                    onClick = { currentScreen = "symptom_checker" },
                    icon = {
                        Icon(
                            if (currentScreen == "symptom_checker") Icons.Filled.Chat else Icons.Outlined.Chat,
                            contentDescription = "Triage"
                        )
                    },
                    label = { Text("Triage", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldDark,
                        selectedTextColor = EmeraldPrimary,
                        indicatorColor = EmeraldLight.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.testTag("nav_triage")
                )

                NavigationBarItem(
                    selected = currentScreen == "skin_scan" || currentScreen == "chest_xray",
                    onClick = { currentScreen = "skin_scan" },
                    icon = {
                        Icon(
                            if (currentScreen == "skin_scan" || currentScreen == "chest_xray") Icons.Filled.HealthAndSafety else Icons.Outlined.HealthAndSafety,
                            contentDescription = "Scans"
                        )
                    },
                    label = { Text("Scans CV", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldDark,
                        selectedTextColor = EmeraldPrimary,
                        indicatorColor = EmeraldLight.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.testTag("nav_scans")
                )

                NavigationBarItem(
                    selected = currentScreen == "vitals_sensor",
                    onClick = { currentScreen = "vitals_sensor" },
                    icon = {
                        Icon(
                            if (currentScreen == "vitals_sensor") Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Vitals"
                        )
                    },
                    label = { Text("Vitals", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldDark,
                        selectedTextColor = EmeraldPrimary,
                        indicatorColor = EmeraldLight.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.testTag("nav_vitals")
                )

                NavigationBarItem(
                    selected = currentScreen == "doctor_handoff",
                    onClick = { currentScreen = "doctor_handoff" },
                    icon = {
                        Icon(
                            if (currentScreen == "doctor_handoff") Icons.Filled.Call else Icons.Outlined.Call,
                            contentDescription = "Reports"
                        )
                    },
                    label = { Text("Handoff", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldDark,
                        selectedTextColor = EmeraldPrimary,
                        indicatorColor = EmeraldLight.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.testTag("nav_handoff")
                )
            }
        }
    ) { innerPadding ->
        when (currentScreen) {
            "home" -> HomeScreen(
                currentLanguage = currentLanguage,
                onNavigate = { route ->
                    currentScreen = route
                },
                modifier = Modifier.padding(innerPadding)
            )

            "symptom_checker" -> SymptomCheckerScreen(
                currentLanguage = currentLanguage,
                onBack = { currentScreen = "home" },
                onDoctorHandoff = { triage ->
                    activeTriageResult = triage
                    currentScreen = "doctor_handoff"
                },
                onSpeakText = { text ->
                    onSpeakText(text, currentLanguage)
                },
                modifier = Modifier.padding(innerPadding)
            )

            "skin_scan" -> SkinScanScreen(
                currentLanguage = currentLanguage,
                onBack = { currentScreen = "home" },
                modifier = Modifier.padding(innerPadding)
            )

            "chest_xray" -> ChestXrayScreen(
                currentLanguage = currentLanguage,
                onBack = { currentScreen = "home" },
                modifier = Modifier.padding(innerPadding)
            )

            "vitals_sensor" -> VitalsSensorScreen(
                currentLanguage = currentLanguage,
                onBack = { currentScreen = "home" },
                modifier = Modifier.padding(innerPadding)
            )

            "eye_anemia" -> EyeAnemiaScreen(
                currentLanguage = currentLanguage,
                onBack = { currentScreen = "home" },
                modifier = Modifier.padding(innerPadding)
            )

            "cough_audio" -> CoughAudioScreen(
                currentLanguage = currentLanguage,
                onBack = { currentScreen = "home" },
                modifier = Modifier.padding(innerPadding)
            )

            "doctor_handoff" -> DoctorHandoffScreen(
                currentLanguage = currentLanguage,
                triageResult = activeTriageResult,
                onBack = { currentScreen = "home" },
                modifier = Modifier.padding(innerPadding)
            )

            "medicine_diet" -> MedicineDietScreen(
                currentLanguage = currentLanguage,
                onBack = { currentScreen = "home" },
                modifier = Modifier.padding(innerPadding)
            )

            "model_accuracy" -> ModelAccuracyScreen(
                currentLanguage = currentLanguage,
                onBack = { currentScreen = "home" },
                modifier = Modifier.padding(innerPadding)
            )

            else -> HomeScreen(
                currentLanguage = currentLanguage,
                onNavigate = { route -> currentScreen = route },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
