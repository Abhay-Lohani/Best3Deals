import 'package:flutter/material.dart';
import 'package:geolocator/geolocator.dart';
import 'package:geocoding/geocoding.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:shared_preferences/shared_preferences.dart';

/// This page allows the user to select a search radius for finding nearby stores.
/// It fetches the user's current location and displays it on a Google Map with a circle representing the search radius.
class SearchRadiusPage extends StatefulWidget {
  const SearchRadiusPage({Key? key}) : super(key: key);

  @override
  _SearchRadiusPageState createState() => _SearchRadiusPageState();
}

class _SearchRadiusPageState extends State<SearchRadiusPage> {
  GoogleMapController? _mapController;
  LatLng? _currentLocation;
  String _currentAddress = "Fetching location...";
  int _radius = 1;
  // Predefined options for radius selection.
  final List<int> _radiusOptions = [1, 2, 5, 10, 20, 40, 80, 100, 250, 500];

  @override
  void initState() {
    super.initState();
    _fetchUserLocation();
    _loadSavedRadius();
  }

  /// Fetches the user's current location and resolves it to a readable address.
  Future<void> _fetchUserLocation() async {
    try {
      Position position = await Geolocator.getCurrentPosition(desiredAccuracy: LocationAccuracy.high);
      List<Placemark> placemarks = await placemarkFromCoordinates(position.latitude, position.longitude);
      Placemark place = placemarks[0];
      setState(() {
        _currentLocation = LatLng(position.latitude, position.longitude);
        _currentAddress = "${place.locality}, ${place.country}";
      });
    } catch (e) {
      print("Error fetching location: $e");
    }
  }

  /// Loads the previously saved search radius from SharedPreferences.
  Future<void> _loadSavedRadius() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    setState(() {
      _radius = prefs.getInt('search_radius') ?? 1;
    });
  }

  /// Saves the selected search radius to SharedPreferences.
  Future<void> _saveRadius() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    await prefs.setInt('search_radius', _radius);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Select Search Radius"),centerTitle: true,
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text("Location: $_currentAddress", style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
            const SizedBox(height: 16),
            // Dropdown for selecting the search radius.
            DropdownButton<int>(
              value: _radius,
              onChanged: (value) {
                setState(() {
                  _radius = value!;
                });
              },
              items: _radiusOptions
                  .map((radius) => DropdownMenuItem(
                value: radius,
                child: Text("$radius kilometre${radius > 1 ? "s" : ""}"),
              ))
                  .toList(),
            ),
            const SizedBox(height: 16),
            // Display a Google Map with a marker and a circle showing the search radius.
            Expanded(
              child: _currentLocation == null
                  ? const Center(child: CircularProgressIndicator())
                  : GoogleMap(
                initialCameraPosition: CameraPosition(target: _currentLocation!, zoom: 13),
                onMapCreated: (GoogleMapController controller) {
                  _mapController = controller;
                },
                markers: {
                  Marker(
                    markerId: const MarkerId("currentLocation"),
                    position: _currentLocation!,
                    icon: BitmapDescriptor.defaultMarker,
                  ),
                },
                circles: {
                  Circle(
                    circleId: const CircleId("searchRadius"),
                    center: _currentLocation!,
                    radius: _radius * 1000,
                    strokeColor: Colors.blue,
                    strokeWidth: 2,
                    fillColor: Colors.blue.withOpacity(0.3),
                  ),
                },
              ),
            ),
            const SizedBox(height: 16),
            // Apply button saves the radius and closes the page.
            ElevatedButton(
              onPressed: () async {
                await _saveRadius();
                Navigator.pop(context);
              },
              child: const Text("Apply"),
            ),
          ],
        ),
      ),
    );
  }
}
