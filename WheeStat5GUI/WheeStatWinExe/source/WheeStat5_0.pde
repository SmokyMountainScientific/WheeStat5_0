/*  Built on DataLogGraph_1_4 Processing sketch
*   GUI for WheeStat 5 series Potentiostats. 
 *  Uses StellarisWheeStat1_1 Energia sketch   
 *    by Jack Summers, Ben Hickman 12-19-2013
 *  
 *    Com port selection tool works
 *    Data logged to file OK. 
 *    Graphs data in real time.
 *    
 *    Revision 1_4 improves update speed using readStringUntil(LINE_FEED)
 *    End of run signaled by transmission of "99999\t999999\n" from LaunchPad
 */
 
///////////////////////////////////////// Imports///////////////////////////////
import org.gicentre.utils.gui.TextPopup; // for warning window
import org.gicentre.utils.stat.*;    // For chart classes.
//import org.gicentre.utils.multisketch.*; // for integration window
import controlP5.*;
import processing.serial.*;
import java.io.*;                        // this is needed for BufferedWriter
/////////////////////////////////////////Classes////////////////////////////////
XYChart lineChart;  

ControlP5 cp5,cp5b,cp5c;
Serial serialPort;
Textarea errorText;   // com port and status window
Textarea myTextarea2;    // save file path window
Textfield Starting_Voltage, End_Voltage, Scan_Rate, Delay_Time, Gain, offset;
Textfield InitialV_Time, FinalV_Time, Number_of_Runs, Run_Interval;
DropdownList ports, mode, ovrLy;              //Define the variable ports and mode as Dropdownlists.

//////////////////////////////////variables/////////////////////////////////////

char[] strtochar;
//char cData;
String sData3;
//String sData3 ="";
//String[] sData = new String[3];  //String sData;
/*float[] V = {0};
float[] I1 = {0};
float[] newV = {0};   // added to reset V after each run Nov 19 BH
float[] newI1 = {0}; */ // added to reset I1 after each run Nov 19 BH
boolean Modesel = false;
int overlay = 0;
String runMode;
String RAMP = "0";
String CV = "1";
String ASV = "2";
String logASV = "3";

int LINE_FEED = 10; // used in serial read to identify data sets
String tab ="\t";   // used in serial read to identify data splits

float[] xData = {0};   
float[] yData = {0};
float[] nullData = {0};
float[] nullY = {0};
float xRead = 0;   
float yRead = 0;
float yRead1 = 0;
float yRead2 = 0;
float yMax = 3280;      // maximum current reading from hardware
float yMin = 630;       // minimum current reading from hardware
int error = 0;
int error1 = 0;
int error2 = 0;
int Ss;                          //The dropdown list returns a float, must convert into an int. 
String[] comList ;               //A string to hold the ports in.
String[] comList2;               // string to compare comlist to and update
boolean serialSet;               //A value to test if we have setup the Serial port.
boolean Comselected = false;     //A value to test if you have chosen a port in the list.
boolean gotparams = false;

boolean run = false;           // start run at bang
float p1;
float p2;

String ComP;
int serialPortNumber;
String file1 = "logdata.txt";
String file2;                  // save file path
//String file;
String[] sData = new String[3];  //String sData;
String sData2 = " ";
char cData;
char cData2;

String Go = "1";
//String star = "*";
int i =0;
int p = 0;           //stop signal
//int updatechart;
/************* parameters for retrieving values from text fields**********/
String vInit;          
String vFinal;
String scanRate;
String initialDelay;
String nRuns;
String logIvl;
String cGain;
String vOffset;
int iInit;
int iFinal;
int iRate;
int iDelay;
int iRuns;
int iIvl;
int iGain;
int iOffset;
//////////////font variables////////////////////////////////////////////////////
PFont font = createFont("arial", 18);
PFont font2 = createFont("arial", 16);
PFont font3 = createFont("arial",12); 
PFont font4 = createFont("andalus",16);
/////////////// setup //////////////////////////////////////////////////////////
void setup()
{
  setup_bangs();
  charts_gic_setup();
  cp5_controllers_setup();

  frameRate(2000);
  size(730, 550); 
  //textFont(font2);
  //frame.setResizable(true);

}
///////////////////End Setup////////////////////////////////////////////////////


void draw()
{
  background(0);
  textFont(font4,18);
  pushMatrix();
  fill(#DEC507);
  text("Smoky Mountain",30,height-30);
  text("Scientific",58,height-12);
  popMatrix(); 
  
  stroke(255);
  noFill();
  rect (12,50,160,85);
  rect (12,143,160,85);
  rect (12,236,160,85);
  rect (12,330,160,85);
  pushMatrix();
  textFont(font,12);

  fill(#DEC507);
  text("https://github.com/SmokyMountainScientific",280,height-12);
  popMatrix(); 
   

  pushMatrix();
  if (Modesel==false) {
    Starting_Voltage.hide();
    End_Voltage.hide();
    Delay_Time.hide();
    Scan_Rate.hide();
    Gain.hide();
    offset.hide();
 //   FinalV_Time.hide();
//    InitialV_Time.hide();
    Number_of_Runs.hide();
    Run_Interval.hide();
 //   cp5.controller("Start_Run").hide();
   // cp5.controller("Intergrate_Data").hide();
  }
   popMatrix(); 
  
   if (Modesel==true) {
  Starting_Voltage.show();  
  End_Voltage.show();
  Delay_Time.show();
  Scan_Rate.show();
  Gain.show();
  offset.show();
  textFont(font2);
  fill(250,250,250);             //Chart heading color
  textSize(16);
  text("Voltage limits (mV)", 20, 70);
  text("Current", 20, 163);
  text("Delay 1   Scan Rate", 20, 256);
  
  if (runMode=="logASV") {
    Number_of_Runs.show();
    Run_Interval.show();
 //  textFont(font2);
 // fill(250,250,250);             //Chart heading color
 // textSize(16);
    text("Multiple Runs", 20, 350);
  }
  else {
    Number_of_Runs.hide();
    Run_Interval.hide();
  }

    
  }
 textFont(font2);
  
  fill(#EADFC9);               // background color
  //noStroke();
  rect(200, 70, 475, 450);    // chart background
/*  fill(250,250,250);             //Chart heading color
  textSize(16);
  text("Scan Rate Gain", 220, 60);  */
//  if (xData [0] !=0) {
  lineChart.draw(220, 70, 430, 430);    //early lineChart
//  }
  if (run == true)
  {
    if(gotparams == false)   // added to update chart in real time Nov19 BH
    {
if (yData[0] != 0 && overlay == 0) {
//if (yData[0] != 0) {   // changed from xData
  xData = nullData;  /// Clear X and Y data to redraw chart
  yData = nullY;
                xData[0] = 0;  //shows up in the final graph when in SerialRead.
}
getParams();    // get paramaters from text fields (text field programs)
      /*   getVoltages();
   Scan_Rate();
   Delay_Time();
   
   if (runMode == logASV) {
   getLogParams();
   } */
             // serialPort.write writes to microcontroller
if (runMode=="RAMP") {
  serialPort.write("000000");  
  println(0);
}
else if (runMode=="dif_Pulse") {
  serialPort.write("000004");  
  println(0);
}
else if (runMode=="ASV") {
  serialPort.write("000002");  
  println(0);
}
else if (runMode=="CV"){
    serialPort.write("000001");  
  println(1);
}
else if (runMode=="logASV") {
    serialPort.write("000003");  
  println(2);
}
else {}
  //      serialPort.write(LOG_ASVmod);  
      delay(100);
      serialPort.write(vInit);
      delay(100);
      serialPort.write(vFinal);
      delay(100);
      serialPort.write(scanRate);
      delay(100);
      serialPort.write(cGain);
      delay(100);      
      serialPort.write(vOffset);
      delay(100);
      serialPort.write(initialDelay);
      delay(100);
      serialPort.write(nRuns);
      delay(100);
      serialPort.write(logIvl);
      delay(100);
//      serialPort.write(Go);  // is this a problem?
      println(vInit);
      println(vFinal);
      println(scanRate);
      println(cGain);
      println(vOffset);
      println(initialDelay);
      println(nRuns);
      println(logIvl);
//      println(Go);
      p=0;                    // reset counter for serial read
      println("begin run");   // shows up in bottom window
      //println(160);         // Go = 1
      // moved up
//      delay(100);
 //     serialPort.write('2');             // value of 2 added to prevent non-specific trigger
      println(262);
      logData(file1, "", false);     // log data to file 1, do not append, start new file
      
      ////////read parameter input until LaunchPad transmits '&'/////////
      while (cData!='&'&& cData !='@')
      {         
          if (serialPort.available () <= 0) {}
          if (serialPort.available() > 0)
          {
            cData =  serialPort.readChar();     // cData is character read from serial comm. port
            sData2 = str(cData);            //sData2  is string of cData 
            logData(file1, sData2, true);   // at this point we are logging the parameters
            println(sData2);
            errorText.setText(""); 
            if (cData == '&')               //  Launchpad sends & char at end of serial write
            {
      //        delay(100);  // did not fix the cariage return issue
              println("parameters received");
              gotparams = true;
           }
          }
      }  // end of while loop with params
  } // end if gotparam == false   Nov 19 BH
    
       //////////// graph data //////////////////////////////////////////////
  //      if (run == true) // redundant?-no.  graph does not update after @
 //       {
          read_serial();
      }
        if (xData.length>4 && xData.length==yData.length)
        {
          lineChart.setMaxX(max(xData));
          lineChart.setMaxY(max(yData));
          lineChart.setMinX(min(xData));
          lineChart.setMinY(min(yData));
          lineChart.setData(xData, yData);

  //        updatechart = i;
        } // End of if (V.length stuff
    } /// end of while (cData not @) loop




 

