//digiPot tab:   / StellarisWheeStat sketch
//  for use with MCP 4231 digital potentiostat
//  working as of 10-23-13
//  Initial amplification should use maximum R6 to avoid clipping current data 
//  resistance between wiper and terminal B, Rwb =  10K * n/128  + Rw (page 38 in doc)
//  terminal B is closer to pin 1 than wiper, terminal A is furthest.
//  Pot 0 is wired with pin B tied to wiper, R4 = 10 * (1-F0) Kohm, where F0 is the fraction of pot 0 max.  
//  Pot 1 is wired wiht pin A tied to wiper, R5 = 10 * F1 Kohm, where F1 is the fraction of the pot 1 max.
//  Current amplification = R4 * R6 / R5 (R6 is currently 10 Kohms).
//  Voltage at Iread pin = 10 * (1-F0) / F1 

void  setupDigiPot()  {             
  //SPIClass mySPI;   //SPI stuff from stellaris forum
 //       mySPI.bits(16);
// gain0 = 120;
// gain0 = iGain;
// gain1 = 0;  //smaller is steeper
 SPI.setModule(0);
        SPI.begin();
        SPI.setClockDivider(SPI_CLOCK_DIV64);  // max clock speed = 10 MHz
        SPI.setBitOrder(MSBFIRST);
        SPI.setDataMode(SPI_MODE0);            // clock idles low, read on rising edge
 /*Serial.print("gain0 = ");
 Serial.println(gain0);
 Serial.print("gain1 = ");
 Serial.println(gain1);*/

  SPI.trans2ByteA(0);   // command 0; write to pot 1, resister 5
  SPI.trans2ByteB(gain0);   //larger number amplifies?

  SPI.trans2ByteA(16);   // command 16; write to pot 1, resister 5
  SPI.trans2ByteB(gain1);  //smaller number amplifies
}
/*
//////// header //////
Serial.print("address 0");    
Serial.print('\t');
Serial.print("address 1");
/*Serial.print('\t');
Serial.println("Iread"); 
  for (int b = 0; b<32; ++b) {
    int address0 = b;
   for (int c = 0; c<32; ++c) {
    int address1 = c;
 Serial.print(address0);    
Serial.print('\t');
Serial.print(address1);
Serial.print('\t');
 
      /////// trans2ByteA writes slave select pin low, transfers first byte, leaves slect pin low
      // //// trans2ByteB transfers second byte, writes slave select pin high
*/ 
 
//strippingVolt();
//}
    // Set gain to channels of digital pot mcp4261
//  unsigned int chan0w = 0;    //writes to channel 0 volatile memory   
//  unsigned int chan1w = 16;    //writes to channel 1 volatile memory 
                               // to write to channel 0 and 1 non-volatile memory, set values to 32 and 48, respectively.



