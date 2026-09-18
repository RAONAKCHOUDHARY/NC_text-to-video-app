package com.example.audio

import com.example.data.model.BeatTransient
import kotlin.math.abs
import kotlin.math.sin

object BeatFlowEngine {

    /**
     * Computes the transient timestamps for a given duration and BPM.
     */
    fun computeBeatTransients(durationSec: Int, bpm: Int): List<BeatTransient> {
        val safeBpm = bpm.coerceIn(60, 240)
        val intervalMs = (60_000.0 / safeBpm).toLong()
        val totalMs = durationSec * 1000L
        val transients = mutableListOf<BeatTransient>()

        var currentMs = 0L
        var beatIndex = 0

        while (currentMs <= totalMs) {
            val isMajor = (beatIndex % 4 == 0)
            val baseIntensity = if (isMajor) 1.0f else (if (beatIndex % 2 == 0) 0.75f else 0.5f)
            // subtle pseudo-random dynamics
            val variance = (sin(beatIndex.toDouble() * 1.7).toFloat() * 0.15f)
            val intensity = (baseIntensity + variance).coerceIn(0.3f, 1.0f)

            transients.add(
                BeatTransient(
                    timestampMs = currentMs,
                    intensity = intensity,
                    isMajorDownbeat = isMajor
                )
            )
            currentMs += intervalMs
            beatIndex++
        }
        return transients
    }

    /**
     * Checks if current playback time is near a beat transient (within tolerance window).
     */
    fun isNearBeatTransient(currentMs: Long, transients: List<BeatTransient>, toleranceMs: Long = 65L): Boolean {
        return transients.any { abs(it.timestampMs - currentMs) <= toleranceMs }
    }

    /**
     * Finds nearest transient for snapping cuts.
     */
    fun findNearestTransient(currentMs: Long, transients: List<BeatTransient>): Long {
        if (transients.isEmpty()) return currentMs
        return transients.minByOrNull { abs(it.timestampMs - currentMs) }?.timestampMs ?: currentMs
    }

    /**
     * Generates simulated audio waveform amplitudes for visualizer rendering.
     */
    fun generateWaveformAmplitudes(count: Int, bpm: Int): List<Float> {
        val list = mutableListOf<Float>()
        val beatPeriod = (60.0 / bpm.coerceIn(60, 240) * 10.0).toInt().coerceAtLeast(4)
        for (i in 0 until count) {
            val beatPhase = (i % beatPeriod).toFloat() / beatPeriod
            val beatThump = if (beatPhase < 0.25f) 0.85f - (beatPhase * 2f) else 0.2f
            val wave = abs(sin(i * 0.35f)) * 0.5f + (abs(sin(i * 0.12f)) * 0.3f)
            val combined = (beatThump * 0.6f + wave * 0.4f).coerceIn(0.12f, 0.98f)
            list.add(combined)
        }
        return list
    }
}
