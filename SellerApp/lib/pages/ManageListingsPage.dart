import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../service/Environment.dart';
import 'EditListingsPage.dart';

class ManageListingsPage extends StatefulWidget {
  const ManageListingsPage({Key? key}) : super(key: key);

  @override
  _ManageListingsPageState createState() => _ManageListingsPageState();
}

class _ManageListingsPageState extends State<ManageListingsPage> {
  List<dynamic> _products = [];
  List<dynamic> _allProducts = [];
  bool _isLoading = true;
  final TextEditingController _searchController = TextEditingController();

  @override
  void initState() {
    super.initState();
    _fetchProducts();
    _searchController.addListener(() {
      _filterProducts(_searchController.text);
    });
  }

  void _filterProducts(String query) {
    if (query.isEmpty) {
      setState(() {
        _products = _allProducts;
      });
    } else {
      setState(() {
        _products = _allProducts.where((item) {
          String productName =
              item["product"]?["name"]?.toString().toLowerCase() ?? '';
          return productName.contains(query.toLowerCase());
        }).toList();
      });
    }
  }

  Future<void> _fetchProducts() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String? jwtToken = prefs.getString('jwtToken');
    try {
      var response = await ApiService().get(
        ApiConfig.getStoreProducts,
        headers: {
          "Authorization": "Bearer $jwtToken",
        },
      );
      if (response["success"] == true) {
        setState(() {
          _allProducts = response["data"];
          _products = _allProducts;
          _isLoading = false;
        });
      } else {
        setState(() {
          _isLoading = false;
        });
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("Failed to load products")),
        );
      }
    } catch (e) {
      setState(() {
        _isLoading = false;
      });
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("An error occurred")),
      );
    }
  }

  Future<void> _deleteProduct(String productId) async {
    try {
      final SharedPreferences prefs = await SharedPreferences.getInstance();
      String? jwtToken = prefs.getString('jwtToken');
      var response = await ApiService().delete(
        "${ApiConfig.deleteStoreProducts}/$productId",
        headers: {
          "Authorization": "Bearer $jwtToken",
        },
      );
      if (response["success"] == true) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("Product deleted successfully")),
        );
        setState(() {
          _allProducts.removeWhere((item) => item['id'].toString() == productId);
          _products.removeWhere((item) => item['id'].toString() == productId);
        });
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("Failed to delete product")),
        );
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Error deleting product: $e")),
      );
    }
  }

  Widget _buildProductTile(Map productItem) {
    final product = productItem['product'];
    final String productName = product?['name'] ?? 'No Name';
    final num price = productItem['price'] ?? 0;
    final int id = productItem['id'] ?? 0;
    final String? imageUrl = product?['imgUrl']?.toString();

    return ListTile(
      leading: Builder(
        builder: (context) {

            if (ApiService.isValidImageUrl(imageUrl!)) {
              return Image.network(
                imageUrl,
                width: 50,
                height: 50,
                fit: BoxFit.cover,
              );
            }else {
              return Image.asset(
                'assets/placeholder.jpg',
                width: 50,
                height: 50,
                fit: BoxFit.cover,
              );
            }
        },
      ),
      title: Text(productName),
      subtitle: Text("Price: \$${price.toString()} \nStore Product Id: $id\nProduct Id: ${product?['id']}"),
      isThreeLine: true,
      trailing: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          IconButton(
            icon: const Icon(Icons.delete, color: Colors.red),
            onPressed: () {
              _deleteProduct(id.toString());
            },
          ),
          const Icon(Icons.chevron_right),
        ],
      ),
      onTap: () {
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => EditProductPage(product: productItem),
          ),
        );
      },
    );
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : Column(
        children: [
          // Search box
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: TextField(
              controller: _searchController,
              decoration: InputDecoration(
                hintText: 'Search products',
                prefixIcon: const Icon(Icons.search),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(30.0),
                ),
              ),
            ),
          ),
          Expanded(
            child: ListView.builder(
              itemCount: _products.length,
              itemBuilder: (context, index) {
                return _buildProductTile(_products[index]);
              },
            ),
          ),
        ],
      ),
    );
  }
}
