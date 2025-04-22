import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'dart:io';
import '../../service/Environment.dart';

/// Displays and allows editing of the user's profile information.
/// Users can update their first name, last name, phone number, and profile picture.
class ProfilePage extends StatefulWidget {
  const ProfilePage({Key? key}) : super(key: key);

  @override
  _ProfilePageState createState() => _ProfilePageState();
}

class _ProfilePageState extends State<ProfilePage> {
  // Controllers for profile input fields.
  final TextEditingController _firstNameController = TextEditingController();
  final TextEditingController _lastNameController  = TextEditingController();
  final TextEditingController _emailController     = TextEditingController();
  final TextEditingController _phoneController     = TextEditingController();

  bool _isLoading = false;
  bool isEditing = false;
  // Holds a locally selected image before it's uploaded.
  File? _localImageFile;
  // URL of the profile image.
  String _imageUrl = "";

  @override
  void initState() {
    super.initState();
    _fetchUserProfile();
  }

  /// Builds a consistent style for text fields.
  InputDecoration _buildInputDecoration(String label) {
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

  /// Fetches the user's profile information from the API.
  Future<void> _fetchUserProfile() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String? jwtToken = prefs.getString('jwtToken');
    setState(() {
      _isLoading = true;
    });

    final response = await ApiService().get(
      ApiConfig.getUser,
      headers: {
        "Authorization": "Bearer $jwtToken",
      },
    );

    setState(() {
      _isLoading = false;
    });

    if (response != null) {
      // Populate fields with the user information.
      _firstNameController.text = response["firstName"] ?? "";
      _lastNameController.text  = response["lastName"] ?? "";
      _emailController.text     = response["email"] ?? "";
      _phoneController.text     = response["phoneNumber"] ?? "";
      _imageUrl = response["address"] ?? "";
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(response["error"] ?? "Failed to fetch profile")),
      );
    }
  }

  /// Updates the user's profile on the backend.
  Future<void> _updateUserProfile() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String? jwtToken = prefs.getString('jwtToken');
    setState(() {
      _isLoading = true;
    });

    // Use a default phone number if the phone field is empty.
    String phoneNumber = _phoneController.text.trim().isEmpty ? "9999999999" : _phoneController.text.trim();

    String updateBody = jsonEncode({
      "firstName": _firstNameController.text.trim(),
      "lastName": _lastNameController.text.trim(),
      "email": _emailController.text.trim(),
      "phoneNumber": phoneNumber,
      // Store the image URL as the address.
      "address": _imageUrl,
    });

    final response = await ApiService().put(
      ApiConfig.putUser,
      headers: {
        "Content-Type": "application/json",
        "Authorization": "Bearer $jwtToken",
      },
      body: updateBody,
    );

    setState(() {
      _isLoading = false;
    });

    if (response != null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Profile updated successfully")),
      );
      setState(() {
        isEditing = false;
      });
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(response["error"] ?? "Failed to update profile")),
      );
    }
  }

  /// Displays a bottom sheet to let the user choose an image source (gallery or camera).
  Future<void> _showImageSourceActionSheet() async {
    showModalBottomSheet(
      context: context,
      builder: (context) => SafeArea(
        child: Wrap(
          children: [
            ListTile(
              leading: const Icon(Icons.photo_library),
              title: const Text('Gallery'),
              onTap: () {
                Navigator.of(context).pop();
                _pickAndUploadImage(ImageSource.gallery);
              },
            ),
            ListTile(
              leading: const Icon(Icons.camera_alt),
              title: const Text('Camera'),
              onTap: () {
                Navigator.of(context).pop();
                _pickAndUploadImage(ImageSource.camera);
              },
            ),
          ],
        ),
      ),
    );
  }

  /// Picks an image from the specified [source], uploads it, and updates the profile.
  Future<void> _pickAndUploadImage(ImageSource source) async {
    final pickedFile = await ImagePicker().pickImage(source: source, imageQuality: 50);
    if (pickedFile != null) {
      File imageFile = File(pickedFile.path);
      // Update the local preview with the selected image.
      setState(() {
        _localImageFile = imageFile;
      });
      // Show a loading indicator while uploading.
      setState(() {
        _isLoading = true;
      });
      final uploadResponse = await ApiService().uploadFile(imageFile);
      setState(() {
        _isLoading = false;
      });
      if (uploadResponse != null && uploadResponse["success"] == true) {
        setState(() {
          _imageUrl = uploadResponse["data"] ?? "";
        });
        // Update the profile with the new image URL.
        _updateUserProfile();
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(uploadResponse["error"] ?? "Image upload failed")),
        );
      }
    }
  }

  /// Toggles edit mode. If editing is finished, it updates the profile.
  void _toggleEdit() {
    if (isEditing) {
      _updateUserProfile();
    } else {
      setState(() {
        isEditing = true;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    // Determine which profile image to display.
    ImageProvider profileImage;
    if (_imageUrl.isNotEmpty) {
      profileImage = NetworkImage(_imageUrl);
    } else if (_localImageFile != null) {
      profileImage = FileImage(_localImageFile!);
    } else {
      profileImage = const AssetImage("assets/default_avatar.jpg");
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text("Profile"),
        centerTitle: true,
        actions: [
          IconButton(
            icon: Icon(isEditing ? Icons.check : Icons.edit),
            onPressed: _toggleEdit,
          ),
        ],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            GestureDetector(
              onTap: _showImageSourceActionSheet,
              child: CircleAvatar(
                radius: 50,
                backgroundImage: profileImage,
              ),
            ),
            const SizedBox(height: 20),
            TextField(
              controller: _firstNameController,
              enabled: isEditing,
              decoration: _buildInputDecoration("First Name"),
            ),
            const SizedBox(height: 20),
            TextField(
              controller: _lastNameController,
              enabled: isEditing,
              decoration: _buildInputDecoration("Last Name"),
            ),
            const SizedBox(height: 20),
            TextField(
              controller: _emailController,
              enabled: false,
              decoration: _buildInputDecoration("Email"),
            ),
            const SizedBox(height: 20),
            TextField(
              controller: _phoneController,
              enabled: isEditing,
              decoration: _buildInputDecoration("Phone Number"),
            ),
          ],
        ),
      ),
    );
  }
}
