import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../service/Environment.dart';
import 'AddStoreDialog.dart';
import 'EditStoreDialog.dart';

class ManageStoresPage extends StatefulWidget {
  const ManageStoresPage({Key? key}) : super(key: key);

  @override
  _ManageStoresPageState createState() => _ManageStoresPageState();
}

class _ManageStoresPageState extends State<ManageStoresPage> {
  List<dynamic> _stores = [];
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _fetchStores();
  }

  /// Fetches all stores from GET /stores
  Future<void> _fetchStores() async {
    setState(() {
      _isLoading = true;
    });
    try {
      final SharedPreferences prefs = await SharedPreferences.getInstance();
      String? jwtToken = prefs.getString('jwtToken');

      final response = await ApiService().get(
        "/stores",
        headers: {
          "Authorization": "Bearer $jwtToken",
        },
      );

      if (response["success"] == true) {
        setState(() {
          _stores = response["data"];
          _isLoading = false;
        });
      } else {
        setState(() {
          _isLoading = false;
        });
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(response["message"] ?? "Failed to load stores")),
        );
      }
    } catch (e) {
      setState(() {
        _isLoading = false;
      });
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Error fetching stores: $e")),
      );
    }
  }

  /// Shows the dialog to add a new store
  Future<void> _showAddStoreDialog() async {
    bool? storeAdded = await showDialog<bool>(
      context: context,
      barrierDismissible: false,
      builder: (BuildContext context) {
        return const AddStoreDialog();
      },
    );
    if (storeAdded == true) {
      _fetchStores();
    }
  }

  /// Shows the dialog to edit an existing store
  Future<void> _showEditStoreDialog(Map<String, dynamic> store) async {
    bool? storeEdited = await showDialog<bool>(
      context: context,
      barrierDismissible: false,
      builder: (BuildContext context) {
        return EditStoreDialog(store: store);
      },
    );
    if (storeEdited == true) {
      _fetchStores();
    }
  }

  /// Deletes a store by calling DELETE /stores?id={storeId}
  Future<void> _deleteStore(int storeId) async {
    try {
      final SharedPreferences prefs = await SharedPreferences.getInstance();
      String? jwtToken = prefs.getString('jwtToken');

      final response = await ApiService().delete(
        "/stores/$storeId",
        headers: {
          "Authorization": "Bearer $jwtToken",
        },
      );

      if (response["success"] == true) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("Store deleted successfully")),
        );
        _fetchStores();
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(response["message"] ?? "Failed to delete store")),
        );
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Error deleting store: $e")),
      );
    }
  }

  /// Builds a ListTile for each store.
  Widget _buildStoreTile(Map<String, dynamic> store) {
    final String name = store["name"] ?? "No Name";
    final int id = store["id"] ?? 0;
    final String imgUrl = store["imgUrl"] ?? "";
    return ListTile(
      leading: Builder(
        builder: (context) {
          if (ApiService.isValidImageUrl(imgUrl)) {
            return Image.network(
              imgUrl,
              width: 50,
              height: 50,
              fit: BoxFit.cover,
            );
          } else {
            return Image.asset(
              'assets/placeholder.jpg',
              width: 50,
              height: 50,
              fit: BoxFit.cover,
            );
          }
        },
      ),
      title: Text(name),
      subtitle: Text("ID: $id"),
      onTap: () => _showEditStoreDialog(store),
      trailing: IconButton(
        icon: const Icon(Icons.delete, color: Colors.red),
        onPressed: () => _deleteStore(id),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Manage Stores"),
        centerTitle: true,
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : Column(
        children: [
          Expanded(
            child: ListView.builder(
              itemCount: _stores.length,
              itemBuilder: (context, index) {
                return _buildStoreTile(_stores[index]);
              },
            ),
          ),
          Container(
            width: double.infinity,
            padding:
            const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            child: ElevatedButton(
              onPressed: _showAddStoreDialog,
              child: const Text("Create Store"),
            ),
          ),
        ],
      ),
    );
  }
}
