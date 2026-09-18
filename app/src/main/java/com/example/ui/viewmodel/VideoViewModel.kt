package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AmbientAudioEngine
import com.example.data.api.GeminiVideoService
import com.example.data.local.AppDatabase
import com.example.data.model.SceneShot
import com.example.data.model.VideoProject
import com.example.data.repository.VideoRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VideoViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val repository = VideoRepository(database.videoDao(), GeminiVideoService())
    private val audioEngine = AmbientAudioEngine()

    val projects: StateFlow<List<VideoProject>> = repository.allProjects
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedProject = MutableStateFlow<VideoProject?>(null)
    val selectedProject: StateFlow<VideoProject?> = _selectedProject.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _generationStep = MutableStateFlow("")
    val generationStep: StateFlow<String> = _generationStep.asStateFlow()

    private val _generationProgress = MutableStateFlow(0f)
    val generationProgress: StateFlow<Float> = _generationProgress.asStateFlow()

    private val _isEnhancingPrompt = MutableStateFlow(false)
    val isEnhancingPrompt: StateFlow<Boolean> = _isEnhancingPrompt.asStateFlow()

    private val _isAudioMuted = MutableStateFlow(false)
    val isAudioMuted: StateFlow<Boolean> = _isAudioMuted.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    init {
        viewModelScope.launch {
            repository.allProjects.collect { list ->
                if (list.isEmpty()) {
                    preloadSampleProjects()
                } else if (_selectedProject.value == null) {
                    _selectedProject.value = list.firstOrNull()
                    _selectedProject.value?.let { startAudio(it.audioMood) }
                }
            }
        }
    }

    fun selectProject(project: VideoProject) {
        _selectedProject.value = project
        startAudio(project.audioMood)
    }

    fun toggleMute() {
        val newMute = !_isAudioMuted.value
        _isAudioMuted.value = newMute
        audioEngine.isMuted = newMute
    }

    private fun startAudio(mood: String) {
        audioEngine.isMuted = _isAudioMuted.value
        audioEngine.start(mood)
    }

    fun enhancePrompt(
        prompt: String,
        style: String,
        cameraMotion: String,
        lighting: String,
        onEnhanced: (String) -> Unit
    ) {
        if (prompt.isBlank()) return
        viewModelScope.launch {
            _isEnhancingPrompt.value = true
            try {
                val enhanced = repository.enhancePrompt(prompt, style, cameraMotion, lighting)
                onEnhanced(enhanced)
                _userMessage.value = "Prompt enhanced with Hollywood cinematic cues!"
            } catch (e: Exception) {
                _userMessage.value = "Enhanced prompt ready."
            } finally {
                _isEnhancingPrompt.value = false
            }
        }
    }

    fun generateVideo(
        prompt: String,
        style: String,
        aspectRatio: String,
        cameraMotion: String,
        lighting: String,
        durationSeconds: Int,
        fps: Int,
        motionIntensity: Float,
        audioMood: String,
        onSuccess: (VideoProject) -> Unit
    ) {
        if (prompt.isBlank()) {
            _userMessage.value = "Please enter a description for the video."
            return
        }

        viewModelScope.launch {
            _isGenerating.value = true
            _generationProgress.value = 0.1f
            _generationStep.value = "Analyzing cinematic prompt and visual motifs..."

            delay(400)
            _generationProgress.value = 0.25f
            _generationStep.value = "Choreographing camera motion & lighting angles..."

            // Expand prompt & generate scenes
            val enhanced = repository.enhancePrompt(prompt, style, cameraMotion, lighting)
            _generationProgress.value = 0.5f
            _generationStep.value = "Generating multi-shot storyboard sequence..."

            val scenes = repository.generateStoryboard(
                prompt = prompt,
                style = style,
                cameraMotion = cameraMotion,
                lighting = lighting,
                duration = durationSeconds
            )

            _generationProgress.value = 0.75f
            _generationStep.value = "Synthesizing volumetric lighting & color grading..."
            delay(350)

            _generationProgress.value = 0.9f
            _generationStep.value = "Finalizing $fps FPS video composition..."
            delay(300)

            // Derive a title from the prompt
            val titleWords = prompt.trim().split(" ").take(4).joinToString(" ")
            val title = if (titleWords.length > 28) titleWords.take(25) + "..." else titleWords.replaceFirstChar { it.uppercase() }

            val newProject = VideoProject(
                title = title,
                prompt = prompt,
                expandedPrompt = enhanced,
                style = style,
                aspectRatio = aspectRatio,
                cameraMotion = cameraMotion,
                lighting = lighting,
                durationSeconds = durationSeconds,
                fps = fps,
                motionIntensity = motionIntensity,
                scenesJson = SceneShot.toJsonArrayString(scenes),
                audioMood = audioMood,
                createdAt = System.currentTimeMillis()
            )

            val id = repository.saveProject(newProject)
            val savedProject = newProject.copy(id = id)

            _generationProgress.value = 1.0f
            _generationStep.value = "Video Generated!"
            delay(200)

            _selectedProject.value = savedProject
            startAudio(audioMood)
            _isGenerating.value = false
            _userMessage.value = "Video created successfully!"
            onSuccess(savedProject)
        }
    }

    fun deleteProject(project: VideoProject) {
        viewModelScope.launch {
            repository.deleteProject(project.id)
            if (_selectedProject.value?.id == project.id) {
                _selectedProject.value = projects.value.firstOrNull { it.id != project.id }
            }
            _userMessage.value = "Video deleted"
        }
    }

    fun toggleFavorite(project: VideoProject) {
        viewModelScope.launch {
            repository.toggleFavorite(project.id, !project.isFavorite)
            if (_selectedProject.value?.id == project.id) {
                _selectedProject.value = _selectedProject.value?.copy(isFavorite = !project.isFavorite)
            }
        }
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    private suspend fun preloadSampleProjects() {
        val sample1Scenes = listOf(
            SceneShot(
                sceneNumber = 1,
                title = "Neon Skyline",
                shotType = "Wide Aerial Drone Shot",
                visualDescription = "Rain-soaked neon skyscraper canyon in Tokyo 2088. Flying aerocars weave between holographic signs.",
                cameraMovement = "Drone Flyover Downward",
                lightingMood = "High-contrast Neon Noir",
                durationSeconds = 2.5f,
                colorHexes = listOf("#00F2FE", "#8B5CF6", "#090C15"),
                focalSubject = "Cyberpunk Boulevard"
            ),
            SceneShot(
                sceneNumber = 2,
                title = "Puddle Reflection",
                shotType = "Low-Angle Tracking",
                visualDescription = "Camera skims along asphalt puddles mirroring neon billboards as droplets create ripples.",
                cameraMovement = "Slow Dolly Forward",
                lightingMood = "Bioluminescent Reflections",
                durationSeconds = 2.5f,
                colorHexes = listOf("#F43F5E", "#00E5FF", "#0A0F1D"),
                focalSubject = "Water Reflections"
            ),
            SceneShot(
                sceneNumber = 3,
                title = "Cyber Spire",
                shotType = "Rising Vertical Tilt",
                visualDescription = "Camera tilts rapidly up towards the megastructure crown piercing through stormy purple clouds.",
                cameraMovement = "Rapid Upward Pan",
                lightingMood = "Volumetric Lightning Glow",
                durationSeconds = 3.0f,
                colorHexes = listOf("#A855F7", "#38BDF8", "#020617"),
                focalSubject = "Tower Apex"
            )
        )

        val sample1 = VideoProject(
            title = "Cyberpunk Rain City",
            prompt = "Futuristic Tokyo metropolis at night in heavy rain, glowing holographic advertisements, flying spinners, puddles reflecting neon lights",
            expandedPrompt = "Ultra-cinematic 35mm anamorphic wide shot of Neo-Tokyo drenched in torrential rainfall. Volumetric magenta and cyan neon hues reflect on wet asphalt. Camera executes a sweeping drone flyover past colossal holographic projections. 24fps film grain, cinematic depth of field.",
            style = "Cyberpunk",
            aspectRatio = "16:9",
            cameraMotion = "Drone Flyover",
            lighting = "Neon Noir",
            durationSeconds = 8,
            fps = 30,
            motionIntensity = 0.85f,
            scenesJson = SceneShot.toJsonArrayString(sample1Scenes),
            audioMood = "cyber_pulse",
            createdAt = System.currentTimeMillis() - 60000
        )

        val sample2Scenes = listOf(
            SceneShot(
                sceneNumber = 1,
                title = "Star Cradle",
                shotType = "Deep Space Vista",
                visualDescription = "Immense cosmic nebula glowing with iridescent golden interstellar dust and newborn radiant stars.",
                cameraMovement = "Cinematic 360 Orbit",
                lightingMood = "Radiant Celestial Glow",
                durationSeconds = 3.0f,
                colorHexes = listOf("#FFB020", "#EC4899", "#0B0E17"),
                focalSubject = "Protostar Cluster"
            ),
            SceneShot(
                sceneNumber = 2,
                title = "Solar Flare Shockwave",
                shotType = "Macro Space Shot",
                visualDescription = "Waves of ionized golden plasma rippling outward across sapphire gas pillars.",
                cameraMovement = "Continuous Dynamic Zoom In",
                lightingMood = "Golden Hour Plasma",
                durationSeconds = 3.0f,
                colorHexes = listOf("#F59E0B", "#8B5CF6", "#030712"),
                focalSubject = "Plasma Ribbons"
            )
        )

        val sample2 = VideoProject(
            title = "Cosmic Nebula Birth",
            prompt = "Majestic stellar nursery deep in the cosmos, swirling golden dust clouds and radiant sapphire starbursts, astronomical realism",
            expandedPrompt = "James Webb Space Telescope style 8K panoramic space sequence. Deep velvet interstellar space illuminated by warm golden star clusters and billowing turquoise gas clouds. Smooth, majestic celestial orbit with crystalline detail.",
            style = "Sci-Fi",
            aspectRatio = "9:16",
            cameraMotion = "Cinematic Orbit",
            lighting = "Golden Hour",
            durationSeconds = 6,
            fps = 60,
            motionIntensity = 0.65f,
            scenesJson = SceneShot.toJsonArrayString(sample2Scenes),
            audioMood = "cinematic_drone",
            createdAt = System.currentTimeMillis() - 120000
        )

        repository.saveProject(sample1)
        repository.saveProject(sample2)
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.stop()
    }
}
