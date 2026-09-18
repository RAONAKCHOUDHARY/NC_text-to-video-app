package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.SceneShot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiVideoService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    /**
     * Enhances a user's short prompt into a rich cinematic prompt with camera, lighting, and style cues.
     */
    suspend fun enhancePrompt(
        prompt: String,
        style: String,
        cameraMotion: String,
        lighting: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Intelligent fallback generator
            return@withContext Result.success(
                generateLocalEnhancedPrompt(prompt, style, cameraMotion, lighting)
            )
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val systemPrompt = "You are an elite Hollywood cinematographer and AI video prompt engineer. " +
                    "Expand the user's prompt into a vivid, single-paragraph cinematic text-to-video prompt. " +
                    "Incorporate style: '$style', camera motion: '$cameraMotion', and lighting: '$lighting'. " +
                    "Specify lens choice (e.g. 35mm anamorphic), depth of field, atmosphere, volumetric lighting, and physical movement. " +
                    "Return ONLY the enhanced prompt string without markdown fences, quotes, or introductions."

            val bodyJson = JSONObject().apply {
                val contents = JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", "Expand this text-to-video prompt: $prompt"))
                        })
                    })
                }
                put("contents", contents)
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", systemPrompt))
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("maxOutputTokens", 500)
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(bodyJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.success(generateLocalEnhancedPrompt(prompt, style, cameraMotion, lighting))
            }

            val responseBody = response.body?.string() ?: ""
            val json = JSONObject(responseBody)
            val candidates = json.optJSONArray("candidates")
            val text = candidates?.optJSONObject(0)
                ?.optJSONObject("content")
                ?.optJSONArray("parts")
                ?.optJSONObject(0)
                ?.optString("text")

            if (!text.isNullOrBlank()) {
                Result.success(text.trim())
            } else {
                Result.success(generateLocalEnhancedPrompt(prompt, style, cameraMotion, lighting))
            }
        } catch (e: Exception) {
            Log.e("GeminiVideoService", "Enhance prompt error: ${e.message}")
            Result.success(generateLocalEnhancedPrompt(prompt, style, cameraMotion, lighting))
        }
    }

    /**
     * Generates a multi-scene director storyboard breakdown for the video.
     */
    suspend fun generateStoryboard(
        prompt: String,
        style: String,
        cameraMotion: String,
        lighting: String,
        totalDuration: Int
    ): Result<List<SceneShot>> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.success(generateLocalScenes(prompt, style, cameraMotion, lighting, totalDuration))
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val promptText = """
                You are a director choreographing a text-to-video sequence.
                Break down this video prompt into 3 sequential shots/scenes for a total duration of $totalDuration seconds:
                Prompt: "$prompt"
                Style: "$style"
                Camera Motion: "$cameraMotion"
                Lighting: "$lighting"

                Respond with a valid JSON array of objects with keys:
                - "sceneNumber": integer (1, 2, 3)
                - "title": short scene title
                - "shotType": e.g. "Wide Establishing Shot", "Medium Tracking Shot", "Dramatic Close-Up"
                - "visualDescription": detailed 1-2 sentence visual description
                - "cameraMovement": camera direction and speed
                - "lightingMood": lighting description
                - "durationSeconds": float (sum should equal $totalDuration)
                - "colors": array of 3 hex colors (e.g. ["#00E5FF", "#8B5CF6", "#090C15"])
                - "focalSubject": main subject in frame

                Return ONLY the raw JSON array.
            """.trimIndent()

            val bodyJson = JSONObject().apply {
                val contents = JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", promptText))
                        })
                    })
                }
                put("contents", contents)
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.6)
                    put("responseMimeType", "application/json")
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(bodyJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.success(generateLocalScenes(prompt, style, cameraMotion, lighting, totalDuration))
            }

            val responseBody = response.body?.string() ?: ""
            val json = JSONObject(responseBody)
            val candidates = json.optJSONArray("candidates")
            val rawText = candidates?.optJSONObject(0)
                ?.optJSONObject("content")
                ?.optJSONArray("parts")
                ?.optJSONObject(0)
                ?.optString("text") ?: ""

            val cleaned = cleanJsonString(rawText)
            val scenes = SceneShot.parseList(cleaned)
            if (scenes.isNotEmpty()) {
                Result.success(scenes)
            } else {
                Result.success(generateLocalScenes(prompt, style, cameraMotion, lighting, totalDuration))
            }
        } catch (e: Exception) {
            Log.e("GeminiVideoService", "Storyboard error: ${e.message}")
            Result.success(generateLocalScenes(prompt, style, cameraMotion, lighting, totalDuration))
        }
    }

    /**
     * Request video generation via Veo (veo-3.1-fast-generate-preview).
     */
    suspend fun requestVeoVideo(
        prompt: String,
        aspectRatio: String = "16:9"
    ): Result<String?> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.success(null)
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/veo-3.1-fast-generate-preview:generateVideos?key=$apiKey"
            val bodyJson = JSONObject().apply {
                put("prompt", prompt)
                put("config", JSONObject().apply {
                    put("numberOfVideos", 1)
                    put("resolution", "720p")
                    put("aspectRatio", aspectRatio)
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(bodyJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val respBody = response.body?.string() ?: ""
                val obj = JSONObject(respBody)
                val opName = if (obj.has("name")) obj.getString("name") else null
                Result.success(opName)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Log.w("GeminiVideoService", "Veo API request notice: ${e.message}")
            Result.success(null)
        }
    }

    private fun cleanJsonString(text: String): String {
        var trimmed = text.trim()
        if (trimmed.startsWith("```json")) {
            trimmed = trimmed.removePrefix("```json").trim()
        }
        if (trimmed.startsWith("```")) {
            trimmed = trimmed.removePrefix("```").trim()
        }
        if (trimmed.endsWith("```")) {
            trimmed = trimmed.removeSuffix("```").trim()
        }
        return trimmed
    }

    private fun generateLocalEnhancedPrompt(
        prompt: String,
        style: String,
        cameraMotion: String,
        lighting: String
    ): String {
        val styleModifier = when (style) {
            "3D Anime Studio" -> "cel-shaded 3D anime cinematic render, sharp contour outlines, dramatic high-key rim lighting, dynamic anime keyframing, stylized particle effects"
            "Indian Cartoon 3D Style" -> "vibrant Indian 3D animation style with expressive stylized proportions, colorful festival palettes, animated town street atmosphere, playful energetic dynamics"
            "Cyberpunk" -> "hyper-detailed futuristic cityscape drenched in rain, neon holographic signs, chromatic aberration, reflections on wet asphalt"
            "Anime" -> "vibrant Makoto Shinkai style, hand-drawn aesthetic, dramatic sky gradients, floating dust motes, emotive character framing"
            "Studio Ghibli" -> "whimsical storybook watercolor art, lush rolling green meadows, fluffy billowing clouds, gentle nostalgic warmth"
            "Sci-Fi" -> "epic interstellar scale, pristine spaceship architecture, bioluminescent alien planetary rings, 8k anamorphic lens flare"
            "Nature Documentary" -> "National Geographic 8K cinema camera, extreme macro details, natural shallow depth of field, crystal clear atmospheric clarity"
            "Film Noir" -> "35mm black and white monochrome, heavy chiaroscuro shadow play, Venetian blind silhouettes, moody cigarette smoke"
            else -> "masterpiece 35mm Panavision cinema film, rich color grading, natural film grain, shallow depth of field"
        }

        return "Cinematic shot of $prompt. Rendered in $style style with $lighting lighting. Camera executes a smooth $cameraMotion with steady framing. $styleModifier, volumetric atmosphere, 24fps motion blur, ultra-high fidelity."
    }

    fun generateLocalScenes(
        prompt: String,
        style: String,
        cameraMotion: String,
        lighting: String,
        totalDuration: Int
    ): List<SceneShot> {
        val shotDuration = (totalDuration / 3.0f)
        val styleColors = when (style) {
            "Cyberpunk" -> listOf("#00F2FE", "#7F39FB", "#0F172A")
            "Anime" -> listOf("#38BDF8", "#F472B6", "#1E1B4B")
            "Studio Ghibli" -> listOf("#4ADE80", "#FBBF24", "#064E3B")
            "Sci-Fi" -> listOf("#06B6D4", "#6366F1", "#020617")
            "Nature Documentary" -> listOf("#10B981", "#EAB308", "#14532D")
            "Film Noir" -> listOf("#E2E8F0", "#64748B", "#020617")
            else -> listOf("#F59E0B", "#8B5CF6", "#0F172A")
        }

        return listOf(
            SceneShot(
                sceneNumber = 1,
                title = "Shot 1: The Awakening",
                shotType = "Wide Establishing Shot",
                visualDescription = "Expansive vista establishing the world: $prompt bathed in soft $lighting illumination.",
                cameraMovement = "$cameraMotion (Slow Drift)",
                lightingMood = lighting,
                durationSeconds = shotDuration,
                colorHexes = styleColors,
                focalSubject = "Horizon & Atmosphere"
            ),
            SceneShot(
                sceneNumber = 2,
                title = "Shot 2: Focus & Motion",
                shotType = "Medium Cinematic Tracking",
                visualDescription = "Moving dynamically closer to the heart of the action, capturing vivid textures and organic motion.",
                cameraMovement = "Dynamic Tracking with Subtle Orbit",
                lightingMood = "$lighting Highlights",
                durationSeconds = shotDuration,
                colorHexes = styleColors.reversed(),
                focalSubject = "Primary Subject"
            ),
            SceneShot(
                sceneNumber = 3,
                title = "Shot 3: Climax & Reveal",
                shotType = "Dramatic Low-Angle Close-up",
                visualDescription = "Intense atmospheric reveal highlighting fine details, volumetric dust, and cinematic depth.",
                cameraMovement = "Slow Dolly Zoom & Rising Tilt",
                lightingMood = "High Contrast $lighting",
                durationSeconds = shotDuration,
                colorHexes = styleColors,
                focalSubject = "Focal Details"
            )
        )
    }
}
