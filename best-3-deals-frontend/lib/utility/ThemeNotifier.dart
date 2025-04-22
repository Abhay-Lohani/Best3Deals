import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

// ThemeNotifier class extends ChangeNotifier to manage theme state changes
class ThemeNotifier extends ChangeNotifier {
  bool _isDarkMode = false; // Private variable to store the current theme state

  bool get isDarkMode => _isDarkMode; // Getter to access the current theme mode

  // Constructor to initialize and load the stored theme preference
  ThemeNotifier() {
    _loadTheme();
  }

  // Asynchronous method to load the saved theme preference from SharedPreferences
  Future<void> _loadTheme() async {
    SharedPreferences prefs = await SharedPreferences.getInstance(); // Get SharedPreferences instance
    _isDarkMode = prefs.getBool('isDarkMode') ?? false; // Retrieve stored value, default to false
    notifyListeners(); // Notify listeners about the change
  }

  // Method to toggle the theme and save the preference in SharedPreferences
  Future<void> toggleTheme(bool value) async {
    _isDarkMode = value; // Update the theme mode
    SharedPreferences prefs = await SharedPreferences.getInstance(); // Get SharedPreferences instance
    await prefs.setBool('isDarkMode', value); // Save the new theme preference
    notifyListeners(); // Notify listeners about the change
  }
}
