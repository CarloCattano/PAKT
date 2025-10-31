import kotlinx.cinterop.*
import platform.posix.srand
import platform.posix.time

import portaudio.*
import kotlin.math.PI
import wave.WaveGenerator

val SAMPLE_RATE = 48000.0
val FRAMES_PER_BUFFER = 256UL

var globalPhase = 0.0

enum class WAVEFORM {
    SINE,
    NOISE,
    SQUARE,
    SAWTOOTH,
    TRIANGLE
}

@OptIn(ExperimentalForeignApi::class)
fun main(args: Array<String>) { memScoped {

    // init posix random
    srand(time(null).toUInt())

    val audio = AudioSetup()

    audio.initialize()
    audio.openDefaultOutput()
    audio.start()

    println("Playing sine wave...")
    Pa_Sleep(2000)          // Play for 2 seconds - blocking

    audio.stop()
    audio.close()
    audio.terminate()

    println("PortAudio Terminated!")
}
}