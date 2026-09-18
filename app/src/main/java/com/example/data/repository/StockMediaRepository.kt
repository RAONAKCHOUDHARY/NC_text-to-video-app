package com.example.data.repository

import com.example.data.model.StockMediaItem
import com.example.data.model.StockMediaType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class StockMediaRepository {
    private val _downloadedIds = MutableStateFlow<Set<String>>(emptySet())
    val downloadedIds: Flow<Set<String>> = _downloadedIds.asStateFlow()

    private val stockItems = listOf(
        // Videos
        StockMediaItem(
            id = "vid_1",
            title = "Alpine Mountain Drone Flyover",
            type = StockMediaType.VIDEO,
            category = "Nature",
            tags = listOf("drone", "mountain", "snow", "forest", "4k", "cinematic"),
            durationSeconds = 15,
            resolution = "4K 60FPS",
            aspectRatio = "16:9",
            fileSizeMb = 48.5f,
            author = "SkyHigh Footage",
            downloadUrl = "https://assets.mixkit.co/videos/preview/mountain-drone.mp4",
            previewGradientHexes = listOf("#38BDF8", "#0369A1", "#082F49")
        ),
        StockMediaItem(
            id = "vid_2",
            title = "Neo Cyberpunk Rain Alley",
            type = StockMediaType.VIDEO,
            category = "Sci-Fi",
            tags = listOf("cyberpunk", "rain", "neon", "tokyo", "futuristic", "night"),
            durationSeconds = 12,
            resolution = "4K 30FPS",
            aspectRatio = "16:9",
            fileSizeMb = 36.2f,
            author = "NeonPulse Media",
            downloadUrl = "https://assets.mixkit.co/videos/preview/cyber-city.mp4",
            previewGradientHexes = listOf("#F43F5E", "#8B5CF6", "#090C15")
        ),
        StockMediaItem(
            id = "vid_3",
            title = "Golden Hour Ocean Breakers",
            type = StockMediaType.VIDEO,
            category = "Landscape",
            tags = listOf("ocean", "waves", "sunset", "golden hour", "beach", "summer"),
            durationSeconds = 20,
            resolution = "4K 60FPS",
            aspectRatio = "16:9",
            fileSizeMb = 62.0f,
            author = "Coastal Horizons",
            downloadUrl = "https://assets.mixkit.co/videos/preview/ocean-waves.mp4",
            previewGradientHexes = listOf("#F59E0B", "#D97706", "#78350F")
        ),
        StockMediaItem(
            id = "vid_4",
            title = "Cosmic Nebula Birth & Galaxy Core",
            type = StockMediaType.VIDEO,
            category = "Space",
            tags = listOf("space", "nebula", "stars", "cosmos", "galaxy", "nasa"),
            durationSeconds = 30,
            resolution = "4K 60FPS",
            aspectRatio = "9:16",
            fileSizeMb = 84.5f,
            author = "AstroVision Lab",
            downloadUrl = "https://assets.mixkit.co/videos/preview/deep-space.mp4",
            previewGradientHexes = listOf("#A855F7", "#EC4899", "#0B0E17")
        ),
        StockMediaItem(
            id = "vid_5",
            title = "Time-lapse Tokyo Crossing",
            type = StockMediaType.VIDEO,
            category = "Urban",
            tags = listOf("tokyo", "timelapse", "city", "lights", "speed", "traffic"),
            durationSeconds = 10,
            resolution = "1080p 60FPS",
            aspectRatio = "9:16",
            fileSizeMb = 24.0f,
            author = "UrbanTime Studios",
            downloadUrl = "https://assets.mixkit.co/videos/preview/tokyo-traffic.mp4",
            previewGradientHexes = listOf("#00E5FF", "#3B82F6", "#0F172A")
        ),

        // Photos
        StockMediaItem(
            id = "img_1",
            title = "Bioluminescent Rainforest Flora",
            type = StockMediaType.PHOTO,
            category = "Nature",
            tags = listOf("bioluminescent", "forest", "glow", "leaves", "plants", "macro"),
            resolution = "6000x4000 8K",
            aspectRatio = "16:9",
            fileSizeMb = 14.8f,
            author = "BioGlow Archive",
            downloadUrl = "https://images.unsplash.com/photo-bioluminescent-forest",
            previewGradientHexes = listOf("#10B981", "#059669", "#022C22")
        ),
        StockMediaItem(
            id = "img_2",
            title = "Futuristic Glass Skyscraper Apex",
            type = StockMediaType.PHOTO,
            category = "Architecture",
            tags = listOf("architecture", "modern", "skyscraper", "glass", "future"),
            resolution = "5400x3600 4K",
            aspectRatio = "9:16",
            fileSizeMb = 12.1f,
            author = "Metropolis Arch",
            downloadUrl = "https://images.unsplash.com/photo-modern-architecture",
            previewGradientHexes = listOf("#38BDF8", "#6366F1", "#1E1B4B")
        ),
        StockMediaItem(
            id = "img_3",
            title = "Desert Dunes Milky Way Core",
            type = StockMediaType.PHOTO,
            category = "Landscape",
            tags = listOf("desert", "sand", "milky way", "night", "astrophotography"),
            resolution = "8000x5000 8K",
            aspectRatio = "16:9",
            fileSizeMb = 22.4f,
            author = "Desert Skies",
            downloadUrl = "https://images.unsplash.com/photo-desert-stars",
            previewGradientHexes = listOf("#F59E0B", "#7C3AED", "#0B0F19")
        ),

        // Audio & Music Tracks
        StockMediaItem(
            id = "aud_1",
            title = "Horizon of Time (Cinematic Trailer)",
            type = StockMediaType.AUDIO,
            category = "Cinematic",
            tags = listOf("trailer", "orchestral", "epic", "strings", "brass", "dramatic"),
            durationSeconds = 145,
            resolution = "Lossless WAV 24-bit",
            fileSizeMb = 28.0f,
            author = "Epic Audio Labs",
            downloadUrl = "https://assets.mixkit.co/music/preview/epic-cinematic.mp3",
            previewGradientHexes = listOf("#F59E0B", "#EF4444", "#18181B"),
            audioMood = "cinematic_drone"
        ),
        StockMediaItem(
            id = "aud_2",
            title = "Cyber Odyssey (Dark Synthwave)",
            type = StockMediaType.AUDIO,
            category = "Electronic",
            tags = listOf("synthwave", "cyberpunk", "retro", "bass", "arpeggio", "night"),
            durationSeconds = 120,
            resolution = "320kbps MP3",
            fileSizeMb = 9.8f,
            author = "Waveform 88",
            downloadUrl = "https://assets.mixkit.co/music/preview/cyber-pulse.mp3",
            previewGradientHexes = listOf("#00E5FF", "#8B5CF6", "#0F172A"),
            audioMood = "cyber_pulse"
        ),
        StockMediaItem(
            id = "aud_3",
            title = "Serene Forest Rainfall & Wind",
            type = StockMediaType.AUDIO,
            category = "Ambient FX",
            tags = listOf("rain", "nature", "meditation", "calm", "wind", "relaxation"),
            durationSeconds = 180,
            resolution = "320kbps MP3",
            fileSizeMb = 12.5f,
            author = "Nature Echoes",
            downloadUrl = "https://assets.mixkit.co/music/preview/nature-rain.mp3",
            previewGradientHexes = listOf("#10B981", "#38BDF8", "#022C22"),
            audioMood = "nature_wind"
        ),
        StockMediaItem(
            id = "aud_4",
            title = "Warm Analog Synth Reflections",
            type = StockMediaType.AUDIO,
            category = "Lo-Fi / Chill",
            tags = listOf("synth", "warm", "peaceful", "study", "relaxing", "pad"),
            durationSeconds = 160,
            resolution = "320kbps MP3",
            fileSizeMb = 11.2f,
            author = "Analog Dreams",
            downloadUrl = "https://assets.mixkit.co/music/preview/warm-synth.mp3",
            previewGradientHexes = listOf("#8B5CF6", "#F43F5E", "#111827"),
            audioMood = "ambient_synth"
        )
    )

    fun searchMedia(query: String, filterType: StockMediaType?): List<StockMediaItem> {
        val q = query.trim().lowercase()
        return stockItems.filter { item ->
            val matchesType = filterType == null || item.type == filterType
            val matchesQuery = if (q.isBlank()) true else {
                item.title.lowercase().contains(q) ||
                        item.category.lowercase().contains(q) ||
                        item.tags.any { it.lowercase().contains(q) } ||
                        item.author.lowercase().contains(q)
            }
            matchesType && matchesQuery
        }
    }

    fun markDownloaded(id: String) {
        val set = _downloadedIds.value.toMutableSet()
        set.add(id)
        _downloadedIds.value = set
    }
}
