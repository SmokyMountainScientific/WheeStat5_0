/*
  Sketch for testing MCP 4231 digital potentiostat with volatile memory.
  modified from the Digital Pot Control example that comes with Energia
    Originally by Tom Igoe, Rick Kimball, Heather Dewey-Hagborg, 2005 to 12
    Spi module 0 requires SPI library from Reaper7. 
    
 
 The circuit:
 CS, SCK, and SDI attached to respective module zero pins (CS(0), SCK(0), MISO(0)}
  Pin P0A connected to VCC,
  Pins P0W, P0B, P1W and P1A all connected,
  Pin P1B connected to A11 with a 10 Kohm resister between A11 and GND.
  This gives:
    VCC-R1-(R2, analogRead)-R3 (10Kohm)-GND
    by cycling values to the two pots, values of R1 and R2 can be changed.
    Values of R1 and R2 can be determined from serial output of analog signal.
   * MOSI - to J1 pin 8 
  * CLK - to J2 pin 11 (SCK pin) P1.5
  
*/
#include<Energia.h>  // required to get spi to work.
#include <SPI.h>  // include the SPI library from Stellarisiti
#include "wiring_analog.c"


   int gain0;
   int gain1;
   unsigned int address1 = 16;
   unsigned int address0 = 0;
#define Iread_pin  A11              //J1 pin 2, Analog read current, was A3  

unsigned int iRead = 0;                 // hight pulse current read 
 
void setup() {

  SPI.setModule(0);
  SPI.begin(); 
  SPI.setClockDivider(SPI_CLOCK_DIV64);
  SPI.setBitOrder(MSBFIRST);
  SPI.setDataMode(SPI_MODE0);
 
  pinMode (Iread_pin,INPUT);


  Serial.begin(9600);             // begin serial comm. at 9600 baud
///////// header //////
Serial.print("gain 0");    
Serial.print('\t');
Serial.print("gain 1");
Serial.print('\t');
Serial.println("Iread"); 
   }

void loop() {
 ///////// ramp up R0 holding R1 constant ////////
  for (int m=0; m<128; ++m) {
   gain1 = m;
   gain0=64;
      digitalPotWrite(16,gain1);  //write to channel 1
      digitalPotWrite(0,gain0);    // write to channel 0
   
    Serial.print(gain0);    
    Serial.print('\t');
    Serial.print(gain1);
    Serial.print('\t');
 
    delay(20);   
    readVolts(); 
  }
  for (int n=0; n<128; ++n) {
    gain0 = n;
    gain1 = 64;
       digitalPotWrite(address1,gain1);  //write to channel 1
      digitalPotWrite(address0,gain0);    // write to channel 0
   
    Serial.print(gain0);    
    Serial.print('\t');
    Serial.print(gain1);
    Serial.print('\t');
    
    readVolts(); 
  }
} 

void digitalPotWrite(int address, int value) {
//int digitalPotWrite(int address, int value) {
  SPI.trans2ByteA(address);   
  SPI.trans2ByteB(value);  
  
}

void readVolts() {
   unsigned int iRead = 0;               
           for (int i =0; i<=15; ++i){  
         iRead += analogRead(Iread_pin);}
         int mVi = (iRead)/16;
         mVi = mVi*3558/4098;          // digital reading converted to mV
         Serial.println(mVi);  

           
         }
