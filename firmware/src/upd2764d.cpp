#include "Arduino.h"
#include "upd2764d.h"

// Initialize the uPD2764D ROM interface
void rom_init() {
    // Set address pins as outputs
    pinMode(ROM_A0 , OUTPUT);
    pinMode(ROM_A1 , OUTPUT);
    pinMode(ROM_A2 , OUTPUT);
    pinMode(ROM_A3 , OUTPUT);
    pinMode(ROM_A4 , OUTPUT);
    pinMode(ROM_A5 , OUTPUT);
    pinMode(ROM_A6 , OUTPUT);
    pinMode(ROM_A7 , OUTPUT);
    pinMode(ROM_A8 , OUTPUT);
    pinMode(ROM_A9 , OUTPUT);
    pinMode(ROM_A10, OUTPUT);
    pinMode(ROM_A11, OUTPUT);

    // Set data pins as inputs
    pinMode(ROM_D0 , INPUT);
    pinMode(ROM_D1 , INPUT);
    pinMode(ROM_D2 , INPUT);
    pinMode(ROM_D3 , INPUT);
    pinMode(ROM_D4 , INPUT);
    pinMode(ROM_D5 , INPUT);
    pinMode(ROM_D6 , INPUT);
    pinMode(ROM_D7 , INPUT);

    // Set control pins to inactive states
    digitalWrite(ROM_CE, HIGH); // Chip Enable inactive
    digitalWrite(ROM_OE, HIGH); // Output Enable inactive
    digitalWrite(ROM_WE, HIGH); // Write Enable inactive

    // Set control pins as outputs
    pinMode(ROM_CE, OUTPUT);
    pinMode(ROM_OE, OUTPUT);
    pinMode(ROM_WE, OUTPUT);
}

// Read a byte from the uPD2764D ROM
uint8_t rom_read(uint16_t address) {
    // Set address lines
    digitalWrite(ROM_A0 , (address >>  0) & 1);
    digitalWrite(ROM_A1 , (address >>  1) & 1);
    digitalWrite(ROM_A2 , (address >>  2) & 1);
    digitalWrite(ROM_A3 , (address >>  3) & 1);
    digitalWrite(ROM_A4 , (address >>  4) & 1);
    digitalWrite(ROM_A5 , (address >>  5) & 1);
    digitalWrite(ROM_A6 , (address >>  6) & 1);
    digitalWrite(ROM_A7 , (address >>  7) & 1);
    digitalWrite(ROM_A8 , (address >>  8) & 1);
    digitalWrite(ROM_A9 , (address >>  9) & 1);
    digitalWrite(ROM_A10, (address >> 10) & 1);
    digitalWrite(ROM_A11, (address >> 11) & 1);

    // Enable the ROM
    delayMicroseconds(10); // Address setup time
    digitalWrite(ROM_CE, LOW);
    delayMicroseconds(10); // Chip enable to output enable time
    digitalWrite(ROM_OE, LOW);
    delayMicroseconds(100); // Output enable to data valid time

    // Read data lines
    uint8_t data = 0;
    data |= digitalRead(ROM_D0) << 0;
    data |= digitalRead(ROM_D1) << 1;
    data |= digitalRead(ROM_D2) << 2;
    data |= digitalRead(ROM_D3) << 3;
    data |= digitalRead(ROM_D4) << 4;
    data |= digitalRead(ROM_D5) << 5;
    data |= digitalRead(ROM_D6) << 6;
    data |= digitalRead(ROM_D7) << 7;
    delayMicroseconds(10); // Data hold time

    // Disable the ROM
    digitalWrite(ROM_OE, HIGH);
    digitalWrite(ROM_CE, HIGH);
    delayMicroseconds(10); // Chip disable time

    return data;
}

// Verify that the uPD2764D ROM is empty (all bytes are 0xFF)
// Returns position of first non-empty byte or 0 if all bytes are 0xFF
// Returns memory size (8192) if all bytes are 0xFF
uint16_t rom_is_empty() {
    uint16_t address;
    for (address = 0; address < 8192; address++)
        if (rom_read(address) != 0xFF) break;
    return address;
}

// Read all bytes from the uPD2764D ROM to Serial in Intel HEX format
void print_rom_contents() {
    uint16_t address;
    uint8_t  data, checksum;

    for (address = 0; address < 8192; address += 16) {
        // Start code
        Serial.print(':');

        // Byte count
        Serial.print("10"); // 16 bytes
        checksum = 0x10;

        // Address
        Serial.print((address >> 8) & 0xFF, HEX);
        Serial.print(address & 0xFF, HEX);
        checksum += (address >> 8) & 0xFF;
        checksum += address & 0xFF;

        // Record type
        Serial.print("00"); // Data record
        checksum += 0x00;

        // Data bytes
        for (uint8_t i = 0; i < 16; i++) {
            data = rom_read(address + i);
            if (data < 0x10) Serial.print('0'); // Leading zero for single digit
            Serial.print(data, HEX);
            checksum += data;
        }

        // Checksum
        checksum = (~checksum + 1) & 0xFF; // Two's complement
        if (checksum < 0x10) Serial.print('0'); // Leading zero for single digit
        Serial.println(checksum, HEX);
    }

    // End of file record
    Serial.println(":00000001FF");
}
