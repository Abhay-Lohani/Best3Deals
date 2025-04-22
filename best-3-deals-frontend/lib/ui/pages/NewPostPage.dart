import 'dart:convert';
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../../service/Environment.dart'; // Contains ApiService and related configuration.
import '../../service/LocationManager.dart'; // (Optional) For fetching location if needed.

/// This page allows the user to create a new post by providing a title,
/// description, optional tags, and an image. The user can also select a store
/// from a dropdown list. Once the form is complete, the post is sent to the backend.
class NewPostPage extends StatefulWidget {
  const NewPostPage({Key? key}) : super(key: key);

  @override
  _NewPostPageState createState() => _NewPostPageState();
}

class _NewPostPageState extends State<NewPostPage> {
  // Controllers for text input fields.
  final TextEditingController _titleController = TextEditingController();
  final TextEditingController _descriptionController = TextEditingController();
  final TextEditingController _tagsController = TextEditingController(); // Optional field for tags.

  // Image file selected by the user.
  File? _selectedImage;
  // URL returned from the backend after uploading the image.
  String _uploadedImageUrl = "";
  // Flag to check if image upload was successful.
  bool _imageUploadSuccess = false;

  // List of available stores to choose from.
  List<dynamic> _stores = [];
  // Currently selected store object (or its ID).
  dynamic _selectedStore;

  // General loading indicator for asynchronous operations.
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    // Fetch the list of stores when the page loads.
    _fetchStores();
  }

  /// Fetches a list of stores from the API based on the user's location.
  /// The endpoint uses latitude, longitude, and a preset radius.
  Future<void> _fetchStores() async {
    setState(() => _isLoading = true);

    final SharedPreferences prefs = await SharedPreferences.getInstance();
    String? jwtToken = prefs.getString('jwtToken');
    double latitude = prefs.getDouble('latitude') ?? 0.0;
    double longitude = prefs.getDouble('longitude') ?? 0.0;
    int radius = 100000; // Default search radius

    final response = await ApiService().get(
      "/stores/all/$latitude/$longitude/$radius",
      headers: {"Authorization": "Bearer $jwtToken"},
    );

    setState(() => _isLoading = false);

    if (response["success"] == true) {
      setState(() {
        _stores = response["data"];
      });
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(response["error"] ?? "Failed to load stores")),
      );
    }
  }

  /// Displays a bottom sheet with options for picking an image either from the gallery or via the camera.
  Future<void> _showImagePickerOptions() async {
    showModalBottomSheet(
      context: context,
      builder: (context) => SafeArea(
        child: Wrap(
          children: [
            ListTile(
              leading: const Icon(Icons.photo_library),
              title: const Text('Pick from Gallery'),
              onTap: () {
                Navigator.of(context).pop();
                _pickImage(ImageSource.gallery);
              },
            ),
            ListTile(
              leading: const Icon(Icons.camera_alt),
              title: const Text('Capture from Camera'),
              onTap: () {
                Navigator.of(context).pop();
                _pickImage(ImageSource.camera);
              },
            ),
          ],
        ),
      ),
    );
  }

  /// Picks an image from the specified [source] (gallery or camera) using the ImagePicker.
  /// Once the image is selected, it immediately calls the upload function.
  Future<void> _pickImage(ImageSource source) async {
    final picker = ImagePicker();
    final pickedFile = await picker.pickImage(source: source, imageQuality: 70);
    if (pickedFile != null) {
      setState(() {
        _selectedImage = File(pickedFile.path);
      });
      // Automatically upload the image after picking.
      await _uploadImage(_selectedImage!);
    }
  }

  /// Uploads the selected image file to the backend.
  /// If the upload is successful, it stores the returned image URL.
  Future<void> _uploadImage(File imageFile) async {
    setState(() => _isLoading = true);
    final uploadResponse = await ApiService().uploadFile(imageFile);
    setState(() => _isLoading = false);

    if (uploadResponse["success"] == true) {
      // The response data is assumed to contain the URL of the uploaded image.
      setState(() {
        _uploadedImageUrl = uploadResponse["data"] ?? "";
        _imageUploadSuccess = true;
      });
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(uploadResponse["error"] ?? "Image upload failed"),
        ),
      );
    }
  }

  /// Sends the new post data to the backend to create a new post.
  /// It validates the title field, constructs the request body, and handles the response.
  Future<void> _createPost() async {
    final title = _titleController.text.trim();
    final description = _descriptionController.text.trim();

    // Validate that the title is not empty.
    if (title.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Title cannot be empty")),
      );
      return;
    }

    setState(() => _isLoading = true);
    final SharedPreferences prefs = await SharedPreferences.getInstance();
    String? jwtToken = prefs.getString('jwtToken');

    // Construct the request body with necessary fields.
    final requestBody = {
      "title": title,
      "content": description,
      "storeId": _selectedStore?["id"] ?? 0, // Default to 0 if no store is selected.
      "originalPrice": 1111110,
      "discountedPrice": 11110,
      "endDate": "2029-03-29T05:32:23.821Z",
      "imgUrl": _uploadedImageUrl,
      // Additional fields such as tags or flair can be added here.
    };

    final response = await ApiService().callApi(
      url: "/posts",
      headers: {
        "Content-Type": "application/json",
        "Authorization": "Bearer $jwtToken",
      },
      body: jsonEncode(requestBody),
    );

    setState(() => _isLoading = false);

    if (response["success"] == true) {
      // Notify the user that the post was created and return to the previous page.
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Post created successfully!")),
      );
      Navigator.pop(context, true); // Return true to indicate refresh.
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(response["error"] ?? "Failed to create post")),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Create Post"),centerTitle: true,
        actions: [
          // "Post" button to trigger the creation of the new post.
          TextButton(
            onPressed: _createPost,
            child: const Text("Post", style: TextStyle(color: Colors.white)),
          ),
        ],
      ),
      // The body uses a Stack to overlay a loading indicator over the main content.
      body: Stack(
        children: [
          _buildContent(),
          if (_isLoading)
            Container(
              color: Colors.black54,
              child: const Center(child: CircularProgressIndicator()),
            ),
        ],
      ),
    );
  }

  /// Builds the main content of the New Post page, including the form fields for title,
  /// description, tags, store selection, and image picking.
  Widget _buildContent() {
    return Container(
      padding: const EdgeInsets.all(16.0),
      // Use the theme's background color for a consistent look.
      color: Theme.of(context).scaffoldBackgroundColor,
      child: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Display the uploaded image if available.
            if (_imageUploadSuccess && _uploadedImageUrl.isNotEmpty)
              Center(
                child: Image.network(
                  _uploadedImageUrl,
                  height: 200,
                  fit: BoxFit.cover,
                ),
              ),
            const SizedBox(height: 16),
            // Store selection widget.
            GestureDetector(
              onTap: _stores.isNotEmpty ? _showStorePicker : null,
              child: Container(
                padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 16),
                decoration: BoxDecoration(
                  color: Theme.of(context).cardColor.withOpacity(0.2),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Row(
                  children: [
                    // Display selected store name or a placeholder.
                    Text(
                      _selectedStore == null
                          ? "Select a store"
                          : _selectedStore["name"] ?? "Unnamed Store",
                      style: const TextStyle(fontSize: 16),
                    ),
                    const Spacer(),
                    const Icon(Icons.arrow_drop_down),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 16),
            // Title input field.
            TextField(
              controller: _titleController,
              style: const TextStyle(fontSize: 16),
              decoration: const InputDecoration(
                labelText: "Title",
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 16),
            // Optional tags or flair input field.
            TextField(
              controller: _tagsController,
              style: const TextStyle(fontSize: 16),
              decoration: const InputDecoration(
                labelText: "Add tags & flair (optional)",
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 16),
            // Description (body text) input field.
            TextField(
              controller: _descriptionController,
              style: const TextStyle(fontSize: 14),
              maxLines: 5,
              decoration: const InputDecoration(
                labelText: "body text (optional)",
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 16),
            // Button to add or capture an image.
            Center(
              child: ElevatedButton.icon(
                onPressed: _showImagePickerOptions,
                icon: const Icon(Icons.add_a_photo),
                label: const Text("Add / Capture Image"),
              ),
            ),
            const SizedBox(height: 16),
          ],
        ),
      ),
    );
  }

  /// Displays a bottom sheet with a list of stores for the user to select.
  void _showStorePicker() {
    showModalBottomSheet(
      context: context,
      builder: (context) => ListView.builder(
        itemCount: _stores.length,
        itemBuilder: (context, index) {
          final store = _stores[index];
          return ListTile(
            title: Text(store["name"] ?? "Unnamed Store"),
            onTap: () {
              setState(() {
                _selectedStore = store;
              });
              Navigator.pop(context);
            },
          );
        },
      ),
    );
  }
}
