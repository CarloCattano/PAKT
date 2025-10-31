import kotlinx.cinterop.*
import platform.posix.srand
import platform.posix.time
import portaudio.*

@OptIn(ExperimentalForeignApi::class)
fun main(args: Array<String>) { memScoped {

    srand(time(null).toUInt())

    val audio = AudioSetup()

    audio.initialize()
    audio.openDefaultOutput()
    audio.start()

    println("Playing sine audio...")
    Pa_Sleep(2000)          // Play for 2 seconds - blocking

    audio.destroy()

    println("PortAudio Terminated!")
}
}