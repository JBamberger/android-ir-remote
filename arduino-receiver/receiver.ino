#include <IRLibDecodeBase.h>
#include <IRLib_P01_NEC.h>
// #include <IRLib_P02_Sony.h>
// #include <IRLib_P07_NECx.h>
// #include <IRLib_P09_GICable.h>
// #include <IRLib_P11_RCMM.h>
#include <IRLibCombo.h>
IRdecode myDecoder;

#include <IRLibRecv.h>
IRrecv myReceiver(11);

#include <rgb_stripe.h>

const Color remote_colors[] = {
    Color(0xF4, 0x43, 0x36), // 00 -> red0
    Color(0xEF, 0x53, 0x50), // 01 -> red1
    Color(0xD8, 0x43, 0x15), // 02 -> red2
    Color(0xFF, 0x8F, 0x00), // 03 -> red3
    Color(0xFF, 0xEB, 0x3B), // 04 -> red4
    Color(0x4C, 0xAF, 0x50), // 05 -> green0
    Color(0x9C, 0xCC, 0x65), // 06 -> green1
    Color(0x4D, 0xB6, 0xAC), // 07 -> green2
    Color(0x4D, 0xD0, 0xE1), // 08 -> green3
    Color(0x00, 0xE5, 0xFF), // 19 -> green4
    Color(0x28, 0x35, 0x93), // 10 -> blue0
    Color(0x3F, 0x51, 0xB5), // 11 -> blue1
    Color(0x62, 0x00, 0xEA), // 12 -> blue2
    Color(0x8E, 0x24, 0xAA), // 13 -> blue3
    Color(0xD5, 0x00, 0xF9), // 14 -> blue4
    Color(0xff, 0xff, 0xff), // 15 -> white0
    Color(0xFC, 0xE4, 0xEC), // 16 -> white1
    Color(0xE1, 0xBE, 0xE7), // 17 -> white2
    Color(0x64, 0xB5, 0xF6), // 18 -> white3
    Color(0x42, 0xA5, 0xF5), // 19 -> white4
};

const uint16_t buttons[] = {
    0xFF02, // power
    0xFF82, // play
    0xFF3A, // up
    0xFFBA, // down
    0xFFE8, // quick
    0xFFC8, // slow
    0xFFF0, // auto
    0xFFD0, // flash
    0xFF20, // jump3
    0xFFA0, // jump7
    0xFF60, // fade3
    0xFFE0, // fade7
    0xFF28, // redUp
    0xFF08, // redDown
    0xFFA8, // greenUp
    0xFF88, // greenDown
    0xFF68, // blueUp
    0xFF48, // blueDown
    0xFF30, // diy1
    0xFFB0, // diy2
    0xFF70, // diy3
    0xFF10, // diy4
    0xFF90, // diy5
    0xFF50, // diy6
    0xFF1A, // red0
    0xFF2A, // red1
    0xFF0A, // red2
    0xFF38, // red3
    0xFF18, // red4
    0xFF9A, // green0
    0xFFAA, // green1
    0xFF8A, // green2
    0xFFB8, // green3
    0xFF98, // green4
    0xFFA2, // blue0
    0xFF92, // blue1
    0xFFB2, // blue2
    0xFF78, // blue3
    0xFF58, // blue4
    0xFF22, // white0
    0xFF12, // white1
    0xFF32, // white2
    0xFFF8, // white3
    0xFFD8, // white4
};

const Color effect_colors_3[] = {RED, GREEN, BLUE};
const Color effect_colors_7[] = {
    remote_colors[0],
    remote_colors[2],
    remote_colors[4],
    remote_colors[5],
    remote_colors[8],
    remote_colors[10],
    remote_colors[14],
};

CrossFadeEffect effect_fade3(effect_colors_3, 3);
CrossFadeEffect effect_fade7(effect_colors_7, 7);

ConstantColorEffect off_effect(BLACK);
ConstantColorEffect red_effect(RED);
ConstantColorEffect green_effect(GREEN);
ConstantColorEffect blue_effect(BLUE);

RgbController light_controller(2, 3, 4, &off_effect, false);

int color = 0;

void setup()
{
    Serial.begin(9600);
    delay(2000);
    while (!Serial)
        ;                    // delay for Leonardo
    myReceiver.enableIRIn(); // Start the receiver
    Serial.println(F("Ready to receive IR signals"));
}
void loop()
{
    light_controller.update();
    //Continue looping until you get a complete signal received
    if (myReceiver.getResults())
    {
        myDecoder.decode();          // Decode it
        myDecoder.dumpResults(true); // Now print results. Use false for less detail
        myReceiver.enableIRIn();     // Restart receiver

        switch (color)
        {
        case 0:
            light_controller.set_effect(&red_effect);
            break;
        case 1:
            light_controller.set_effect(&effect_fade3);
            light_controller.set_speed(1);
            break;
        case 2:
            light_controller.set_speed(15);
            break;
        }
        Serial.println(color);
        color++;
        if (color > 2)
            color = 0;
    }
}