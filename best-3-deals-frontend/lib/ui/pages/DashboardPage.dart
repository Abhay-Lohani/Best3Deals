import 'dart:convert';
import 'package:carousel_slider/carousel_slider.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:url_launcher/url_launcher.dart';
import '../../service/Environment.dart';
import 'PostsPage.dart';
import 'ProfilePage.dart';
import '../../db/DatabaseHelper.dart';
import '../../service/LocationManager.dart';
import 'ProductListPage.dart';
import 'FlyerPage.dart';
import 'SearchRadiusPage.dart';
import 'SignInPage.dart';
import 'StoreFlyers.dart';
import 'WishlistPage.dart';
import '../../main.dart';
import 'SettingsPage.dart';
import 'package:shared_preferences/shared_preferences.dart';

/// Main dashboard page for the Best 3 Deals app.
/// Displays nearby stores, promotional images, and recently viewed products.
/// Also provides navigation via a drawer and bottom navigation bar.
class DashboardPage extends StatefulWidget {
  const DashboardPage({super.key});

  @override
  _DashboardPageState createState() => _DashboardPageState();
}

class _DashboardPageState extends State<DashboardPage> with RouteAware {
  // List to hold store data fetched from the API.
  List<dynamic> storeList = [];

  // List of promotional image paths for the carousel.
  final List<String> promoImages = [
    'assets/promos/image1.png',
    'assets/promos/image2.jpg',
    'assets/promos/image3.png',
  ];

  // List to hold recently viewed products fetched from the local database.
  List<Map<String, dynamic>> _recentlyViewed = [];

  // Holds the user's postal code (fetched via location services).
  String postalCodeLocation = "";

  // Notification setting flag.
  bool notificationsEnabled = true;

  @override
  void initState() {
    super.initState();
    // Load location, store list, recently viewed items, and notification settings on startup.
    loadlocation();
    loadStoreList();
    _loadRecentlyViewed();
    _loadNotificationSetting();
  }

  // Subscribe to the route observer so the dashboard refreshes when the user returns.
  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    routeObserver.subscribe(this, ModalRoute.of(context)! as PageRoute);
  }

  // Unsubscribe from the route observer when disposing the widget.
  @override
  void dispose() {
    routeObserver.unsubscribe(this);
    super.dispose();
  }

  // Called when the user navigates back to this page.
  @override
  void didPopNext() {
    super.didPopNext();
    // Reload recently viewed items and update the wishlist badge.
    _loadRecentlyViewed();
    setState(() {});
  }

  /// Fetches the saved location and postal code, then updates the state.
  Future<void> loadlocation() async {
    var latlong = await LocationManager().getLocation();
    var postalCode = await LocationManager().getPostalCode();
    setState(() {
      postalCodeLocation = postalCode!['postalCode']!;
    });
  }

  /// Fetches a list of nearby stores from the API based on the saved latitude, longitude, and a fixed radius.
  Future<void> loadStoreList() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    double latitude = prefs.getDouble('latitude') ?? 0.0;
    double longitude = prefs.getDouble('longitude') ?? 0.0;
    int radius = 10000000; // Large radius to include many stores

    // Construct the endpoint URL with the current coordinates.
    final endpoint = "/stores/all/$latitude/$longitude/$radius";
    String? jwtToken = prefs.getString('jwtToken');
    final response = await ApiService().get(
      endpoint,
      headers: {
        "Authorization": "Bearer $jwtToken",
      },
    );

    // Check if the API call was successful and update the store list.
    if (response["success"] == true) {
      setState(() {
        storeList = response["data"];
      });
    } else {
      // If there's an error, clear the store list and show a message.
      setState(() {
        storeList = [];
      });
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(response["error"] ?? "Failed to load stores")),
      );
    }
  }

  /// Loads the notification settings from SharedPreferences.
  Future<void> _loadNotificationSetting() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    setState(() {
      notificationsEnabled = prefs.getBool('notificationsEnabled') ?? true;
    });
  }

  /// Toggles the notifications setting and saves the new value.
  Future<void> _toggleNotifications(bool value) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    await prefs.setBool('notificationsEnabled', value);
    setState(() {
      notificationsEnabled = value;
    });
  }

  /// Loads recently viewed products from the local database.
  Future<void> _loadRecentlyViewed() async {
    final items = await DatabaseHelper().getRecentlyViewedItems();
    setState(() {
      _recentlyViewed = items;
    });
  }

  /// Removes a product from the recently viewed list by its id and reloads the list.
  Future<void> _removeFromRecentlyViewed(int id) async {
    await DatabaseHelper().deleteRecentlyViewedItem(id);
    _loadRecentlyViewed();
  }

  /// Logs out the user, updates the database, navigates to the SignIn page, and shows a confirmation.
  Future<void> _logout() async {
    await DatabaseHelper().logoutUser();
    Navigator.pushReplacement(
      context,
      MaterialPageRoute(builder: (context) => const SignInPage()),
    );
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text("Logged out")),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      // Navigation drawer with profile, posts, settings, notifications, and logout.
      drawer: Drawer(
        child: ListView(
          padding: EdgeInsets.zero,
          children: [
            // Drawer header with a blue background.
            const DrawerHeader(
              decoration: BoxDecoration(
                color: Colors.blue,
              ),
              margin: EdgeInsets.zero,
              padding: EdgeInsets.all(16.0),
              child: Align(
                alignment: Alignment.centerLeft,
                child: null,
              ),
            ),
            ListTile(
              leading: const Icon(Icons.person),
              title: const Text('Profile'),
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => const ProfilePage()),
                );
              },
            ),
            ListTile(
              leading: const Icon(Icons.forum),
              title: const Text('Posts'),
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => const PostsPage()),
                );
              },
            ),
            ListTile(
              leading: const Icon(Icons.settings),
              title: const Text('Preferences'),
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => const SettingsPage()),
                );
              },
            ),
            ListTile(
              leading: const Icon(Icons.notifications),
              title: const Text('Notifications'),
              trailing: Switch(
                value: notificationsEnabled,
                onChanged: _toggleNotifications,
              ),
            ),
            ListTile(
              leading: const Icon(Icons.logout),
              title: const Text('Log Out'),
              onTap: _logout,
            ),
          ],
        ),
      ),
      appBar: AppBar(
        title: const Text('Best 3 Deals'),
        centerTitle: true,
        actions: [
          // Displays wishlist count as a badge on the favorite icon.
          FutureBuilder<int>(
            future: DatabaseHelper().getWishlistCount(),
            builder: (context, snapshot) {
              int count = snapshot.data ?? 0;
              return Stack(
                clipBehavior: Clip.none,
                children: [
                  IconButton(
                    icon: const Icon(Icons.favorite),
                    onPressed: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(builder: (context) => const WishlistPage()),
                      ).then((_) {
                        // Trigger a rebuild when returning from the wishlist.
                        setState(() {});
                      });
                    },
                  ),
                  // Show a red badge with the count if there are any items.
                  if (count > 0)
                    Positioned(
                      right: 4,
                      top: 4,
                      child: Container(
                        padding: const EdgeInsets.all(2),
                        decoration: BoxDecoration(
                          color: Colors.red,
                          borderRadius: BorderRadius.circular(10),
                        ),
                        constraints: const BoxConstraints(
                          minWidth: 16,
                          minHeight: 16,
                        ),
                        child: Text(
                          '$count',
                          style: const TextStyle(
                            color: Colors.white,
                            fontSize: 10,
                          ),
                          textAlign: TextAlign.center,
                        ),
                      ),
                    ),
                ],
              );
            },
          ),
        ],
      ),
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16.0),
          child: SingleChildScrollView(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const SizedBox(height: 16.0),
                // Search bar for product lookup.
                const SearchBar(),
                const SizedBox(height: 5.0),
                // Tapable text to navigate to the search radius page.
                InkWell(
                  onTap: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(builder: (context) => const SearchRadiusPage()),
                    );
                  },
                  child: Text(
                    "Get best deals for $postalCodeLocation",
                    style: TextStyle(
                      color: Theme.of(context).primaryColor,
                      decoration: TextDecoration.underline,
                    ),
                  ),
                ),
                const SizedBox(height: 16.0),
                // Section header for nearby stores.
                const SectionHeader(title: 'Nearby Stores'),
                const SizedBox(height: 8.0),
                // Display stores in a horizontal list.
                StoreList(storeList: storeList),
                const SizedBox(height: 16.0),
                // Promotional carousel slider.
                CarouselSlider(
                  options: CarouselOptions(
                    aspectRatio: 4 / 3,
                    autoPlay: true,
                    enlargeCenterPage: false,
                    autoPlayInterval: const Duration(seconds: 5),
                  ),
                  items: promoImages.map((imagePath) {
                    return Builder(
                      builder: (BuildContext context) {
                        return ClipRRect(
                          borderRadius: BorderRadius.circular(10.0),
                          child: Image.asset(
                            imagePath,
                            fit: BoxFit.cover,
                          ),
                        );
                      },
                    );
                  }).toList(),
                ),
                const SizedBox(height: 16.0),
                // Section header for recently viewed products.
                const SectionHeader(title: "Recently Viewed Products"),
                const SizedBox(height: 8.0),
                // Display recently viewed products in a horizontal scroll list.
                _buildRecentlyViewedList(),
              ],
            ),
          ),
        ),
      ),
      // Bottom navigation bar for Posts, Wishlist, and Profile.
      bottomNavigationBar: BottomNavigationBar(
        items: const [
          BottomNavigationBarItem(
            icon: Icon(Icons.forum),
            label: 'Posts',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.favorite),
            label: 'Wishlist',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.person),
            label: 'Profile',
          ),
        ],
        // Navigate to the appropriate page when an item is tapped.
        onTap: (index) {
          if (index == 0) {
            Navigator.push(
              context,
              MaterialPageRoute(builder: (context) => const PostsPage()),
            );
          } else if (index == 1) {
            Navigator.push(
              context,
              MaterialPageRoute(builder: (context) => const WishlistPage()),
            );
          } else if (index == 2) {
            Navigator.push(
              context,
              MaterialPageRoute(builder: (context) => const ProfilePage()),
            );
          }
        },
      ),
    );
  }

  /// Builds a horizontal list view of recently viewed products.
  Widget _buildRecentlyViewedList() {
    return FutureBuilder<List<Map<String, dynamic>>>(
      future: DatabaseHelper().getRecentlyViewedItems(),
      builder: (context, snapshot) {
        if (!snapshot.hasData) {
          return const Center(child: CircularProgressIndicator());
        }
        final items = snapshot.data!;
        if (items.isEmpty) {
          return const Center(child: Text("No recently viewed products"));
        }
        return Container(
          height: 220,
          child: ListView.builder(
            scrollDirection: Axis.horizontal,
            itemCount: items.length,
            itemBuilder: (context, index) {
              final item = items[index];
              final productName = item['product_name'] ?? '';
              final productPrice =
              item['price'] != null ? "\$${item['price']}" : '';
              final storeName = item['store_name'] ?? '';
              final storeImgUrl = item['store_imgUrl'] ?? '';
              final productImgUrl = item['product_imgUrl'] ?? '';
              return Container(
                width: 160,
                margin: const EdgeInsets.symmetric(horizontal: 8.0),
                child: Stack(
                  children: [
                    GestureDetector(
                      onTap: () async {
                        // Show the detailed product popup when the card is tapped.
                        await _showRecentlyViewedProductDetails(item);
                        // Rebuild widget to reflect any updates.
                        setState(() {});
                      },
                      child: Card(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            // Product image.
                            Container(
                              height: 100,
                              width: double.infinity,
                              child: (productImgUrl.isNotEmpty)
                                  ? Image.network(productImgUrl, fit: BoxFit.cover)
                                  : Image.asset('assets/default_avatar.jpg',
                                  fit: BoxFit.cover),
                            ),
                            Padding(
                              padding: const EdgeInsets.all(8.0),
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  // Product name.
                                  Text(
                                    productName,
                                    style: const TextStyle(
                                        fontSize: 16,
                                        fontWeight: FontWeight.bold),
                                    maxLines: 1,
                                    overflow: TextOverflow.ellipsis,
                                  ),
                                  const SizedBox(height: 4),
                                  // Product price.
                                  Text(
                                    productPrice,
                                    style: const TextStyle(fontSize: 14),
                                  ),
                                  const SizedBox(height: 8),
                                  // Store info: image and name.
                                  Row(
                                    children: [
                                      storeImgUrl.isNotEmpty
                                          ? CircleAvatar(
                                        radius: 12,
                                        backgroundImage:
                                        NetworkImage(storeImgUrl),
                                      )
                                          : const CircleAvatar(
                                        radius: 12,
                                        backgroundImage: AssetImage(
                                            'assets/default_avatar.jpg'),
                                      ),
                                      const SizedBox(width: 6),
                                      Expanded(
                                        child: Text(
                                          storeName,
                                          style: const TextStyle(fontSize: 14),
                                          overflow: TextOverflow.ellipsis,
                                        ),
                                      ),
                                    ],
                                  ),
                                ],
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),
                    // Remove icon positioned at the top-right corner.
                    Positioned(
                      top: 4,
                      right: 4,
                      child: IconButton(
                        icon: const Icon(
                          Icons.close,
                          color: Colors.red,
                          size: 18,
                        ),
                        onPressed: () async {
                          await DatabaseHelper().deleteRecentlyViewedItem(item['id']);
                          setState(() {}); // Rebuild to update the list.
                          ScaffoldMessenger.of(context).showSnackBar(
                            const SnackBar(content: Text("Removed from recently viewed")),
                          );
                        },
                      ),
                    ),
                  ],
                ),
              );
            },
          ),
        );
      },
    );
  }



// Add this new method to show the product details popup:
  Future<void> _showRecentlyViewedProductDetails(Map<String, dynamic> item) async {
    await showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.vertical(top: Radius.circular(16))),
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
                            // Convert the flat 'recently_viewed' item to the structure needed for wishlist insertion.
                            await DatabaseHelper().insertWishlistItem({
                              'id': item['id'],
                              'product': {
                                'id': item['product_id'],
                                'name': item['product_name'],
                                'description': item['product_description'],
                                'imgUrl': item['product_imgUrl'],
                              },
                              'store': {
                                'id': item['store_id'],
                                'name': item['store_name'],
                                'imgUrl': item['store_imgUrl'],
                              },
                              'price': item['price'],
                              'previousPrice': item['previous_price'],
                              'quantityInStock': item['quantity_in_stock'],
                              'productUrl': item['productUrl'] ?? '',
                              'dateAdded': item['dateAdded'] ?? '',
                              'dateModified': item['dateModified'] ?? '',
                            });
                            ScaffoldMessenger.of(context).showSnackBar(
                                const SnackBar(content: Text("Added to wishlist")));
                          },
                          child: const Text("Add to Wishlist"),
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
}

/// A search bar widget that lets users enter a query and navigate to the ProductListPage.
class SearchBar extends StatefulWidget {
  const SearchBar({super.key});

  @override
  _SearchBarState createState() => _SearchBarState();
}

class _SearchBarState extends State<SearchBar> {
  final TextEditingController _searchController = TextEditingController();

  /// When the user submits a search query, navigate to the ProductListPage.
  void _performSearch() {
    String searchQuery = _searchController.text;
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => ProductListPage(searchQuery: searchQuery),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Expanded(
          child: TextField(
            controller: _searchController,
            decoration: InputDecoration(
              hintText: 'Search',
              hintStyle: const TextStyle(color: Colors.black54),
              prefixIcon: const Icon(Icons.search, color: Colors.grey),
              border: OutlineInputBorder(
                borderRadius: BorderRadius.circular(30.0),
              ),
              filled: true,
              fillColor: Colors.white.withOpacity(0.6),
            ),
            style: const TextStyle(color: Colors.black),
          ),
        ),
        Container(
          decoration: const BoxDecoration(
            color: Colors.blue,
            shape: BoxShape.circle,
          ),
          child: IconButton(
            icon: const Icon(Icons.search, color: Colors.white),
            onPressed: _performSearch,
          ),
        ),
      ],
    );
  }
}

/// A simple header widget to label different sections on the Dashboard.
class SectionHeader extends StatelessWidget {
  final String title;

  const SectionHeader({super.key, required this.title});

  @override
  Widget build(BuildContext context) {
    return Text(
      title,
      style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
    );
  }
}

/// Widget for displaying a horizontally scrollable list of stores.
class StoreList extends StatelessWidget {
  final List<dynamic> storeList;

  const StoreList({super.key, required this.storeList});

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 100.0,
      child: ListView.builder(
        scrollDirection: Axis.horizontal,
        itemCount: storeList.length,
        itemBuilder: (context, index) {
          return Padding(
            padding: const EdgeInsets.symmetric(horizontal: 8.0),
            child: GestureDetector(
              onTap: () {
                // When a store is tapped, navigate to the StoreFlyers page with the store ID.
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => StoreFlyers(storeId: storeList[index]['id']),
                  ),
                );
              },
              child: Column(
                children: [
                  // Display the store logo inside a circular avatar.
                  CircleAvatar(
                    radius: 30,
                    backgroundImage: NetworkImage(storeList[index]['imgUrl']),
                  ),
                  const SizedBox(height: 4.0),
                  // Show the store name below the avatar.
                  SizedBox(
                    width: 60,
                    child: Text(
                      storeList[index]['name'],
                      style: const TextStyle(fontSize: 12),
                      textAlign: TextAlign.center,
                      overflow: TextOverflow.ellipsis,
                      maxLines: 1,
                    ),
                  ),
                ],
              ),
            ),
          );
        },
      ),
    );
  }
}
