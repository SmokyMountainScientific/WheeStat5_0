import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import org.gicentre.utils.gui.TextPopup; 
import org.gicentre.utils.stat.*; 
import controlP5.*; 
import processing.serial.*; 
import java.io.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class WheeStat5_0 extends PApplet {

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
 // for warning window
    // For chart classes.
//import org.gicentre.utils.multisketch.*; // for integration window


                        // this is needed for BufferedWriter
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
public void setup()
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


public void draw()
{
  background(0);
  textFont(font4,18);
  pushMatrix();
  fill(0xffDEC507);
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

  fill(0xffDEC507);
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
  
  fill(0xffEADFC9);               // background color
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




 

/////////////////////////////Bang's///////////////////////////////////////////////////////////
public void setup_bangs() {
  cp5 = new ControlP5(this);
  
  cp5.addBang("Start_Run")
    .setColorBackground(0xffFFFEFC)//#FFFEFC 
        .setColorCaptionLabel(0xff030302) //#030302
          .setColorForeground(0xffAA8A16)
          .setColorActive(0xff06CB49)
            .setPosition(20, 430)
              .setSize(100, 40)
                .setTriggerEvent(Bang.RELEASE)
                  .setLabel("Start Run") //
                    .getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER)  
                      ;

  cp5.addBang("Connect")
    .setColorBackground(0xffFFFEFC) 
        .setColorCaptionLabel(0xff030302) 
          .setColorForeground(0xffAA8A16)  
          .setPosition(100, 8)
            .setSize(40, 20)
              .setTriggerEvent(Bang.RELEASE)
                .getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER)  //.setLabel("Start_Run")
                  ;

  cp5.addBang("Save_run")
    .setColorBackground(0xffFFFEFC)//#FFFEFC 
        .setColorCaptionLabel(0xff030302) //#030302
          .setColorForeground(0xffAA8A16)  
          .setPosition(600, 10)    // was 450
            .setSize(80, 20)
              .setTriggerEvent(Bang.RELEASE)
                .setLabel("Save Run")
                  .getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER)  //.setLabel("Start_Run")
                    ;
     
}
/////////////////////bang programs ////////////////////////////////////////

public void Connect() {             // conect to com port bang
//   if(Comselected==false){
// try{
  serialPort = new Serial(this, comList[Ss], 9600);
  println(comList[Ss]);
  myTextarea2.setText("CONNECTED");
  Comselected = true;
// }
/* catch (Exception e){
   warning.show();
   warningtxt.setText("Some type of com port error");
   println("Some type of com port error. Restart program");
   myTextarea2.setText("COM ERROR");
 }
  }
   else{
   println("already connected");
   }*/   }

public void Start_Run() {  // start run bang
  run = true;
  myTextarea2.setColor(0xffD8070E);
  myTextarea2.setText("RUNNING SCAN");
}

public void Save_run() {             // set path bang   
  selectInput("Select a file to process:", "fileSelected");
}

public void fileSelected(File selection) {
  if (selection == null) {
    println("Window was closed or the user hit cancel.");
  } 
  else {
    file2 = selection.getAbsolutePath();
    println("User selected " + file2);
   // myTextarea.setText(file2);
      ///////////////////////////////////////
  //  String file2 = "C:/Users/Ben/Documents/Voltammetry Stuff/log/data.txt";
  try{
  saveStream(file2,file1);
      }
      catch(Exception e){}
/////////////////////////////////////////
  }
} 

//Controllers tab
// controllers setup


public void cp5_controllers_setup(){
 ////////////////////////////////////////////////Text Fields//////////////////////////////
  cp5 = new ControlP5(this);  //cp5 = new ControlP5(this);
  PFont font = createFont("arial", 20);
  PFont font2 = createFont("arial", 16);
  PFont font3 = createFont("arial",12); 
  
  
  Starting_Voltage = cp5.addTextfield("Starting_Voltage")
    .setColor(0xff030302) 
      .setColorBackground(0xffCEC6C6)//(#FFFEFC) 
        .setColorForeground(0xffAA8A16) 
         .setPosition(20, 80)
            .setSize(60, 30)
              .setFont(font)
                .setFocus(false)
                     .setText("-400");
                      controlP5.Label svl = Starting_Voltage.captionLabel(); 
                        svl.setFont(font2);
                          svl.toUpperCase(false);
                            svl.setText("Initial");
  ;

  
  End_Voltage = cp5.addTextfield("End_Voltage")
    .setColor(0xff030302) 
      .setColorBackground(0xffCEC6C6) 
        .setColorForeground(0xffAA8A16) 
          .setPosition(100, 80)
            .setSize(60, 30)
              .setFont(font)
                .setFocus(false)
                   .setText("400");
                      controlP5.Label evl = End_Voltage.captionLabel(); 
                        evl.setFont(font2);
                          evl.toUpperCase(false);
                            evl.setText("Final");
  ;

  Scan_Rate = cp5.addTextfield("Scan_Rate")
    .setColor(0xff030302) 
      .setColorBackground(0xffCEC6C6) 
        .setColorForeground(0xffAA8A16) 
           .setPosition(100, 266)
            .setSize(60, 30)
              .setFont(font)
                .setFocus(false)
                   .setText("100");
                      controlP5.Label srl = Scan_Rate.captionLabel(); 
                        srl.setFont(font2);
                          srl.toUpperCase(false);
                            srl.setText("mV/sec");
  ;
    Gain = cp5.addTextfield("Gain")
    .setColor(0xff030302) 
      .setColorBackground(0xffCEC6C6) 
        .setColorForeground(0xffAA8A16) 
           .setPosition(20, 173) 
            .setSize(60, 30)
              .setFont(font)
                .setFocus(false)
                   .setText("0");
                      controlP5.Label gain = Gain.captionLabel(); 
                        gain.setFont(font2);
                          gain.toUpperCase(false);
                            gain.setText("Gain");
  ;
  offset = cp5.addTextfield("offset")
    .setColor(0xff030302) 
      .setColorBackground(0xffCEC6C6) 
        .setColorForeground(0xffAA8A16)  //position next
           .setPosition(100, 173)       

            .setSize(60, 30)
              .setFont(font)
                .setFocus(false)
                   .setText("-3");
                      controlP5.Label oLb = offset.captionLabel(); 
                        oLb.setFont(font2);
                          oLb.toUpperCase(false);
                            oLb.setText("Offset");
  ;

  Delay_Time = cp5.addTextfield("Delay_Time")
    .setColor(0xff030302) 
      .setColorBackground(0xffCEC6C6) 
        .setColorForeground(0xffAA8A16) 
          .setPosition(20, 266)
            .setSize(60, 30)
              .setFont(font)
                .setFocus(false)
                    .setText("2");
                      controlP5.Label dtl = Delay_Time.captionLabel(); 
                        dtl.setFont(font2);
                          dtl.toUpperCase(false);
                            dtl.setText("seconds");                    
  ;

  
    Number_of_Runs = cp5.addTextfield("Number_of_Runs")  // time based txt field
    .setColor(0xff030302) 
      .setColorBackground(0xffCEC6C6) 
        .setColorForeground(0xffAA8A16) 
          .setPosition(20, 360)
            .setSize(60, 30)
              .setFont(font)
                .setFocus(false)
                    .setText("3");
                      controlP5.Label norl = Number_of_Runs.captionLabel(); 
                        norl.setFont(font2);
                          norl.toUpperCase(false);
                            norl.setText("Number");                    
  ;
  
    Run_Interval = cp5.addTextfield("Run_Interval")  // time based txt field
    .setColor(0xff030302) 
      .setColorBackground(0xffCEC6C6) 
        .setColorForeground(0xffAA8A16) 
          .setPosition(100, 360)
            .setSize(60, 30)
              .setFont(font)
                .setFocus(false)
                   .setText("1");
                      controlP5.Label ril = Run_Interval.captionLabel(); 
                        ril.setFont(font2);
                          ril.toUpperCase(false);
                            ril.setText("Delay Min");                    
  ;

  ///////////////////////////////////////text area//////////////////////////

  errorText = cp5.addTextarea("txt")  // save path text area
    .setPosition(350, 5) // was 280,5
      .setSize(240, 45)
        .setFont(font)      // was font 4
          .setLineHeight(20)
            .setColor(0xffFF9100)        //(#D60202)
              .setColorBackground(0)         //(#CEC6C6)
                .setColorForeground(0xffAA8A16)//#CEC6C6
                    ;  

 myTextarea2 = cp5.addTextarea("txt2")  // status and com port text area
    .setPosition(150, 8)
      .setSize(100, 20)   //was 30
        .setFont(createFont("arial", 12)) //(font)
          .setLineHeight(10)
            .setColor(0xff030302)
              .setColorBackground(0xffCEC6C6)
                .setColorForeground(0xffAA8A16)//#CEC6C6
                    ;

 

/******************* end cp5_controllers-setup ***********************/


 /////////////////////////////////////////Dropdownlist//////////////////////////
  ports = cp5.addDropdownList("list-1", 10, 30, 80, 84)
    .setBackgroundColor(color(200))
      .setItemHeight(20)    // was 20
        .setBarHeight(20) 
          .setColorBackground(color(60))
            .setColorActive(color(255, 128))
              .setUpdate(true)
                ;
  ports.captionLabel().set("Select Port");
  ports.captionLabel().style().marginTop = 3;
  ports.captionLabel().style().marginLeft = 3;
  ports.valueLabel().style().marginTop = 3;
  comList = serialPort.list(); 
  for (int i=0; i< comList.length; i++)
  {
    ports.addItem(comList[i], i);
  }  

//}
ovrLy = cp5.addDropdownList("list-3", 200, 60, 80, 64)  // last digit was 84
    .setBackgroundColor(color(200))
      .setItemHeight(20)
          .setBarHeight(20)
          .setColorBackground(color(60))
            .setColorActive(color(255, 128))
              .setUpdate(true)
                ;
  ovrLy.captionLabel().set("No_Overlay");
  ovrLy.captionLabel().style().marginTop = 3;
  ovrLy.captionLabel().style().marginLeft = 3;
  ovrLy.valueLabel().style().marginTop = 3;
  ovrLy.setScrollbarWidth(10);

  ovrLy.addItem("no_overlay",0);
  ovrLy.addItem("overlay", 1);

 ///////////// mode dropdown list /////////////////////////////
  mode = cp5.addDropdownList("list-2", 260, 30, 80, 124)  // last digit was 84
    .setBackgroundColor(color(200))
      .setItemHeight(20)
          .setBarHeight(20)
          .setColorBackground(color(60))
            .setColorActive(color(255, 128))
              .setUpdate(true)
                ;
  mode.captionLabel().set("Select Mode");
  mode.captionLabel().style().marginTop = 3;
  mode.captionLabel().style().marginLeft = 3;
  mode.valueLabel().style().marginTop = 3;
  mode.setScrollbarWidth(10);

  mode.addItem("RAMP",0);
  mode.addItem("CV", 1);
  mode.addItem("dif_Pulse", 2);
  mode.addItem("ASV", 3);
//  mode.addItem("Chrono-Amp",2);
  mode.addItem("logASV",4);
 

}

/////////////////////////////////////////////////group programs/////////////////////////////////

public void controlEvent(ControlEvent theEvent) {
  if (theEvent.isGroup()) 
  {
    if (theEvent.name().equals("list-1")) {

      float S = theEvent.group().value();
      Ss = PApplet.parseInt(S);
      Comselected = true;
    }
    if (theEvent.name().equals("list-2")) {
      float Mod = theEvent.group().value(); 
      int Modi = PApplet.parseInt(Mod);
      String [][] Modetype = mode.getListBoxItems(); 
      //Modetorun = Modetype[Modi][Modi];
      runMode = Modetype[Modi][0]; // replaced earlier line in newer sketch?
      Modesel = true;
      println(runMode);
    }
    if (theEvent.name().equals("list-3")) {
      float ovr = theEvent.group().value(); 
      overlay = PApplet.parseInt(ovr);
//      String [][] Modetype = mode.getListBoxItems(); 
//      runMode = Modetype[Modi][0]; // replaced earlier line in newer sketch?
//      Modesel = true;
//      println(runMode);
    }
  }
}

public void charts_gic_setup(){
  
              ////////////////////////////////gicentre charts///
  lineChart = new XYChart(this);
  lineChart.setData(new float[] {1, 2, 3}, new float[] {1, 2, 3});
  lineChart.showXAxis(true); 
  lineChart.showYAxis(true);
 // lineChart.setXAxisLabelColour(color(234, 28, 28));  
//  fill(#DEC507);    
  lineChart.setXAxisLabel("Potential (mV)");
  lineChart.setYAxisLabel("Current Response (of 3287)"); 
  //lineChart.setMinY(0);   
  lineChart.setYFormat("##.##");  
  lineChart.setXFormat("##.##");       
  // Symbol colours
  lineChart.setPointColour(color(234, 28, 28));
  lineChart.setPointSize(5);
  lineChart.setLineWidth(2);

 
}

////////////////////////////////////////////////end charts_gic_setup///////////////////////////////////////////////
public void logData( String fileName, String newData, boolean appendData)  
{
  BufferedWriter bw=null;
  try { //try to open the file
    FileWriter fw = new FileWriter(fileName, appendData);
    bw = new BufferedWriter(fw);
    bw.write(newData);// + System.getProperty("line.separator"));
  } 
  catch (IOException e) {
  } 
  finally {
    if (bw != null) { //if file was opened try to close
      try {
        bw.close();
      } 
      catch (IOException e) {
      }
    }
  }
}

// read_serial tab.  

public void  read_serial() {
      if (serialPort.available () <= 0) {}
      if (serialPort.available() > 0) { 
        sData3 = serialPort.readStringUntil(LINE_FEED);  // new JS11/22
     
         if(sData3 != null && p != 0) {
           String[] tokens = sData3.split(tab);
           tokens = trim(tokens);  
             if (run == true)  {  
                if (runMode == "ASV" || runMode == "logASV" || runMode == "dif_Pulse") {
                  xRead = Float.parseFloat(tokens[0]);  
                  yRead1 = Float.parseFloat(tokens[1]);  
                  yRead2 = Float.parseFloat(tokens[2]); 
                  yRead = yRead1 - yRead2;
                  }
                else {    // for RAMP and CV experiments
                  xRead = Float.parseFloat(tokens[0]);  
                  yRead = Float.parseFloat(tokens[1]);  
                  }
                if (xRead == 99999)  {  // signals end of run
       //       if (xRead == 99999  && yRead == 99999) { // signals end of run
                  run = false;    // stops program
                  println("end the madness");
                  gotparams = false;
                  myTextarea2.setColor(0xff036C09);
                  myTextarea2.setText("FINISHED");
                  cData = 'a';   
                  error = error1 + error2;
                  if (error == 1){
                     errorText.setText("ERROR: I-max Too Hi, Decrease Offset");   
                     }
                  else if (error == 2){
                     errorText.setText("ERROR: I-min Too Low, Increase Offset");   
                     }
                  else if (error == 3){
                     errorText.setText("Scale Error: Decrease Gain");   
                     }
                  error1 = 0;
                  error2 = 0;
                  error = 0;
   //               xData[0] = 0;  //shows up in the final graph.
                  xRead = 0;  // new
                  yRead = 0;
               }  // end of if xRead = 99999 

             else if(xRead == 55555)  // start of log run
               {
               println("new run");
               myTextarea2.setColor(0xff036C09);
               myTextarea2.setText("run-"+(yRead1));
               }
        
             else if (xData[0] == 0) 
               {
               delay(10);
        //       yData[0] = yRead; 
               xData[0] = xRead;
               yData[0] = yRead; 
         println(xRead);
         println(yRead1);
         println(yRead2);
         println(yRead);
         println(xData[0]);
         println(yData[0]);
               }
             else {  
               xData = append(xData, xRead);
               yData = append(yData, yRead);
               logData(file1, sData3, true);
               }   
             if (yRead2 > yMax && run == true)  {
               error1 = 1; //  
               println (yRead2);
               }
             if (yRead1 !=0 && yRead1 < yMin && run == true && xRead != 55555)  {
               error2 = 2;
               println (yRead1);
               }
         }
     }
         p +=1;
    } // end of if serial available > 0
  }


/******************* begin text field programs ***********************/

//public void Starting_Voltage() {              //get start voltage from text box
public void getParams() {  
  //public void getVoltages() {  
  vInit = cp5.get(Textfield.class, "Starting_Voltage").getText();
  iInit = round(PApplet.parseFloat(vInit));
  iInit=iInit+2000;                // changed to 2000 from ldo
  vInit = nf(iInit, 6);   // Pad with zero if to 6 digits
//}
//public void End_Voltage() {               // get end voltage from text box
  vFinal = cp5.get(Textfield.class, "End_Voltage").getText();
  iFinal = round(PApplet.parseFloat(vFinal));
  iFinal=iFinal+2000;
  vFinal = nf(iFinal, 6);   // make EndV have 4 digits. pad with zero if no digits

  cGain = cp5.get(Textfield.class, "Gain").getText();
  iGain = round(PApplet.parseFloat(cGain)+128);       // added 128 makes range from -128 to +128
  cGain = nf(iGain, 6);   // make EndV have 4 digits. pad with zero if no digits

  vOffset = cp5.get(Textfield.class, "offset").getText();
  iOffset = round(PApplet.parseFloat(vOffset))+512;
  vOffset = nf(iOffset, 6);   // make EndV have 4 digits. pad with zero if no digits
//}
//public void Scan_Rate() {                 // get scan rate from text box
  scanRate = cp5.get(Textfield.class, "Scan_Rate").getText();
  iRate = round(PApplet.parseFloat(scanRate));
  scanRate = nf(iRate, 6);   // make ScanR have 3 digits. pad with zero if no digits
//}
//public void Delay_Time() {                // get delay time from text box
  initialDelay = cp5.get(Textfield.class, "Delay_Time").getText();
  iDelay = round(PApplet.parseFloat(initialDelay));
  initialDelay = nf(iDelay, 6);   // make DelayT have 3 digits. pad with zero if no digits
//}
//public void getLogParams() {  
  //public void Number_of_Runs() {                // get run count from text box
  nRuns = cp5.get(Textfield.class, "Number_of_Runs").getText();
  iRuns = round(PApplet.parseFloat(nRuns));
  nRuns = nf(iRuns, 6);   // Pad with zeros to 6 digits

//public void Run_Interval() {                // get delay time from text box
  logIvl = cp5.get(Textfield.class, "Run_Interval").getText();
  iIvl = round(PApplet.parseFloat(logIvl));
  logIvl = nf(iIvl, 6);   // make DelayT have 3 digits. pad with zero if no digits
}
/************* parameters for retrieving values from text fields
String vInit;          
String vFinal;
String scanRate;
String initialDelay;
String nRuns
String logIvl
int iInit;
int iFinal;
int iRate;
int iDelay;
int iRuns;
int iIvl;
*/
/*public void InitialV_Time() {                 // get scan rate from text box
  InitVT = cp5.get(Textfield.class, "InitialV_Time").getText();
  iInitVT = round(float(InitVT));
  InitVT = nf(iInitVT, 6);   // make ScanR have 3 digits. pad with zero if no digits
}
public void FinalV_Time() {                // get delay time from text box
  FnlVT = cp5.get(Textfield.class, "FinalV_Time").getText();
  iFnlVT = round(float(FnlVT));
  FnlVT = nf(iFnlVT, 6);   // make DelayT have 3 digits. pad with zero if no digits
}*/
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "WheeStat5_0" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
