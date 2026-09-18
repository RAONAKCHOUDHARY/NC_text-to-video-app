package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AmbientAudioEngine
import com.example.audio.BeatFlowEngine
import com.example.audio.MicrophoneRecorder
import com.example.audio.VoiceProfile
import com.example.audio.VoiceTone
import com.example.audio.VoiceoverEngine
import com.example.data.api.GeminiVideoService
import com.example.data.preferences.ApiKeyManager
import com.example.data.local.AppDatabase
import com.example.data.model.BeatTransient
import com.example.data.model.CaptionSegment
import com.example.data.model.EqualizerPreset
import com.example.data.model.SceneShot
import com.example.data.model.StockMediaItem
import com.example.data.model.StockMediaType
import com.example.data.model.VideoProject
import com.example.data.repository.StockMediaRepository
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
    val voiceoverEngine = VoiceoverEngine(application)
    val micRecorder = MicrophoneRecorder(application)
    private val stockRepository = StockMediaRepository()

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

    private val _isGeneratingScript = MutableStateFlow(false)
    val isGeneratingScript: StateFlow<Boolean> = _isGeneratingScript.asStateFlow()

    private val _isAudioMuted = MutableStateFlow(false)
    val isAudioMuted: StateFlow<Boolean> = _isAudioMuted.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Beat Transients & Flow
    private val _beatTransients = MutableStateFlow<List<BeatTransient>>(emptyList())
    val beatTransients: StateFlow<List<BeatTransient>> = _beatTransients.asStateFlow()

    private val _waveformAmplitudes = MutableStateFlow<List<Float>>(emptyList())
    val waveformAmplitudes: StateFlow<List<Float>> = _waveformAmplitudes.asStateFlow()

    // Stock Media State
    private val _stockSearchQuery = MutableStateFlow("")
    val stockSearchQuery: StateFlow<String> = _stockSearchQuery.asStateFlow()

    private val _stockCategory = MutableStateFlow<StockMediaType?>(null)
    val stockCategory: StateFlow<StockMediaType?> = _stockCategory.asStateFlow()

    private val _stockItems = MutableStateFlow<List<StockMediaItem>>(stockRepository.searchMedia("", null))
    val stockItems: StateFlow<List<StockMediaItem>> = _stockItems.asStateFlow()

    // Dynamic Gemini API Key persistence state
    private val _geminiApiKey = MutableStateFlow(ApiKeyManager.getApiKey(application))
    val geminiApiKey: StateFlow<String> = _geminiApiKey.asStateFlow()

    // Realistic Human-like & Deep Horror Voice Controls
    private val _selectedVoiceTone = MutableStateFlow(VoiceTone.DEEP_HORROR)
    val selectedVoiceTone: StateFlow<VoiceTone> = _selectedVoiceTone.asStateFlow()

    private val _voicePitch = MutableStateFlow(0.60f) // extra deep heavy default
    val voicePitch: StateFlow<Float> = _voicePitch.asStateFlow()

    private val _voiceSpeed = MutableStateFlow(0.85f) // suspenseful pacing default
    val voiceSpeed: StateFlow<Float> = _voiceSpeed.asStateFlow()

    init {
        viewModelScope.launch {
            repository.allProjects.collect { list ->
                if (list.isEmpty()) {
                    preloadSampleProjects()
                } else if (_selectedProject.value == null) {
                    val initial = list.firstOrNull()
                    _selectedProject.value = initial
                    initial?.let {
                        recomputeBeatGrid(it.durationSeconds, it.bpm)
                    }
                }
            }
        }
    }

    fun selectProject(project: VideoProject) {
        _selectedProject.value = project
        startAudio(project.audioMood)
        recomputeBeatGrid(project.durationSeconds, project.bpm)
    }

    fun toggleMute() {
        val newMute = !_isAudioMuted.value
        _isAudioMuted.value = newMute
        audioEngine.isMuted = newMute
    }

    private fun startAudio(mood: String) {
        val proj = _selectedProject.value
        audioEngine.isMuted = _isAudioMuted.value
        audioEngine.volume = proj?.bgVolume ?: 1.0f
        audioEngine.pitchSemitones = proj?.pitchSemitones ?: 0
        audioEngine.noiseSuppression = proj?.noiseSuppression ?: true
        audioEngine.equalizerPreset = when (proj?.equalizerPreset) {
            "Bass Boost" -> EqualizerPreset.BASS_BOOST
            "Vocal Clarity" -> EqualizerPreset.VOCAL_CLARITY
            "Flat" -> EqualizerPreset.FLAT
            else -> EqualizerPreset.STUDIO_WARMTH
        }
        audioEngine.start(mood)
    }

    private fun recomputeBeatGrid(durationSec: Int, bpm: Int) {
        _beatTransients.value = BeatFlowEngine.computeBeatTransients(durationSec, bpm)
        _waveformAmplitudes.value = BeatFlowEngine.generateWaveformAmplitudes(64, bpm)
    }

    // ==========================================
    // LOCAL FILE MANAGER IMPORTER (AUDIO & VIDEO)
    // ==========================================
    fun importBackgroundMedia(uri: String, displayName: String, isVideo: Boolean) {
        val current = _selectedProject.value ?: return
        val updated = current.copy(
            backgroundMediaUri = uri,
            backgroundMediaName = displayName,
            sourceType = if (isVideo) "video" else "audio"
        )
        _selectedProject.value = updated
        saveProjectAsync(updated)
        _userMessage.value = "Imported '$displayName' into Track 1 (Main Media)"
    }

    fun importOverlayMedia(uri: String, displayName: String) {
        val current = _selectedProject.value ?: return
        val updated = current.copy(
            overlayMediaUri = uri,
            overlayMediaName = displayName
        )
        _selectedProject.value = updated
        saveProjectAsync(updated)
        _userMessage.value = "Imported '$displayName' into Track 2 (Overlay Track)"
    }

    fun clearOverlayMedia() {
        val current = _selectedProject.value ?: return
        val updated = current.copy(
            overlayMediaUri = null,
            overlayMediaName = null
        )
        _selectedProject.value = updated
        saveProjectAsync(updated)
        _userMessage.value = "Cleared Track 2 overlay"
    }

    fun updateOverlayConfig(scale: Float, posX: Float, posY: Float, opacity: Float) {
        val current = _selectedProject.value ?: return
        val updated = current.copy(
            overlayScale = scale.coerceIn(0.2f, 1.0f),
            overlayPosX = posX.coerceIn(0f, 1f),
            overlayPosY = posY.coerceIn(0f, 1f),
            overlayOpacity = opacity.coerceIn(0.1f, 1f)
        )
        _selectedProject.value = updated
        saveProjectAsync(updated)
    }

    // ==========================================
    // CANVAS TRANSFORMS & ASPECT RATIO
    // ==========================================
    fun updateCanvasTransforms(scale: Float, panX: Float, panY: Float, rotation: Float) {
        val current = _selectedProject.value ?: return
        val updated = current.copy(
            canvasScale = scale.coerceIn(0.5f, 3.5f),
            canvasPanX = panX.coerceIn(-600f, 600f),
            canvasPanY = panY.coerceIn(-600f, 600f),
            canvasRotation = rotation.coerceIn(-180f, 180f)
        )
        _selectedProject.value = updated
        saveProjectAsync(updated)
    }

    fun resetCanvasTransforms() {
        val current = _selectedProject.value ?: return
        val updated = current.copy(
            canvasScale = 1.0f,
            canvasPanX = 0.0f,
            canvasPanY = 0.0f,
            canvasRotation = 0.0f
        )
        _selectedProject.value = updated
        saveProjectAsync(updated)
        _userMessage.value = "Canvas transform reset to default"
    }

    fun updateAspectRatio(ratio: String) {
        val current = _selectedProject.value ?: return
        val updated = current.copy(aspectRatio = ratio)
        _selectedProject.value = updated
        saveProjectAsync(updated)
    }

    // ==========================================
    // FILTER GRADING & TRANSITIONS
    // ==========================================
    fun updateFilterGrading(filter: String) {
        val current = _selectedProject.value ?: return
        val updated = current.copy(filterGrading = filter)
        _selectedProject.value = updated
        saveProjectAsync(updated)
        _userMessage.value = "Filter set to: $filter"
    }

    fun updateTransitionEffect(transition: String) {
        val current = _selectedProject.value ?: return
        val updated = current.copy(transitionEffect = transition)
        _selectedProject.value = updated
        saveProjectAsync(updated)
        _userMessage.value = "Transition: $transition"
    }

    // ==========================================
    // BEAT DETECTION & FLOW SYNC
    // ==========================================
    fun updateBpm(newBpm: Int) {
        val safeBpm = newBpm.coerceIn(60, 200)
        val current = _selectedProject.value ?: return
        val updated = current.copy(bpm = safeBpm)
        _selectedProject.value = updated
        recomputeBeatGrid(updated.durationSeconds, safeBpm)
        saveProjectAsync(updated)
    }

    fun updatePlaybackSpeed(speed: Float) {
        val current = _selectedProject.value ?: return
        val updated = current.copy(playbackSpeed = speed)
        _selectedProject.value = updated
        saveProjectAsync(updated)
    }

    fun toggleSnapToTransients(enabled: Boolean) {
        val current = _selectedProject.value ?: return
        val updated = current.copy(snapToTransients = enabled)
        _selectedProject.value = updated
        saveProjectAsync(updated)
        _userMessage.value = if (enabled) "Beat transient snap: ON" else "Beat transient snap: OFF"
    }

    // ==========================================
    // AUTO-CAPTIONS GENERATOR
    // ==========================================
    fun generateAutoCaptions(style: String = "karaoke") {
        val current = _selectedProject.value ?: return
        val captions = CaptionSegment.generateDefaultCaptions(current.durationSeconds, current.prompt)
        val updated = current.copy(
            hasCaptions = true,
            captionStyle = style,
            captionsJson = CaptionSegment.toJsonArrayString(captions)
        )
        _selectedProject.value = updated
        saveProjectAsync(updated)
        _userMessage.value = "Auto-captions generated with ${captions.size} dynamic segments!"
    }

    fun updateCaptionStyle(style: String) {
        val current = _selectedProject.value ?: return
        val updated = current.copy(captionStyle = style)
        _selectedProject.value = updated
        saveProjectAsync(updated)
    }

    fun toggleCaptions(enabled: Boolean) {
        val current = _selectedProject.value ?: return
        val updated = current.copy(hasCaptions = enabled)
        _selectedProject.value = updated
        saveProjectAsync(updated)
    }

    // ==========================================
    // AUDIO EDITING SUITE & MULTI-LAYER MIXING
    // ==========================================
    fun updateBgVolume(volume: Float) {
        val current = _selectedProject.value ?: return
        val updated = current.copy(bgVolume = volume)
        _selectedProject.value = updated
        audioEngine.volume = volume
        saveProjectAsync(updated)
    }

    fun updateVoiceoverVolume(volume: Float) {
        val current = _selectedProject.value ?: return
        val updated = current.copy(voiceoverVolume = volume)
        _selectedProject.value = updated
        saveProjectAsync(updated)
    }

    fun updateAiVoiceVolume(volume: Float) {
        val current = _selectedProject.value ?: return
        val updated = current.copy(aiVoiceVolume = volume)
        _selectedProject.value = updated
        saveProjectAsync(updated)
    }

    fun updateFadeDurations(fadeIn: Float, fadeOut: Float) {
        val current = _selectedProject.value ?: return
        val updated = current.copy(fadeInDuration = fadeIn, fadeOutDuration = fadeOut)
        _selectedProject.value = updated
        saveProjectAsync(updated)
    }

    fun updatePitchSemitones(semitones: Int) {
        val safeSemitones = semitones.coerceIn(-12, 12)
        val current = _selectedProject.value ?: return
        val updated = current.copy(pitchSemitones = safeSemitones)
        _selectedProject.value = updated
        audioEngine.pitchSemitones = safeSemitones
        saveProjectAsync(updated)
    }

    fun updateEqualizerPreset(preset: String) {
        val current = _selectedProject.value ?: return
        val updated = current.copy(equalizerPreset = preset)
        _selectedProject.value = updated
        audioEngine.equalizerPreset = when (preset) {
            "Bass Boost" -> EqualizerPreset.BASS_BOOST
            "Vocal Clarity" -> EqualizerPreset.VOCAL_CLARITY
            "Flat" -> EqualizerPreset.FLAT
            else -> EqualizerPreset.STUDIO_WARMTH
        }
        saveProjectAsync(updated)
        _userMessage.value = "EQ profile: $preset"
    }

    fun toggleNoiseSuppression(enabled: Boolean) {
        val current = _selectedProject.value ?: return
        val updated = current.copy(noiseSuppression = enabled)
        _selectedProject.value = updated
        audioEngine.noiseSuppression = enabled
        saveProjectAsync(updated)
        _userMessage.value = if (enabled) "Noise suppression active" else "Noise suppression bypassed"
    }

    fun startVoiceoverRecording() {
        micRecorder.startRecording("track2_mic")
    }

    fun stopVoiceoverRecording() {
        val uri = micRecorder.stopRecording()
        val durationSec = micRecorder.recordedDurationMs.value / 1000f
        val current = _selectedProject.value ?: return
        val updated = current.copy(
            recordedVoiceUri = uri,
            recordedDurationSec = durationSec
        )
        _selectedProject.value = updated
        saveProjectAsync(updated)
        _userMessage.value = "Voiceover mic recorded (${String.format("%.1f", durationSec)}s) saved to project!"
    }

    // Voiceover Narration Methods
    fun speakVoiceover(text: String, profile: VoiceProfile, pitch: Float? = null, speed: Float? = null) {
        voiceoverEngine.speak(text, profile, pitch, speed)
    }

    fun stopVoiceover() {
        voiceoverEngine.stop()
    }

    fun attachVoiceoverToActiveProject(script: String, profile: VoiceProfile) {
        val current = _selectedProject.value ?: return
        val updated = current.copy(
            hasVoiceover = true,
            voiceProfile = profile.displayName,
            voiceScript = script
        )
        _selectedProject.value = updated
        saveProjectAsync(updated)
    }

    fun generateAIScript(topic: String, onDone: (String) -> Unit) {
        viewModelScope.launch {
            _isGeneratingScript.value = true
            try {
                val script = if (topic.contains("hindi", ignoreCase = true)) {
                    "नमस्ते और स्वागत है PulseCraft Studio में। हर बीट और हर फ्रेम आपकी रचनात्मकता को नया आयाम देता है।"
                } else {
                    "Welcome to PulseCraft Studio. In every frame, light and sound intersect to craft unforgettable visual stories."
                }
                delay(400)
                onDone(script)
            } finally {
                _isGeneratingScript.value = false
            }
        }
    }

    // Stock Media search & filter
    fun setStockSearchQuery(query: String) {
        _stockSearchQuery.value = query
        _stockItems.value = stockRepository.searchMedia(query, _stockCategory.value)
    }

    fun setStockCategory(type: StockMediaType?) {
        _stockCategory.value = type
        _stockItems.value = stockRepository.searchMedia(_stockSearchQuery.value, type)
    }

    fun downloadStockItem(item: StockMediaItem) {
        stockRepository.markDownloaded(item.id)
        _userMessage.value = "Downloaded '${item.title}' to device storage!"
    }

    // API Key Settings
    fun saveGeminiApiKey(key: String) {
        val trimmed = key.trim()
        ApiKeyManager.saveApiKey(getApplication(), trimmed)
        _geminiApiKey.value = trimmed
        _userMessage.value = if (trimmed.isNotBlank()) "Gemini API Key saved securely!" else "API Key cleared."
    }

    fun clearGeminiApiKey() {
        ApiKeyManager.clearApiKey(getApplication())
        _geminiApiKey.value = ""
        _userMessage.value = "Gemini API Key cleared."
    }

    // Voice Tone Controls
    fun setVoiceTone(tone: VoiceTone) {
        _selectedVoiceTone.value = tone
        _voicePitch.value = tone.defaultPitch
        _voiceSpeed.value = tone.defaultSpeed
    }

    fun setVoicePitch(pitch: Float) {
        _voicePitch.value = pitch.coerceIn(0.40f, 1.80f)
    }

    fun setVoiceSpeed(speed: Float) {
        _voiceSpeed.value = speed.coerceIn(0.50f, 1.50f)
    }

    fun previewVoiceTone(text: String) {
        val tone = _selectedVoiceTone.value
        val textToSpeak = text.ifBlank { tone.sampleScript }
        voiceoverEngine.speakTone(
            text = textToSpeak,
            tone = tone,
            customPitch = _voicePitch.value,
            customSpeed = _voiceSpeed.value
        )
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
                val enhanced = repository.enhancePrompt(
                    prompt = prompt,
                    style = style,
                    cameraMotion = cameraMotion,
                    lighting = lighting,
                    apiKey = _geminiApiKey.value
                )
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
        sourceType: String = "text",
        sourceImageUri: String? = null,
        onSuccess: (VideoProject) -> Unit
    ) {
        if (prompt.isBlank()) {
            _userMessage.value = "Please enter a description for the video."
            return
        }

        viewModelScope.launch {
            _isGenerating.value = true
            _generationProgress.value = 0.1f
            _generationStep.value = if (sourceType == "image") "Analyzing source image and motion vectors..." else "Analyzing cinematic prompt and visual motifs..."

            delay(350)
            _generationProgress.value = 0.28f
            _generationStep.value = "Choreographing camera motion & lighting angles ($cameraMotion)..."

            val currentKey = _geminiApiKey.value
            val enhanced = repository.enhancePrompt(
                prompt = prompt,
                style = style,
                cameraMotion = cameraMotion,
                lighting = lighting,
                apiKey = currentKey
            )
            _generationProgress.value = 0.52f
            _generationStep.value = "Generating ${durationSeconds}s multi-shot storyboard sequence..."

            val scenes = repository.generateStoryboard(
                prompt = prompt,
                style = style,
                cameraMotion = cameraMotion,
                lighting = lighting,
                duration = durationSeconds,
                apiKey = currentKey
            )

            _generationProgress.value = 0.76f
            _generationStep.value = "Synthesizing volumetric lighting & color grading..."
            delay(300)

            _generationProgress.value = 0.92f
            _generationStep.value = "Finalizing $fps FPS video composition ($aspectRatio)..."
            delay(250)

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
                sourceType = sourceType,
                sourceImageUri = sourceImageUri,
                resolution = "1080p",
                hasCaptions = true,
                captionStyle = "karaoke",
                captionsJson = CaptionSegment.toJsonArrayString(
                    CaptionSegment.generateDefaultCaptions(durationSeconds, prompt)
                ),
                createdAt = System.currentTimeMillis()
            )

            val id = repository.saveProject(newProject)
            val savedProject = newProject.copy(id = id)

            _generationProgress.value = 1.0f
            _generationStep.value = "Video Generated!"
            delay(150)

            _selectedProject.value = savedProject
            startAudio(audioMood)
            recomputeBeatGrid(durationSeconds, savedProject.bpm)
            _isGenerating.value = false
            _userMessage.value = "Video created successfully!"
            onSuccess(savedProject)
        }
    }

    private fun saveProjectAsync(project: VideoProject) {
        viewModelScope.launch {
            repository.saveProject(project)
        }
    }

    fun deleteProject(project: VideoProject) {
        viewModelScope.launch {
            repository.deleteProject(project.id)
            if (_selectedProject.value?.id == project.id) {
                _selectedProject.value = projects.value.firstOrNull { it.id != project.id }
            }
            _userMessage.value = "Project removed"
        }
    }

    fun duplicateProject(project: VideoProject) {
        viewModelScope.launch {
            val duplicated = project.copy(
                id = 0,
                title = "${project.title} (Copy)",
                createdAt = System.currentTimeMillis()
            )
            repository.saveProject(duplicated)
            _userMessage.value = "Project duplicated"
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
                durationSeconds = 3.0f,
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
                durationSeconds = 3.0f,
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
                durationSeconds = 4.0f,
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
            durationSeconds = 10,
            fps = 30,
            motionIntensity = 0.85f,
            scenesJson = SceneShot.toJsonArrayString(sample1Scenes),
            audioMood = "cyber_pulse",
            resolution = "4K Ultra HD",
            hasVoiceover = true,
            voiceProfile = "Adam Deep Male",
            voiceScript = "In the rain-drenched canyons of Neo-Tokyo, shadows dance beneath neon spires of a forgotten tomorrow.",
            hasCaptions = true,
            captionStyle = "karaoke",
            captionsJson = CaptionSegment.toJsonArrayString(
                CaptionSegment.generateDefaultCaptions(10, "cyber")
            ),
            filterGrading = "Cyberpunk Neon",
            transitionEffect = "Glitch Warp",
            bpm = 124,
            playbackSpeed = 1.0f,
            bgVolume = 0.9f,
            equalizerPreset = "Studio Warmth",
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
                durationSeconds = 5.0f,
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
                durationSeconds = 5.0f,
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
            durationSeconds = 10,
            fps = 60,
            motionIntensity = 0.65f,
            scenesJson = SceneShot.toJsonArrayString(sample2Scenes),
            audioMood = "cinematic_drone",
            resolution = "4K Cinematic Master",
            hasCaptions = true,
            captionStyle = "word_pop",
            captionsJson = CaptionSegment.toJsonArrayString(
                CaptionSegment.generateDefaultCaptions(10, "space cosmic")
            ),
            filterGrading = "Teal & Orange",
            transitionEffect = "Fade",
            bpm = 110,
            playbackSpeed = 1.0f,
            createdAt = System.currentTimeMillis() - 120000
        )

        repository.saveProject(sample1)
        repository.saveProject(sample2)
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.stop()
        voiceoverEngine.shutdown()
    }
}

