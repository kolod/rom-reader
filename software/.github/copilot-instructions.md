# ROM Reader Project - Kotlin Desktop Application

## Project Summary
- **Language**: Kotlin
- **UI Framework**: Swing with FlatLaf Look and Feel
- **Build System**: Gradle with Kotlin DSL
- **Target Compatibility**: Java 8
- **Platforms**: Windows and Linux

## Features Implemented
- [x] File selection with browse button for HEX files
- [x] Serial port detection and selection with refresh capability
- [x] Hex viewer with ASCII representation
- [x] READ functionality for ROM data via serial port
- [x] VERIFY functionality to compare ROM data with files
- [x] Intel HEX format parsing and generation
- [x] Modern UI with FlatLaf theme
- [x] Comprehensive error handling and logging
- [x] Cross-platform compatibility

## Development Status
- [x] Project scaffolded with proper Gradle configuration
- [x] Main application implemented with all required features
- [x] Build system configured for Java 8 compatibility
- [x] Dependencies configured (jSerialComm, FlatLaf, Log4j2)
- [x] Application successfully compiled and tested
- [x] Task configuration created for easy running
- [x] Documentation completed

## Usage
Run the application using: `./gradlew run` (Linux/Mac) or `gradlew.bat run` (Windows)

## Architecture
- **Main Class**: `io.github.kolod.RomReaderApp`
- **Serial Communication**: Uses jSerialComm library
- **Data Format**: Intel HEX format support
- **UI Layout**: BorderLayout with organized panels
- **Concurrency**: CompletableFuture for async operations