import 'dart:convert';
import 'dart:io';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart'; // For input formatters.
import 'package:image_picker/image_picker.dart';
import 'package:http/http.dart' as http;
import 'package:path/path.dart';
import 'package:async/async.dart';
import 'package:sellerapp/service/Environment.dart';
import 'package:shared_preferences/shared_preferences.dart';

class CreateListingPage extends StatefulWidget {
  @override
  _CreateListingPageState createState() => _CreateListingPageState();
}

class _CreateListingPageState extends State<CreateListingPage> {
  final _formKey = GlobalKey<FormState>();

  // Text controllers for the form fields
  final _nameController = TextEditingController();
  final _descriptionController = TextEditingController();
  final _priceController = TextEditingController();
  final _quantityController = TextEditingController();
  final _categoryIdController = TextEditingController();
  final _storeIdController = TextEditingController();
  final _brandIdController = TextEditingController();
  final _storeProductUrlController = TextEditingController();

  // Image related variables
  File? _imageFile;
  bool _isUploadingImage = false;
  String? _uploadedImageUrl;

  final ImagePicker _picker = ImagePicker();

  @override
  void initState() {
    super.initState();
    _loadStoreIdFromPrefs();
  }

  /// Loads the storeId from SharedPreferences
  Future<void> _loadStoreIdFromPrefs() async {
    final SharedPreferences prefs = await SharedPreferences.getInstance();
    String? storeId = prefs.getString('storeId');
    if (storeId != null && storeId.isNotEmpty) {
      setState(() {
        _storeIdController.text = storeId;
      });
    }
  }

  // Helper function to create a consistent input decoration.
  InputDecoration buildInputDecoration(String label) {
    return InputDecoration(
      labelText: label,
      filled: true,
      fillColor: Colors.white.withOpacity(0.7),
      border: OutlineInputBorder(
        borderRadius: BorderRadius.circular(30.0),
        borderSide: const BorderSide(
          color: Colors.black,
        ),
      ),
      enabledBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(30.0),
        borderSide: BorderSide(
          color: Colors.black.withOpacity(0.5),
        ),
      ),
      focusedBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(30.0),
        borderSide: const BorderSide(
          color: Colors.black,
        ),
      ),
      errorStyle: const TextStyle(color: Colors.red),
    );
  }

  // New method to show image source options.
  void _showImageSourceOptions() {
    showModalBottomSheet(
      context: this.context,
      builder: (context) {
        return SafeArea(
          child: Wrap(
            children: [
              ListTile(
                leading: const Icon(Icons.camera_alt),
                title: const Text("Camera"),
                onTap: () {
                  Navigator.of(context).pop();
                  _pickImage(ImageSource.camera);
                },
              ),
              ListTile(
                leading: const Icon(Icons.photo_library),
                title: const Text("Gallery"),
                onTap: () {
                  Navigator.of(context).pop();
                  _pickImage(ImageSource.gallery);
                },
              ),
            ],
          ),
        );
      },
    );
  }

  // Updated _pickImage method with imageQuality set to 50.
  Future<void> _pickImage(ImageSource source) async {
    final pickedFile = await _picker.pickImage(
      source: source,
      imageQuality: 50, // Reduce image quality by 50%
    );
    if (pickedFile != null) {
      setState(() {
        _imageFile = File(pickedFile.path);
      });
      await _uploadImageToAWS(_imageFile!);
    }
  }

  Future<void> _uploadImageToAWS(File imageFile) async {
    setState(() {
      _isUploadingImage = true;
    });
    // AWS upload endpoint URL.
    var uri = Uri.parse("${ApiConfig.baseUrl}/upload");
    // Prepare the file stream and file length.
    var stream = http.ByteStream(DelegatingStream.typed(imageFile.openRead()));
    var length = await imageFile.length();
    // Create the multipart request.
    var request = http.MultipartRequest("POST", uri);
    var multipartFile = http.MultipartFile(
      'file',
      stream,
      length,
      filename: basename(imageFile.path),
    );
    request.files.add(multipartFile);
    try {
      var response = await request.send();
      if (response.statusCode == 200) {
        var responseString = await response.stream.bytesToString();
        var jsonResponse = jsonDecode(responseString);
        if (jsonResponse['success'] == true) {
          setState(() {
            _uploadedImageUrl = jsonResponse['data'];
          });
        } else {
          if (kDebugMode) {
            print("Upload failed: ${jsonResponse['message']}");
          }
          ScaffoldMessenger.of(this.context).showSnackBar(
            const SnackBar(
              content: Text("Image upload failed, please try again"),
            ),
          );
        }
      } else {
        if (kDebugMode) {
          print("Image upload failed with status: ${response.statusCode}");
        }
        ScaffoldMessenger.of(this.context).showSnackBar(
          const SnackBar(
            content: Text("Image upload failed, please try again"),
          ),
        );
      }
    } catch (e) {
      if (kDebugMode) {
        print("Error uploading image: $e");
      }
      ScaffoldMessenger.of(this.context).showSnackBar(
        const SnackBar(
          content: Text("Image upload failed, please try again"),
        ),
      );
    } finally {
      setState(() {
        _isUploadingImage = false;
      });
    }
  }


  // Create the listing by hitting another API endpoint.
  Future<void> _createListing() async {
    if (_formKey.currentState!.validate()) {
      var listingData = {
        "name": _nameController.text,
        "description": _descriptionController.text,
        "categoryId": "1",
        "imgUrl": _uploadedImageUrl ?? ""
      };

      try {
        final SharedPreferences prefs = await SharedPreferences.getInstance();
        var jwtToken = prefs.getString('jwtToken');
        // Use the ApiService from your environment file instead of http directly.
        var response = await ApiService().callApi(
          url: ApiConfig.postProducts,
          headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer $jwtToken"
          },
          body: jsonEncode(listingData),
        );

        if (response["success"] == true) {
          var storeProductData = {
            "storeId": _storeIdController.text,
            "productId": response['data']['data']['id'],
            "brandId": "1",
            "storeProductUrl": _storeProductUrlController.text,
            "price": _priceController.text,
            "quantityInStock": _quantityController.text,
          };
          var response2 = await ApiService().callApi(
            url: ApiConfig.postStoreProducts,
            headers: {
              "Content-Type": "application/json",
              "Authorization": "Bearer $jwtToken"
            },
            body: jsonEncode(storeProductData),
          );

          if (response2["success"] == true) {
            // Show success dialog
            showDialog(
              context: this.context,
              builder: (context) {
                return AlertDialog(
                  title: const Text("Success"),
                  content: const Text("Listing created successfully"),
                  actions: [
                    TextButton(
                      onPressed: () {
                        Navigator.of(context).pop(); // Close the dialog
                      },
                      child: const Text("OK"),
                    ),
                  ],
                );
              },
            );

            // Reset all form fields
            _nameController.clear();
            _descriptionController.clear();
            _priceController.clear();
            _quantityController.clear();
            _categoryIdController.clear();
            _brandIdController.clear();
            _storeProductUrlController.clear();
            setState(() {
              _imageFile = null;
              _uploadedImageUrl = null;
            });
          } else {
            showDialog(
              context: this.context,
              builder: (context) {
                return AlertDialog(
                  title: const Text("Error"),
                  content: const Text("Failed to create listing"),
                  actions: [
                    TextButton(
                      onPressed: () {
                        Navigator.of(context).pop();
                      },
                      child: const Text("OK"),
                    ),
                  ],
                );
              },
            );
          }
        } else {
          showDialog(
            context: this.context,
            builder: (context) {
              return AlertDialog(
                title: const Text("Error"),
                content: const Text("Failed to create listing"),
                actions: [
                  TextButton(
                    onPressed: () {
                      Navigator.of(context).pop();
                    },
                    child: const Text("OK"),
                  ),
                ],
              );
            },
          );
        }
      } catch (e) {
        if (kDebugMode) {
          print("Error creating listing: $e");
        }
      }
    }
  }

  @override
  void dispose() {
    _nameController.dispose();
    _descriptionController.dispose();
    _priceController.dispose();
    _quantityController.dispose();
    _categoryIdController.dispose();
    _storeIdController.dispose();
    _brandIdController.dispose();
    _storeProductUrlController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      // No appBar property
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: Column(
            children: [
              // Image capture field
              GestureDetector(
                onTap: _showImageSourceOptions,
                child: Container(
                  height: 150,
                  width: double.infinity,
                  color: Colors.grey[300],
                  child: _imageFile != null
                      ? Image.file(_imageFile!, fit: BoxFit.cover)
                      : const Center(child: Icon(Icons.camera_alt, size: 50)),
                ),
              ),

              if (_isUploadingImage)
                const Padding(
                  padding: EdgeInsets.all(8.0),
                  child: CircularProgressIndicator(),
                ),
              const SizedBox(height: 16),
              // Name field
              TextFormField(
                controller: _nameController,
                decoration: buildInputDecoration("Name"),
                validator: (value) =>
                value == null || value.isEmpty ? "Please enter a name" : null,
              ),
              const SizedBox(height: 16),
              // Description field
              TextFormField(
                controller: _descriptionController,
                decoration: buildInputDecoration("Description"),
                validator: (value) => value == null || value.isEmpty
                    ? "Please enter a description"
                    : null,
              ),
              const SizedBox(height: 16),
              // Row for Price and Quantity
              Row(
                children: [
                  Expanded(
                    child: TextFormField(
                      controller: _priceController,
                      decoration: buildInputDecoration("Price"),
                      keyboardType: TextInputType.number,
                      inputFormatters: [FilteringTextInputFormatter.digitsOnly],
                      validator: (value) =>
                      value == null || value.isEmpty ? "Enter a price" : null,
                    ),
                  ),
                  const SizedBox(width: 16),
                  Expanded(
                    child: TextFormField(
                      controller: _quantityController,
                      decoration: buildInputDecoration("Quantity In Stock"),
                      keyboardType: TextInputType.number,
                      inputFormatters: [FilteringTextInputFormatter.digitsOnly],
                      validator: (value) =>
                      value == null || value.isEmpty ? "Enter quantity" : null,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 16),
              // Category ID field
              TextFormField(
                controller: _categoryIdController,
                decoration: buildInputDecoration("Category ID"),
                keyboardType: TextInputType.number,
                inputFormatters: [FilteringTextInputFormatter.digitsOnly],
                validator: (value) => value == null || value.isEmpty
                    ? "Please enter a category id"
                    : null,
              ),
              const SizedBox(height: 16),
              // Store ID field (populated from SharedPreferences)
              TextFormField(
                controller: _storeIdController,
                decoration: buildInputDecoration("Store ID"),
                keyboardType: TextInputType.number,
                inputFormatters: [FilteringTextInputFormatter.digitsOnly],
                validator: (value) => value == null || value.isEmpty
                    ? "Please enter a store id"
                    : null,
              ),
              const SizedBox(height: 16),
              // Brand ID field
              TextFormField(
                controller: _brandIdController,
                decoration: buildInputDecoration("Brand ID"),
                keyboardType: TextInputType.number,
                inputFormatters: [FilteringTextInputFormatter.digitsOnly],
                validator: (value) => value == null || value.isEmpty
                    ? "Please enter a brand id"
                    : null,
              ),
              const SizedBox(height: 16),
              // Product URL field
              TextFormField(
                controller: _storeProductUrlController,
                decoration: buildInputDecoration("Product URL"),
                validator: (value) => value == null || value.isEmpty
                    ? "Please enter a product url"
                    : null,
              ),
              const SizedBox(height: 24),
              // Create Listing button
              SizedBox(
                width: double.infinity,
                height: 60,
                child: ElevatedButton(
                  onPressed: _createListing,
                  child: const Text(
                    "Create Listing",
                    style: TextStyle(fontSize: 18),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
