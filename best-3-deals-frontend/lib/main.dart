import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/material.dart';
import 'package:flutter_native_splash/flutter_native_splash.dart';
import 'package:frontend_ui/service/NotificationManager.dart';
import 'package:frontend_ui/ui/pages/SplashPage.dart';
import 'package:frontend_ui/utility/ThemeNotifier.dart';
import 'package:provider/provider.dart';
import 'db/DatabaseHelper.dart';

// Define a RouteObserver to listen for route changes.
final RouteObserver<PageRoute> routeObserver = RouteObserver<PageRoute>();

void main() async {
  WidgetsBinding widgetsBinding = WidgetsFlutterBinding.ensureInitialized();
  FlutterNativeSplash.preserve(widgetsBinding: widgetsBinding);

  // Initialize Firebase
  await Firebase.initializeApp();

  // Initialize the notifications manager.
  await NotificationManager().initialize();

  // Initialize the database
  await DatabaseHelper().database;

  runApp(
    ChangeNotifierProvider(
      create: (_) => ThemeNotifier(),
      child: const MyApp(),
    ),
  );
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    final themeNotifier = Provider.of<ThemeNotifier>(context);
    return MaterialApp(
      title: 'Best 3 Deals',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        elevatedButtonTheme: ElevatedButtonThemeData(
          style: ElevatedButton.styleFrom(
            backgroundColor: Colors.blue,
            foregroundColor: Colors.white,
          ),
        ),
        brightness: Brightness.light,
        primarySwatch: Colors.blue,
        scaffoldBackgroundColor: Colors.white,
        appBarTheme: const AppBarTheme(
          backgroundColor: Colors.blue,
          foregroundColor: Colors.white,
        ),
        textTheme: const TextTheme(
          bodyLarge: TextStyle(color: Colors.black),
          bodyMedium: TextStyle(color: Colors.black87),
        ),
      ),

      darkTheme: ThemeData(
        elevatedButtonTheme: ElevatedButtonThemeData(
          style: ElevatedButton.styleFrom(
            backgroundColor: Colors.blue,
            foregroundColor: Colors.white,
          ),
        ),
        brightness: Brightness.dark,
        primaryColor: Colors.teal,
        scaffoldBackgroundColor: Colors.grey[900],
        appBarTheme: AppBarTheme(
          backgroundColor: Colors.grey[850],
          foregroundColor: Colors.white,
        ),
        textTheme: ThemeData.dark().textTheme.copyWith(
          bodyLarge: const TextStyle(color: Colors.white),
          bodyMedium: const TextStyle(color: Colors.white70),
        ),
        cardColor: Colors.grey[800],
        dividerColor: Colors.grey[700],
      ),
      themeMode: themeNotifier.isDarkMode ? ThemeMode.dark : ThemeMode.light,
      home: const SplashPage(), // or your initial widget
    );
  }
}
