package com.example.data.model

import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class CaptionSegment(
    val id: String = UUID.randomUUID().toString(),
    val startMs: Long,
    val endMs: Long,
    val text: String
) {
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("id", id)
            put("startMs", startMs)
            put("endMs", endMs)
            put("text", text)
        }
    }

    companion object {
        fun fromJson(json: JSONObject): CaptionSegment {
            return CaptionSegment(
                id = json.optString("id", UUID.randomUUID().toString()),
                startMs = json.optLong("startMs", 0),
                endMs = json.optLong("endMs", 2000),
                text = json.optString("text", "")
            )
        }

        fun parseList(jsonString: String): List<CaptionSegment> {
            if (jsonString.isBlank()) return emptyList()
            return try {
                val array = JSONArray(jsonString)
                val list = mutableListOf<CaptionSegment>()
                for (i in 0 until array.length()) {
                    list.add(fromJson(array.getJSONObject(i)))
                }
                list
            } catch (e: Exception) {
                emptyList()
            }
        }

        fun toJsonArrayString(list: List<CaptionSegment>): String {
            val array = JSONArray()
            list.forEach { array.put(it.toJson()) }
            return array.toString()
        }

        fun generateDefaultCaptions(durationSec: Int, topic: String): List<CaptionSegment> {
            val phrases = when {
                topic.contains("hindi", ignoreCase = true) -> listOf(
                    "नमस्ते दोस्तों, PulseCraft Studio में आपका स्वागत है!",
                    "हर एक फ्रेम में छिपा है एक नया सिनेमाई जादू।",
                    "रिदम और बीट्स के साथ अपनी कहानी को जीवंत बनाएं।",
                    "अल्ट्रा-फास्ट फ्लो और वाइब्रेंट कलर्स का संगम।"
                )
                topic.contains("cyber", ignoreCase = true) || topic.contains("rain", ignoreCase = true) -> listOf(
                    "Neon spires pierce through the velvet midnight.",
                    "Raindrops mirror the holographic pulse of Neo-Tokyo.",
                    "Synthesizers awaken the hidden city beneath the clouds.",
                    "Every transient cut moves in perfect harmonic sync."
                )
                else -> listOf(
                    "Welcome to PulseCraft Studio creative suite.",
                    "Crafting dynamic visual poetry frame by frame.",
                    "Harmonizing beats, rhythm transients, and cinematic light.",
                    "Exporting ultra-crisp motion ready for the world."
                )
            }

            val stepMs = (durationSec * 1000L) / phrases.size.coerceAtLeast(1)
            return phrases.mapIndexed { index, phrase ->
                val start = index * stepMs
                val end = (index + 1) * stepMs - 200L
                CaptionSegment(startMs = start, endMs = end, text = phrase)
            }
        }
    }
}

enum class CaptionStyle(val displayName: String, val description: String) {
    KARAOKE("Karaoke Highlight", "Glowing highlight tracks each active word"),
    WORD_POP("Word-by-Word Pop", "Kinetic bounce pop-in for punchy pacing"),
    BOLD_CINEMATIC("Bold Cinematic", "Letterboxed lower-third with sleek typography")
}

enum class FilterGrading(val displayName: String, val subtitle: String) {
    NONE("Normal Direct", "Natural raw color grading"),
    TEAL_ORANGE("Teal & Orange", "Hollywood contrast, warm skin & cyan skies"),
    MOODY_MONO("Moody Monochrome", "Film noir deep blacks with subtle grain"),
    CYBERPUNK_NEON("Cyberpunk Neon", "High-voltage electric violet & cyan glow"),
    VINTAGE_VHS("Vintage VHS", "Analog phosphor scanlines with tape warmth")
}

enum class TransitionType(val displayName: String, val durationMs: Long) {
    NONE("Cut (Direct)", 0L),
    FADE("Smooth Fade", 450L),
    WHIP_PAN("Whip Pan Snap", 350L),
    ZOOM_PUNCH("Zoom Punch", 300L),
    GLITCH_WARP("Glitch Warp", 400L),
    RGB_SPLIT("RGB Chromatic Split", 350L)
}

enum class EqualizerPreset(val displayName: String, val description: String) {
    FLAT("Flat / Direct", "Neutral transparent frequency curve"),
    BASS_BOOST("Bass Boost +6dB", "Punchy sub-bass & low-end thump"),
    VOCAL_CLARITY("Vocal Clarity", "Accentuates 2-4kHz speech presence"),
    STUDIO_WARMTH("Studio Warmth", "Analog tape saturation & smooth highs")
}

data class BeatTransient(
    val timestampMs: Long,
    val intensity: Float, // 0.0 to 1.0
    val isMajorDownbeat: Boolean
)
