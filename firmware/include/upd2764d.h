#pragma once

#include "main.h"

// Initialize the uPD2764D ROM interface
void rom_init();

// Read a byte from the uPD2764D ROM
uint8_t rom_read(uint16_t address);

// Verify that the uPD2764D ROM is empty (all bytes are 0xFF)
uint16_t rom_is_empty();

// Read all bytes from the uPD2764D ROM to Serial in Intel HEX format
void print_rom_contents();
