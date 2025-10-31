[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)

# PortAudio

PortAudio Kotlin Bindings could allow for multiplatform access to the [PortAudio](http://www.portaudio.com/) audio I/O library from Kotlin.

## Status

- Basic audio output: ✅ in Main.kt
- Waveform selection: ✅ in Main.kt
- Audio Setup abstracted: ✅ in AudioSetup.kt
- Input support: ❌
- Advanced audio processing: ❌

## Requirements

PortAudio headers should be installed on your system.

## Usage:
```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
./gradlew runDebugExecutableNative
```

A sound will play for 2 seconds and then stop.

tested on:

- Linux ✅
- Windows ❌
- MacOS ❌
