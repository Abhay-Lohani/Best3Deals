import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../../service/Environment.dart';
import 'FlyerPage.dart';

/// Displays a grid of flyers for a given store.
/// When a flyer is tapped, it navigates to the FlyerPage for further details.
class StoreFlyers extends StatefulWidget {
  final int storeId;
  const StoreFlyers({Key? key, required this.storeId}) : super(key: key);

  @override
  _StoreFlyersState createState() => _StoreFlyersState();
}

class _StoreFlyersState extends State<StoreFlyers> {
  // Flag to indicate if data is loading.
  bool isLoading = true;
  // List to store fetched flyers.
  List<dynamic> flyers = [];

  @override
  void initState() {
    super.initState();
    loadFlyers();
  }

  /// Fetches flyers for the given store using the API.
  Future<void> loadFlyers() async {
    final endpoint = "/flyers/store/${widget.storeId}";
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String? jwtToken = prefs.getString('jwtToken');
    final response = await ApiService().get(
      endpoint,
      headers: {
        "Authorization": "Bearer $jwtToken",
      },
    );
    if (response["success"] == true) {
      setState(() {
        flyers = response["data"];
        isLoading = false;
      });
    } else {
      setState(() {
        isLoading = false;
      });
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(response["error"] ?? "Failed to load flyers")),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Store Flyers"),
        centerTitle: true,
      ),
      body: isLoading
          ? const Center(child: CircularProgressIndicator())
          : flyers.isEmpty
          ? const Center(child: Text("No flyers available"))
          : GridView.builder(
        padding: const EdgeInsets.all(8),
        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 2,
          childAspectRatio: 0.75,
          crossAxisSpacing: 8,
          mainAxisSpacing: 8,
        ),
        itemCount: flyers.length,
        itemBuilder: (context, index) {
          final flyer = flyers[index];
          return GestureDetector(
            onTap: () {
              // Navigate to the FlyerPage using the flyer ID.
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => FlyerPage(flyerId: flyer['id']),
                ),
              );
            },
            child: Card(
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(8),
              ),
              elevation: 5,
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // Display flyer image if available.
                  flyer["imageUrl"] != null && flyer["imageUrl"].toString().isNotEmpty
                      ? Image.network(
                    flyer["imageUrl"],
                    fit: BoxFit.cover,
                    height: 120,
                    width: double.infinity,
                  )
                      : Container(
                    height: 120,
                    color: Colors.grey,
                  ),
                  Padding(
                    padding: const EdgeInsets.all(8.0),
                    child: Text(
                      flyer["name"] ?? "",
                      style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
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
