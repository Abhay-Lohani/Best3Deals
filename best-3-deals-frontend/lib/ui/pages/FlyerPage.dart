import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../../service/Environment.dart';
import 'package:url_launcher/url_launcher.dart';
import '../../db/DatabaseHelper.dart';

/// FlyerPage displays a grid of products from a specific flyer.
/// It loads flyer details, fetches corresponding store product information,
/// and allows users to view more details or add/remove items from the wishlist.
class FlyerPage extends StatefulWidget {
  final int flyerId;
  const FlyerPage({Key? key, required this.flyerId}) : super(key: key);

  @override
  _FlyerPageState createState() => _FlyerPageState();
}

class _FlyerPageState extends State<FlyerPage> {
  // Flag to indicate if data is being loaded.
  bool isLoading = true;

  // List holding combined data for each flyer product and its corresponding store product details.
  List<Map<String, dynamic>> combinedProducts = [];

  @override
  void initState() {
    super.initState();
    // Load the flyer details as soon as the page is initialized.
    loadFlyerDetails();
  }

  /// Loads flyer details from the API using the flyerId.
  /// For each product in the flyer, it makes a separate API call to get the store product details.
  Future<void> loadFlyerDetails() async {
    // Get the stored JWT token from shared preferences.
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String? jwtToken = prefs.getString('jwtToken');

    // Make a GET request to fetch the flyer details.
    final flyerResponse = await ApiService().get(
      "/flyers/${widget.flyerId}",
      headers: {
        "Authorization": "Bearer $jwtToken",
      },
    );

    if (flyerResponse["success"] == true) {
      final flyerData = flyerResponse["data"];
      // Extract the list of products from the flyer data.
      final List<dynamic> flyerProducts = flyerData["products"];
      List<Map<String, dynamic>> tempList = [];
      // For each product in the flyer, fetch the corresponding store product details.
      for (var fp in flyerProducts) {
        int storeProductId = fp["storeProductId"];
        // Get JWT token again for each request (or reuse the one obtained earlier).
        SharedPreferences prefs = await SharedPreferences.getInstance();
        String? jwtToken = prefs.getString('jwtToken');

        final storeProductResponse = await ApiService().get(
          "/store-products/$storeProductId",
          headers: {
            "Authorization": "Bearer $jwtToken",
          },
        );

        if (storeProductResponse["success"] == true) {
          // Add the combined data for this product to the temporary list.
          tempList.add({
            "flyerProduct": fp,
            "storeProduct": storeProductResponse["data"],
          });
        }
      }
      // Update the state with the loaded products and mark loading as complete.
      setState(() {
        combinedProducts = tempList;
        isLoading = false;
      });
    } else {
      // If there was an error fetching the flyer, stop the loader and show an error message.
      setState(() {
        isLoading = false;
      });
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(flyerResponse["error"] ?? "Failed to load flyer details"),
        ),
      );
    }
  }

  /// Displays a dialog with detailed information for a selected product,
  /// along with options to add/remove it from the wishlist or open the product URL.
  void _showProductDetailsPopup(Map<String, dynamic> item) async {
    final flyerProduct = item["flyerProduct"];
    final storeProduct = item["storeProduct"];

    // Retrieve product and store details needed for the dialog.
    String productImgUrl = storeProduct["product"]["imgUrl"] ?? "";
    String productDescription = storeProduct["product"]["description"] ?? "";
    String storeName = storeProduct["store"]["name"] ?? "";
    String storeImgUrl = storeProduct["store"]["imgUrl"] ?? "";
    String productUrl = storeProduct["productUrl"] ?? "";

    // Check if the product is already in the wishlist.
    bool isInWishlist = await DatabaseHelper().isProductInWishlist(
        storeProduct["product"]["id"].toString());

    // Show a dialog with product details.
    showDialog(
      context: context,
      builder: (context) {
        // Local state for tracking wishlist status inside the dialog.
        bool wishlistState = isInWishlist;
        return StatefulBuilder(
          builder: (context, setStateDialog) {
            return AlertDialog(
              content: SingleChildScrollView(
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    // Display the product image or a default image if none available.
                    productImgUrl.isNotEmpty
                        ? Image.network(productImgUrl, height: 150)
                        : Image.asset('assets/default_avatar.jpg', height: 150),
                    const SizedBox(height: 10),
                    // Show the product description.
                    Text(
                      productDescription,
                      textAlign: TextAlign.center,
                    ),
                    const SizedBox(height: 10),
                    // Display the store's image and name.
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        storeImgUrl.isNotEmpty
                            ? CircleAvatar(
                          backgroundImage: NetworkImage(storeImgUrl),
                        )
                            : const CircleAvatar(
                          backgroundImage: AssetImage('assets/default_avatar.jpg'),
                        ),
                        const SizedBox(width: 8),
                        Text(storeName),
                      ],
                    ),
                  ],
                ),
              ),
              actions: [
                // Button to add or remove the product from the wishlist.
                TextButton(
                  onPressed: () async {
                    if (wishlistState) {
                      // Remove from wishlist if already added.
                      await DatabaseHelper().deleteWishlistItem(
                          storeProduct["product"]["id"]);
                      setStateDialog(() {
                        wishlistState = false;
                      });
                      ScaffoldMessenger.of(context).showSnackBar(
                        const SnackBar(content: Text("Removed from wishlist")),
                      );
                    } else {
                      // Add to wishlist if not present.
                      await DatabaseHelper().insertWishlistItem(storeProduct);
                      setStateDialog(() {
                        wishlistState = true;
                      });
                      ScaffoldMessenger.of(context).showSnackBar(
                        const SnackBar(content: Text("Added to wishlist")),
                      );
                    }
                  },
                  child: Text(wishlistState
                      ? "Remove from wishlist"
                      : "Add to wishlist"),
                ),
                // Button to open the product URL in a browser.
                TextButton(
                  onPressed: () async {
                    final Uri url = Uri.parse(productUrl);
                    if (await canLaunchUrl(url)) {
                      await launchUrl(url);
                    }
                  },
                  child: const Text("Go to product"),
                ),
              ],
            );
          },
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Flyer Products"),
        centerTitle: true,
      ),
      body: isLoading
      // Show a loading indicator while data is being fetched.
          ? const Center(child: CircularProgressIndicator())
      // If no products were loaded, display a message.
          : combinedProducts.isEmpty
          ? const Center(child: Text("No products available"))
      // Otherwise, display the products in a grid view.
          : GridView.builder(
        padding: const EdgeInsets.all(8),
        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 2,
          childAspectRatio: 0.75,
          crossAxisSpacing: 8,
          mainAxisSpacing: 8,
        ),
        itemCount: combinedProducts.length,
        itemBuilder: (context, index) {
          final item = combinedProducts[index];
          final flyerProduct = item["flyerProduct"];
          final storeProduct = item["storeProduct"];

          // Extract image and pricing details.
          String productImgUrl = storeProduct["product"]["imgUrl"] ?? "";
          String storeName = storeProduct["store"]["name"] ?? "";
          String storeImgUrl = storeProduct["store"]["imgUrl"] ?? "";
          String originalPrice = flyerProduct["originalPrice"].toString();
          String discountedPrice = flyerProduct["discountedPrice"].toString();

          return GestureDetector(
            // When the card is tapped, show the product details popup.
            onTap: () => _showProductDetailsPopup(item),
            child: Card(
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(8),
              ),
              elevation: 5,
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // Display the product image at the top of the card.
                  Expanded(
                    child: productImgUrl.isNotEmpty
                        ? Image.network(
                      productImgUrl,
                      fit: BoxFit.cover,
                      width: double.infinity,
                    )
                        : Image.asset(
                      'assets/default_avatar.jpg',
                      fit: BoxFit.cover,
                      width: double.infinity,
                    ),
                  ),
                  Padding(
                    padding: const EdgeInsets.all(8.0),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        // Show the original price with a strikethrough.
                        Text(
                          "Original: \$${originalPrice}",
                          style: const TextStyle(
                              decoration: TextDecoration.lineThrough),
                        ),
                        // Display the discounted price in bold.
                        Text(
                          "Discount: \$${discountedPrice}",
                          style: const TextStyle(
                              fontWeight: FontWeight.bold),
                        ),
                        const SizedBox(height: 4),
                        // Show the store's image and name.
                        Row(
                          children: [
                            storeImgUrl.isNotEmpty
                                ? CircleAvatar(
                              backgroundImage: NetworkImage(storeImgUrl),
                              radius: 12,
                            )
                                : const CircleAvatar(
                              backgroundImage:
                              AssetImage('assets/default_avatar.jpg'),
                              radius: 12,
                            ),
                            const SizedBox(width: 6),
                            Expanded(
                              child: Text(
                                storeName,
                                style: const TextStyle(fontSize: 12),
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
          );
        },
      ),
    );
  }
}
