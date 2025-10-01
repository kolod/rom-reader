#include <Arduino.h>
#include "upd2764d.h"

// The setup function runs once when you press reset or power the board
void setup() {
    Serial.begin(115200);
    while (!Serial);
    Serial.println("uPD2764D ROM Reader");
    rom_init();
}

// The loop function runs over and over again forever
void loop() {
    // Verify that the ROM is empty
    Serial.print("Checking if ROM is empty...");
    uint16_t empty_address = rom_is_empty();
    if (empty_address == 8192) {
        Serial.println("ROM is empty.");
    } else {
        Serial.print("ROM is empty. First non-empty byte at address: ");
        Serial.println(empty_address);
        print_rom_contents();
    }
    delay(1000); // Wait for a second before the next check
}
