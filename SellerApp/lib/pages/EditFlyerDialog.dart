import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../service/Environment.dart';
import 'dart:convert';

class EditFlyerDialog extends StatefulWidget {
  final Map<String, dynamic> flyer;

  const EditFlyerDialog({Key? key, required this.flyer}) : super(key: key);

  @override
  _EditFlyerDialogState createState() => _EditFlyerDialogState();
}

class _EditFlyerDialogState extends State<EditFlyerDialog> {
  final _formKey = GlobalKey<FormState>();

  final TextEditingController _nameController = TextEditingController();
  final TextEditingController _descriptionController = TextEditingController();
  final TextEditingController _imageUrlController = TextEditingController();
  final TextEditingController _storeIdController = TextEditingController();

  bool _isSubmitting = false;
  late int flyerId; // The ID of the flyer to edit

  @override
  void initState() {
    super.initState();
    // Populate controllers with the existing flyer data
    flyerId = widget.flyer["id"] ?? 0;
    _nameController.text = widget.flyer["name"] ?? "";
    _descriptionController.text = widget.flyer["description"] ?? "";
    _imageUrlController.text = widget.flyer["imageUrl"] ?? "";
    _storeIdController.text = widget.flyer["storeId"]?.toString() ?? "0";
  }

  /// Calls PUT /flyers?id={flyerId}
  Future<void> _editFlyer() async {
    if (!_formKey.currentState!.validate()) return;
    setState(() => _isSubmitting = true);

    try {
      final SharedPreferences prefs = await SharedPreferences.getInstance();
      final String? jwtToken = prefs.getString('jwtToken');

      final flyerData = {
        "name": _nameController.text.trim(),
        "description": _descriptionController.text.trim(),
        "imageUrl": _imageUrlController.text.trim(),
        "storeId": int.tryParse(_storeIdController.text.trim()) ?? 0
      };

      // PUT /flyers?id={flyerId}
      final response = await ApiService().put(
        "${ApiConfig.putFlyers}/$flyerId",
        headers: {
          "Content-Type": "application/json",
          "Authorization": "Bearer $jwtToken",
        },
        body: jsonEncode(flyerData),
      );

      if (response["success"] == true) {
        Navigator.of(context).pop(true); // Return true => refresh
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(response["message"] ?? "Failed to edit flyer")),
        );
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Error editing flyer: $e")),
      );
    } finally {
      setState(() => _isSubmitting = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text("Edit Flyer"),
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
                controller: _descriptionController,
                decoration: const InputDecoration(labelText: "Description"),
                validator: (val) =>
                (val == null || val.isEmpty) ? "Enter a description" : null,
              ),
              TextFormField(
                controller: _imageUrlController,
                decoration: const InputDecoration(labelText: "Image URL"),
                validator: (val) =>
                (val == null || val.isEmpty) ? "Enter an image URL" : null,
              ),
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
          onPressed: _isSubmitting ? null : _editFlyer,
          child: _isSubmitting
              ? const SizedBox(
            width: 16,
            height: 16,
            child: CircularProgressIndicator(
              strokeWidth: 2,
              valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
            ),
          )
              : const Text("Done"),
        ),
      ],
    );
  }
}
