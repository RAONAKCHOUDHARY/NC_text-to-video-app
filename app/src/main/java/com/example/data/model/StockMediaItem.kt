package com.example.data.model

enum class StockMediaType(val label: String) {
    VIDEO("Video"),
    PHOTO("Photo"),
    AUDIO("Music & FX")
}

data class StockMediaItem(
    val id: String,
    val title: String,
    val type: StockMediaType,
    val category: String,
    val tags: List<String>,
    val durationSeconds: Int = 0, // For video/audio
    val resolution: String = "4K UHD",
    val aspectRatio: String = "16:9",
    val fileSizeMb: Float,
    val author: String = "Studio Creative Commons",
    val license: String = "CC0 Public Domain • Free Commercial",
    val downloadUrl: String,
    val previewGradientHexes: List<String> = listOf("#00E5FF", "#8B5CF6", "#090C15"),
    val audioMood: String? = null,
    val isDownloaded: Boolean = false
)
