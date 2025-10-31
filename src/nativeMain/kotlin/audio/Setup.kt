import audio.WaveGenerator
import kotlinx.cinterop.*
import portaudio.*
import kotlin.math.PI

private val sinwave = WaveGenerator()

const val SAMPLE_RATE = 48000.0
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
class AudioSetup {
    private var stream: COpaquePointer? = null

    fun initialize() {
        val err = Pa_Initialize()
        check(err == paNoError) { "PortAudio init failed: ${Pa_GetErrorText(err)?.toKString()}" }
    }

    fun openDefaultOutput() = memScoped {
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

        val streamVar = alloc<COpaquePointerVar>()
        val openErr = Pa_OpenStream(
            streamVar.ptr,
            null,
            outputParams.ptr,
            SAMPLE_RATE,
            FRAMES_PER_BUFFER,
            paClipOff,
            staticCFunction(::audioCallback),
            null
        )
        check(openErr == paNoError) { "OpenStream failed: ${Pa_GetErrorText(openErr)?.toKString()}" }
        stream = streamVar.value
    }

    fun start() {
        check(stream != null) { "Stream is not opened" }
        Pa_StartStream(stream)
    }

    fun stop() {
        stream?.let { Pa_StopStream(it) }
    }

    fun close() {
        stream?.let { Pa_CloseStream(it) }
        stream = null
    }

    fun terminate() {
        Pa_Terminate()
    }

    fun destroy() {
        stop()
        close()
        terminate()
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
        out[i.toInt()] = sinwave.generate(freq, sampleRate, WAVEFORM.SINE)
        phase += phaseIncrement
    }
    globalPhase = phase
    return paContinue.toInt()
}
