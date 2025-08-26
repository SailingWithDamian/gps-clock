# "GPS" Clock

This project implements a basic clock application.

Features:
* Synchronise time to that present in `GGA` NMEA messages (x-ref https://aprs.gids.nl/nmea/)
* Record the current time on key press

Dependencies:
* None - If a NMEA feed is available on 1456/udp (such as that produced by a Yacht Devices Wi-Fi Gateway) the messages will be parsed

## Why?

(1) Accurate time
When performing sight reduction accurate time is important, if you are lucky then you have a chronometer,
if you are cheap you have a casio watch. All clocks drift and require re-syncing from an accurate resource,
some clocks drift very quickly (some phones), others drift at a predictable and slow rate (chronometers).

Using a casio watch as an example, the paperwork advertises ±30 seconds per month,
reality is around 4 seconds in 2 weeks, this is suitable for a short passage but starts to become problematic.

Re-syncing from the GPS time gives a 'known point' in time to then start accounting for error from.

Is this cheating? Yes and some alternate-to-gps solutions include Iridium STL & SSB (in certain parts of the world).

(2) Not enough hands
On some sextants, such as those from Cassens & Plath, provide dedicated brackets for mounting your casio watch.
On some boats you have crew to show "now" at.
When on your own, with a more basic sextant, you quickly run out of hands while adjusting the sextant, not falling overboard and hitting the stop watch.

While putting the watch on the arm you are holding the sextant with and pressing the button with your other hand works well enough,
when practicing or wanting multiple shots in reasonably quick succession (for example to get an average), becomes a little cumbersome.

In dry conditions, being able to 'click' the button on a phone in your pocket, scribble down the altitude and then afterwards match up the times, makes life easier.

Is this reliable? Not super, but at least for standing on a beach practicing is quite nice.

## Warning:

Do not rely on this for navigation purposes, you should be well versed in the sight reduction process and understand the limitations by this kind of solution.

## Local development

Generally I deploy to my phone via Device Manager in Android Studio (development over wifi enabled).

### Command line
Build APK:
```
$ ./gradlew build
```

Start an emulated device
```
$ ~/Library/Android/sdk/emulator/emulator -avd Pixel_9
```

Push APK to device (virtual or physical - uses device manager):
```
$ ./gradlew installDebug

> Task :app:installDebug
Installing APK 'app-debug.apk' on 'Pixel_9(AVD) - 16' for :app:debug
Installed on 1 device.

BUILD SUCCESSFUL in 7s
35 actionable tasks: 1 executed, 34 up-to-date

```

## Publishing

### Locally
