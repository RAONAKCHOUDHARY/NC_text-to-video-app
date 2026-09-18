package com.example.data.repository

import com.example.data.api.GeminiVideoService
import com.example.data.local.VideoDao
import com.example.data.model.SceneShot
import com.example.data.model.VideoProject
import kotlinx.coroutines.flow.Flow

class VideoRepository(
    private val videoDao: VideoDao,
    private val apiService: GeminiVideoService = GeminiVideoService()
) {
    val allProjects: Flow<List<VideoProject>> = videoDao.getAllProjects()
    val favoriteProjects: Flow<List<VideoProject>> = videoDao.getFavoriteProjects()

    suspend fun getProjectById(id: Long): VideoProject? {
        return videoDao.getProjectById(id)
    }

    suspend fun saveProject(project: VideoProject): Long {
        return videoDao.insertProject(project)
    }

    suspend fun updateProject(project: VideoProject) {
        videoDao.updateProject(project)
    }

    suspend fun deleteProject(id: Long) {
        videoDao.deleteById(id)
    }

    suspend fun toggleFavorite(id: Long, isFavorite: Boolean) {
        videoDao.toggleFavorite(id, isFavorite)
    }

    suspend fun enhancePrompt(
        prompt: String,
        style: String,
        cameraMotion: String,
        lighting: String,
        apiKey: String? = null
    ): String {
        return apiService.enhancePrompt(prompt, style, cameraMotion, lighting, apiKey).getOrDefault(prompt)
    }

    suspend fun generateStoryboard(
        prompt: String,
        style: String,
        cameraMotion: String,
        lighting: String,
        duration: Int,
        apiKey: String? = null
    ): List<SceneShot> {
        val result = apiService.generateStoryboard(prompt, style, cameraMotion, lighting, duration, apiKey)
        return result.getOrElse {
            apiService.generateLocalScenes(prompt, style, cameraMotion, lighting, duration)
        }
    }

    suspend fun requestVeoVideo(prompt: String, aspectRatio: String): String? {
        return apiService.requestVeoVideo(prompt, aspectRatio).getOrNull()
    }
}
