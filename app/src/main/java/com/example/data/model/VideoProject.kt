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
    val durationSeconds: Int = 8,
    val fps: Int = 30,
    val motionIntensity: Float = 0.7f,
    val scenesJson: String = "",
    val audioMood: String = "ambient_synth", // "ambient_synth", "cinematic_drone", "cyber_pulse", "none"
    val videoUrl: String? = null,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getScenes(): List<SceneShot> {
        return SceneShot.parseList(scenesJson)
    }
}
