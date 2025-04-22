import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter_native_splash/flutter_native_splash.dart';
import 'DashboardPage.dart';
import 'SignInPage.dart';
import '../../db/DatabaseHelper.dart';
import '../../service/LocationManager.dart';

/// The splash screen that is shown on app startup.
/// It fetches the user's location and then checks if the user is already logged in
/// to decide whether to navigate to the Dashboard or the SignIn page.
class SplashPage extends StatefulWidget {
  const SplashPage({Key? key}) : super(key: key);

  @override
  _SplashPageState createState() => _SplashPageState();
}

class _SplashPageState extends State<SplashPage> {
  final DatabaseHelper _dbHelper = DatabaseHelper();

  @override
  void initState() {
    // Remove the native splash screen.
    FlutterNativeSplash.remove();
    super.initState();
    // Fetch location and check login status.
    _getLocationAndCheckLoginStatus();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      // Uses the default scaffold background.
      body: Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            // App logo.
            Image.asset('assets/logo.jpg', width: 150),
            const Text(
              'Best 3 Deals',
              style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
            ),
          ],
        ),
      ),
    );
  }

  /// Fetches the user's location and then checks if the user is logged in.
  Future<void> _getLocationAndCheckLoginStatus() async {
    await LocationManager().fetchLocationAddress();
    _checkLoginStatus();
  }

  /// Checks the login status from the local database and navigates accordingly.
  Future<void> _checkLoginStatus() async {
    bool isLoggedIn = await _dbHelper.isUserLoggedIn();
    // Wait for 2 seconds before navigating.
    Future.delayed(const Duration(seconds: 2), () {
      if (isLoggedIn) {
        Navigator.pushReplacement(context, MaterialPageRoute(builder: (context) => const DashboardPage()));
      } else {
        Navigator.pushReplacement(context, MaterialPageRoute(builder: (context) => const SignInPage()));
      }
    });
  }
}
