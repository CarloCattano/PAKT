package wave

import WAVEFORM
import globalPhase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.RAND_MAX
import platform.posix.rand
import platform.posix.sin
import kotlin.math.PI


class WaveGenerator {
    @OptIn(ExperimentalForeignApi::class)
    private var phase = globalPhase
    fun generate(frequency: Double, sampleRate: Double, waveForm: WAVEFORM = WAVEFORM.SINE): Float {
        when (waveForm) {
            WAVEFORM.SINE -> {
                val sample = sin(phase).toFloat()
                val phaseIncrement = 2.0 * PI * frequency / sampleRate
                phase += phaseIncrement
                if (phase >= 2.0 * PI) phase -= 2.0 * PI
                return sample
            }
            WAVEFORM.NOISE-> {
                return rand() / RAND_MAX.toFloat() * 2.0f - 1.0f
            }
            WAVEFORM.SQUARE -> {
                val sample = if (sin(phase) >= 0) 1.0f else -1.0f
                val phaseIncrement = 2.0 * PI * frequency / sampleRate
                phase += phaseIncrement
                if (phase >= 2.0 * PI) phase -= 2.0 * PI
                return sample
            }
            WAVEFORM.SAWTOOTH -> {
                val sample = (2.0 * (phase / (2.0 * PI)) - 1.0).toFloat()
                val phaseIncrement = 2.0 * PI * frequency / sampleRate
                phase += phaseIncrement
                if (phase >= 2.0 * PI) phase -= 2.0 * PI
                return sample
            }
            WAVEFORM.TRIANGLE -> {
                val sample = (2.0 * kotlin.math.abs(2.0 * (phase / (2.0 * PI)) - 1.0) - 1.0).toFloat()
                val phaseIncrement = 2.0 * PI * frequency / sampleRate
                phase += phaseIncrement
                if (phase >= 2.0 * PI) phase -= 2.0 * PI
                return sample
            }
        }
    }
}
