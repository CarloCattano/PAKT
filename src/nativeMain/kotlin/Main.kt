import kotlinx.cinterop.*
import platform.posix.sin
import portaudio.*
import kotlin.math.PI

val SAMPLE_RATE = 48000.0
val FRAMES_PER_BUFFER = 256UL
private var globalPhase = 0.0

@OptIn(ExperimentalForeignApi::class)
fun main(args: Array<String>) { memScoped {
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
        staticCFunction(::sineCallback),
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
private fun sineCallback(
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

    for (i in 0UL until frameCount) {

        out[i.toInt()] = sin(phase).toFloat()

        phase += phaseIncrement
        if (phase >= 2.0 * PI) phase -= 2.0 * PI
    }
    globalPhase = phase
    return paContinue.toInt()
}