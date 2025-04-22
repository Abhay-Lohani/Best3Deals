import 'dart:async';
import 'package:geocoding/geocoding.dart';
import 'package:geolocator/geolocator.dart';
import 'package:shared_preferences/shared_preferences.dart';

class LocationManager {
  static final LocationManager _singleton = LocationManager._internal();
  double latitude=0.0;
  double longitude=0.0;
  StreamSubscription<Position>? subscription;
  Position? currentLocation; // Made nullable
  factory LocationManager() => _singleton;
  LocationManager._internal();

  Future<Position?> getCurrentLocation() async {
    try {
      currentLocation ??= await Geolocator.getCurrentPosition();
    } catch (ex) {
      print("Error in location retrieval: $ex");
    }
    return currentLocation;
  }

  Future<Position?> getGeoLocation() async {
    try {
      bool serviceEnabled = await Geolocator.isLocationServiceEnabled();
      LocationPermission permission = await Geolocator.checkPermission();
      if (permission == LocationPermission.denied) {
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

  fetchLocationAddress() async {
    var addresses;
    var locationMap;
    try{
      var location = await LocationManager().getGeoLocation();
      if (location != null) {
        locationMap = <String, dynamic>{};
        locationMap["LATITUDE"] = location.latitude;
        locationMap["LONGITUDE"] = location.longitude;
        addresses =
        await placemarkFromCoordinates(locationMap["LATITUDE"], locationMap["LONGITUDE"]);
        locationMap["LOCATION"] =
        "${addresses.first.name}, ${addresses.first.subLocality},${addresses.first.locality},"
            "${addresses.first.postalCode},${addresses.first.administrativeArea},${addresses.first.country}";
        locationMap["LOCATION"] =locationMap["LOCATION"].replaceAll(RegExp(r'[^a-zA-Z0-9,.\-\s]+'), '');
        saveLocation(location.latitude, location.longitude);
        savePostalCode(addresses.first.postalCode.toString());
      }
      else {
        print('Something went wrong while fetching location coordinates');
      }
    }catch(e){
      if(locationMap!=null){
        print("Location retrieved");
      }else{
        print('Something went wrong while fetching location coordinates');
      }
    }
  }

  Future<void> saveLocation(double latitude, double longitude) async {
    final SharedPreferences prefs = await SharedPreferences.getInstance();
    await prefs.setDouble('latitude', latitude);
    await prefs.setDouble('longitude', longitude);
  }

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

  Future<void> savePostalCode(String postalCode) async {
    final SharedPreferences prefs = await SharedPreferences.getInstance();
    await prefs.setString('postalCode', postalCode);
  }

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


