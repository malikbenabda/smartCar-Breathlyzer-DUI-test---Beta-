// VERSION 01/May/16 

/*added features :
   Automatic Alcohol level set ( just click button + blow  in working mode -- drunkModeActivated:false )
   -----------------------------------------------
   send values to phone when phone enters :
    T :  ALCOHOL_THRESHHOLD
    P :  CODE
    I : isInit
    S : drunkModeActivated
  TODO : +debug changing CODE from - out EEPROM   +++ Packaging

*/

/*Architecture and pluging in order 
 * Alcohol Sensor : red : 5v  --- back : GRND  - -- yellow : A0
 * buzzer : D4
 * LED    : D13 
 * relay  : D7
 * BTN    : D2

******** BT cables********** : 

 *    TX  10: green with black attachement      
 *    RX  11: green with no attachement 
 *    GND   : grey with black attachement
 *    VCC 5v: grey with yellow attachement
 *    
 */

/*
 * Messages from phone <---> arduino : 
 * always start with CODE ( 00000000) ;
 *  + T  : return Txxx   : xxx = ALCOHOL_THRESHHOLD
 *  +S : retrun  Sxxxxxx   : xxxxxx = LOCKED / UNLOCKED   this signal is also sent after test
 *    arduino send : Vxxx   : xxx -> scanned values
 *    arduino send : Mxxx   : xxx -> scanned Max alcohol value after ending test 
 *    
 * 
 */


// insclude EEPPROM for memory save
#include <EEPROM.h>
#include <SoftwareSerial.h>

int bluetoothTx = 10;//green->black
int bluetoothRx = 11;//green
SoftwareSerial bluetooth(bluetoothTx, bluetoothRx);

/*
   To secure access to arduino the incoming BT messages always start with the code below
   this String code is emprinted on the breathalyzer Box and entered by the user in the initialization of the app in first run.
*/
String CODE ; //of 8 digits 

//---------------------------------------------------
int BUZZER = 4;
int LED = 13;   //red diode for locked !
int LED0 = 12;  //green diode for unlocked
int BTN = 2; //bouton qui fonctionne temp comme barometer //btn signals the start of alcohol testing
int VLM = 0;  // the alcohol sensor
int RELAY = 7;
boolean drunkModeActivated; // false by default in startup
String order; // character read from BT
int ALCOHOL_THRESHHOLD;
int BAROMETER_THRESHHOLD;
int alcohol_max_value;
boolean tested;
boolean isInit;
/********************SETUP ***************************/
void setup() {
  // put your setup code here, to run once:
  pinMode(LED, OUTPUT);
  pinMode(LED0, OUTPUT);
  pinMode(RELAY, OUTPUT);
  Serial.begin(115200);
  bluetooth.begin(9600);
  order = "";
  //BAROMETER_THRESHHOLD = 500;
  ALCOHOL_THRESHHOLD = 450;
  alcohol_max_value = 0;
  CODE = "00000000";
  drunkModeActivated = false;
  tested = false;
  digitalWrite(LED, LOW );
  //digitalWrite(LED0, LOW );
  isInit = false;
  getIsInit();
  Serial.print((int)isInit + 0);
  if (( (int)isInit + 0 ) == 1 ) loadAll();
  else  saveAll();
  delay(200);
  readyBuzz();


  //display variables :


  //Serial.print("ALCOHOL_THRESHHOLD:    ");
  //Serial.println(ALCOHOL_THRESHHOLD);
  //Serial.print("CODE:    ");
  //Serial.println(CODE);
  //Serial.print("is init : ");
  //Serial.print((int)isInit + 0);

  send2phone("C", CODE);
  send2phone("BT","-Checked");
  send2phone("S", getStatus());
  send2phone("T" , String(ALCOHOL_THRESHHOLD));
}
/****************************LOOP*****************************/
void loop()
{

  
  order = "";
//  Serial.println("----------------------------------------------------------------");



  if ( !drunkModeActivated )
  {
    digitalWrite( RELAY, HIGH);
     digitalWrite(LED, LOW);
//    Serial.print("normal work mode \n");
    order = listenToPhoneBT();
     /* these  4 following conditions are a responce to smartphone asking for values!
        add letter type before sendinbg value to differenciate it from others   */
      if (  order.charAt(0) == 'T'  ) { // send value  ALCOHOL_THRESHHOLD to phone

        send2phone( "T", String (ALCOHOL_THRESHHOLD)  );
      }
   //   if (  order.charAt(0) == 'P'  ) {      bluetooth.print("P" + CODE);      }// send  value  CODE to phone
  //    if (  order.charAt(0) == 'I'  ) { /*send value  isInit  ( 1 / 0 )  to phone */        bluetooth.print("I" + (int)isInit + 0);}
      if (  order.charAt(0) == 'S'  ) { /* send value  drunkModeActivated ( 1/0 )  to phone */     send2phone("S" , getStatus()  ); }


//    if (  order.charAt(0) == 'C'  ) { /*change CODE*/  CODE = order.substring(1) ;  saveCODE(); }

    if ( order.charAt(0)  == 'L' ) { //lock signal from smartphone
      disengageEngine();/* stuff to do when phone sends lock signal*/
      saveState();
    //  Serial.print(" Engine Deactivated == LOCKED -- driking mode ACTIVATED \n");
     }
    if ( order.charAt(0) == 'A' )  // change ALCOHOL_THRESHHOLD
    { //user inputs A+ number ( between 1-> 1023)
      Serial.print(" setting alcohol level \n");
      flicker(LED);
      setAlcoholLevel(order.substring(1));  /*set Alcohol thresshold*/
      saveAlcoholLevel();
    }

    if ( digitalRead(BTN) )      {// if button is pressed
     int newV= testing() ; // gets max value of alcohol in breath
      Serial.print(" setting alcohol level from sensor automatic");
      Serial.print(newV);
      Serial.print("  -- instead of alcohol max value : -  ");
      Serial.println(alcohol_max_value);
      flicker(LED);
      alcohol_max_value=newV;
      setAlcoholLevel(String(alcohol_max_value+100)); /*set Alcohol thresshold*/   
      saveAlcoholLevel();
    }

  
  digitalWrite(LED, HIGH);
  }

  else // if drunkModeActivated is true : ( engine motor  is disabled ( msakkar )  and application is locked
  {
    digitalWrite( RELAY, LOW);
    
//   Serial.print("engine is disabled - awaiting test");

    testing();
//    Serial.print("testing \n");
    if (tested)
    {
      if ( isAlcoholLevelMaxed() )
      { // user can not drive
  //      Serial.print("Alcohol level has reached its Limits \n");

        send_DUI_Signal(true); // with value true ; user is chareb sends buzz ( telling driver u r intoxicated and can not drive
        // send signal to phone that user can not drive + opens app+ show appropriate messages and frames in app
  //      Serial.print(" USER IS SIGNALED TO BE UNABLE TO DRIVE   \n");
      }
      else {        // laisser user conduire --- let user drive :D
        send_DUI_Signal(false); // paramater false user can drive because he is not drunk
        saveState();
    //    Serial.print(" Alcohol level is low enough to drive  \n");
      }

      send2phone("M",String(alcohol_max_value) );
      alcohol_max_value = 0;
      tested = false;
    }
  }
  digitalWrite(LED, LOW);
  delay (200);
}

void setAlcoholLevel(String ALnumber) {


  ALCOHOL_THRESHHOLD =   ALnumber.toInt();
//Serial.print("ALCOHOL_THRESHHOLD  ---=");
  //Serial.print(ALCOHOL_THRESHHOLD );

 // Serial.print(" \n");


}
String listenToPhoneBT() {
  // faire le traitement d'ecoute au BT et return the string sent
  String incoming = "";
  //read BT when sendibng and put it in string incoming
  while (bluetooth.available()) {
    char t;
 //   t=(char)  bluetooth.read();
    t= bluetooth.read();
  incoming += t;
  }
 // Serial.print("incoming");
 // Serial.println(incoming);

  // test if icoming string has the 8 first chars ==  CODE --> return new string incoming taht starts from char #8  ( code is removed )
  if ( incoming.substring(0, 8) == CODE ) {
  //  Serial.print("valid code : ");
  //  Serial.println(incoming.substring(0, 8));
  
    incoming = incoming.substring(8);
  
  }
  else incoming = ""; //msg is ignored
//  Serial.print("order = " + incoming);
  return incoming;

}

void send2phone(String type , String val ) {
    String result = type+val;
    bluetooth.println(result);  
}


void disengageEngine() {
  drunkModeActivated = true;
  for (int i = 0; i < 4; i++) {
    digitalWrite (BUZZER , HIGH) ;
    delay(25);
    digitalWrite (BUZZER , LOW) ;
    delay(25);
  }
 send2phone ("S",getStatus());
 
}
int testing() {
  alcohol_max_value=0;
  while ( digitalRead(BTN) ) {
    int AV = analogRead(VLM) ;
    Serial.println(AV);
     if ( alcohol_max_value < AV)    alcohol_max_value = AV;
   // Serial.println(alcohol_max_value);
    send2phone("V", String(alcohol_max_value) ); // send alcohol current value to user indicating alcohol value in breath  
    tested = true;
    delay(200);
  }
 // Serial.println(alcohol_max_value);
  return  alcohol_max_value;
}
boolean isAlcoholLevelMaxed () {
  return  alcohol_max_value > ALCOHOL_THRESHHOLD;
}
void send_DUI_Signal( boolean state ) {
  if (state) //user is declared drunk
  { //
    send2phone("S","DRUNK");


  } else { // user is sober
    send2phone("S","SOBER");
    drunkModeActivated = false;


  }
  testResult(state);



}

void testResult(bool state ) {
  if ( state) { //user is declared drunk // do test fail buzzing
    digitalWrite (BUZZER , HIGH) ;
    for ( int i = 0 ; i < 10 ; i++) {

      digitalWrite(LED, HIGH);

      delay(50);
      digitalWrite(LED, LOW);
      delay(50);
      digitalWrite(LED, HIGH);
    }
    digitalWrite (BUZZER , LOW) ;


  }
  else { //user is declared Sober // do test succeeded buzzing
    for ( int i = 0 ; i < 3 ; i++ )
    {
      digitalWrite (BUZZER , HIGH) ;
      digitalWrite(LED0, HIGH);
      delay(100);
      digitalWrite(LED0, LOW);
      digitalWrite (BUZZER , LOW) ;
      delay(100);
      digitalWrite(LED0, HIGH);
    }
  }
}

String getStatus(){
  String s="";
  if (drunkModeActivated ) return "LOCKED" ;
  else return "UNLOCKED";
}


void readyBuzz() {
  digitalWrite (BUZZER , HIGH) ;
  delay(250);
  digitalWrite (BUZZER , LOW) ;
  delay(200);
  digitalWrite (BUZZER , HIGH) ;
  delay(100);
  digitalWrite (BUZZER , LOW) ;
}

void flicker( int LEDid) {
  for ( int i = 0 ; i < 5 ; i++ )
  {
    digitalWrite(LEDid, HIGH);
    delay(25);
    digitalWrite(LEDid, LOW);
    delay(25);
  }
}


void loadAll() {
  int address = 0 + sizeof(boolean); // begin at end of isInit
  EEPROM.get ( address, drunkModeActivated ) ;
  address += sizeof(boolean);
  EEPROM.get ( address, ALCOHOL_THRESHHOLD ) ;
  address += sizeof(int);
  //EEPROM.get ( address, CODE ) ;

}

/*
  void save(){
  //save in this order : boolean drunkModeActivated   ->  int ALCOHOL_THRESHHOLD    -->  String CODE
  // EEPROM.put( adress , item) uses EEPROM.update() to perform the write, so does not rewrites the value if it didn't change.

  int address=0;
  EEPROM.put ( address, drunkModeActivated ) ;
  address+= sizeof(boolean);
  EEPROM.put ( address, ALCOHOL_THRESHHOLD ) ;
  address+= sizeof(int);
  EEPROM.put ( address, CODE ) ;

  }
*/
void saveState() {
  // save   drunkModeActivated starting from byte 0
  EEPROM.put (0 + sizeof(boolean), drunkModeActivated ) ;
}
void saveAlcoholLevel() {
  //save ALCOHOL_THRESHHOLD starting from byte just after the drunkModeActivated
  EEPROM.put ( 0 + sizeof(boolean) + sizeof(boolean) , ALCOHOL_THRESHHOLD ) ;
}
void saveCODE() {
  int address = 0 + sizeof(boolean) + sizeof(boolean) + sizeof(int);
  EEPROM.put ( address, CODE ) ;
}
void saveAll () {
  saveState();
  saveAlcoholLevel();
 // saveCODE();
  setIsInit();
}

void setIsInit () {
  EEPROM.put ( 0, true ) ;
}
void getIsInit() {
  EEPROM.get ( 0, isInit ) ;

}









