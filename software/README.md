# ROM Reader

A Kotlin desktop application for reading ROM chips via serial port communication. This application provides a user-friendly Swing GUI interface for reading ROM data in Intel HEX format and comparing it with existing files.

## Features

- **Cross-platform compatibility**: Works on Windows and Linux
- **Java 8 runtime compatibility**: Built with Java 8 target compatibility
- **Serial port communication**: Automatic detection and selection of available serial ports
- **Intel HEX format support**: Reads and writes data in Intel HEX format
- **Hex viewer**: Built-in hex viewer with ASCII representation
- **File operations**: Save ROM data to file and load for verification
- **Verification mode**: Compare ROM data with existing files and highlight differences
- **Modern UI**: Uses FlatLaf look and feel for a modern appearance

## Requirements

- Java 8 or higher
- Serial port connection to ROM reader device
- Windows or Linux operating system

## Building the Application

### Prerequisites

- JDK 8 or higher installed
- Gradle wrapper is included in the project

### Build Commands

```bash
# Build the application
./gradlew build

# Run the application
./gradlew run

# Create a fat JAR with all dependencies (recommended)
./gradlew shadowJar

# Create a standard JAR (requires classpath)
./gradlew jar
```

### Windows

```cmd
# Build the application
gradlew.bat build

# Run the application
gradlew.bat run

# Create a fat JAR with all dependencies (recommended)
gradlew.bat shadowJar

# Create a standard JAR (requires classpath)
gradlew.bat jar
```

## Usage

1. **Start the application**: Run the JAR file or use `./gradlew run`

2. **Select a file**: Click "Browse..." to select a HEX file for saving ROM data or verification

3. **Choose serial port**: Select the appropriate serial port from the dropdown. Use "Refresh" to update the list if devices are connected/disconnected

4. **Read ROM data**:
   - Click "READ" to read data from the ROM chip via serial port
   - Data will be saved to the selected file and displayed in the hex viewer

5. **Verify ROM data**:
   - Select a HEX file containing expected data
   - Click "VERIFY" to read ROM data and compare with the file
   - Differences will be highlighted in the hex viewer

## Deployment

### Fat JAR (Recommended)
The application uses the Shadow JAR plugin to create a self-contained executable JAR:

```bash
# Build the fat JAR
./gradlew shadowJar

# The JAR will be created in build/libs/
# Run with: java -jar build/libs/rom-reader-<version>.jar
```

**Benefits of Shadow JAR:**
- ✅ All dependencies included in a single JAR file
- ✅ No classpath configuration required
- ✅ Easy distribution and deployment
- ✅ Minimized JAR size (unused classes removed)
- ✅ Cross-platform compatibility

## Serial Communication Protocol

The application expects the ROM reader device to:

1. Accept a "READ\n" command via serial port (115200 baud, 8N1)
2. Respond with Intel HEX formatted data
3. End transmission with the standard Intel HEX end-of-file record (`:00000001FF`)

## Intel HEX Format

The application supports standard Intel HEX format with the following record types:
- `00`: Data records
- `01`: End of file record
- `04`: Extended linear address records

## Configuration

### Serial Port Settings
- Baud rate: 115200
- Data bits: 8
- Stop bits: 1
- Parity: None
- Timeout: 5 seconds

## Development

### Project Structure

```
src/
├── main/
│   ├── kotlin/
│   │   └── io/github/kolod/
│   │       └── RomReader.kt          # Main application
│   └── resources/
│       └── log4j2.xml                # Logging configuration
├── build.gradle.kts                  # Build configuration
├── settings.gradle.kts               # Gradle settings
└── README.md                         # This file
```

### Dependencies

- **Kotlin**: Programming language
- **jSerialComm**: Serial port communication library
- **FlatLaf**: Modern look and feel
- **Log4j2**: Logging framework
- **Shadow JAR**: Fat JAR creation plugin
- **JUnit**: Testing framework

## License

This project is open source. Please check the license file for more details.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## Troubleshooting

### Common Issues

1. **No serial ports detected**: 
   - Ensure the ROM reader device is connected
   - Check device drivers are installed
   - Click "Refresh" to update the port list

2. **Cannot open serial port**:
   - Port might be in use by another application
   - Check port permissions on Linux
   - Try a different port or restart the device

3. **No data received**:
   - Check serial cable connections
   - Verify ROM reader device is powered and functional
   - Ensure correct baud rate and settings

4. **Invalid HEX data**:
   - Check the ROM reader firmware outputs valid Intel HEX format
   - Verify the end-of-file record is sent

### Logging

Application logs are written to:
- Console output
- `rom-reader.log` file in the application directory

Check the log file for detailed error information and debugging.