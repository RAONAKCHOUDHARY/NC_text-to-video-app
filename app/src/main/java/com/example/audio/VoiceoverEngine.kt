package com.example.audio

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

enum class VoiceTone(
    val id: String,
    val displayName: String,
    val description: String,
    val defaultPitch: Float,
    val defaultSpeed: Float,
    val sampleScript: String
) {
    DEEP_HORROR(
        id = "deep_horror",
        displayName = "Deep Horror / Bhari Aawaz",
        description = "Terrifying heavy bass rumble, ominous dark presence & scary deep undertone",
        defaultPitch = 0.55f, // Extra deep heavy voice
        defaultSpeed = 0.78f, // Slow suspenseful cadence
        sampleScript = "उस सुनसान अंधेरी रात में... सन्नाटे को चीरती हुई एक खौफनाक परछाई आगे बढ़ रही थी..."
    ),
    CINEMATIC_THRILLER(
        id = "cinematic_thriller",
        displayName = "Cinematic Thriller / Darawni",
        description = "Chilling suspense, atmospheric dread & breathy theatrical tension",
        defaultPitch = 0.65f, // Heavy dark thriller
        defaultSpeed = 0.85f, // Suspenseful pacing
        sampleScript = "Don't turn around. Whatever is lurking in the fog... has already locked its eyes on you."
    ),
    REALISTIC_STORYTELLER(
        id = "realistic_storyteller",
        displayName = "Realistic Storyteller",
        description = "Natural human inflection, warm articulation & documentary resonance",
        defaultPitch = 0.95f,
        defaultSpeed = 0.98f,
        sampleScript = "Every ancient relic carries an untold tale waiting to be unveiled by courageous explorers."
    ),
    DRAMATIC_SHOCKING(
        id = "dramatic_shocking",
        displayName = "Dramatic Shocking",
        description = "High-stakes urgency, abrupt tension spikes & dramatic cinematic punches",
        defaultPitch = 0.80f,
        defaultSpeed = 1.15f,
        sampleScript = "Emergency alert! The barrier has collapsed, and containment is completely lost!"
    );

    companion object {
        fun fromId(id: String): VoiceTone = entries.firstOrNull { it.id == id } ?: REALISTIC_STORYTELLER
    }
}

enum class VoiceProfile(
    val id: String,
    val displayName: String,
    val description: String,
    val defaultPitch: Float,
    val defaultSpeed: Float,
    val locale: Locale,
    val sampleText: String
) {
    ADAM_DEEP_MALE(
        id = "adam",
        displayName = "Adam Deep Male",
        description = "Cinematic, deep bass resonance for movie trailers & epics",
        defaultPitch = 0.72f,
        defaultSpeed = 0.90f,
        locale = Locale.US,
        sampleText = "In a world sculpted by shadows, the dawn of cinematic imagination awakens."
    ),
    ENERGETIC_CREATOR(
        id = "energetic_creator",
        displayName = "Energetic Creator",
        description = "Upbeat, punchy cadence for reels, shorts & gaming",
        defaultPitch = 1.30f,
        defaultSpeed = 1.15f,
        locale = Locale.US,
        sampleText = "What is up creators! Welcome back to another high-octane visual breakdown!"
    ),
    ASMR_WHISPER(
        id = "asmr_whisper",
        displayName = "ASMR Gentle Breeze",
        description = "Soft, intimate, whispered cadence with delicate tonal curve",
        defaultPitch = 0.95f,
        defaultSpeed = 0.82f,
        locale = Locale.US,
        sampleText = "Close your eyes. Listen to the subtle sound of rain falling on velvet leaves."
    ),
    HINDI_NATURAL(
        id = "hindi_natural",
        displayName = "Hindi (Natural Inflection)",
        description = "Fluent, warm & expressive modern Hindi storytelling",
        defaultPitch = 1.15f,
        defaultSpeed = 1.00f,
        locale = Locale("hi", "IN"),
        sampleText = "नमस्ते! PulseCraft Studio में आपका स्वागत है। सिनेमाई जादू अब आपकी मुट्ठी में है।"
    ),
    RAJASTHANI_HERITAGE(
        id = "rajasthani_heritage",
        displayName = "Rajasthani (Marwari Flavor)",
        description = "Traditional royal Marwari cadence & historic folkloric warmth",
        defaultPitch = 0.88f,
        defaultSpeed = 0.92f,
        locale = Locale("hi", "IN"),
        sampleText = "खम्मा घणी सा! रेगिस्तान री धरा सूं आन-बान और शान रो अनुपम नजरो।"
    ),
    PUNJABI_VIBRANT(
        id = "punjabi_vibrant",
        displayName = "Punjabi (Vibrant & Energetic)",
        description = "Lively, high-energy Punjabi spirit with rhythmic inflections",
        defaultPitch = 1.10f,
        defaultSpeed = 1.05f,
        locale = Locale("pa", "IN"),
        sampleText = "ਸਤਿ ਸ੍ਰੀ ਅਕਾਲ ਜੀ! ਪੰਜਾਬ ਦੀ ਖੁਸ਼ਬੂ ਅਤੇ ਊਰਜਾ ਨਾਲ ਭਰਪੂਰ ਇਹ ਸਿਨੇਮੈਟਿਕ ਅੰਦਾਜ਼ ਦੇਖੋ।"
    ),
    HARYANVI_BOLD(
        id = "haryanvi_bold",
        displayName = "Haryanvi (Bold & Gritty)",
        description = "Direct, powerful regional accent with hearty rustic punch",
        defaultPitch = 0.80f,
        defaultSpeed = 0.98f,
        locale = Locale("hi", "IN"),
        sampleText = "राम राम भाई सारे ने! यो दृश्य देख कै थारी तबीयत खुश हो ज्यागी।"
    ),
    AUTHORITATIVE_MATURE_MAN(
        id = "authoritative_man",
        displayName = "Authoritative UK Narration",
        description = "Commanding, grounded tone for documentaries & journalism",
        defaultPitch = 0.82f,
        defaultSpeed = 0.94f,
        locale = Locale.UK,
        sampleText = "Across millennia of recorded history, humanity has sought to capture time itself in motion."
    )
}

class VoiceoverEngine(context: Context) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _speechProgress = MutableStateFlow(0f)
    val speechProgress: StateFlow<Float> = _speechProgress.asStateFlow()

    private val _currentUtterance = MutableStateFlow("")
    val currentUtterance: StateFlow<String> = _currentUtterance.asStateFlow()

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                setupUtteranceListener()
            } else {
                Log.e("VoiceoverEngine", "TTS initialization failed: status=$status")
            }
        }
    }

    private fun setupUtteranceListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _isSpeaking.value = true
                _speechProgress.value = 0.1f
            }

            override fun onDone(utteranceId: String?) {
                _isSpeaking.value = false
                _speechProgress.value = 1.0f
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _isSpeaking.value = false
            }

            override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                super.onRangeStart(utteranceId, start, end, frame)
                val fullText = _currentUtterance.value
                if (fullText.isNotEmpty()) {
                    _speechProgress.value = (end.toFloat() / fullText.length.toFloat()).coerceIn(0f, 1f)
                }
            }
        })
    }

    fun speak(
        text: String,
        profile: VoiceProfile,
        customPitch: Float? = null,
        customSpeed: Float? = null
    ) {
        if (!isInitialized || text.isBlank()) return
        stop()

        _currentUtterance.value = text

        val engine = tts ?: return

        // Set locale
        val langResult = engine.setLanguage(profile.locale)
        if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            // Fallback to English if specific locale not present
            engine.language = Locale.US
        }

        // Apply pitch & rate
        val pitch = customPitch ?: profile.defaultPitch
        val speed = customSpeed ?: profile.defaultSpeed

        engine.setPitch(pitch)
        engine.setSpeechRate(speed)

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "VOICEOVER_${System.currentTimeMillis()}")
        }

        engine.speak(text, TextToSpeech.QUEUE_FLUSH, params, params.getString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID))
    }

    fun speakTone(
        text: String,
        tone: VoiceTone,
        customPitch: Float? = null,
        customSpeed: Float? = null
    ) {
        if (!isInitialized || text.isBlank()) return
        stop()

        // Prepare dramatic expressive pauses for realistic narrative cadence
        val formattedText = formatExpressiveScript(text, tone)
        _currentUtterance.value = formattedText

        val engine = tts ?: return

        // Set locale depending on script content (Hindi if contains Devanagari characters, else US)
        val hasHindi = text.any { it in '\u0900'..'\u097F' }
        val targetLocale = if (hasHindi) Locale("hi", "IN") else Locale.US
        val langResult = engine.setLanguage(targetLocale)
        if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            engine.language = Locale.US
        }

        // Search for a deep / resonant voice if available on device
        try {
            val availableVoices = engine.voices
            if (!availableVoices.isNullOrEmpty()) {
                val bestVoice = when (tone) {
                    VoiceTone.DEEP_HORROR -> availableVoices.firstOrNull {
                        it.locale.language == targetLocale.language &&
                                (it.name.contains("male", ignoreCase = true) || it.name.contains("deep", ignoreCase = true))
                    } ?: availableVoices.firstOrNull { it.locale.language == targetLocale.language }
                    VoiceTone.CINEMATIC_THRILLER -> availableVoices.firstOrNull {
                        it.locale.language == targetLocale.language && it.quality >= Voice.QUALITY_HIGH
                    }
                    else -> availableVoices.firstOrNull { it.locale.language == targetLocale.language }
                }
                if (bestVoice != null) {
                    engine.voice = bestVoice
                }
            }
        } catch (e: Exception) {
            Log.w("VoiceoverEngine", "Voice selection fallback: ${e.message}")
        }

        // Apply pitch (e.g. 0.5x - 0.7x for deep horror) and speed (0.7x - 1.2x)
        val finalPitch = (customPitch ?: tone.defaultPitch).coerceIn(0.40f, 1.80f)
        val finalSpeed = (customSpeed ?: tone.defaultSpeed).coerceIn(0.50f, 1.50f)

        engine.setPitch(finalPitch)
        engine.setSpeechRate(finalSpeed)

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "VOICEOVER_TONE_${System.currentTimeMillis()}")
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
        }

        engine.speak(formattedText, TextToSpeech.QUEUE_FLUSH, params, params.getString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID))
    }

    private fun formatExpressiveScript(raw: String, tone: VoiceTone): String {
        val s = raw.trim()
        return when (tone) {
            VoiceTone.DEEP_HORROR -> {
                // Add ominous rhythmic spacing for chilling suspense
                s.replace("...", ", ... , ")
                    .replace(".", "... ")
                    .replace("?", "... ? ")
                    .replace("!", "... ! ")
            }
            VoiceTone.CINEMATIC_THRILLER -> {
                s.replace("...", ", ... ")
                    .replace(".", ", ")
            }
            VoiceTone.DRAMATIC_SHOCKING -> {
                s.replace("!", "! ")
                    .replace("?", "? ")
            }
            VoiceTone.REALISTIC_STORYTELLER -> s
        }
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
        _speechProgress.value = 0f
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
