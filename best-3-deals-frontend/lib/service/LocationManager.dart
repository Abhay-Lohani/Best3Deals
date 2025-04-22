import 'dart:async';
import 'package:geocoding/geocoding.dart';
import 'package:geolocator/geolocator.dart';
import 'package:shared_preferences/shared_preferences.dart';

/// Handles all operations related to obtaining and storing the device's location.
/// Uses the Geolocator and Geocoding packages to fetch coordinates and convert them into a readable address.
class LocationManager {
  // Singleton instance to ensure one consistent instance across the app.
  static final LocationManager _singleton = LocationManager._internal();

  // Default values for latitude and longitude.
  double latitude = 0.0;
  double longitude = 0.0;

  // Optional subscription for listening to location updates (if needed).
  StreamSubscription<Position>? subscription;

  // Holds the most recent location; nullable because it may not be available immediately.
  Position? currentLocation;

  // Factory constructor that always returns the same instance.
  factory LocationManager() => _singleton;

  // Private constructor used for the singleton pattern.
  LocationManager._internal();

  /// Retrieves the current location.
  /// If not already available, it fetches it using the Geolocator.
  Future<Position?> getCurrentLocation() async {
    try {
      // Only fetch if we haven't already set the current location.
      currentLocation ??= await Geolocator.getCurrentPosition();
    } catch (ex) {
      print("Error in location retrieval: $ex");
    }
    return currentLocation;
  }

  /// Checks if location services are enabled and permissions are granted.
  /// If permissions are denied, it requests them, then returns the current location.
  Future<Position?> getGeoLocation() async {
    try {
      bool serviceEnabled = await Geolocator.isLocationServiceEnabled();
      LocationPermission permission = await Geolocator.checkPermission();
      if (permission == LocationPermission.denied) {
        // Request permission if it's denied.
        permission = await Geolocator.requestPermission();
        if (permission != LocationPermission.denied && serviceEnabled) {
          return getCurrentLocation();
        } else {
          return null;
        }
      } else if ((permission == LocationPermission.always ||
          permission == LocationPermission.whileInUse) &&
          serviceEnabled) {
        return getCurrentLocation();
      } else {
        return null;
      }
    } catch (e) {
      print("Error in location retrieval: $e");
      return null;
    }
  }

  /// Converts the current coordinates into a human-readable address.
  /// Saves both the coordinates and postal code locally for later use.
  fetchLocationAddress() async {
    var addresses;
    var locationMap;
    try {
      var location = await LocationManager().getGeoLocation();
      if (location != null) {
        // Create a map to hold location details.
        locationMap = <String, dynamic>{};
        locationMap["LATITUDE"] = location.latitude;
        locationMap["LONGITUDE"] = location.longitude;

        // Use the geocoding package to get address details from coordinates.
        addresses = await placemarkFromCoordinates(
            locationMap["LATITUDE"], locationMap["LONGITUDE"]);

        // Build an address string using key details.
        locationMap["LOCATION"] =
        "${addresses.first.name}, ${addresses.first.subLocality},${addresses.first.locality},"
            "${addresses.first.postalCode},${addresses.first.administrativeArea},${addresses.first.country}";
        // Clean the address string from any unwanted characters.
        locationMap["LOCATION"] = locationMap["LOCATION"]
            .replaceAll(RegExp(r'[^a-zA-Z0-9,.\-\s]+'), '');

        // Save the coordinates and postal code locally.
        saveLocation(location.latitude, location.longitude);
        savePostalCode(addresses.first.postalCode.toString());
      } else {
        print('Something went wrong while fetching location coordinates');
      }
    } catch (e) {
      if (locationMap != null) {
        print("Location retrieved");
      } else {
        print('Something went wrong while fetching location coordinates');
      }
    }
  }

  /// Stores the latitude and longitude in SharedPreferences.
  Future<void> saveLocation(double latitude, double longitude) async {
    final SharedPreferences prefs = await SharedPreferences.getInstance();
    await prefs.setDouble('latitude', latitude);
    await prefs.setDouble('longitude', longitude);
  }

  /// Retrieves the stored latitude and longitude from SharedPreferences.
  /// Returns a map if available, otherwise null.
  Future<Map<String, double>?> getLocation() async {
    final SharedPreferences prefs = await SharedPreferences.getInstance();
    final double? latitude = prefs.getDouble('latitude');
    final double? longitude = prefs.getDouble('longitude');

    if (latitude != null && longitude != null) {
      return {'latitude': latitude, 'longitude': longitude};
    } else {
      return null; // No saved location found
    }
  }

  /// Saves the provided postal code in SharedPreferences.
  Future<void> savePostalCode(String postalCode) async {
    final SharedPreferences prefs = await SharedPreferences.getInstance();
    await prefs.setString('postalCode', postalCode);
  }

  /// Retrieves the postal code from SharedPreferences.
  /// Returns a map containing the postal code if it exists, otherwise null.
  Future<Map<String, String>?> getPostalCode() async {
    final SharedPreferences prefs = await SharedPreferences.getInstance();
    final String? postalCode = prefs.getString('postalCode');

    if (!postalCode!.isEmpty) {
      return {'postalCode': postalCode};
    } else {
      return null; // No saved location found
    }
  }
}
