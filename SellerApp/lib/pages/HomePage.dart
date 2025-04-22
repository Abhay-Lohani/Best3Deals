import 'package:flutter/material.dart';
import 'CreateListingPage.dart';
import 'FlyersProduct.dart';
import 'ManageFlyers.dart';
import 'ManageListingsPage.dart';
import 'ManageStores.dart';
import 'SignInPage.dart';
import '../service/DatabaseHelper.dart';

class HomePage extends StatefulWidget {
  const HomePage({Key? key}) : super(key: key);
  @override
  _HomePageState createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  int _selectedIndex = 0;

  // Bottom navigation pages.
  final List<Widget> _pages = [
    CreateListingPage(),
    const ManageListingsPage(),
  ];

  void _onItemTapped(int index) {
    setState(() {
      _selectedIndex = index;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Listings"),
        centerTitle: true,
      ),
      drawer: Drawer(
        child: ListView(
          children: [
            const DrawerHeader(
              decoration: BoxDecoration(
                color: Colors.blue,
              ),
              child: null
            ),
            ListTile(
              leading: const Icon(Icons.local_activity),
              title: const Text("Flyers"),
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => const ManageFlyersPage()),
                );
              },
            ),
            ListTile(
              leading: const Icon(Icons.local_offer),
              title: const Text("FlyerProduct"),
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => const FlyerProductPage()),
                );
              },
            ),
            ListTile(
              leading: const Icon(Icons.store),
              title: const Text("Stores"),
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => const ManageStoresPage()),
                );
              },
            ),
            ListTile(
              leading: const Icon(Icons.logout),
              title: const Text("Log Out"),
              onTap: () async {
                _logout();
              },
            ),
          ],
        ),
      ),
      body: _pages[_selectedIndex],
      bottomNavigationBar: SizedBox(
        height: 70, // Adjust height as needed.
        child: BottomNavigationBar(
          items: const [
            BottomNavigationBarItem(
              icon: Icon(Icons.add),
              label: "Create Listing",
            ),
            BottomNavigationBarItem(
              icon: Icon(Icons.list),
              label: "Manage Listings",
            ),
          ],
          currentIndex: _selectedIndex,
          onTap: _onItemTapped,
        ),
      ),
    );
  }

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
}
