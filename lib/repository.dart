import 'package:flutter/services.dart';

class Repository {
  static const platform = const MethodChannel('samples.flutter.dev/battery');

  Future getLeve() async {
    String batteryLevel;
    try {
      final int result = await platform.invokeMethod('getBatteryLevel');
      batteryLevel = 'Battery level at $result % .';
    } on PlatformException catch (e) {
      batteryLevel = "Failed to get battery level: '${e.message}'.";
    }

    return batteryLevel;
  }

  Future decrypt(file, path) async {
    try {
      await platform.invokeMethod("decrypt", {
        "salt": "AWaVR4YLKDphH3in",
        "path": path,
        "filename": file,
      });
      print("file decrypted");
    } on PlatformException catch (e) {
      print(e);
    }
  }
}
