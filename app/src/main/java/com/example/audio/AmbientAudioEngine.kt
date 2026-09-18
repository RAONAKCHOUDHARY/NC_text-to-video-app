package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.data.model.EqualizerPreset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.tanh

/**
 * Real-time synthesis of cinematic ambient soundscapes using AudioTrack.
 * Features pitch shifting (-12 to +12 semitones), equalizer presets,
 * volume automation, and noise suppression.
 */
class AmbientAudioEngine {
    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    @Volatile
    private var isPlaying = false

    @Volatile
    var isMuted = false

    @Volatile
    var volume = 1.0f

    @Volatile
    var pitchSemitones = 0

    @Volatile
    var equalizerPreset: EqualizerPreset = EqualizerPreset.STUDIO_WARMTH

    @Volatile
    var noiseSuppression = true

    private val sampleRate = 44100
    private val bufferSize = AudioTrack.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_OUT_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    ).coerceAtLeast(4096)

    fun start(mood: String = "ambient_synth") {
        if (isPlaying) stop()
        if (mood == "none") return

        try {
            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack?.play()
            isPlaying = true

            playbackJob = scope.launch {
                val shortBuffer = ShortArray(bufferSize / 2)
                var phase1 = 0.0
                var phase2 = 0.0
                var phase3 = 0.0
                var lfoPhase = 0.0

                // Base frequencies based on mood
                val (rawFreq1, rawFreq2, rawFreq3) = when (mood) {
                    "cyber_pulse" -> Triple(110.0, 164.8, 220.0) // A2, E3, A3
                    "cinematic_drone" -> Triple(55.0, 82.4, 110.0) // A1, E2, A2 (sub-bass)
                    "nature_wind" -> Triple(146.83, 220.0, 293.66) // D3, A3, D4
                    else -> Triple(73.42, 110.0, 146.83) // D2, A2, D3 warm pad
                }

                while (isActive && isPlaying) {
                    if (isMuted || volume <= 0.01f) {
                        shortBuffer.fill(0)
                    } else {
                        // Compute pitch multiplier from semitones: 2^(semitones / 12)
                        val pitchMultiplier = 2.0.pow(pitchSemitones.toDouble() / 12.0)
                        val baseFreq1 = rawFreq1 * pitchMultiplier
                        val baseFreq2 = rawFreq2 * pitchMultiplier
                        val baseFreq3 = rawFreq3 * pitchMultiplier

                        // EQ weight adjustments
                        val (w1, w2, w3) = when (equalizerPreset) {
                            EqualizerPreset.BASS_BOOST -> Triple(0.75, 0.25, 0.15)
                            EqualizerPreset.VOCAL_CLARITY -> Triple(0.25, 0.45, 0.50)
                            EqualizerPreset.STUDIO_WARMTH -> Triple(0.55, 0.35, 0.30)
                            EqualizerPreset.FLAT -> Triple(0.40, 0.35, 0.30)
                        }

                        for (i in shortBuffer.indices) {
                            lfoPhase += 2.0 * Math.PI * 0.2 / sampleRate // 0.2 Hz slow breath
                            val lfo = 0.7 + 0.3 * sin(lfoPhase)

                            val step1 = 2.0 * Math.PI * (baseFreq1 * (1.0 + 0.005 * sin(lfoPhase * 1.5))) / sampleRate
                            val step2 = 2.0 * Math.PI * (baseFreq2 * (1.0 + 0.003 * sin(lfoPhase))) / sampleRate
                            val step3 = 2.0 * Math.PI * (baseFreq3 * (1.0 - 0.004 * sin(lfoPhase * 0.8))) / sampleRate

                            phase1 = (phase1 + step1) % (2.0 * Math.PI)
                            phase2 = (phase2 + step2) % (2.0 * Math.PI)
                            phase3 = (phase3 + step3) % (2.0 * Math.PI)

                            val sample1 = sin(phase1) * w1
                            val sample2 = sin(phase2) * w2
                            val sample3 = sin(phase3) * w3

                            var mixed = (sample1 + sample2 + sample3) * lfo * volume

                            // Studio warmth tube saturation simulation
                            if (equalizerPreset == EqualizerPreset.STUDIO_WARMTH) {
                                mixed = tanh(mixed * 1.25)
                            }

                            // Noise suppression threshold gate
                            if (noiseSuppression && kotlin.math.abs(mixed) < 0.02) {
                                mixed = 0.0
                            }

                            shortBuffer[i] = (mixed * 0.4 * Short.MAX_VALUE).toInt().coerceIn(
                                Short.MIN_VALUE.toInt(),
                                Short.MAX_VALUE.toInt()
                            ).toShort()
                        }
                    }

                    audioTrack?.write(shortBuffer, 0, shortBuffer.size)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stop() {
        isPlaying = false
        playbackJob?.cancel()
        playbackJob = null
        try {
            audioTrack?.pause()
            audioTrack?.flush()
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        audioTrack = null
    }
}
