import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

/// This function handles background messages from Firebase Cloud Messaging.
/// It is invoked when the app is in the background or terminated.
Future<void> _firebaseMessagingBackgroundHandler(RemoteMessage message) async {
  // Ensure Firebase is initialized before processing the message.
  await Firebase.initializeApp();
  debugPrint("Handling a background message: ${message.messageId}");
}

/// NotificationManager is a singleton class that manages FCM and local notifications.
/// It requests necessary permissions, retrieves the FCM token, and displays notifications when needed.
class NotificationManager {
  // Singleton instance
  static final NotificationManager _instance = NotificationManager._internal();
  factory NotificationManager() => _instance;
  NotificationManager._internal();

  // Instance of FirebaseMessaging to handle cloud messaging.
  final FirebaseMessaging _messaging = FirebaseMessaging.instance;

  // Instance of FlutterLocalNotificationsPlugin to show local notifications.
  final FlutterLocalNotificationsPlugin _localNotificationsPlugin =
  FlutterLocalNotificationsPlugin();

  /// Initializes the notification manager.
  /// This method requests notification permissions, retrieves the FCM token, and sets up listeners.
  Future<void> initialize() async {
    // Request notification permissions (important for iOS and Android 13+).
    await _requestPermission();

    // Retrieve and log the FCM token.
    String? token = await _messaging.getToken();
    debugPrint("FCM Token: $token");

    // Listen for token refresh events and log the new token.
    _messaging.onTokenRefresh.listen((newToken) {
      debugPrint("FCM Token refreshed: $newToken");
    });

    // Initialize the local notifications plugin.
    await _initializeLocalNotifications();

    // Listen for messages when the app is in the foreground.
    FirebaseMessaging.onMessage.listen((RemoteMessage message) {
      RemoteNotification? notification = message.notification;
      if (notification != null) {
        _showLocalNotification(notification);
      }
    });

    // Set up the background message handler.
    FirebaseMessaging.onBackgroundMessage(_firebaseMessagingBackgroundHandler);
  }

  /// Requests the user's permission to display notifications.
  Future<void> _requestPermission() async {
    NotificationSettings settings = await _messaging.requestPermission(
      alert: true,
      badge: true,
      sound: true,
    );
    debugPrint("User granted permission: ${settings.authorizationStatus}");
  }

  /// Initializes the local notifications plugin with basic Android settings.
  Future<void> _initializeLocalNotifications() async {
    const AndroidInitializationSettings androidSettings =
    AndroidInitializationSettings('@mipmap/ic_launcher');
    const InitializationSettings initializationSettings =
    InitializationSettings(android: androidSettings);
    await _localNotificationsPlugin.initialize(initializationSettings);
  }

  /// Reads user preferences for sound and vibration and returns Android-specific notification details.
  Future<AndroidNotificationDetails> _getAndroidNotificationDetails() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    bool soundEnabled = prefs.getBool('soundEnabled') ?? true;
    bool vibrationEnabled = prefs.getBool('vibrationEnabled') ?? true;

    return AndroidNotificationDetails(
      'default_channel',
      'Default',
      channelDescription: 'Default channel for notifications',
      importance: Importance.max,
      priority: Priority.high,
      playSound: soundEnabled,
      enableVibration: vibrationEnabled,
    );
  }

  /// Displays a local notification with the provided [notification] details.
  Future<void> _showLocalNotification(RemoteNotification notification) async {
    AndroidNotificationDetails androidDetails = await _getAndroidNotificationDetails();
    NotificationDetails platformDetails = NotificationDetails(android: androidDetails);
    await _localNotificationsPlugin.show(
      notification.hashCode,
      notification.title,
      notification.body,
      platformDetails,
      payload: 'notification_payload',
    );
  }
}
