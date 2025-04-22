import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:sellerapp/pages/HomePage.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../service/Environment.dart';
import 'AddToFlyerDialog.dart';
import 'ManageListingsPage.dart';

class EditProductPage extends StatefulWidget {
  final Map product;
  const EditProductPage({Key? key, required this.product}) : super(key: key);

  @override
  _EditProductPageState createState() => _EditProductPageState();
}

class _EditProductPageState extends State<EditProductPage> {
  final _formKey = GlobalKey<FormState>();
  late TextEditingController _nameController;
  late TextEditingController _descriptionController;
  late TextEditingController _priceController;
  late TextEditingController _quantityController;
  late TextEditingController _productUrlController;

  @override
  void initState() {
    super.initState();
    // Initialize controllers with the passed product item.
    _nameController = TextEditingController(
        text: widget.product['product']?['name'] ?? '');
    _descriptionController = TextEditingController(
        text: widget.product['product']?['description'] ?? '');
    _priceController =
        TextEditingController(text: widget.product['price'].toString());
    _quantityController =
        TextEditingController(text: widget.product['quantityInStock'].toString());
    _productUrlController =
        TextEditingController(text: widget.product['productUrl'] ?? '');
  }

  Future<void> _updateProduct() async {
    if (_formKey.currentState!.validate()) {
      var updatedProduct = {
        "name": _nameController.text,
        "description": _descriptionController.text,
        "imgUrl": widget.product['product']['imgUrl'],
        "price": int.tryParse(_priceController.text),
        "quantityInStock": int.tryParse(_quantityController.text),
        "categoryId": 1,
        "storeId": widget.product['store']['Id']
      };

      var updatedStoreProduct = {
        "quantityInStock": int.tryParse(_quantityController.text) ?? 0,
        "storeProductUrl": _productUrlController.text,
        "storeProductId": widget.product['id'],
        "storeId": widget.product['store']['id'],
        "productId": widget.product['product']['id'],
        "brandId": 1,
        "price": double.tryParse(_priceController.text),
      };

      SharedPreferences prefs = await SharedPreferences.getInstance();
      String? jwtToken = prefs.getString('jwtToken');

      var response = await ApiService().put(
        "/products/${widget.product['product']['id']}",
        headers: {
          "Content-Type": "application/json",
          "Authorization": "Bearer $jwtToken"
        },
        body: jsonEncode(updatedProduct),
      );
      var response2 = await ApiService().put(
        "/store-products/${widget.product['id']}",
        headers: {
          "Content-Type": "application/json",
          "Authorization": "Bearer $jwtToken"
        },
        body: jsonEncode(updatedStoreProduct),
      );

      if (response["success"] == true && response2["success"] == true) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("Product updated successfully")),
        );
        Navigator.pushReplacement(
          context,
          MaterialPageRoute(builder: (context) => const HomePage()),
        );
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("Failed to update product")),
        );
      }
    }
  }

  @override
  void dispose() {
    _nameController.dispose();
    _descriptionController.dispose();
    _priceController.dispose();
    _quantityController.dispose();
    _productUrlController.dispose();
    super.dispose();
  }

  InputDecoration _buildInputDecoration(String label) {
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

  void _showAddToFlyerPopup() {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (BuildContext context) {
        return AddToFlyerDialog(
          storeProductId: widget.product['id'],
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Edit Product"),
        centerTitle: true,
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: SingleChildScrollView(
            child: Column(
              children: [
                // Name field
                TextFormField(
                  controller: _nameController,
                  decoration: _buildInputDecoration("Name"),
                  validator: (value) =>
                  value == null || value.isEmpty ? "Please enter a name" : null,
                ),
                const SizedBox(height: 16),
                // Description field
                TextFormField(
                  controller: _descriptionController,
                  decoration: _buildInputDecoration("Description"),
                  validator: (value) =>
                  value == null || value.isEmpty ? "Please enter a description" : null,
                ),
                const SizedBox(height: 16),
                // Price field
                TextFormField(
                  controller: _priceController,
                  decoration: _buildInputDecoration("Price"),
                  keyboardType: TextInputType.number,
                  validator: (value) =>
                  value == null || value.isEmpty ? "Please enter a price" : null,
                ),
                const SizedBox(height: 16),
                // Quantity In Stock field
                TextFormField(
                  controller: _quantityController,
                  decoration: _buildInputDecoration("Quantity In Stock"),
                  keyboardType: TextInputType.number,
                  validator: (value) =>
                  value == null || value.isEmpty ? "Please enter quantity" : null,
                ),
                const SizedBox(height: 16),
                // Product URL field
                TextFormField(
                  controller: _productUrlController,
                  decoration: _buildInputDecoration("Product URL"),
                  validator: (value) =>
                  value == null || value.isEmpty ? "Please enter product URL" : null,
                ),
                const SizedBox(height: 24),
                // Update product button
                SizedBox(
                  width: double.infinity,
                  height: 60,
                  child: ElevatedButton(
                    onPressed: _updateProduct,
                    child: const Text(
                      "Update Product",
                      style: TextStyle(fontSize: 18),
                    ),
                  ),
                ),
                const SizedBox(height: 16),
                // Yellow button to add product to flyers
                SizedBox(
                  width: double.infinity,
                  height: 60,
                  child: ElevatedButton(
                    style: ElevatedButton.styleFrom(
                      foregroundColor: Colors.white, backgroundColor: Colors.yellow, // black text
                    ),
                    onPressed: _showAddToFlyerPopup,
                    child: const Text(
                      "Add to Flyers",
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
}
