// Open_Circuit tab

void openCircuit() {
 PWMWrite(signal_pin,1024,openCirc,5000);  // set signal pin to open circuit
 readCurrent(false);        //reads current, does not Serial print
 int zeroI = vcc/2;
 if (mVi > zeroI+6) {
 ++openCirc;
 }
  else if (mVi < zeroI-6) {
 --openCirc;
  }
 else {
 oCircRun = true;
 }
}
