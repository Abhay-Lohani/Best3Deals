import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../service/Environment.dart';

class AddFlyerDialog extends StatefulWidget {
  const AddFlyerDialog({Key? key}) : super(key: key);

  @override
  _AddFlyerDialogState createState() => _AddFlyerDialogState();
}

class _AddFlyerDialogState extends State<AddFlyerDialog> {
  final _formKey = GlobalKey<FormState>();

  // Text controllers
  final TextEditingController _nameController = TextEditingController();
  final TextEditingController _descriptionController = TextEditingController();
  final TextEditingController _imageUrlController = TextEditingController();
  final TextEditingController _storeIdController = TextEditingController();

  bool _isSubmitting = false;

  /// Calls POST /flyers to create a new flyer
  Future<void> _addFlyer() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() {
      _isSubmitting = true;
    });

    try {
      final SharedPreferences prefs = await SharedPreferences.getInstance();
      String? jwtToken = prefs.getString('jwtToken');

      final flyerData = {
        "name": _nameController.text.trim(),
        "description": _descriptionController.text.trim(),
        "imageUrl": _imageUrlController.text.trim(),
        "storeId": int.tryParse(_storeIdController.text.trim()) ?? 0
      };

      // POST /flyers
      final response = await ApiService().callApi(
        url: ApiConfig.postFlyers,
        headers: {
          "Content-Type": "application/json",
          "Authorization": "Bearer $jwtToken",
        },
        body: jsonEncode(flyerData), // can jsonEncode here or let callApi handle it
      );

      if (response["success"] == true) {
        // Flyer created successfully
        Navigator.of(context).pop(true); // Return 'true' to indicate success
      } else {
        // Show an error
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(response["message"] ?? "Failed to add flyer")),
        );
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Error adding flyer: $e")),
      );
    } finally {
      setState(() {
        _isSubmitting = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text("Add Flyer"),
      content: SingleChildScrollView(
        child: Form(
          key: _formKey,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              // Name field
              TextFormField(
                controller: _nameController,
                decoration: const InputDecoration(labelText: "Name"),
                validator: (val) =>
                (val == null || val.isEmpty) ? "Enter a name" : null,
              ),
              // Description field
              TextFormField(
                controller: _descriptionController,
                decoration: const InputDecoration(labelText: "Description"),
                validator: (val) =>
                (val == null || val.isEmpty) ? "Enter a description" : null,
              ),
              // Image URL field
              TextFormField(
                controller: _imageUrlController,
                decoration: const InputDecoration(labelText: "Image URL"),
                validator: (val) =>
                (val == null || val.isEmpty) ? "Enter an image URL" : null,
              ),
              // Store ID field
              TextFormField(
                controller: _storeIdController,
                decoration: const InputDecoration(labelText: "Store ID"),
                keyboardType: TextInputType.number,
                validator: (val) =>
                (val == null || val.isEmpty) ? "Enter a store ID" : null,
              ),
            ],
          ),
        ),
      ),
      actions: [
        TextButton(
          onPressed: _isSubmitting ? null : () => Navigator.of(context).pop(false),
          child: const Text("Cancel"),
        ),
        ElevatedButton(
          onPressed: _isSubmitting ? null : _addFlyer,
          child: _isSubmitting
              ? const SizedBox(
            width: 16,
            height: 16,
            child: CircularProgressIndicator(
              strokeWidth: 2,
              valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
            ),
          )
              : const Text("Add Flyer"),
        ),
      ],
    );
  }
}
