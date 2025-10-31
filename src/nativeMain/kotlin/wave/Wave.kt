package wave

import WAVEFORM
import globalPhase
import kotlin.math.PI
import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.RAND_MAX
import platform.posix.rand
import platform.posix.sin

class WaveGenerator {
    @OptIn(ExperimentalForeignApi::class) private var phase = globalPhase

    fun generate(frequency: Double, sampleRate: Double, waveForm: WAVEFORM = WAVEFORM.SINE): Float {
        if (waveForm == WAVEFORM.NOISE) {
            return rand() / RAND_MAX.toFloat() * 2.0f - 1.0f
        }

        val phaseIncrement = 2.0 * PI * frequency / sampleRate

        val sample = when (waveForm) {
            WAVEFORM.SINE -> sin(phase).toFloat()
            WAVEFORM.SQUARE -> if (sin(phase) >= 0) 1.0f else -1.0f
            WAVEFORM.SAWTOOTH -> (2.0 * (phase / (2.0 * PI)) - 1.0).toFloat()
            WAVEFORM.TRIANGLE -> (2.0 * kotlin.math.abs(2.0 * (phase / (2.0 * PI)) - 1.0) - 1.0).toFloat()
            else -> 0.0f
        }

        phase += phaseIncrement
        val twoPi = 2.0 * PI
        if (phase >= twoPi) phase %= twoPi

        return sample
    }
}
