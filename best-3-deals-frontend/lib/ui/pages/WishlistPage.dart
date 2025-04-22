import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';
import '../../db/DatabaseHelper.dart';

/// Displays the user's wishlist items stored in the local database.
/// Provides an option to remove items and view details.
class WishlistPage extends StatefulWidget {
  const WishlistPage({Key? key}) : super(key: key);

  @override
  _WishlistPageState createState() => _WishlistPageState();
}

class _WishlistPageState extends State<WishlistPage> {
  // List of wishlist items fetched from the database.
  List<Map<String, dynamic>> wishlistItems = [];

  @override
  void initState() {
    super.initState();
    _loadWishlist(); // Load wishlist items when the widget initializes.
  }

  /// Loads wishlist items from the local SQLite database.
  Future<void> _loadWishlist() async {
    final db = await DatabaseHelper().database; // Get database instance.
    final List<Map<String, dynamic>> items = await db.query('wishlist'); // Fetch wishlist items.
    setState(() {
      wishlistItems = items; // Update UI with retrieved wishlist items.
    });
  }

  /// Removes an item from the wishlist and refreshes the list.
  Future<void> _removeItem(int id) async {
    await DatabaseHelper().deleteWishlistItem(id); // Delete item from database.
    _loadWishlist(); // Reload wishlist to reflect changes.
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text("Removed from wishlist")),
    );
  }

  /// Displays a modal bottom sheet with product details for a wishlist item.
  /// The popup mimics the style of the recently viewed popup and includes
  /// options to remove the item from the wishlist or go to the product URL.
  Future<void> _showProductDetails(Map<String, dynamic> item) async {
    await showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
      ),
      builder: (BuildContext context) {
        return Padding(
          padding: MediaQuery.of(context).viewInsets,
          child: SingleChildScrollView(
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Center(
                    child: (item['product_imgUrl'] != null &&
                        item['product_imgUrl'].toString().isNotEmpty)
                        ? Image.network(item['product_imgUrl'], height: 150)
                        : Image.asset('assets/default_avatar.jpg', height: 150),
                  ),
                  const SizedBox(height: 16),
                  Text(
                    item['product_name'] ?? '',
                    style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    "Price: \$${item['price']}",
                    style: const TextStyle(fontSize: 18, color: Colors.green),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    "Store: ${item['store_name'] ?? ''}",
                    style: const TextStyle(fontSize: 16),
                  ),
                  const SizedBox(height: 16),
                  Text(
                    item['product_description'] ?? '',
                    style: const TextStyle(fontSize: 16),
                  ),
                  const SizedBox(height: 24),
                  Row(
                    children: [
                      Expanded(
                        child: ElevatedButton(
                          onPressed: () async {
                            await _removeItem(item['id']);
                            Navigator.pop(context);
                          },
                          child: const Text("Remove from Wishlist"),
                        ),
                      ),
                      const SizedBox(width: 16),
                      Expanded(
                        child: ElevatedButton(
                          onPressed: () async {
                            final Uri url = Uri.parse(item['productUrl'] ?? '');
                            if (await canLaunchUrl(url)) {
                              await launchUrl(url);
                            }
                          },
                          child: const Text("Go to Product"),
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ),
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Wishlist'),
        centerTitle: true,
      ),
      body: wishlistItems.isEmpty
          ? const Center(child: Text('No items in wishlist'))
          : ListView.builder(
        itemCount: wishlistItems.length,
        itemBuilder: (context, index) {
          final item = wishlistItems[index];
          return Card(
            margin: const EdgeInsets.all(10),
            child: ListTile(
              // Display product image or a default placeholder.
              leading: (item['product_imgUrl'] != null &&
                  item['product_imgUrl'].toString().isNotEmpty)
                  ? Image.network(
                item['product_imgUrl'],
                width: 50,
                height: 50,
                fit: BoxFit.cover,
              )
                  : Image.asset(
                'assets/default_avatar.jpg',
                width: 50,
                height: 50,
                fit: BoxFit.cover,
              ),
              title: Text(item['product_name'] ?? ''),
              subtitle: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text("Price: \$${item['price']}"),
                  Text("Store: ${item['store_name'] ?? ''}"),
                ],
              ),
              trailing: IconButton(
                icon: const Icon(Icons.delete, color: Colors.red),
                onPressed: () => _removeItem(item['id']),
              ),
              onTap: () => _showProductDetails(item),
            ),
          );
        },
      ),
    );
  }
}
