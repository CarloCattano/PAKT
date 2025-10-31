import kotlinx.cinterop.*
import platform.posix.srand
import platform.posix.time

import portaudio.*
import kotlin.math.PI
import wave.WaveGenerator

val SAMPLE_RATE = 48000.0
val FRAMES_PER_BUFFER = 256UL

var globalPhase = 0.0
private val sinwave = WaveGenerator()

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

    val err = Pa_Initialize()
    check(err == paNoError) {
        "PortAudio init failed: ${Pa_GetErrorText(err)?.toKString()}"
    }

    val defaultDevice = Pa_GetDefaultOutputDevice()
    val deviceInfo = Pa_GetDeviceInfo(defaultDevice)

    println("Using device: ${deviceInfo?.pointed?.name?.toKString()}")

    val outputParams = alloc<PaStreamParameters>().apply {
        device = defaultDevice
        channelCount = 1
        sampleFormat = paFloat32
        suggestedLatency = deviceInfo!!.pointed.defaultLowOutputLatency
        hostApiSpecificStreamInfo = null
    }
    // Allocate a pointer to hold the stream handle (opaque pointer)
    val stream = alloc<COpaquePointerVar>()

    val openErr = Pa_OpenStream(
        stream.ptr,
        null,
        outputParams.ptr,
        SAMPLE_RATE,
        FRAMES_PER_BUFFER,
        paClipOff,
        staticCFunction(::audioCallback),
        null
    )
    check(openErr == paNoError) { "OpenStream failed: ${Pa_GetErrorText(openErr)?.toKString()}" }

    Pa_StartStream(stream.value)
    println("Playing sine wave...")
    Pa_Sleep(2000)
    Pa_StopStream(stream.value)
    Pa_CloseStream(stream.value)

    Pa_Terminate()
    println("PortAudio Terminated!")
}
}

@OptIn(ExperimentalForeignApi::class)
private fun audioCallback(
    input: CPointer<*>?,
    output: CPointer<*>?,
    frameCount: ULong,
    timeInfo: CPointer<PaStreamCallbackTimeInfo>?,
    statusFlags: PaStreamCallbackFlags,
    userData: COpaquePointer?
): Int {
    val out = output!!.reinterpret<FloatVar>()
    val freq = 440.0
    val sampleRate = SAMPLE_RATE
    val phaseIncrement = 2.0 * PI * freq / sampleRate

    var phase = globalPhase

    // Per sample processing
    for (i in 0UL until frameCount) {
        out[i.toInt()] = sinwave.generate(freq, sampleRate, WAVEFORM.SAWTOOTH)
    }
    globalPhase = phase
    return paContinue.toInt()
}