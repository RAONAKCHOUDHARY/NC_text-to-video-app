package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.VideoProject
import com.example.ui.components.ThemeSettingsDialog
import com.example.ui.screens.AudioVoiceStudioScreen
import com.example.ui.screens.CreativeEditorScreen
import com.example.ui.screens.StockLibraryScreen
import com.example.ui.screens.StudioGeneratorScreen
import com.example.ui.screens.VideoDetailScreen
import com.example.ui.screens.VideoGalleryScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ThemeManager
import com.example.ui.viewmodel.VideoViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: VideoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                TextToVideoApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun TextToVideoApp(viewModel: VideoViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var studioSubTab by remember { mutableIntStateOf(0) } // 0: Creative Suite / Editor, 1: AI Generator
    var activeDetailProject by remember { mutableStateOf<VideoProject?>(null) }
    var showThemeSettings by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    val projects by viewModel.projects.collectAsStateWithLifecycle()
    val selectedProject by viewModel.selectedProject.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val generationStep by viewModel.generationStep.collectAsStateWithLifecycle()
    val generationProgress by viewModel.generationProgress.collectAsStateWithLifecycle()
    val isEnhancingPrompt by viewModel.isEnhancingPrompt.collectAsStateWithLifecycle()
    val isGeneratingScript by viewModel.isGeneratingScript.collectAsStateWithLifecycle()
    val isAudioMuted by viewModel.isAudioMuted.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

    val isSpeaking by viewModel.voiceoverEngine.isSpeaking.collectAsStateWithLifecycle()
    val speechProgress by viewModel.voiceoverEngine.speechProgress.collectAsStateWithLifecycle()

    val stockItems by viewModel.stockItems.collectAsStateWithLifecycle()
    val stockSearchQuery by viewModel.stockSearchQuery.collectAsStateWithLifecycle()
    val stockCategory by viewModel.stockCategory.collectAsStateWithLifecycle()

    // Handle user messages in snackbar
    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(ThemeManager.backgroundColor),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = ThemeManager.surfaceColor,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .background(ThemeManager.surfaceColor)
                    .navigationBarsPadding()
                    .testTag("bottom_nav_bar")
            ) {
                // Tab 0: Video Studio
                NavigationBarItem(
                    selected = selectedTab == 0 && activeDetailProject == null,
                    onClick = {
                        activeDetailProject = null
                        selectedTab = 0
                    },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 0 && activeDetailProject == null) Icons.Filled.Movie else Icons.Outlined.Movie,
                            contentDescription = "Video Studio"
                        )
                    },
                    label = {
                        Text(
                            text = "Video Studio",
                            fontSize = 10.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ThemeManager.primaryAccent,
                        selectedTextColor = ThemeManager.primaryAccent,
                        indicatorColor = ThemeManager.surfaceElevatedColor,
                        unselectedIconColor = ThemeManager.textMutedColor,
                        unselectedTextColor = ThemeManager.textMutedColor
                    ),
                    modifier = Modifier.testTag("nav_video_studio")
                )

                // Tab 1: Audio & Voice Studio
                NavigationBarItem(
                    selected = selectedTab == 1 && activeDetailProject == null,
                    onClick = {
                        activeDetailProject = null
                        selectedTab = 1
                    },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 1 && activeDetailProject == null) Icons.Filled.Mic else Icons.Outlined.Mic,
                            contentDescription = "Audio & Voice"
                        )
                    },
                    label = {
                        Text(
                            text = "Audio & Voice",
                            fontSize = 10.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ThemeManager.primaryAccent,
                        selectedTextColor = ThemeManager.primaryAccent,
                        indicatorColor = ThemeManager.surfaceElevatedColor,
                        unselectedIconColor = ThemeManager.textMutedColor,
                        unselectedTextColor = ThemeManager.textMutedColor
                    ),
                    modifier = Modifier.testTag("nav_audio_studio")
                )

                // Tab 2: Stock Library
                NavigationBarItem(
                    selected = selectedTab == 2 && activeDetailProject == null,
                    onClick = {
                        activeDetailProject = null
                        selectedTab = 2
                    },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 2 && activeDetailProject == null) Icons.Filled.Search else Icons.Outlined.Search,
                            contentDescription = "Stock Library"
                        )
                    },
                    label = {
                        Text(
                            text = "Stock Media",
                            fontSize = 10.sp,
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ThemeManager.primaryAccent,
                        selectedTextColor = ThemeManager.primaryAccent,
                        indicatorColor = ThemeManager.surfaceElevatedColor,
                        unselectedIconColor = ThemeManager.textMutedColor,
                        unselectedTextColor = ThemeManager.textMutedColor
                    ),
                    modifier = Modifier.testTag("nav_stock_library")
                )

                // Tab 3: Studio Gallery / Vault
                NavigationBarItem(
                    selected = selectedTab == 3 || activeDetailProject != null,
                    onClick = {
                        activeDetailProject = null
                        selectedTab = 3
                    },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 3) Icons.Filled.VideoLibrary else Icons.Outlined.VideoLibrary,
                            contentDescription = "Gallery"
                        )
                    },
                    label = {
                        Text(
                            text = "Gallery",
                            fontSize = 10.sp,
                            fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ThemeManager.primaryAccent,
                        selectedTextColor = ThemeManager.primaryAccent,
                        indicatorColor = ThemeManager.surfaceElevatedColor,
                        unselectedIconColor = ThemeManager.textMutedColor,
                        unselectedTextColor = ThemeManager.textMutedColor
                    ),
                    modifier = Modifier.testTag("nav_gallery")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // If viewing detail screen
            if (activeDetailProject != null) {
                VideoDetailScreen(
                    project = activeDetailProject!!,
                    isAudioMuted = isAudioMuted,
                    onToggleMute = { viewModel.toggleMute() },
                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                    onDeleteProject = {
                        viewModel.deleteProject(it)
                        activeDetailProject = null
                    },
                    onRemixPrompt = {
                        activeDetailProject = null
                        selectedTab = 0
                        studioSubTab = 1
                    },
                    onOpenInEditor = { proj ->
                        viewModel.selectProject(proj)
                        activeDetailProject = null
                        selectedTab = 0
                        studioSubTab = 0
                    },
                    onBack = {
                        activeDetailProject = null
                    }
                )
            } else {
                when (selectedTab) {
                    0 -> {
                        if (studioSubTab == 0) {
                            CreativeEditorScreen(
                                viewModel = viewModel,
                                onOpenGenerator = { studioSubTab = 1 }
                            )
                        } else {
                            StudioGeneratorScreen(
                                isGenerating = isGenerating,
                                generationStep = generationStep,
                                generationProgress = generationProgress,
                                isEnhancingPrompt = isEnhancingPrompt,
                                onEnhancePrompt = { prompt, style, camera, lighting, onDone ->
                                    viewModel.enhancePrompt(prompt, style, camera, lighting, onDone)
                                },
                                onGenerateVideo = { prompt, style, ratio, camera, lighting, dur, fps, motion, audio, srcType, srcImg ->
                                    viewModel.generateVideo(
                                        prompt = prompt,
                                        style = style,
                                        aspectRatio = ratio,
                                        cameraMotion = camera,
                                        lighting = lighting,
                                        durationSeconds = dur,
                                        fps = fps,
                                        motionIntensity = motion,
                                        audioMood = audio,
                                        sourceType = srcType,
                                        sourceImageUri = srcImg
                                    ) { created ->
                                        viewModel.selectProject(created)
                                        activeDetailProject = created
                                    }
                                },
                                onOpenThemeSettings = { showThemeSettings = true },
                                onSwitchToEditor = { studioSubTab = 0 }
                            )
                        }
                    }

                    1 -> {
                        AudioVoiceStudioScreen(
                            isSpeaking = isSpeaking,
                            speechProgress = speechProgress,
                            onSpeak = { text, profile, pitch, speed ->
                                viewModel.speakVoiceover(text, profile, pitch, speed)
                            },
                            onStop = { viewModel.stopVoiceover() },
                            onAttachToVideo = { script, profile ->
                                viewModel.attachVoiceoverToActiveProject(script, profile)
                            },
                            onGenerateAIScript = { topic, onDone ->
                                viewModel.generateAIScript(topic, onDone)
                            },
                            isGeneratingScript = isGeneratingScript
                        )
                    }

                    2 -> {
                        StockLibraryScreen(
                            stockItems = stockItems,
                            searchQuery = stockSearchQuery,
                            onSearchQueryChange = { viewModel.setStockSearchQuery(it) },
                            selectedCategory = stockCategory,
                            onSelectCategory = { viewModel.setStockCategory(it) },
                            onDownloadItem = { viewModel.downloadStockItem(it) },
                            onImportToStudio = { stockItem ->
                                selectedTab = 0
                                studioSubTab = 0
                            }
                        )
                    }

                    3 -> {
                        VideoGalleryScreen(
                            projects = projects,
                            onSelectProject = { project ->
                                viewModel.selectProject(project)
                                activeDetailProject = project
                            },
                            onToggleFavorite = { viewModel.toggleFavorite(it) },
                            onNewVideoClick = {
                                selectedTab = 0
                                studioSubTab = 1
                            }
                        )
                    }
                }
            }
        }
    }

    if (showThemeSettings) {
        ThemeSettingsDialog(onDismiss = { showThemeSettings = false })
    }
}
