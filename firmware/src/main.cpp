#include <Arduino.h>
#include "upd2764d.h"

// The setup function runs once when you press reset or power the board
void setup() {
    // Initialize the built-in LED pin
    pinMode(LED_BUILTIN, OUTPUT);
    digitalWrite(LED_BUILTIN, LOW);

    // Start serial communication
    Serial.begin(115200);
    while (!Serial);

    // Initialize the ROM interface
    rom_init();

    // Read and print the ROM contents to Serial in Intel HEX format
    print_rom_contents();
}

// The loop function runs over and over again forever
void loop() {
    // Blink the built-in LED to indicate the program is running
    digitalWrite(LED_BUILTIN, HIGH);
    delay(500);
    digitalWrite(LED_BUILTIN, LOW);
    delay(500);
}
