## General Instructions:

Follow the code style and analyze and read all the files in the project before making changes.
use commands like find, grep or anything you need to understand the codebase.

## Coding Style:

- Follow the existing coding conventions used throughout the project.
- Ensure proper indentation, spacing, and naming conventions are maintained.
- Cinteroperability is pretty recent , so dont assume just follow from what you see

## Specific Component:

This project uses portaudio C library for audio processing. Make sure to understand how the C interop is done in this project before making changes to audio-related code.

-Review the current code that works and produces a sine wave using portaudio. in Main.tk

# Tasks:

1. Modularize the code to keep a clean main. Refactor sine wave generation into a general WaveForm class, implement
   also SquareWave and TriangleWave, Noise Wave classes inheriting from WaveForm.
2. Implement a WaveFactory class that can create instances of different waveforms based on input parameters.
3. Update the main application logic to utilize the WaveFactory for waveform generation.
