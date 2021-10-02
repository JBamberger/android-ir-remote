# Android Infrared Remote Control
[![Android CI](https://github.com/JBamberger/android-ir-remote/actions/workflows/android.yml/badge.svg)](https://github.com/JBamberger/android-ir-remote/actions/workflows/android.yml)

This repository contains a prototypical implementation of an infrared remote control. The App uses
the integrated IR blaster of the phone to mimic the original ir codes. The IR codes must be
extracted from the original remote control. To do so, an Arduino board an an IR receiver can be
used. The following snippet samples the IR receiver connected to an Arduino and prints the received
codes. It uses the following great IR library: [IRLib](https://github.com/cyborg5/IRLib/)

```c++
#include <IRLib.h>
 
IRrecv My_Receiver(11); //connect the receiver to pin 11
 
IRdecode My_Decoder;

int c = 0;

void setup()
{
  Serial.begin(9600);
  My_Receiver.enableIRIn(); // Start the receiver
}
 
void loop()
{

//Continuously look for results. When you have them pass them to the decoder
  if (My_Receiver.GetResults(&My_Decoder)) {
    Serial.print("Sample nr.");
    Serial.print(c++);
    Serial.print("\n");
    My_Decoder.decode();    //Decode the data
    My_Decoder.DumpResults(); //Show the results on serial monitor
    My_Receiver.resume();     //Restart the receiver
  }
}
```
