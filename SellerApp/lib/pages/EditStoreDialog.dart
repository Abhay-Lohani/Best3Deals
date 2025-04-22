import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../service/Environment.dart';
import 'dart:convert';

class EditStoreDialog extends StatefulWidget {
  final Map<String, dynamic> store;
  const EditStoreDialog({Key? key, required this.store}) : super(key: key);

  @override
  _EditStoreDialogState createState() => _EditStoreDialogState();
}

class _EditStoreDialogState extends State<EditStoreDialog> {
  final _formKey = GlobalKey<FormState>();

  late TextEditingController _nameController;
  late TextEditingController _addressController;
  late TextEditingController _imgUrlController;
  late TextEditingController _latitudeController;
  late TextEditingController _longitudeController;
  late TextEditingController _brandIdController;

  bool _isSubmitting = false;
  late int storeId;

  @override
  void initState() {
    super.initState();
    storeId = widget.store["id"] ?? 0;
    _nameController = TextEditingController(text: widget.store["name"] ?? "");
    _addressController = TextEditingController(text: widget.store["address"] ?? "");
    _imgUrlController = TextEditingController(text: widget.store["imgUrl"] ?? "");

    // Initialize location controllers if available
    var location = widget.store["location"];
    if (location != null) {
      _latitudeController =
          TextEditingController(text: location["latitude"].toString());
      _longitudeController =
          TextEditingController(text: location["longitude"].toString());
    } else {
      _latitudeController = TextEditingController();
      _longitudeController = TextEditingController();
    }
    _brandIdController =
        TextEditingController(text: widget.store["brandId"]?.toString() ?? "0");
  }

  Future<void> _editStore() async {
    if (!_formKey.currentState!.validate()) return;
    setState(() => _isSubmitting = true);

    try {
      final SharedPreferences prefs = await SharedPreferences.getInstance();
      final String? jwtToken = prefs.getString('jwtToken');

      final storeData = {
        "name": _nameController.text.trim(),
        "address": _addressController.text.trim(),
        "imgUrl": _imgUrlController.text.trim(),
        "location": {
          "latitude": double.tryParse(_latitudeController.text.trim()) ?? 0.0,
          "longitude": double.tryParse(_longitudeController.text.trim()) ?? 0.0,
        },
        "brandId": int.tryParse(_brandIdController.text.trim()) ?? 0,
      };

      final response = await ApiService().put(
        "/stores/$storeId",
        headers: {
          "Content-Type": "application/json",
          "Authorization": "Bearer $jwtToken",
        },
        body: jsonEncode(storeData),
      );

      if (response["success"] == true) {
        Navigator.of(context).pop(true);
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(response["message"] ?? "Failed to edit store")),
        );
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Error editing store: $e")),
      );
    } finally {
      setState(() => _isSubmitting = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text("Edit Store"),
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
          onPressed: _isSubmitting ? null : _editStore,
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
              : const Text("Done"),
        ),
      ],
    );
  }
}
