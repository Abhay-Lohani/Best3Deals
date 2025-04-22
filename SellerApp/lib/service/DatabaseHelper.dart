// DatabaseHelper.dart
import 'dart:convert';
import 'package:sqflite/sqflite.dart';
import 'package:path/path.dart';

class DatabaseHelper {
  static final DatabaseHelper _instance = DatabaseHelper._internal();
  static Database? _database;

  factory DatabaseHelper() {
    return _instance;
  }

  DatabaseHelper._internal();

  Future<Database> get database async {
    if (_database != null) return _database!;
    _database = await _initDatabase();
    return _database!;
  }

  Future<Database> _initDatabase() async {
    String path = join(await getDatabasesPath(), 'user_database.db');
    return await openDatabase(
      path,
      version: 1,
      onCreate: (db, version) async {
        // Existing users table
        await db.execute(
          "CREATE TABLE users(email TEXT PRIMARY KEY, isLoggedIn INTEGER)",
        );
        // New wishlist table – storing key product fields from your JSON
        await db.execute(
          '''
          CREATE TABLE wishlist(
            product_id TEXT PRIMARY KEY,
            position INTEGER,
            sponsored INTEGER,
            is_prime INTEGER,
            kindle_unlimited INTEGER,
            is_amazon_fresh INTEGER,
            is_whole_foods_market INTEGER,
            is_climate_pledge_friendly INTEGER,
            is_carousel INTEGER,
            thumbnail TEXT,
            title TEXT,
            link TEXT,
            price TEXT,
            currency TEXT,
            old_price TEXT,
            rating TEXT,
            deal TEXT,
            store_id TEXT,
            delivery TEXT
          )
          ''',
        );
      },
    );
  }

  // Save user login status
  Future<void> saveUserLogin(String email) async {
    final db = await database;
    await db.insert(
      'users',
      {'email': email, 'isLoggedIn': 1},
      conflictAlgorithm: ConflictAlgorithm.replace,
    );
  }

  // Check if a user is logged in
  Future<bool> isUserLoggedIn() async {
    final db = await database;
    List<Map<String, dynamic>> result =
    await db.query('users', where: "isLoggedIn = ?", whereArgs: [1]);
    return result.isNotEmpty;
  }

  // Log out a user
  Future<void> logoutUser() async {
    final db = await database;
    await db.update(
      'users',
      {'isLoggedIn': 0},
    );
  }

  // Insert a product into the wishlist table.
  Future<void> insertWishlistItem(Map<String, dynamic> product) async {
    final db = await database;
    await db.insert(
      'wishlist',
      {
        'product_id': product['product_id'],
        'position': product['position'],
        'sponsored': product['sponsored'] == true ? 1 : 0,
        'is_prime': product['is_prime'] == true ? 1 : 0,
        'kindle_unlimited': product['kindle_unlimited'] == true ? 1 : 0,
        'is_amazon_fresh': product['is_amazon_fresh'] == true ? 1 : 0,
        'is_whole_foods_market': product['is_whole_foods_market'] == true ? 1 : 0,
        'is_climate_pledge_friendly': product['is_climate_pledge_friendly'] == true ? 1 : 0,
        'is_carousel': product['is_carousel'] == true ? 1 : 0,
        'thumbnail': product['thumbnail'],
        'title': product['title'],
        'link': product['link'],
        'price': product['price'],
        'currency': product['currency'],
        'old_price': product['old_price'],
        // Convert the rating object to a JSON string for storage
        'rating': jsonEncode(product['rating']),
        'deal': product['deal'],
        'store_id': product['store_id'],
        'delivery': product['delivery'],
      },
      conflictAlgorithm: ConflictAlgorithm.replace,
    );
  }

// Delete a wishlist item by its product_id.
  Future<void> deleteWishlistItem(String productId) async {
    final db = await database;
    await db.delete('wishlist', where: 'product_id = ?', whereArgs: [productId]);
  }

// Check if a product is already in the wishlist.
  Future<bool> isProductInWishlist(String productId) async {
    final db = await database;
    List<Map<String, dynamic>> results = await db.query(
      'wishlist',
      where: 'product_id = ?',
      whereArgs: [productId],
    );
    return results.isNotEmpty;
  }

  // Get count of wishlist items.
  Future<int> getWishlistCount() async {
    final db = await database;
    // Using Sqflite.firstIntValue to extract count from the query result.
    final count = Sqflite.firstIntValue(await db.rawQuery("SELECT COUNT(*) FROM wishlist"));
    return count ?? 0;
  }

}
