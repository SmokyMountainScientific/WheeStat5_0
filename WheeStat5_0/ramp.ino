//  ASV_CSV tab / StellarisWheeStat sketch
//  In anodic stripping voltammetry, the working electrode is initially held
//    at a voltage negative of that where the analyte is reduced for a 
//    given plating time.  The reduced analyte is precipitated onto the
//    electrode and the amount on the electrode is analyzed by measuring 
//    current passed as the voltage is scanned positive through the potential
//    at which the material is re-oxidized.

void ramp()  {

  //  int  nSteps = (dFnl - dInit)/pwm_step;   // number of steps in scan
  /*
  ////// print header ///////////////
  Serial.print("Vread");
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
  if (dInit<dFnl) {
    pRamp(dInit);
  }
  else {
    nRamp(dInit);
  }
  if (mode == CV) {              ///// CV experiment
    if (dInit<dFnl) {
      nRamp(dFnl);
    }
    else {
      pRamp(dFnl);
    }
  }
  openCircuit();    // go to open circuit
}
///////// Stripping ////////////////////////////////////////////////
void pRamp(int start)  {
  for (int dC = 0; dC <= nSteps; ++dC){  
    dSig = start + (dC*pwm_step);      // calculate digital Signal value, step =3
    PWMWrite(signal_pin,1024,dSig,5000);

    delay(stepTime);  
    int calcV = dSig*vcc/1024 - dRef*vcc/1024;
    readVolts();
//    printI = true;
 //   readCurrent(printI);
    readCurrent(true);
   Serial.println("");

  }
}

void nRamp(int start)  {
  for (int dC = 0; dC <= nSteps; ++dC){  
    dSig = start - (dC*pwm_step);      // calculate digital Signal value, step =3
    PWMWrite(signal_pin,1024,dSig,5000);

    delay(stepTime);  
    int calcV = dSig*vcc/1024 - dRef*vcc/1024;
    readVolts();
 //   printI = true;
 //   readCurrent(printI);
    readCurrent(true);
    Serial.println("");

  }
}


