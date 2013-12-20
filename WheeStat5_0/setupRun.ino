//setupRun Tab / work_on_WheeStat_outputs sketch

void setupRun() {
  //readSerial();  // chill untill triggered by "2"
  //////////// mode ////////////////
                //  0:ASV, 1:CV, 2:Log ASV"
  Serial.print('\t');
  Serial.print("Mode");
  Serial.print('\t');
  readParam();  
  mode=param;     
  delay(20);  

  ///////////initial voltage///////
  Serial.print("mV init");
  Serial.print('\t');
  readParam();  
  mVinit=param-2000;     // 2000 added in GUI to transfer positive value
  dInit = 1024*mVinit/vcc + 512;       //digital value for pwm
  delay(20);  

  ///////////final voltage///////

  Serial.print("mV Final");
  Serial.print('\t');
  readParam();
  mVfnl=param-2000;       // 2000 added to value in GUI
  dFnl = 1024*mVfnl/vcc + 512;       //digital value for pwm
  delay(20);

  ///////////scan rate///////
  Serial.print("mV/s");
  Serial.print('\t');
  readParam();
  scanRate = param;     
  stepTime = 1000*pwm_step*vcc/1024/scanRate;      // ms per step
  delay(20);

  ///////////  Current gain  ///////
  Serial.print("gain");
  Serial.print('\t');
  readParam();   
  if (param >= 128) {
    gain0 = 0;
    gain1 = 256-param;
    }
  else {
    gain0 = 128 - param;
    gain1 = 128;
  }    
  delay(20);
   
  ///////////offset voltage///////
  Serial.print("offset");
  Serial.print('\t');
  readParam();  
  dOff=param;     
  delay(20);

  /////////// delay 1 ////////////////
  Serial.print("initial delay");
  Serial.print('\t');
  readParam();  
  delay1=param;          // global varaible for current gain
  delay(20);

  ////// number of runs for data logging //////////////
  Serial.print("runs");
  Serial.print('\t');
  readParam();
  runs=param;                
  delay(20);
  
  //////// delay between runs for data logging //////////////
  Serial.println("delay 2");
  readParam();
  runDelay=param;                
  delay(20);
  
/********************* print values for header ********************/
  Serial.print('\t');
  Serial.print(mode);
  Serial.print('\t');
  Serial.print(mVinit);
  Serial.print('\t');
  Serial.print(mVfnl);
  Serial.print('\t');
  Serial.print(scanRate);
  Serial.print('\t');
  Serial.print(iGain);
  Serial.print('\t');
  Serial.print(dOff); 
  Serial.print('\t');
  Serial.print(delay1); 
  Serial.print('\t');
  Serial.print(runs);
  Serial.print('\t');
  Serial.println(runDelay); 

   /////// print header ///////////////
  Serial.print("Vread");
  Serial.print('\t');
  Serial.println("Iread");
  Serial.println("&");
}  
/******************************  end setupRun *******************************/

/****************************** read Paramaters ****************************/
void readParam() {
  //void readParam(int number) {
  //void readParam(int number, float param) {
  long place[6]={
    100000,10000,1000,100,10,1  };           
  int incomingByte[6] = {
    0,0,0,0,0,0  };      // serial read variable. each incoming byte (multiplied by the place)
  int n=0;
  param = 0;
  delayMicroseconds(300);  
  while (Serial.available()<= 0) {
  }
  while (Serial.available() > 0){  
    incomingByte[n] = Serial.read()-48;
    delay(2);
    ++n;          
  }
   
  for(int n=0;n<6;++n){ 
    param = param + (place[n]*(incomingByte[n]));  
  }
}          
/* void   readSerial() {      // added Dec 11, 2013 to initiate run on GUI trigger
  //  Serial.println("enter number of loops");
  int k = 0;
  while (Serial.available()<= 0) {
  }
  while (Serial.available() > 0){  
    while(k !=2) {             // when GUI writes "2", program is initiated
      k = Serial.read()-48;
    }
  }

}
*/
