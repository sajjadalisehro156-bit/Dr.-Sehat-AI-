package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.ChatMessage
import com.example.data.model.TriageResult
import com.example.data.model.UrgencyLevel
import com.example.data.repository.GeminiHealthAiClient
import com.example.data.repository.HealthKnowledgeBase
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.UrgencyEmergencyRed
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymptomCheckerScreen(
    currentLanguage: AppLanguage,
    onBack: () -> Unit,
    onDoctorHandoff: (TriageResult) -> Unit,
    onSpeakText: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var inputText by remember { mutableStateOf("") }
    val selectedSymptomIds = remember { mutableStateListOf<String>() }
    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current

    var isAiThinking by remember { mutableStateOf(false) }
    var showVoicePromptDialog by remember { mutableStateOf(false) }
    var showKaggleExplorer by remember { mutableStateOf(false) }
    var showApiKeyDialog by remember { mutableStateOf(false) }

    var isOnlineAiActive by remember {
        mutableStateOf(GeminiHealthAiClient.isLiveAiConfigured(context))
    }
    var apiKeyInput by remember {
        mutableStateOf(GeminiHealthAiClient.getEffectiveApiKey(context))
    }

    // Seed initial greeting message
    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                id = "init_1",
                text = when (currentLanguage) {
                    AppLanguage.URDU -> "السلام علیکم! میں ہوں ڈاکٹر صحت (Dr. Sehat AI)، آپ کا ورچوئل میڈیکل اسسٹنٹ۔\n\nمیری تشخیصی صلاحیتیں کیگل (Kaggle) میڈیکل ڈیٹا سیٹ اور جدید AI پر مبنی ہیں۔ آپ کوئی بھی طبی سوال پوچھ سکتے ہیں، یا اپنی علامات بتا سکتے ہیں۔"
                    AppLanguage.ROMAN_URDU -> "Assalam-o-Alaikum! Main Dr. Sehat AI hoon. Meri clinical diagnosis Kaggle medical datasets aur advanced AI par mabni hai. Aap koi bhi tibbi sawal pooch saktay hain ya apni alamat bata saktay hain."
                    AppLanguage.ENGLISH -> "Assalam-o-Alaikum! I am Dr. Sehat AI, your virtual clinical assistant grounded in Kaggle medical datasets and AI. You can ask any medical question, inquire about symptoms, medicines, or healthy diet."
                },
                isFromUser = false
            )
        )
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    fun handleSend(textToSend: String) {
        val trimmed = textToSend.trim()
        if (trimmed.isBlank() && selectedSymptomIds.isEmpty()) return

        val userMsg = if (trimmed.isNotBlank()) trimmed else {
            selectedSymptomIds.joinToString(", ") { id ->
                HealthKnowledgeBase.symptomsList.find { it.id == id }?.nameRoman ?: id
            }
        }

        // Add user message
        messages.add(
            ChatMessage(
                id = "user_${System.currentTimeMillis()}",
                text = userMsg,
                isFromUser = true
            )
        )
        inputText = ""
        keyboardController?.hide()
        isAiThinking = true

        // Build history turns for multi-turn AI reasoning
        val history = messages.map { it.text to it.isFromUser }

        coroutineScope.launch {
            val (botAnswer, triageResult) = GeminiHealthAiClient.queryVirtualDoctor(
                userQuery = userMsg,
                language = currentLanguage,
                conversationHistory = history,
                context = context
            )

            isAiThinking = false
            messages.add(
                ChatMessage(
                    id = "bot_${System.currentTimeMillis()}",
                    text = botAnswer,
                    isFromUser = false,
                    triageResult = triageResult
                )
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Dr. Sehat AI Doctor",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isOnlineAiActive) EmeraldLight.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isOnlineAiActive) "Gemini 3.5 AI" else "Kaggle AI (Offline)",
                                    color = if (isOnlineAiActive) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = if (isOnlineAiActive) "Online Medical Reasoning Active" else "4,920 Kaggle Diseases Grounded",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // API Key Settings Dialog Button
                    IconButton(
                        onClick = { showApiKeyDialog = true },
                        modifier = Modifier.testTag("btn_api_key_settings")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "AI Settings",
                            tint = if (isOnlineAiActive) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Kaggle Dataset Explorer button
                    IconButton(
                        onClick = { showKaggleExplorer = true },
                        modifier = Modifier.testTag("btn_kaggle_explorer")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dataset,
                            contentDescription = "Kaggle Database",
                            tint = EmeraldPrimary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Quick Symptom Selector Chips: Tapping one immediately inquires
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(HealthKnowledgeBase.symptomsList) { symptom ->
                    val isSelected = selectedSymptomIds.contains(symptom.id)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (isSelected) {
                                selectedSymptomIds.remove(symptom.id)
                            } else {
                                selectedSymptomIds.add(symptom.id)
                                // Immediate 1-tap query for instant response
                                handleSend("Mujhe ${symptom.nameRoman} (${symptom.nameUrdu}) ki takleef hai, kya ilaj aur parhez hai?")
                            }
                        },
                        label = {
                            Text(
                                text = "${symptom.nameRoman} (${symptom.nameUrdu})",
                                fontSize = 11.sp
                            )
                        },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (symptom.isRedFlag) UrgencyEmergencyRed.copy(alpha = 0.2f) else EmeraldPrimary.copy(alpha = 0.2f),
                            selectedLabelColor = if (symptom.isRedFlag) UrgencyEmergencyRed else EmeraldPrimary
                        )
                    )
                }
            }

            // Chat Messages Stream
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages) { msg ->
                    if (msg.isFromUser) {
                        UserMessageBubble(msg.text)
                    } else {
                        BotMessageBubble(
                            msg = msg,
                            onSpeak = { onSpeakText(msg.text) },
                            onDoctorConnect = { msg.triageResult?.let { onDoctorHandoff(it) } }
                        )
                    }
                }

                if (isAiThinking) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = EmeraldPrimary
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isOnlineAiActive) "Gemini AI clinical reasoning..." else "Dr. Sehat AI analyzing...",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldPrimary
                                    )
                                    Text(
                                        text = "Synthesizing evidence-based diagnosis and Pakistani nutrition",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 1-Tap Quick Clinical Inquiries (Urdu / Roman)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val prompts = listOf(
                    "Sar dard ki konsi dawa lu?" to "سر درد کی دوا",
                    "Sugar ke mareez ko kya khana chahiye?" to "شوگر کا پرہیز",
                    "BP high ho gaya hai kya karu?" to "ہائی بلڈ پریشر",
                    "Dengue ki alamat aur bachao" to "ڈینگی کی علامات",
                    "3 hafte se khansi hai" to "مسلسل کھانسی",
                    "Bachay ko bukhar aur ulti" to "بچوں کا بخار",
                    "Pait me shadeed dard aur dast" to "پیٹ کا انفیکشن",
                    "Seenay me dard aur ghabrahat" to "سینے میں درد"
                )
                items(prompts) { (roman, urdu) ->
                    Box(
                        modifier = Modifier
                            .background(EmeraldPrimary.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                            .border(1.dp, EmeraldPrimary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                            .clickable { handleSend(roman) }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "$roman ($urdu)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = EmeraldPrimary
                        )
                    }
                }
            }

            // Input Bar with IME Send & Voice Dictation
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 3.dp,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Voice prompt helper button
                    IconButton(
                        onClick = { showVoicePromptDialog = true },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Dictation",
                            tint = EmeraldPrimary
                        )
                    }

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("symptom_input_field"),
                        placeholder = {
                            Text(
                                text = "Ask any health question or symptom...",
                                fontSize = 13.sp
                            )
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { handleSend(inputText) }),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { handleSend(inputText) },
                        modifier = Modifier
                            .size(44.dp)
                            .background(EmeraldPrimary, CircleShape)
                            .testTag("btn_send_symptom")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }

    // AI Settings / API Key Dialog
    if (showApiKeyDialog) {
        AlertDialog(
            onDismissRequest = { showApiKeyDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Key, contentDescription = null, tint = EmeraldPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI Engine Configuration", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Dr. Sehat AI includes a complete on-device Kaggle clinical knowledge base (4,920 conditions) that works 100% offline without any API key.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "To enable live cloud reasoning with Gemini 3.5 Flash, enter your Google AI Studio Gemini API key below (optional):",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = { apiKeyInput = it },
                        label = { Text("Gemini API Key", fontSize = 11.sp) },
                        placeholder = { Text("AIzaSy...", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldPrimary
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isOnlineAiActive) "🟢 Live Gemini Active" else "🌐 Kaggle Offline Engine Active",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isOnlineAiActive) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (apiKeyInput.isNotBlank()) {
                            TextButton(onClick = {
                                apiKeyInput = ""
                                GeminiHealthAiClient.setCustomApiKey(context, "")
                                isOnlineAiActive = GeminiHealthAiClient.isLiveAiConfigured(context)
                                Toast.makeText(context, "Reset to Kaggle Offline Engine", Toast.LENGTH_SHORT).show()
                            }) {
                                Text("Clear Key", fontSize = 10.sp, color = UrgencyEmergencyRed)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        GeminiHealthAiClient.setCustomApiKey(context, apiKeyInput)
                        isOnlineAiActive = GeminiHealthAiClient.isLiveAiConfigured(context)
                        showApiKeyDialog = false
                        Toast.makeText(
                            context,
                            if (isOnlineAiActive) "Gemini AI Engine Activated" else "Kaggle Offline Engine Active",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("Save & Apply")
                }
            },
            dismissButton = {
                TextButton(onClick = { showApiKeyDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Kaggle Disease Explorer Dialog
    if (showKaggleExplorer) {
        AlertDialog(
            onDismissRequest = { showKaggleExplorer = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Dataset, contentDescription = null, tint = EmeraldPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Kaggle Medical Dataset Explorer", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "Verified disease profiles from Kaggle Symptom-Disease Knowledge Graph used by Dr. Sehat AI for high-accuracy virtual triage:",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    items(HealthKnowledgeBase.kaggleDiseases) { dis ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    handleSend("Explain ${dis.name} (${dis.nameUrdu}) symptoms, treatment and precautions")
                                    showKaggleExplorer = false
                                },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = dis.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = dis.nameUrdu, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = EmeraldPrimary)
                                }
                                Text(text = "Category: ${dis.category}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = dis.summaryEn, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Diet: ${dis.recommendedDiet}", fontSize = 10.sp, color = EmeraldDark, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showKaggleExplorer = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Voice Dictation Quick Phrases Dialog
    if (showVoicePromptDialog) {
        AlertDialog(
            onDismissRequest = { showVoicePromptDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Mic, contentDescription = null, tint = EmeraldPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Urdu Voice Dictation • آواز سے بات کریں", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select a spoken phrase or speak to Dr. Sehat AI:", fontSize = 12.sp)
                    listOf(
                        "Mujhe 3 din se bukhar aur shadeed khansi hai",
                        "Mere pet me bohot dard hai aur ulti ho rahi hai",
                        "Seenay mein shadeed dard aur saans lene mein takleef hai",
                        "Dengue bukhar se bachao ke liye kya karein?",
                        "Sugar ke mareez ko kya khana chahiye?",
                        "Dr Sahab meri tabiat theek nahi hai, mashwara dein"
                    ).forEach { phrase ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                .clickable {
                                    handleSend(phrase)
                                    showVoicePromptDialog = false
                                }
                                .padding(10.dp)
                        ) {
                            Text(text = "🗣️ \"$phrase\"", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showVoicePromptDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun UserMessageBubble(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
                .background(EmeraldPrimary)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun BotMessageBubble(
    msg: ChatMessage,
    onSpeak: () -> Unit,
    onDoctorConnect: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Column(modifier = Modifier.fillMaxWidth(0.94f)) {
            ElevatedCard(
                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .background(EmeraldPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("+", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Dr. Sehat AI",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = EmeraldPrimary
                            )
                        }

                        IconButton(onClick = onSpeak, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Listen in Urdu",
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = msg.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )

                    // If triage result is attached, render Clinical Decision Card
                    msg.triageResult?.let { triage ->
                        Spacer(modifier = Modifier.height(10.dp))
                        ClinicalTriageCard(triage = triage, onDoctorConnect = onDoctorConnect)
                    }
                }
            }
        }
    }
}

@Composable
fun ClinicalTriageCard(
    triage: TriageResult,
    onDoctorConnect: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = triage.urgency.bgColor)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Urgency Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = triage.urgency.titleEn,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = triage.urgency.color
                )
                Text(
                    text = triage.urgency.titleUr,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = triage.urgency.color
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Differential Diagnosis: ${triage.conditionEn}",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "امکانی صورتحال: ${triage.conditionUrdu}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Home Care Tips list
            Text(
                text = "Recommended Steps • تجویز کردہ اقدامات:",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            triage.homeCareTips.take(2).forEach { tip ->
                Text(
                    text = "• $tip",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Doctor Connect Action
            Button(
                onClick = onDoctorConnect,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (triage.urgency == UrgencyLevel.EMERGENCY) UrgencyEmergencyRed else EmeraldPrimary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (triage.urgency == UrgencyLevel.EMERGENCY) "Call 1122 / Hospital ER" else "Connect with Doctor on WhatsApp",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
