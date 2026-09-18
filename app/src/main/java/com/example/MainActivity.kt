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
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.PlayCircle
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.VideoProject
import com.example.ui.screens.StudioGeneratorScreen
import com.example.ui.screens.VideoDetailScreen
import com.example.ui.screens.VideoGalleryScreen
import com.example.ui.theme.CinemaBackground
import com.example.ui.theme.CinemaCyan
import com.example.ui.theme.CinemaSurface
import com.example.ui.theme.CinemaSurfaceBorder
import com.example.ui.theme.CinemaSurfaceElevated
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
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
    val snackbarHostState = remember { SnackbarHostState() }

    val projects by viewModel.projects.collectAsStateWithLifecycle()
    val selectedProject by viewModel.selectedProject.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val generationStep by viewModel.generationStep.collectAsStateWithLifecycle()
    val generationProgress by viewModel.generationProgress.collectAsStateWithLifecycle()
    val isEnhancingPrompt by viewModel.isEnhancingPrompt.collectAsStateWithLifecycle()
    val isAudioMuted by viewModel.isAudioMuted.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

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
            .background(CinemaBackground),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = CinemaSurface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .background(CinemaSurface)
                    .navigationBarsPadding()
                    .testTag("bottom_nav_bar")
            ) {
                // Tab 0: Studio
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 0) Icons.Filled.Movie else Icons.Outlined.Movie,
                            contentDescription = "Studio"
                        )
                    },
                    label = {
                        Text(
                            text = "Studio",
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CinemaCyan,
                        selectedTextColor = CinemaCyan,
                        indicatorColor = CinemaSurfaceElevated,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("nav_studio")
                )

                // Tab 1: Video Player
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 1) Icons.Filled.PlayCircle else Icons.Outlined.PlayCircle,
                            contentDescription = "Player"
                        )
                    },
                    label = {
                        Text(
                            text = "Player",
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CinemaCyan,
                        selectedTextColor = CinemaCyan,
                        indicatorColor = CinemaSurfaceElevated,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("nav_player")
                )

                // Tab 2: Gallery / Vault
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 2) Icons.Filled.VideoLibrary else Icons.Outlined.VideoLibrary,
                            contentDescription = "Vault"
                        )
                    },
                    label = {
                        Text(
                            text = "Vault",
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CinemaCyan,
                        selectedTextColor = CinemaCyan,
                        indicatorColor = CinemaSurfaceElevated,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("nav_vault")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> {
                    StudioGeneratorScreen(
                        isGenerating = isGenerating,
                        generationStep = generationStep,
                        generationProgress = generationProgress,
                        isEnhancingPrompt = isEnhancingPrompt,
                        onEnhancePrompt = { prompt, style, camera, lighting, onDone ->
                            viewModel.enhancePrompt(prompt, style, camera, lighting, onDone)
                        },
                        onGenerateVideo = { prompt, style, ratio, camera, lighting, dur, fps, motion, audio ->
                            viewModel.generateVideo(
                                prompt = prompt,
                                style = style,
                                aspectRatio = ratio,
                                cameraMotion = camera,
                                lighting = lighting,
                                durationSeconds = dur,
                                fps = fps,
                                motionIntensity = motion,
                                audioMood = audio
                            ) { created ->
                                selectedTab = 1 // Switch directly to player to view the video!
                            }
                        }
                    )
                }
                1 -> {
                    val current = selectedProject ?: projects.firstOrNull()
                    if (current != null) {
                        VideoDetailScreen(
                            project = current,
                            isAudioMuted = isAudioMuted,
                            onToggleMute = { viewModel.toggleMute() },
                            onToggleFavorite = { viewModel.toggleFavorite(it) },
                            onDeleteProject = {
                                viewModel.deleteProject(it)
                                if (projects.size <= 1) {
                                    selectedTab = 0
                                }
                            },
                            onRemixPrompt = { remixTarget ->
                                selectedTab = 0
                            },
                            onBack = { selectedTab = 2 }
                        )
                    } else {
                        // Empty player fallback -> guide to Studio
                        StudioGeneratorScreen(
                            isGenerating = isGenerating,
                            generationStep = generationStep,
                            generationProgress = generationProgress,
                            isEnhancingPrompt = isEnhancingPrompt,
                            onEnhancePrompt = { prompt, style, camera, lighting, onDone ->
                                viewModel.enhancePrompt(prompt, style, camera, lighting, onDone)
                            },
                            onGenerateVideo = { prompt, style, ratio, camera, lighting, dur, fps, motion, audio ->
                                viewModel.generateVideo(
                                    prompt = prompt,
                                    style = style,
                                    aspectRatio = ratio,
                                    cameraMotion = camera,
                                    lighting = lighting,
                                    durationSeconds = dur,
                                    fps = fps,
                                    motionIntensity = motion,
                                    audioMood = audio
                                ) {
                                    selectedTab = 1
                                }
                            }
                        )
                    }
                }
                2 -> {
                    VideoGalleryScreen(
                        projects = projects,
                        onSelectProject = { project ->
                            viewModel.selectProject(project)
                            selectedTab = 1 // Open player
                        },
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        onNewVideoClick = { selectedTab = 0 }
                    )
                }
            }
        }
    }
}
