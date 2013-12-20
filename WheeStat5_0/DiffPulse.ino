//  DiffPulse tab / StellarisWheeStat sketch
//  In anodic stripping voltammetry, the working electrode is initially held
//    at a voltage negative of that where the analyte is reduced for a 
//    given plating time.  The reduced analyte is precipitated onto the
//    electrode and the amount on the electrode is analyzed by measuring 
//    current passed as the voltage is scanned positive through the potential
//    at which the material is re-oxidized.

void diffPulse()  {

   /////// print header ///////////////
 /* Serial.print("Vread");
  Serial.print('\t');
  Serial.println("Iread");
  Serial.println("&");*/

  ////////// Plating /////////////////////////////////////////////////  
  digitalWrite(pulse_pin,LOW);              // set pulse pin to low
  PWMWrite(signal_pin,1024,dInit,5000);  // set signal voltage to 512 + 1/2 Vinit

  /*     digitalWrite(stir_pin,HIGH);
   delay(initial_ms);            // stay at Vinit for initial_delay in sec 
   digitalWrite(stir_pin,LOW); */
  delay(delay1*1000);

  ///////// Stripping ////////////////////////////////////////////////

  for (int dC = 0; dC <= nSteps; ++dC){  
    if (dInit < dFnl){
      dSig = dInit + (dC*pwm_step);      // calculate digital Signal value, step =3
    }
    else {
      dSig = dInit - (dC*pwm_step);
    }
    PWMWrite(signal_pin,1024,dSig,5000);
    digitalWrite(pulse_pin,HIGH);       // added Dec 18 
    delay(stepTime/2);  

    readVolts();
    readCurrent(true);
 //   printI = true;
   // readCurrent(printI);
    digitalWrite(pulse_pin,LOW);       // added Dec 18 
    delay(stepTime/2);  

 //    printI = true;
 //   readCurrent(printI);
    readCurrent(true);
    Serial.println("");

  }
  openCircuit();    // go to open circuit
  //        int Iread1 = 0;                 // high pulse current read 
  //        int Iread2 = 0;                 // low pulse curent read 
  //    iRead2 = iRead  */

  //             Serial.print('\t');
  //    Iread2 = iRead;
  //    inVolt = vRead;
  //           Serial.println("end ");
  //            Serial.print(inVolt); 
  //           Serial.print('\t');
  //         Serial.println(Iread2);  


  //  Serial.print(aVolt,4); 
  //}
}

///////// Stripping /////////////////////////////////////////////////////////////////////
/*   if(dInit < dFnl) {                              //assumes stripping is anodic
 for (int dC = 0.5*dInit; dC <= 0.5*dFnl; ++dC){
 //       analogWrite(offset_pin,offSet);
 analogWrite(signal_pin,dSignal);
 analogWrite(ref_pin,dRef);
 digitalWrite(pulse_pin,LOW);              // set pulse pin to low
 //        delay(sr);
 delay(stepTime);  */


/*   Serial.print(aVolt,4); 
 }
 
 int aVolt = inVolt/4;
 float finVolt = (dRef-(inVolt/4))*0.003474609;
 float fIread2 = (Iread2/16)*0.003474609;  */
//      Serial.print(aVolt,4);      //fix this later
/*     Serial.print(finVolt,4);      //fix this later
 Serial.print('\t');
 Iread = analogRead(Iread_pin);
 Serial.print(fIread2,4);  
 
 
 digitalWrite(pulse_pin,HIGH);                // set pulse pin to high
 //        delay(sr);
 delay(stepTime);
 for (int i =0; i<=15; ++i){
 Iread1 += analogRead(Iread_pin);
 }
 float fIread1 = (Iread1/16)*0.003474609;
 Serial.print('\t');
 Serial.println(fIread1,4);
 }
 */// }
//  }
//}
/*
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
void readCurrent() {
  unsigned int iRead = 0;                 // current read
  for (int i =0; i<=15; ++i){  
    iRead += analogRead(Iread_pin);
  }
  //        iRead = analogRead(Iread_pin);       //read current
  int mVi = (iRead)*vcc/4098/16;          // digital reading converted to mV
  Serial.print(mVi);  
  Serial.print('\t');
}*/
/*     for (int i =0; i<=15; ++i){
 Iread1 += analogRead(Iread_pin);
 }
 float fIread1 = (Iread1/16)*0.003474609;
 Serial.print('\t');
 Serial.println(fIread1,4);
 */
/*       if(dC == 255){
 dC = dC + 1;
 }*/

