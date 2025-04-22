import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter_native_splash/flutter_native_splash.dart';
import 'package:sellerapp/service/DatabaseHelper.dart';
import 'package:sellerapp/service/LocationManager.dart';
import 'HomePage.dart';
import 'SignInPage.dart';


class SplashPage extends StatefulWidget {
  const SplashPage({Key? key}) : super(key: key);

  @override
  _SplashPageState createState() => _SplashPageState();
}

class _SplashPageState extends State<SplashPage> {
  final DatabaseHelper _dbHelper = DatabaseHelper();

  @override
  void initState() {
    FlutterNativeSplash.remove();
    super.initState();
    _getLocationAndCheckLoginStatus();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      // No explicit background color; theme’s white scaffold background is used.
      body: Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
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

  Future<void> _getLocationAndCheckLoginStatus() async {
    await LocationManager().fetchLocationAddress();
    _checkLoginStatus();
  }

  Future<void> _checkLoginStatus() async {
    bool isLoggedIn = await _dbHelper.isUserLoggedIn();
    Future.delayed(const Duration(seconds: 2), () {
      if (isLoggedIn) {
        Navigator.pushReplacement(context, MaterialPageRoute(builder: (context) => const HomePage()));
      } else {
        Navigator.pushReplacement(context, MaterialPageRoute(builder: (context) => const SignInPage()));
      }
    });
  }
}
