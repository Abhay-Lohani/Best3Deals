import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../../utility/ThemeNotifier.dart';

/// This page displays application settings including dark mode, sound, and vibration options.
/// The settings are persisted using SharedPreferences.
class SettingsPage extends StatefulWidget {
  const SettingsPage({Key? key}) : super(key: key);

  @override
  _SettingsPageState createState() => _SettingsPageState();
}

class _SettingsPageState extends State<SettingsPage> {
  bool soundEnabled = true;
  bool vibrationEnabled = true;

  @override
  void initState() {
    super.initState();
    _loadOtherSettings();
  }

  /// Loads additional settings (sound and vibration) from SharedPreferences.
  Future<void> _loadOtherSettings() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    setState(() {
      soundEnabled = prefs.getBool('soundEnabled') ?? true;
      vibrationEnabled = prefs.getBool('vibrationEnabled') ?? true;
    });
  }

  /// Toggles the sound setting and saves the value.
  Future<void> _toggleSound(bool value) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    await prefs.setBool('soundEnabled', value);
    setState(() {
      soundEnabled = value;
    });
  }

  /// Toggles the vibration setting and saves the value.
  Future<void> _toggleVibration(bool value) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    await prefs.setBool('vibrationEnabled', value);
    setState(() {
      vibrationEnabled = value;
    });
  }

  @override
  Widget build(BuildContext context) {
    final themeNotifier = Provider.of<ThemeNotifier>(context);

    return Scaffold(
      appBar: AppBar(
        title: const Text("App Settings"),centerTitle: true,
      ),
      body: ListView(
        children: [
          // Dark mode toggle.
          SwitchListTile(
            title: const Text("Dark Mode"),
            value: themeNotifier.isDarkMode,
            onChanged: (value) {
              themeNotifier.toggleTheme(value);
            },
          ),
          // Sound toggle.
          SwitchListTile(
            title: const Text("Sound Enabled"),
            value: soundEnabled,
            onChanged: _toggleSound,
          ),
          // Vibration toggle.
          SwitchListTile(
            title: const Text("Vibration Enabled"),
            value: vibrationEnabled,
            onChanged: _toggleVibration,
          ),
          // Additional settings can be added here.
        ],
      ),
    );
  }
}
