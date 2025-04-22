import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../service/Environment.dart'; // Make sure ApiService and ApiConfig are imported from your service layer

// Page that lists all flyers
class FlyerProductPage extends StatefulWidget {
  const FlyerProductPage({Key? key}) : super(key: key);

  @override
  _FlyerProductPageState createState() => _FlyerProductPageState();
}

class _FlyerProductPageState extends State<FlyerProductPage> {
  List<dynamic> _flyers = [];
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _fetchFlyers();
  }

  Future<void> _fetchFlyers() async {
    setState(() {
      _isLoading = true;
    });
    try {
      final SharedPreferences prefs = await SharedPreferences.getInstance();
      String? jwtToken = prefs.getString('jwtToken');

      // GET /flyers
      final response = await ApiService().get(
        ApiConfig.getFlyers,
        headers: {"Authorization": "Bearer $jwtToken"},
      );

      if (response["success"] == true) {
        setState(() {
          _flyers = response["data"];
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
      ScaffoldMessenger.of(context)
          .showSnackBar(SnackBar(content: Text("Error fetching flyers: $e")));
    }
  }

  Widget _buildFlyerTile(Map<String, dynamic> flyer) {
    final String name = flyer["name"] ?? "No Name";
    final int id = flyer["id"] ?? 0;
    final int storeId = flyer["storeId"] ?? 0;
    final String imageUrl = flyer["imageUrl"] ?? "";

    return ListTile(
      leading: imageUrl.isNotEmpty &&
          ApiService.isValidImageUrl(imageUrl)
          ? Image.network(
        imageUrl,
        width: 50,
        height: 50,
        fit: BoxFit.cover,
      )
          : Image.asset(
        'assets/placeholder.jpg',
        width: 50,
        height: 50,
        fit: BoxFit.cover,
      ),
      title: Text(name),
      subtitle: Text("Flyer ID: $id, Store: $storeId"),
      onTap: () {
        // Navigate to details page for this flyer
        Navigator.push(
          context,
          MaterialPageRoute(builder: (context) => FlyerDetailsPage(flyerId: id)),
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Flyers"),
        centerTitle: true,
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : ListView.builder(
        itemCount: _flyers.length,
        itemBuilder: (context, index) {
          return _buildFlyerTile(_flyers[index]);
        },
      ),
    );
  }
}

// Page that displays details for a single flyer along with its products.
class FlyerDetailsPage extends StatefulWidget {
  final int flyerId;
  const FlyerDetailsPage({Key? key, required this.flyerId}) : super(key: key);

  @override
  _FlyerDetailsPageState createState() => _FlyerDetailsPageState();
}

class _FlyerDetailsPageState extends State<FlyerDetailsPage> {
  Map<String, dynamic>? _flyerDetails;
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _fetchFlyerDetails();
  }

  Future<void> _fetchFlyerDetails() async {
    setState(() {
      _isLoading = true;
    });
    try {
      final SharedPreferences prefs = await SharedPreferences.getInstance();
      String? jwtToken = prefs.getString('jwtToken');

      // GET /flyers/{id} endpoint (adjust the URL format per your API)
      final response = await ApiService().get(
        "${ApiConfig.getFlyers}/${widget.flyerId}",
        headers: {"Authorization": "Bearer $jwtToken"},
      );

      if (response["success"] == true) {
        setState(() {
          _flyerDetails = response["data"];
          _isLoading = false;
        });
      } else {
        setState(() {
          _isLoading = false;
        });
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
              content:
              Text(response["message"] ?? "Failed to load flyer details")),
        );
      }
    } catch (e) {
      setState(() {
        _isLoading = false;
      });
      ScaffoldMessenger.of(context)
          .showSnackBar(SnackBar(content: Text("Error: $e")));
    }
  }

  Future<void> _deleteFlyerProduct(int flyerProductId) async {
    try {
      final SharedPreferences prefs = await SharedPreferences.getInstance();
      String? jwtToken = prefs.getString('jwtToken');

      // DELETE /flyers/product/{storeProductId}
      final response = await ApiService().delete(
        "${ApiConfig.deleteFlyerProduct}/$flyerProductId",
        headers: {"Authorization": "Bearer $jwtToken"},
      );

      if (response["success"] == true) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("Product deleted successfully")),
        );
        // Refresh the flyer details to update the products list
        _fetchFlyerDetails();
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
              content:
              Text(response["message"] ?? "Failed to delete flyer product")),
        );
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Error deleting product: $e")),
      );
    }
  }

  Widget _buildProductTile(Map<String, dynamic> product) {
    final int id = product["id"] ?? 0;
    final int flyerId = product["flyerId"] ?? 0;
    final int storeProductId = product["storeProductId"] ?? 0;
    final num originalPrice = product["originalPrice"] ?? 0;
    final num discountedPrice = product["discountedPrice"] ?? 0;

    return ListTile(
      title: Text("Product ID: $id"),
      subtitle: Text(
          "Flyer: $flyerId, Store Product: $storeProductId\nOriginal: $originalPrice, Discounted: $discountedPrice"),
      trailing: IconButton(
        icon: const Icon(Icons.delete, color: Colors.red),
        onPressed: () {
          _deleteFlyerProduct(id);
        },
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Flyer Products"),
        centerTitle: true,

      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _flyerDetails == null
          ? const Center(child: Text("No details found."))
          : SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [

            _flyerDetails!["products"] != null &&
                (_flyerDetails!["products"] as List).isNotEmpty
                ? ListView.builder(
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              itemCount:
              (_flyerDetails!["products"] as List).length,
              itemBuilder: (context, index) {
                return _buildProductTile(
                    (_flyerDetails!["products"] as List)[index]);
              },
            )
                : const Padding(
              padding: EdgeInsets.all(8.0),
              child: Text("No products available."),
            ),
          ],
        ),
      ),
    );
  }
}
