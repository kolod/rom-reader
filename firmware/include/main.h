#pragma once

#include "stdint.h"
#include "stdbool.h"

// Pins

// SPI pins
#define PIN_SPI_SS    53 // PB0
#define PIN_SPI_SCK   52 // PB1
#define PIN_SPI_MOSI  51 // PB2
#define PIN_SPI_MISO  50 // PB3

// Internal LED pin
#define PIN_LED       13 // PB7

// uPD2764D pins
#define ROM_D0       22  // PA0
#define ROM_D1       23  // PA1
#define ROM_D2       24  // PA2
#define ROM_D3       25  // PA3
#define ROM_D4       26  // PA4
#define ROM_D5       27  // PA5
#define ROM_D6       28  // PA6
#define ROM_D7       29  // PA7

#define ROM_A0       37  // PC0
#define ROM_A1       36  // PC1
#define ROM_A2       35  // PC2
#define ROM_A3       34  // PC3
#define ROM_A4       33  // PC4
#define ROM_A5       32  // PC5
#define ROM_A6       69  // PK7
#define ROM_A7       68  // PK6
#define ROM_A8       67  // PK5
#define ROM_A9       66  // PK4
#define ROM_A10      65  // PK3
#define ROM_A11      64  // PK2

#define ROM_CE       63  // PK1
#define ROM_OE       62  // PK0
#define ROM_WE       61  // PF7