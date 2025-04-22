import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:sellerapp/pages/HomePage.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../service/Environment.dart';

class EnterStoreDetailsPage extends StatefulWidget {
  const EnterStoreDetailsPage({Key? key}) : super(key: key);

  @override
  _EnterStoreDetailsPageState createState() => _EnterStoreDetailsPageState();
}

class _EnterStoreDetailsPageState extends State<EnterStoreDetailsPage> {
  final _formKey = GlobalKey<FormState>();

  // Controllers for the text fields.
  final _nameController = TextEditingController();
  final _addressController = TextEditingController();
  final _imageUrlController = TextEditingController();

  bool _isSubmitting = false;

  // Submits the store details using the post stores API.
  Future<void> _submitStoreDetails() async {
    if (_formKey.currentState!.validate()) {
      setState(() {
        _isSubmitting = true;
      });
      final SharedPreferences prefs = await SharedPreferences.getInstance();
      var latitude = await prefs.getDouble('latitude');
      var longitude = await prefs.getDouble('longitude');
      var storeDetails = {
        "name": _nameController.text,
        "address": _addressController.text,
        "imageUrl": _imageUrlController.text,
        "location": {
          "latitude": latitude ?? 0,
          "longitude": longitude ?? 0,
        },
        "brandId": 1
      };

      try {
        String? jwtToken = prefs.getString('jwtToken');

        // Post the store details to the backend.
        var response = await ApiService().callApi(
          url: ApiConfig.postStores,
          headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer $jwtToken",
          },
          body: jsonEncode(storeDetails),
        );

        if (response["success"] == true) {
          final storeId = response["data"]["data"]['id'];

          // Save the storeId in SharedPreferences
          await prefs.setString("storeId", storeId.toString());

          // Show a dialog box indicating success.
          showDialog(
            context: context,
            barrierDismissible: false, // Prevent dismissing the dialog by tapping outside.
            builder: (BuildContext dialogContext) {
              return AlertDialog(
                title: const Text("Success"),
                content: const Text("Store details submitted successfully."),
                actions: [
                  TextButton(
                    onPressed: () {
                      Navigator.pushReplacement(
                        context,
                        MaterialPageRoute(builder: (context) => const HomePage()),
                      );
                    },
                    child: const Text("OK"),
                  ),
                ],
              );
            },
          );

          // Clear the form fields.
          _nameController.clear();
          _addressController.clear();
          _imageUrlController.clear();
        } else {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text("Failed to submit store details")),
          );
        }
      } catch (e) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text("Error submitting store details: $e")),
        );
      } finally {
        setState(() {
          _isSubmitting = false;
        });
      }
    }
  }

  @override
  void dispose() {
    _nameController.dispose();
    _addressController.dispose();
    _imageUrlController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    // Wrapping the Scaffold with WillPopScope prevents navigating back using the device's back button.
    return WillPopScope(
      onWillPop: () async => false,
      child: Scaffold(
        appBar: AppBar(
          title: const Text("Enter Store Details"),
          centerTitle: true,
        ),
        body: SingleChildScrollView(
          padding: const EdgeInsets.all(16.0),
          child: Form(
            key: _formKey,
            child: Column(
              children: [
                // Name text field.
                TextFormField(
                  controller: _nameController,
                  decoration: buildInputDecoration("Name"),
                  validator: (value) => value == null || value.isEmpty
                      ? "Please enter a name"
                      : null,
                ),
                const SizedBox(height: 16),
                // Address text field.
                TextFormField(
                  controller: _addressController,
                  decoration: buildInputDecoration("Address"),
                  validator: (value) => value == null || value.isEmpty
                      ? "Please enter an address"
                      : null,
                ),
                const SizedBox(height: 16),
                // Image URL text field.
                TextFormField(
                  controller: _imageUrlController,
                  decoration: buildInputDecoration("Enter Image URL"),
                  validator: (value) => value == null || value.isEmpty
                      ? "Please enter an image URL"
                      : null,
                ),
                const SizedBox(height: 24),
                // Submit button.
                SizedBox(
                  width: double.infinity,
                  height: 60,
                  child: ElevatedButton(
                    onPressed: _isSubmitting ? null : _submitStoreDetails,
                    child: _isSubmitting
                        ? const CircularProgressIndicator(
                      valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
                    )
                        : const Text(
                      "Submit",
                      style: TextStyle(fontSize: 18),
                    ),
                  ),
                ),
                const SizedBox(height: 16),
                // "Store already exists?" button.
                SizedBox(
                  width: double.infinity,
                  height: 60,
                  child: OutlinedButton(
                    onPressed: () {
                      Navigator.pushReplacement(
                        context,
                        MaterialPageRoute(builder: (context) => const HomePage()),
                      );
                    },
                    child: const Text(
                      "Store already exists?",
                      style: TextStyle(fontSize: 18),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  InputDecoration buildInputDecoration(String label) {
    return InputDecoration(
      labelText: label,
      filled: true,
      fillColor: Colors.white.withOpacity(0.7),
      border: OutlineInputBorder(
        borderRadius: BorderRadius.circular(30.0),
        borderSide: const BorderSide(color: Colors.black),
      ),
      enabledBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(30.0),
        borderSide: BorderSide(color: Colors.black.withOpacity(0.5)),
      ),
      focusedBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(30.0),
        borderSide: const BorderSide(color: Colors.black),
      ),
      errorStyle: const TextStyle(color: Colors.red),
    );
  }
}
