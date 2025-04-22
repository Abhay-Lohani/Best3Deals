import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../service/Environment.dart';
import 'dart:convert';

class AddStoreDialog extends StatefulWidget {
  const AddStoreDialog({Key? key}) : super(key: key);

  @override
  _AddStoreDialogState createState() => _AddStoreDialogState();
}

class _AddStoreDialogState extends State<AddStoreDialog> {
  final _formKey = GlobalKey<FormState>();

  // Controllers for store fields
  final TextEditingController _nameController = TextEditingController();
  final TextEditingController _addressController = TextEditingController();
  final TextEditingController _imgUrlController = TextEditingController();
  final TextEditingController _latitudeController = TextEditingController();
  final TextEditingController _longitudeController = TextEditingController();
  final TextEditingController _brandIdController = TextEditingController();

  bool _isSubmitting = false;

  Future<void> _addStore() async {
    if (!_formKey.currentState!.validate()) return;
    setState(() => _isSubmitting = true);

    try {
      final SharedPreferences prefs = await SharedPreferences.getInstance();
      final String? jwtToken = prefs.getString('jwtToken');
      final double? latitude = prefs.getDouble('latitude');
      final double? longitude = prefs.getDouble('longitude');

      final storeData = {
        "name": _nameController.text.trim(),
        "address": _addressController.text.trim(),
        "imgUrl": _imgUrlController.text.trim(),
        "location": {
          "latitude": latitude ?? 0.0,
          "longitude": longitude ?? 0.0,
        },
        "brandId": 1 ?? 0,
      };

      final response = await ApiService().callApi(
        url: "/stores",
        headers: {
          "Content-Type": "application/json",
          "Authorization": "Bearer $jwtToken",
        },
        body: jsonEncode(storeData),
      );

      if (response["success"] == true) {
        Navigator.of(context).pop(true); // Signal success to refresh list
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(response["message"] ?? "Failed to add store")),
        );
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Error adding store: $e")),
      );
    } finally {
      setState(() => _isSubmitting = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text("Add Store"),
      content: SingleChildScrollView(
        child: Form(
          key: _formKey,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextFormField(
                controller: _nameController,
                decoration: const InputDecoration(labelText: "Name"),
                validator: (val) =>
                (val == null || val.isEmpty) ? "Enter a name" : null,
              ),
              TextFormField(
                controller: _addressController,
                decoration: const InputDecoration(labelText: "Address"),
                validator: (val) =>
                (val == null || val.isEmpty) ? "Enter an address" : null,
              ),
              TextFormField(
                controller: _imgUrlController,
                decoration: const InputDecoration(labelText: "Image URL"),
                validator: (val) =>
                (val == null || val.isEmpty) ? "Enter an image URL" : null,
              ),
              TextFormField(
                controller: _latitudeController,
                decoration: const InputDecoration(labelText: "Latitude"),
                keyboardType:
                const TextInputType.numberWithOptions(decimal: true),
                validator: (val) =>
                (val == null || val.isEmpty) ? "Enter latitude" : null,
              ),
              TextFormField(
                controller: _longitudeController,
                decoration: const InputDecoration(labelText: "Longitude"),
                keyboardType:
                const TextInputType.numberWithOptions(decimal: true),
                validator: (val) =>
                (val == null || val.isEmpty) ? "Enter longitude" : null,
              ),
              TextFormField(
                controller: _brandIdController,
                decoration: const InputDecoration(labelText: "Brand ID"),
                keyboardType: TextInputType.number,
                validator: (val) =>
                (val == null || val.isEmpty) ? "Enter brand ID" : null,
              ),
            ],
          ),
        ),
      ),
      actions: [
        TextButton(
          onPressed:
          _isSubmitting ? null : () => Navigator.of(context).pop(false),
          child: const Text("Cancel"),
        ),
        ElevatedButton(
          onPressed: _isSubmitting ? null : _addStore,
          child: _isSubmitting
              ? const SizedBox(
            width: 16,
            height: 16,
            child: CircularProgressIndicator(
              strokeWidth: 2,
              valueColor:
              AlwaysStoppedAnimation<Color>(Colors.white),
            ),
          )
              : const Text("Add Store"),
        ),
      ],
    );
  }
}
