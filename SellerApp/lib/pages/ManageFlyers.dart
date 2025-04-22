import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../service/Environment.dart';
import 'AddFlyerDialog.dart';   // The create flyer dialog
import 'EditFlyerDialog.dart'; // The edit flyer dialog

class ManageFlyersPage extends StatefulWidget {
  const ManageFlyersPage({Key? key}) : super(key: key);

  @override
  _ManageFlyersPageState createState() => _ManageFlyersPageState();
}

class _ManageFlyersPageState extends State<ManageFlyersPage> {
  List<dynamic> _flyers = [];
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _fetchFlyers();
  }

  /// Fetch flyers from GET /flyers
  Future<void> _fetchFlyers() async {
    setState(() {
      _isLoading = true;
    });
    try {
      final SharedPreferences prefs = await SharedPreferences.getInstance();
      String? jwtToken = prefs.getString('jwtToken');

      final response = await ApiService().get(
        ApiConfig.getFlyers,
        headers: {
          "Authorization": "Bearer $jwtToken",
        },
      );

      if (response["success"] == true) {
        setState(() {
          _flyers = response["data"]; // array of flyers
          _isLoading = false;
        });
      } else {
        setState(() {
          _isLoading = false;
        });
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(response["message"] ?? "Failed to load flyers")),
        );
      }
    } catch (e) {
      setState(() {
        _isLoading = false;
      });
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Error fetching flyers: $e")),
      );
    }
  }

  /// Show the dialog to create a new flyer
  Future<void> _showAddFlyerDialog() async {
    bool? flyerAdded = await showDialog<bool>(
      context: context,
      barrierDismissible: false,
      builder: (BuildContext context) {
        return const AddFlyerDialog();
      },
    );

    // If a flyer was added, refresh
    if (flyerAdded == true) {
      _fetchFlyers();
    }
  }

  /// Show the dialog to edit an existing flyer
  Future<void> _showEditFlyerDialog(Map<String, dynamic> flyer) async {
    bool? flyerEdited = await showDialog<bool>(
      context: context,
      barrierDismissible: false,
      builder: (BuildContext context) {
        return EditFlyerDialog(flyer: flyer);
      },
    );

    // If a flyer was edited, refresh
    if (flyerEdited == true) {
      _fetchFlyers();
    }
  }

  /// Delete a flyer by ID using DELETE /flyers?id={flyerId}
  Future<void> _deleteFlyer(int flyerId) async {
    try {
      final SharedPreferences prefs = await SharedPreferences.getInstance();
      String? jwtToken = prefs.getString('jwtToken');

      final response = await ApiService().delete(
        "${ApiConfig.deleteFlyers}/$flyerId",
        headers: {
          "Authorization": "Bearer $jwtToken",
        },
      );

      if (response["success"] == true) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("Flyer deleted successfully")),
        );
        // Refresh the list
        _fetchFlyers();
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(response["message"] ?? "Failed to delete flyer")),
        );
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Error deleting flyer: $e")),
      );
    }
  }

  /// Build each flyer tile
  Widget _buildFlyerTile(Map<String, dynamic> flyer) {
    final String name = flyer["name"] ?? "No Name";
    final int id = flyer["id"] ?? 0;
    final int storeId = flyer["storeId"] ?? 0;
    final String imageUrl = flyer["imageUrl"] ?? "";

    return ListTile(
      leading: Builder(
        builder: (context) {
          if (ApiService.isValidImageUrl(imageUrl)) {
            return Image.network(
              imageUrl,
              width: 50,
              height: 50,
              fit: BoxFit.cover,
            );
          } else {
            return Image.asset(
              'assets/placeholder.jpg', // Replace with your placeholder
              width: 50,
              height: 50,
              fit: BoxFit.cover,
            );
          }
        },
      ),
      title: Text(name),
      subtitle: Text("ID: $id, StoreID: $storeId"),
      // Tapping the tile opens edit dialog
      onTap: () => _showEditFlyerDialog(flyer),
      // Delete button on the trailing side
      trailing: IconButton(
        icon: const Icon(Icons.delete, color: Colors.red),
        onPressed: () => _deleteFlyer(id),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Manage Flyers"),
        centerTitle: true,
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : Column(
        children: [
          Expanded(
            child: ListView.builder(
              itemCount: _flyers.length,
              itemBuilder: (context, index) {
                return _buildFlyerTile(_flyers[index]);
              },
            ),
          ),
          Container(
            width: double.infinity,
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            child: ElevatedButton(
              onPressed: _showAddFlyerDialog,
              child: const Text("Create Flyer"),
            ),
          ),
        ],
      ),
    );
  }
}
