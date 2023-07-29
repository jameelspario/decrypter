import 'package:flutter/material.dart';

import 'repository.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: LevelApp(),
    );
  }
}

class LevelApp extends StatefulWidget {
  const LevelApp({super.key});

  @override
  State<LevelApp> createState() => _LevelAppState();
}

class _LevelAppState extends State<LevelApp> {
  String _level = "Battry Level";
  String _lableDecrypt = "decrypt";
  final repo = Repository();

  Future _getLevel() async {
    _level = await repo.getLeve();
    setState(() {});
  }

  Future _decrypt() async {
    _lableDecrypt = "staring...";
    setState(() {

    });
    await repo.decrypt("CIAPart1SecAClass2.mp4.enc", "");
    _lableDecrypt = "decrypted...";
    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        child: Center(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Text(_level),
              ElevatedButton(
                child: Text("Get"),
                onPressed: _getLevel,
              ),
              Text(_lableDecrypt),
              ElevatedButton(
                child: Text("decrypt"),
                onPressed: _decrypt,
              ),
            ],
          ),
        ),
      ),
    );
  }
}
