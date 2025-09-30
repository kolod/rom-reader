package io.github.kolod

import com.fazecast.jSerialComm.SerialPort
import com.formdev.flatlaf.FlatLightLaf
import org.apache.logging.log4j.LogManager
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.io.File
import java.util.concurrent.CompletableFuture
import javax.swing.*
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.math.max

/**
 * ROM Reader - Kotlin desktop application for reading ROM chips via serial port
 * Supports Intel HEX format and provides hex viewer functionality
 */
class RomReaderApp : JFrame() {
    private val logger = LogManager.getLogger()
    
    // UI Components
    private val filePathField = JTextField(30)
    private val browseButton = JButton("Browse...")
    private val serialPortCombo = JComboBox<String>()
    private val refreshPortsButton = JButton("Refresh")
    private val hexViewer = JTextArea()
    private val readButton = JButton("READ")
    private val verifyButton = JButton("VERIFY")
    private val statusLabel = JLabel("Ready")
    
    // Data
    private var romData = ByteArray(0)
    private var selectedFile: File? = null
    
    init {
        initializeComponents()
        setupLayout()
        setupEventHandlers()
        refreshSerialPorts()
        
        defaultCloseOperation = EXIT_ON_CLOSE
        title = "ROM Reader v1.0"
        minimumSize = Dimension(700, 500)
        setLocationRelativeTo(null)
    }
    

    
    private fun initializeComponents() {
        // File selection components
        filePathField.isEditable = false
        filePathField.toolTipText = "Select a file to save/verify ROM data"
        
        // Serial port components
        serialPortCombo.toolTipText = "Select serial port for ROM communication"
        refreshPortsButton.toolTipText = "Refresh available serial ports"
        
        // Hex viewer
        hexViewer.isEditable = false
        hexViewer.font = Font(Font.MONOSPACED, Font.PLAIN, 12)
        hexViewer.background = Color.WHITE
        
        // Action buttons
        readButton.toolTipText = "Read ROM data from serial port and save to file"
        verifyButton.toolTipText = "Read ROM data and compare with selected file"
        verifyButton.isEnabled = false
        
        // Status label
        statusLabel.border = BorderFactory.createLoweredBevelBorder()
    }
    
    private fun setupLayout() {
        layout = BorderLayout()
        
        // Top panel - File selection
        val filePanel = JPanel(FlowLayout(FlowLayout.LEFT))
        filePanel.add(JLabel("File:"))
        filePanel.add(filePathField)
        filePanel.add(browseButton)
        
        // Serial port panel
        val serialPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        serialPanel.add(JLabel("Serial Port:"))
        serialPanel.add(serialPortCombo)
        serialPanel.add(refreshPortsButton)
        
        // Top container
        val topPanel = JPanel(BorderLayout())
        topPanel.add(filePanel, BorderLayout.NORTH)
        topPanel.add(serialPanel, BorderLayout.SOUTH)
        
        // Center panel - Hex viewer
        val hexScrollPane = JScrollPane(hexViewer)
        hexScrollPane.border = BorderFactory.createTitledBorder("ROM Data (Hex View)")
        
        // Bottom panel - Action buttons
        val buttonPanel = JPanel(FlowLayout())
        buttonPanel.add(readButton)
        buttonPanel.add(verifyButton)
        
        // Status panel
        val statusPanel = JPanel(BorderLayout())
        statusPanel.add(statusLabel, BorderLayout.WEST)
        
        add(topPanel, BorderLayout.NORTH)
        add(hexScrollPane, BorderLayout.CENTER)
        add(buttonPanel, BorderLayout.SOUTH)
        add(statusPanel, BorderLayout.PAGE_END)
    }
    
    private fun setupEventHandlers() {
        browseButton.addActionListener { selectFile() }
        refreshPortsButton.addActionListener { refreshSerialPorts() }
        readButton.addActionListener { performRead() }
        verifyButton.addActionListener { performVerify() }
        
        // Auto-refresh serial ports when window gains focus
        addComponentListener(object : ComponentAdapter() {
            override fun componentShown(e: ComponentEvent?) {
                refreshSerialPorts()
            }
        })
    }
    
    private fun selectFile() {
        val fileChooser = JFileChooser()
        fileChooser.fileFilter = FileNameExtensionFilter("HEX files (*.hex)", "hex")
        fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.selectedFile
            filePathField.text = selectedFile?.absolutePath ?: ""
            verifyButton.isEnabled = selectedFile != null
            setStatus("File selected: ${selectedFile?.name}")
        }
    }
    
    private fun refreshSerialPorts() {
        val currentSelection = serialPortCombo.selectedItem
        serialPortCombo.removeAllItems()
        
        val ports = SerialPort.getCommPorts()
        if (ports.isEmpty()) {
            serialPortCombo.addItem("No ports available")
            serialPortCombo.isEnabled = false
            readButton.isEnabled = false
            verifyButton.isEnabled = false
        } else {
            serialPortCombo.isEnabled = true
            for (port in ports) {
                serialPortCombo.addItem("${port.systemPortName} - ${port.descriptivePortName}")
            }
            
            // Try to restore previous selection
            if (currentSelection != null) {
                for (i in 0 until serialPortCombo.itemCount) {
                    if (serialPortCombo.getItemAt(i) == currentSelection) {
                        serialPortCombo.selectedIndex = i
                        break
                    }
                }
            }
            
            readButton.isEnabled = true
            verifyButton.isEnabled = selectedFile != null
        }
        
        setStatus("Found ${ports.size} serial port(s)")
    }
    
    private fun performRead() {
        if (serialPortCombo.selectedIndex < 0 || serialPortCombo.selectedItem == "No ports available") {
            showError("No serial port selected")
            return
        }
        
        if (selectedFile == null) {
            showError("No file selected")
            return
        }
        
        setStatus("Reading ROM data...")
        readButton.isEnabled = false
        verifyButton.isEnabled = false
        
        CompletableFuture.supplyAsync {
            try {
                val portName = getSelectedPortName()
                val data = readRomData(portName)
                data
            } catch (e: Exception) {
                logger.error("Error reading ROM data", e)
                throw e
            }
        }.thenAccept { data ->
            SwingUtilities.invokeLater {
                try {
                    romData = data
                    saveDataToFile(data, selectedFile!!)
                    updateHexViewer(data)
                    setStatus("ROM data read and saved successfully (${data.size} bytes)")
                } catch (e: Exception) {
                    showError("Error saving data: ${e.message}")
                } finally {
                    readButton.isEnabled = true
                    verifyButton.isEnabled = selectedFile != null
                }
            }
        }.exceptionally { throwable ->
            SwingUtilities.invokeLater {
                showError("Error reading ROM: ${throwable.message}")
                readButton.isEnabled = true
                verifyButton.isEnabled = selectedFile != null
            }
            null
        }
    }
    
    private fun performVerify() {
        if (serialPortCombo.selectedIndex < 0 || serialPortCombo.selectedItem == "No ports available") {
            showError("No serial port selected")
            return
        }
        
        if (selectedFile == null || !selectedFile!!.exists()) {
            showError("No valid file selected for verification")
            return
        }
        
        setStatus("Reading ROM data for verification...")
        readButton.isEnabled = false
        verifyButton.isEnabled = false
        
        CompletableFuture.supplyAsync {
            try {
                val portName = getSelectedPortName()
                val romData = readRomData(portName)
                val fileData = loadDataFromFile(selectedFile!!)
                Pair(romData, fileData)
            } catch (e: Exception) {
                logger.error("Error during verification", e)
                throw e
            }
        }.thenAccept { (romData, fileData) ->
            SwingUtilities.invokeLater {
                try {
                    this.romData = romData
                    val differences = compareData(romData, fileData)
                    updateHexViewerWithComparison(romData, fileData, differences)
                    
                    if (differences.isEmpty()) {
                        setStatus("Verification successful - ROM data matches file (${romData.size} bytes)")
                    } else {
                        setStatus("Verification failed - Found ${differences.size} differences")
                    }
                } finally {
                    readButton.isEnabled = true
                    verifyButton.isEnabled = true
                }
            }
        }.exceptionally { throwable ->
            SwingUtilities.invokeLater {
                showError("Error during verification: ${throwable.message}")
                readButton.isEnabled = true
                verifyButton.isEnabled = true
            }
            null
        }
    }
    
    private fun getSelectedPortName(): String {
        val selectedItem = serialPortCombo.selectedItem as String
        return selectedItem.split(" - ")[0]
    }
    
    private fun readRomData(portName: String): ByteArray {
        val port = SerialPort.getCommPort(portName)
        
        try {
            // Configure port
            port.baudRate = 115200
            port.numDataBits = 8
            port.numStopBits = 1
            port.parity = SerialPort.NO_PARITY
            port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 5000, 0)
            
            if (!port.openPort()) {
                throw Exception("Failed to open serial port $portName")
            }
            
            // Send read command (this would be specific to your ROM reader protocol)
            port.writeBytes("READ\n".toByteArray(), "READ\n".length.toLong())
            
            // Read Intel HEX format data
            val hexData = StringBuilder()
            val buffer = ByteArray(1024)
            
            while (true) {
                val bytesRead = port.readBytes(buffer, buffer.size.toLong())
                if (bytesRead > 0) {
                    val chunk = String(buffer, 0, bytesRead)
                    hexData.append(chunk)
                    
                    // Check for end of file record (:00000001FF)
                    if (hexData.contains(":00000001FF")) {
                        break
                    }
                } else {
                    // Timeout - check if we have any data
                    if (hexData.isEmpty()) {
                        throw Exception("No data received from ROM reader")
                    }
                    break
                }
            }
            
            return parseIntelHex(hexData.toString())
            
        } finally {
            if (port.isOpen) {
                port.closePort()
            }
        }
    }
    
    private fun parseIntelHex(hexData: String): ByteArray {
        val data = mutableListOf<Byte>()
        var baseAddress = 0
        
        val lines = hexData.split("\n")
        for (line in lines) {
            val trimmedLine = line.trim()
            if (trimmedLine.isEmpty() || !trimmedLine.startsWith(":")) {
                continue
            }
            
            try {
                val byteCount = trimmedLine.substring(1, 3).toInt(16)
                val address = trimmedLine.substring(3, 7).toInt(16)
                val recordType = trimmedLine.substring(7, 9).toInt(16)
                
                when (recordType) {
                    0x00 -> { // Data record
                        val fullAddress = baseAddress + address
                        // Ensure data list is large enough
                        while (data.size < fullAddress + byteCount) {
                            data.add(0xFF.toByte()) // Fill with 0xFF (typical for unprogrammed ROM)
                        }
                        
                        // Extract data bytes
                        for (i in 0 until byteCount) {
                            val byteValue = trimmedLine.substring(9 + i * 2, 11 + i * 2).toInt(16).toByte()
                            if (fullAddress + i < data.size) {
                                data[fullAddress + i] = byteValue
                            }
                        }
                    }
                    0x01 -> { // End of file
                        break
                    }
                    0x04 -> { // Extended linear address
                        baseAddress = (trimmedLine.substring(9, 13).toInt(16)) shl 16
                    }
                }
            } catch (e: Exception) {
                logger.warn("Error parsing hex line: $trimmedLine", e)
            }
        }
        
        return data.toByteArray()
    }
    
    private fun saveDataToFile(data: ByteArray, file: File) {
        val hexContent = generateIntelHex(data)
        file.writeText(hexContent)
    }
    
    private fun loadDataFromFile(file: File): ByteArray {
        val hexContent = file.readText()
        return parseIntelHex(hexContent)
    }
    
    private fun generateIntelHex(data: ByteArray): String {
        val hex = StringBuilder()
        val bytesPerLine = 16
        
        var address = 0
        while (address < data.size) {
            val lineLength = minOf(bytesPerLine, data.size - address)
            val line = StringBuilder()
            
            // Record length
            line.append(String.format(":%02X", lineLength))
            
            // Address
            line.append(String.format("%04X", address))
            
            // Record type (00 = data)
            line.append("00")
            
            // Data bytes
            var checksum = lineLength + ((address shr 8) and 0xFF) + (address and 0xFF)
            for (i in 0 until lineLength) {
                val byteValue = data[address + i].toInt() and 0xFF
                line.append(String.format("%02X", byteValue))
                checksum += byteValue
            }
            
            // Checksum
            checksum = (256 - (checksum and 0xFF)) and 0xFF
            line.append(String.format("%02X", checksum))
            
            hex.append(line.toString()).append("\n")
            address += lineLength
        }
        
        // End of file record
        hex.append(":00000001FF\n")
        
        return hex.toString()
    }
    
    private fun compareData(data1: ByteArray, data2: ByteArray): List<Int> {
        val differences = mutableListOf<Int>()
        val maxSize = max(data1.size, data2.size)
        
        for (i in 0 until maxSize) {
            val byte1 = if (i < data1.size) data1[i] else 0xFF.toByte()
            val byte2 = if (i < data2.size) data2[i] else 0xFF.toByte()
            
            if (byte1 != byte2) {
                differences.add(i)
            }
        }
        
        return differences
    }
    
    private fun updateHexViewer(data: ByteArray) {
        hexViewer.text = formatHexData(data)
        hexViewer.caretPosition = 0
    }
    
    private fun updateHexViewerWithComparison(romData: ByteArray, fileData: ByteArray, differences: List<Int>) {
        // For now, just show the ROM data with a note about differences
        // In a more advanced implementation, you could use HTML formatting to highlight differences
        val text = formatHexData(romData)
        val fileText = formatHexData(fileData)
        if (differences.isNotEmpty()) {
            hexViewer.text = "--- DIFFERENCES FOUND AT ADDRESSES: ${differences.take(10).joinToString(", ")} ${if (differences.size > 10) "..." else ""} ---\n\n" +
                    "ROM DATA (${romData.size} bytes):\n$text\n\n" +
                    "FILE DATA (${fileData.size} bytes):\n$fileText"
        } else {
            hexViewer.text = "--- VERIFICATION SUCCESSFUL ---\n\n$text"
        }
        hexViewer.caretPosition = 0
    }
    
    private fun formatHexData(data: ByteArray): String {
        if (data.isEmpty()) {
            return "No data"
        }
        
        val result = StringBuilder()
        val bytesPerLine = 16
        
        for (i in data.indices step bytesPerLine) {
            // Address
            result.append(String.format("%08X: ", i))
            
            // Hex bytes
            for (j in 0 until bytesPerLine) {
                if (i + j < data.size) {
                    result.append(String.format("%02X ", data[i + j].toInt() and 0xFF))
                } else {
                    result.append("   ")
                }
                
                // Add extra space after 8 bytes
                if (j == 7) {
                    result.append(" ")
                }
            }
            
            // ASCII representation
            result.append(" |")
            for (j in 0 until bytesPerLine) {
                if (i + j < data.size) {
                    val byte = data[i + j].toInt() and 0xFF
                    val char = if (byte in 32..126) byte.toChar() else '.'
                    result.append(char)
                } else {
                    result.append(' ')
                }
            }
            result.append("|\n")
        }
        
        return result.toString()
    }
    
    private fun setStatus(message: String) {
        statusLabel.text = message
        logger.info(message)
    }
    
    private fun showError(message: String) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE)
        setStatus("Error: $message")
        logger.error(message)
    }
}

fun main() {
    // Set system properties for Log4j2
    System.setProperty("log4j2.configurationFile", "log4j2.xml")
    System.setProperty("log4j.skipJansi", "false")
    
    SwingUtilities.invokeLater {
        try {
            // Setup Look and Feel before creating any components
            setupLookAndFeel()
            val app = RomReaderApp()
            app.isVisible = true
        } catch (e: Exception) {
            e.printStackTrace()
            // Use system error stream in case logging isn't working
            System.err.println("Failed to start application: ${e.message}")
            JOptionPane.showMessageDialog(null, "Failed to start application: ${e.message}", "Error", JOptionPane.ERROR_MESSAGE)
        }
    }
}

private fun setupLookAndFeel() {
    try {
        // Ensure Look and Feel is set up properly
        System.setProperty("flatlaf.useWindowDecorations", "false")
        System.setProperty("flatlaf.menuBarEmbedded", "false")
        
        // Install FlatLaf
        FlatLightLaf.setup()
        
        // Verify the Look and Feel was set
        println("Current Look and Feel: ${UIManager.getLookAndFeel()?.name}")
        
    } catch (e: Exception) {
        System.err.println("Failed to setup FlatLaf: ${e.message}")
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
            println("Fallback to system Look and Feel: ${UIManager.getLookAndFeel()?.name}")
        } catch (fallbackException: Exception) {
            System.err.println("Failed to set system look and feel: ${fallbackException.message}")
        }
    }
}