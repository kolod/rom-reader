# ROM Reader Project

[![CI/CD Pipeline](https://github.com/kolod/rom-reader/actions/workflows/ci.yml/badge.svg)](https://github.com/kolod/rom-reader/actions/workflows/ci.yml)
[![Manual Release](https://github.com/kolod/rom-reader/actions/workflows/manual-release.yml/badge.svg)](https://github.com/kolod/rom-reader/actions/workflows/manual-release.yml)

A complete ROM reader system consisting of Arduino firmware and Kotlin desktop application for reading and verifying ROM data.

## 🏗️ Build Status

The project uses GitHub Actions for continuous integration and deployment:

- **Automated CI/CD**: Builds and tests on every push to main/develop branches
- **Cross-platform testing**: Validates builds on both Windows and Linux
- **Automatic releases**: Creates GitHub releases when tags are pushed
- **Manual releases**: Supports manual release creation via workflow dispatch

## 📦 Releases

The CI system automatically creates releases containing:
- `firmware.hex` - Arduino firmware for the ROM reader hardware
- `rom-reader-{version}.jar` - Standalone desktop application

## 🔧 Development

### Firmware (Arduino/PlatformIO)
```bash
cd firmware
pio run              # Build firmware
pio run -t upload    # Upload to device
pio device monitor   # Monitor serial output
```

### Software (Kotlin/Gradle)
```bash
cd software
./gradlew shadowJar  # Build standalone JAR
./gradlew runJar     # Run the application
./gradlew test       # Run tests
```

## 🚀 Release Process

### Automatic Release (Recommended)
1. Create and push a version tag:
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```
2. GitHub Actions will automatically build and create the release

### Manual Release
1. Go to the "Actions" tab in GitHub
2. Select "Manual Release" workflow
3. Click "Run workflow"
4. Enter the desired tag and options
5. The workflow will build and create the release

## 📋 System Requirements

- **For development**: Windows or Linux with Java 8+, Python 3.11+
- **For running**: Java 8+ (the JAR includes all dependencies)
- **Hardware**: Arduino Mega 2560 with custom ROM reader shield

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Ensure all tests pass locally
5. Submit a pull request

The CI system will automatically test your changes on both Windows and Linux platforms.