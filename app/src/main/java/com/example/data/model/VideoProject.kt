package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_projects")
data class VideoProject(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val prompt: String,
    val expandedPrompt: String = "",
    val style: String = "Cinematic Film",
    val aspectRatio: String = "16:9",
    val cameraMotion: String = "Cinematic Orbit",
    val lighting: String = "Golden Hour",
    val durationSeconds: Int = 10,
    val fps: Int = 30,
    val motionIntensity: Float = 0.7f,
    val scenesJson: String = "",
    val audioMood: String = "ambient_synth", // "ambient_synth", "cinematic_drone", "cyber_pulse", "nature_wind", "none"
    val videoUrl: String? = null,
    val isFavorite: Boolean = false,
    val sourceType: String = "text", // "text" or "image"
    val sourceImageUri: String? = null,
    val resolution: String = "1080p",
    val hasVoiceover: Boolean = false,
    val voiceProfile: String = "adam",
    val voiceScript: String = "",
    val isExported: Boolean = false,
    val exportedFilePath: String? = null,
    val createdAt: Long = System.currentTimeMillis(),

    // Creative Editing Suite Extensions (PulseCraft Studio)
    val backgroundMediaUri: String? = null,
    val backgroundMediaName: String? = null,
    val overlayMediaUri: String? = null,
    val overlayMediaName: String? = null,
    val overlayScale: Float = 0.42f,
    val overlayPosX: Float = 0.62f,
    val overlayPosY: Float = 0.12f,
    val overlayOpacity: Float = 0.95f,

    // Canvas transforms
    val canvasScale: Float = 1.0f,
    val canvasPanX: Float = 0.0f,
    val canvasPanY: Float = 0.0f,
    val canvasRotation: Float = 0.0f,

    // Filter & Transitions
    val filterGrading: String = "None", // "None", "Teal & Orange", "Moody Monochrome", "Cyberpunk Neon", "Vintage VHS"
    val transitionEffect: String = "Fade", // "None", "Fade", "Whip Pan", "Zoom-in Punch", "Glitch Warp", "RGB Split"

    // Beat Detection & Flow Sync
    val bpm: Int = 120,
    val playbackSpeed: Float = 1.0f, // 0.5x to 3.0x
    val snapToTransients: Boolean = true,

    // Auto-Captions
    val hasCaptions: Boolean = true,
    val captionStyle: String = "karaoke", // "karaoke", "word_pop", "bold_cinematic"
    val captionsJson: String = "",

    // Audio Editing Suite
    val bgVolume: Float = 0.85f,
    val voiceoverVolume: Float = 1.0f,
    val aiVoiceVolume: Float = 1.0f,
    val fadeInDuration: Float = 0.5f,
    val fadeOutDuration: Float = 0.8f,
    val pitchSemitones: Int = 0, // -12 to +12
    val equalizerPreset: String = "Studio Warmth", // "Flat", "Bass Boost", "Vocal Clarity", "Studio Warmth"
    val noiseSuppression: Boolean = true,
    val recordedVoiceUri: String? = null,
    val recordedDurationSec: Float = 0f
) {
    fun getScenes(): List<SceneShot> {
        return SceneShot.parseList(scenesJson)
    }

    fun getCaptions(): List<CaptionSegment> {
        val parsed = CaptionSegment.parseList(captionsJson)
        return if (parsed.isNotEmpty()) parsed else CaptionSegment.generateDefaultCaptions(durationSeconds, prompt)
    }
}
