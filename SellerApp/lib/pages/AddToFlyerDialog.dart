import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../service/Environment.dart';

class AddToFlyerDialog extends StatefulWidget {
  final int storeProductId;
  const AddToFlyerDialog({Key? key, required this.storeProductId}) : super(key: key);

  @override
  _AddToFlyerDialogState createState() => _AddToFlyerDialogState();
}

class _AddToFlyerDialogState extends State<AddToFlyerDialog> {
  final _formKey = GlobalKey<FormState>();
  final TextEditingController _flyerIdController = TextEditingController();
  final TextEditingController _originalPriceController = TextEditingController();
  final TextEditingController _discountedPriceController = TextEditingController();

  bool _isSubmitting = false;

  Future<void> _addProductToFlyers() async {
    if (!_formKey.currentState!.validate()) return;
    setState(() {
      _isSubmitting = true;
    });
    try {
      final SharedPreferences prefs = await SharedPreferences.getInstance();
      String? jwtToken = prefs.getString('jwtToken');

      var requestBody = {
        "flyerId": int.tryParse(_flyerIdController.text.trim()) ?? 0,
        "storeProductId": widget.storeProductId,
        "originalPrice": int.tryParse(_originalPriceController.text.trim()) ?? 0,
        "discountedPrice": int.tryParse(_discountedPriceController.text.trim()) ?? 0
      };

      var response = await ApiService().callApi(
        url: "/flyers/product",
        headers: {
          "Content-Type": "application/json",
          "Authorization": "Bearer $jwtToken"
        },
        body: jsonEncode(requestBody),
      );

      if (response["success"] == true) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("Product added to flyers successfully")),
        );
        Navigator.of(context).pop();
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(response["message"] ?? "Failed to add product to flyers")),
        );
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Error: $e")),
      );
    } finally {
      setState(() {
        _isSubmitting = false;
      });
    }
  }

  @override
  void dispose() {
    _flyerIdController.dispose();
    _originalPriceController.dispose();
    _discountedPriceController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text("Add Product to Flyers"),
      content: SingleChildScrollView(
        child: Form(
          key: _formKey,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              // Flyer ID field
              TextFormField(
                controller: _flyerIdController,
                decoration: const InputDecoration(labelText: "Flyer ID"),
                keyboardType: TextInputType.number,
                validator: (val) => (val == null || val.isEmpty) ? "Enter Flyer ID" : null,
              ),
              // Original Price field
              TextFormField(
                controller: _originalPriceController,
                decoration: const InputDecoration(labelText: "Original Price"),
                keyboardType: TextInputType.number,
                validator: (val) => (val == null || val.isEmpty) ? "Enter Original Price" : null,
              ),
              // Discounted Price field
              TextFormField(
                controller: _discountedPriceController,
                decoration: const InputDecoration(labelText: "Discounted Price"),
                keyboardType: TextInputType.number,
                validator: (val) => (val == null || val.isEmpty) ? "Enter Discounted Price" : null,
              ),
              // Uneditable Store Product ID field
              TextFormField(
                initialValue: widget.storeProductId.toString(),
                decoration: const InputDecoration(labelText: "Store Product ID"),
                validator: (val) => (val == null || val.isEmpty) ? "Enter Store Product ID" : null,
              ),
            ],
          ),
        ),
      ),
      actions: [
        TextButton(
          onPressed: _isSubmitting ? null : () => Navigator.of(context).pop(),
          child: const Text("Cancel"),
        ),
        ElevatedButton(
          onPressed: _isSubmitting ? null : _addProductToFlyers,
          child: _isSubmitting
              ? const SizedBox(
            width: 16,
            height: 16,
            child: CircularProgressIndicator(
              strokeWidth: 2,
              valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
            ),
          )
              : const Text("Add"),
        ),
      ],
    );
  }
}
