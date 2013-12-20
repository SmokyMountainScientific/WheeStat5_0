
//////////////// Read voltage pin ///////////
void readVolts() {
  int inVolt = 0;                 // voltage read
  for (int i =0; i<=3; ++i){  
    inVolt += analogRead(Vread_pin);
  }
  mVread = inVolt/4;
  mVread = mVread*vcc/4096;
  mVread = (vcc/2)-mVread;
  //          mVread = (-inVolt/4)*vcc/4096 + (vcc/2);
  Serial.print(mVread);  
  Serial.print('\t');

}
//////////////////// Read current pin //////////////
void readCurrent(boolean printI) {
  unsigned int iRead = 0;                 // current read
  for (int i =0; i<=15; ++i){  
    iRead += analogRead(Iread_pin);
  }
  //        iRead = analogRead(Iread_pin);       //read current
  mVi = (iRead)*vcc/4098/16;          // digital reading converted to mV
  if (printI == true) {
    Serial.print(mVi);  
    Serial.print('\t');
  }
}
