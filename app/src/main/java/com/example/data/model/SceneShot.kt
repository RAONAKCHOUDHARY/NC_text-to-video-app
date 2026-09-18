package com.example.data.model

import org.json.JSONArray
import org.json.JSONObject

data class SceneShot(
    val sceneNumber: Int,
    val title: String,
    val shotType: String, // e.g. "Establishing Drone", "Close-up", "Low-angle tracking"
    val visualDescription: String,
    val cameraMovement: String, // "Pan Right", "Slow Zoom In", "Orbit", "Tilt Up", "Static"
    val lightingMood: String,
    val durationSeconds: Float = 2.5f,
    val colorHexes: List<String> = listOf("#00E5FF", "#8B5CF6", "#090C15"),
    val focalSubject: String = ""
) {
    fun toJson(): JSONObject {
        val obj = JSONObject()
        obj.put("sceneNumber", sceneNumber)
        obj.put("title", title)
        obj.put("shotType", shotType)
        obj.put("visualDescription", visualDescription)
        obj.put("cameraMovement", cameraMovement)
        obj.put("lightingMood", lightingMood)
        obj.put("durationSeconds", durationSeconds.toDouble())
        val colorsArr = JSONArray()
        colorHexes.forEach { colorsArr.put(it) }
        obj.put("colors", colorsArr)
        obj.put("focalSubject", focalSubject)
        return obj
    }

    companion object {
        fun fromJson(obj: JSONObject): SceneShot {
            val colorsList = mutableListOf<String>()
            val colorsArr = obj.optJSONArray("colors")
            if (colorsArr != null) {
                for (i in 0 until colorsArr.length()) {
                    colorsList.add(colorsArr.getString(i))
                }
            }
            if (colorsList.isEmpty()) {
                colorsList.addAll(listOf("#00E5FF", "#8B5CF6", "#090C15"))
            }

            return SceneShot(
                sceneNumber = obj.optInt("sceneNumber", 1),
                title = obj.optString("title", "Scene 1"),
                shotType = obj.optString("shotType", "Cinematic Shot"),
                visualDescription = obj.optString("visualDescription", ""),
                cameraMovement = obj.optString("cameraMovement", "Slow Zoom In"),
                lightingMood = obj.optString("lightingMood", "Cinematic"),
                durationSeconds = obj.optDouble("durationSeconds", 2.5).toFloat(),
                colorHexes = colorsList,
                focalSubject = obj.optString("focalSubject", "")
            )
        }

        fun parseList(jsonString: String): List<SceneShot> {
            val list = mutableListOf<SceneShot>()
            if (jsonString.isBlank()) return list
            try {
                val array = JSONArray(jsonString)
                for (i in 0 until array.length()) {
                    list.add(fromJson(array.getJSONObject(i)))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return list
        }

        fun toJsonArrayString(shots: List<SceneShot>): String {
            val array = JSONArray()
            shots.forEach { array.put(it.toJson()) }
            return array.toString()
        }
    }
}
