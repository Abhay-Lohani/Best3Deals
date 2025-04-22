import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';
import '../../db/DatabaseHelper.dart';
import '../../service/Environment.dart';
import 'package:url_launcher/url_launcher.dart';

class ProductListPage extends StatefulWidget {
  final String searchQuery;
  const ProductListPage({Key? key, required this.searchQuery}) : super(key: key);

  @override
  _ProductListPageState createState() => _ProductListPageState();
}

class _ProductListPageState extends State<ProductListPage> {
  List<dynamic> products = [];
  bool isLoading = true;
  Set<int> favoriteProductIds = {}; // Track favourited product IDs locally

  @override
  void initState() {
    super.initState();
    fetchProducts();
  }

  Future<void> fetchProducts() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    final int? searchRadius = prefs.getInt('search_radius') ?? 10000000;
    final double? latitude = prefs.getDouble('latitude') ?? 0.0;
    final double? longitude = prefs.getDouble('longitude') ?? 0.0;
    String jwtToken = prefs.getString('jwtToken') ?? '';

    final uri = Uri.parse('${ApiConfig.baseUrl}/store-products/nearby/best3deals').replace(
      queryParameters: {
        'organicApples': widget.searchQuery,
        'latitude': latitude.toString(),
        'longitude': longitude.toString(),
        'distanceInKM': searchRadius.toString(),
      },
    );

    final response = await http.get(
      uri,
      headers: {
        "Authorization": "Bearer $jwtToken",
      },
    );

    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      setState(() {
        products = data;
        isLoading = false;
      });
    } else {
      setState(() {
        isLoading = false;
      });
      throw Exception('Failed to load products');
    }
  }

  Future<void> addToWishlist(dynamic productData) async {
    try {
      await DatabaseHelper().insertWishlistItem(productData);
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Added to wishlist")),
      );
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Failed to add to wishlist: $e")),
      );
    }
  }

  Future<void> _addToRecentlyViewed(dynamic productData) async {
    // Insert or replace the product in recently_viewed
    await DatabaseHelper().insertRecentlyViewedItem(productData);
  }

  void _showProductDetails(dynamic productData) {
    final product = productData['product'];
    final store = productData['store'];

    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      shape: const RoundedRectangleBorder(
          borderRadius: BorderRadius.vertical(top: Radius.circular(16))),
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
                    child: product['imgUrl'] != null &&
                        product['imgUrl'].toString().isNotEmpty
                        ? Image.network(product['imgUrl'], height: 150)
                        : Image.asset('assets/default_avatar.jpg', height: 150),
                  ),
                  const SizedBox(height: 16),
                  Text(
                    product['name'] ?? '',
                    style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    "Price: \$${productData['price']}",
                    style: const TextStyle(fontSize: 18, color: Colors.green),
                  ),
                  const SizedBox(height: 8),
                  Row(
                    children: [
                      store['imgUrl'] != null && store['imgUrl'].toString().isNotEmpty
                          ? CircleAvatar(
                        backgroundImage: NetworkImage(store['imgUrl']),
                        radius: 20,
                      )
                          : const CircleAvatar(
                        backgroundImage: AssetImage('assets/default_avatar.jpg'),
                        radius: 20,
                      ),
                      const SizedBox(width: 8),
                      Text(
                        store['name'] ?? '',
                        style: const TextStyle(fontSize: 16),
                      ),
                    ],
                  ),
                  const SizedBox(height: 16),
                  Text(
                    product['description'] ?? '',
                    style: const TextStyle(fontSize: 16),
                  ),
                  const SizedBox(height: 24),
                  Row(
                    children: [
                      Expanded(
                        child: ElevatedButton(
                          onPressed: () async {
                            await addToWishlist(productData);
                            setState(() {
                              favoriteProductIds.add(productData['id']);
                            });
                            Navigator.of(context).pop();
                          },
                          child: const Text("Add to Wishlist"),
                        ),
                      ),
                      const SizedBox(width: 16),
                      Expanded(
                        child: ElevatedButton(
                          onPressed: () async {
                            final Uri url = Uri.parse(productData['productUrl'] ?? '');
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
          title: const Text('Search Results'),
          centerTitle: true,
        ),
        body: isLoading
            ? const Center(child: CircularProgressIndicator())
            : products.isEmpty
            ? const Center(child: Text("No products found"))
            : ListView.builder(
          itemCount: products.length,
          itemBuilder: (context, index) {
            final productData = products[index];
            final product = productData['product'];
            final store = productData['store'];
            final int productId = productData['id'];

            return Card(
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(10),
              ),
              margin: const EdgeInsets.all(10),
              child: InkWell(
                onTap: () async {
                  // 1. Mark as recently viewed
                  await _addToRecentlyViewed(productData);

                  // 2. Show product details
                  _showProductDetails(productData);
                },
                child: Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: Row(
                    crossAxisAlignment: CrossAxisAlignment.start, // Align items at the top
                    children: [
                      // Product image
                      Container(
                        width: 80,
                        height: 80,
                        clipBehavior: Clip.hardEdge,
                        decoration: BoxDecoration(
                          borderRadius: BorderRadius.circular(8),
                        ),
                        child: Image.network(
                          product['imgUrl'] ?? '',
                          fit: BoxFit.cover,
                        ),
                      ),
                      const SizedBox(width: 10),

                      // Main column: product name, price, store details
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            // Product Name
                            Text(
                              product['name'] ?? '',
                              style: const TextStyle(
                                fontSize: 16,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                            const SizedBox(height: 5),

                            // Price
                            Text(
                              "Price: \$${productData['price']}",
                              style: const TextStyle(fontSize: 14),
                            ),
                            const SizedBox(height: 5),

                            // Store details row
                            Row(
                              children: [
                                Container(
                                  width: 30,
                                  height: 30,
                                  decoration: BoxDecoration(
                                    shape: BoxShape.circle,
                                    image: DecorationImage(
                                      image: (store['imgUrl'] != null &&
                                          store['imgUrl'].toString().isNotEmpty)
                                          ? NetworkImage(store['imgUrl'])
                                          : const AssetImage('assets/default_avatar.jpg')
                                      as ImageProvider,
                                      fit: BoxFit.cover,
                                    ),
                                  ),
                                ),
                                const SizedBox(width: 5),
                                Expanded(
                                  child: Text(
                                    store['name'] ?? '',
                                    style: const TextStyle(fontSize: 14),
                                    overflow: TextOverflow.ellipsis, // Avoid overflow
                                  ),
                                ),
                              ],
                            ),
                          ],
                        ),
                      ),

                      // Favorite button
                      IconButton(
                        icon: Icon(
                          favoriteProductIds.contains(productId)
                              ? Icons.favorite
                              : Icons.favorite_border,
                          color: Colors.red,
                        ),
                        onPressed: () async {
                          if (!favoriteProductIds.contains(productId)) {
                            await addToWishlist(productData);
                            setState(() {
                              favoriteProductIds.add(productId);
                            });
                          } else {
                            ScaffoldMessenger.of(context).showSnackBar(
                              const SnackBar(content: Text("Already in wishlist")),
                            );
                          }
                        },
                      ),
                    ],
                  ),
                ),
              ),
            );
          },
        )

    );
  }
}
