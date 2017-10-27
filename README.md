# ir-led-remote

Just some code to control cheap chinese rgb led stripes. There is some code hidden which can be used to replace any ir remote control. It may be necessary to reverse engineer the ir codes of the remote. I've used an Arduino with an IR Receiver and the following lib: [IRLib](https://github.com/cyborg5/IRLib/)

```
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
