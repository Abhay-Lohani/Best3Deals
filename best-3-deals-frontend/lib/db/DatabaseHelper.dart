import 'dart:convert';
import 'package:sqflite/sqflite.dart';
import 'package:path/path.dart';

/// Helper class for handling SQLite database operations.
/// Implements a singleton to ensure that only one instance of the database is used.
class DatabaseHelper {
  static final DatabaseHelper _instance = DatabaseHelper._internal();
  static Database? _database;

  /// Factory constructor returns the same instance every time.
  factory DatabaseHelper() {
    return _instance;
  }

  /// Private constructor for singleton implementation.
  DatabaseHelper._internal();

  /// Returns the existing database instance if available,
  /// otherwise initializes a new database.
  Future<Database> get database async {
    if (_database != null) return _database!;
    _database = await _initDatabase();
    return _database!;
  }

  /// Initializes the database by setting up its file path and creating tables.
  Future<Database> _initDatabase() async {
    // Get the default path to store the database file.
    String path = join(await getDatabasesPath(), 'user_database.db');

    // Open or create the database at the specified path.
    return await openDatabase(
      path,
      version: 1,
      onCreate: (db, version) async {
        // Create 'users' table to track login status.
        await db.execute(
          "CREATE TABLE users(email TEXT PRIMARY KEY, isLoggedIn INTEGER)",
        );
        // Create 'wishlist' table to store key product information.
        await db.execute(
            '''
  CREATE TABLE wishlist(
    id INTEGER PRIMARY KEY,
    product_id INTEGER,
    product_name TEXT,
    product_description TEXT,
    product_imgUrl TEXT,
    product_category_id INTEGER,
    product_category_name TEXT,
    store_id INTEGER,
    store_name TEXT,
    store_address TEXT,
    store_imgUrl TEXT,
    store_location_id INTEGER,
    store_location_latitude REAL,
    store_location_longitude REAL,
    store_location_timestamp TEXT,
    store_brandId INTEGER,
    brand_id INTEGER,
    brand_name TEXT,
    brand_imageUrl TEXT,
    price REAL,
    previous_price REAL,
    quantity_in_stock INTEGER,
    productUrl TEXT,
    dateAdded TEXT,
    dateModified TEXT
  )
  '''
        );
        // Create 'recently_viewed' table to log products the user has recently seen.
        await db.execute('''
          CREATE TABLE recently_viewed(
            id INTEGER PRIMARY KEY,
            product_id INTEGER,
            product_name TEXT,
            product_description TEXT,
            product_imgUrl TEXT,
            product_category_id INTEGER,
            product_category_name TEXT,
            store_id INTEGER,
            store_name TEXT,
            store_address TEXT,
            store_imgUrl TEXT,
            store_location_id INTEGER,
            store_location_latitude REAL,
            store_location_longitude REAL,
            store_location_timestamp TEXT,
            store_brandId INTEGER,
            brand_id INTEGER,
            brand_name TEXT,
            brand_imageUrl TEXT,
            price REAL,
            previous_price REAL,
            quantity_in_stock INTEGER,
            productUrl TEXT,
            dateAdded TEXT,
            dateModified TEXT
          )
        ''');
      },
    );
  }

  /// Saves the user's login status by inserting the user's email and marking them as logged in.
  Future<void> saveUserLogin(String email) async {
    final db = await database;
    await db.insert(
      'users',
      {'email': email, 'isLoggedIn': 1},
      conflictAlgorithm: ConflictAlgorithm.replace,
    );
  }

  /// Checks if any user is currently marked as logged in.
  Future<bool> isUserLoggedIn() async {
    final db = await database;
    List<Map<String, dynamic>> result =
    await db.query('users', where: "isLoggedIn = ?", whereArgs: [1]);
    return result.isNotEmpty;
  }

  /// Logs out the user by updating their login status to 0.
  Future<void> logoutUser() async {
    final db = await database;
    await db.update(
      'users',
      {'isLoggedIn': 0},
    );
  }

  /// Inserts a new product into the 'wishlist' table.
  /// The method extracts required fields from the provided product map.
  Future<void> insertWishlistItem(Map<String, dynamic> product) async {
    final db = await database;
    await db.insert(
      'wishlist',
      {
        'id': product['id'],
        'product_id': product['product'] != null ? product['product']['id'] : null,
        'product_name': product['product'] != null ? product['product']['name'] : '',
        'product_description': product['product'] != null ? product['product']['description'] : '',
        'product_imgUrl': product['product'] != null ? product['product']['imgUrl'] : '',
        'product_category_id': product['product'] != null && product['product']['category'] != null ? product['product']['category']['id'] : null,
        'product_category_name': product['product'] != null && product['product']['category'] != null ? product['product']['category']['name'] : '',
        'store_id': product['store'] != null ? product['store']['id'] : null,
        'store_name': product['store'] != null ? product['store']['name'] : '',
        'store_address': product['store'] != null ? product['store']['address'] : '',
        'store_imgUrl': product['store'] != null ? product['store']['imgUrl'] : '',
        'store_location_id': product['store'] != null && product['store']['location'] != null ? product['store']['location']['id'] : null,
        'store_location_latitude': product['store'] != null && product['store']['location'] != null ? product['store']['location']['latitude'] : null,
        'store_location_longitude': product['store'] != null && product['store']['location'] != null ? product['store']['location']['longitude'] : null,
        'store_location_timestamp': product['store'] != null && product['store']['location'] != null ? product['store']['location']['timestamp'] : '',
        'store_brandId': product['store'] != null ? product['store']['brandId'] : null,
        'brand_id': product['brand'] != null ? product['brand']['id'] : null,
        'brand_name': product['brand'] != null ? product['brand']['name'] : '',
        'brand_imageUrl': product['brand'] != null ? product['brand']['imageUrl'] : '',
        'price': product['price'],
        'previous_price': product['previousPrice'],
        'quantity_in_stock': product['quantityInStock'],
        'productUrl': product['productUrl'] ?? '',
        'dateAdded': product['dateAdded'] ?? '',
        'dateModified': product['dateModified'] ?? '',
      },
      conflictAlgorithm: ConflictAlgorithm.replace,
    );
  }

  /// Deletes an item from the 'wishlist' table based on its id.
  Future<void> deleteWishlistItem(int id) async {
    final db = await database;
    await db.delete('wishlist', where: 'id = ?', whereArgs: [id]);
  }

  /// Checks if a product is already present in the wishlist.
  /// Returns true if found, otherwise false.
  Future<bool> isProductInWishlist(String productId) async {
    final db = await database;
    List<Map<String, dynamic>> results = await db.query(
      'wishlist',
      where: 'product_id = ?',
      whereArgs: [productId],
    );
    return results.isNotEmpty;
  }

  /// Returns the total number of items in the wishlist.
  Future<int> getWishlistCount() async {
    final db = await database;
    final count = Sqflite.firstIntValue(await db.rawQuery("SELECT COUNT(*) FROM wishlist"));
    return count ?? 0;
  }

  /// Inserts a product into the 'recently_viewed' table.
  /// This helps in maintaining a history of products the user has viewed.
  Future<void> insertRecentlyViewedItem(Map<String, dynamic> product) async {
    final db = await database;
    await db.insert(
      'recently_viewed',
      {
        'id': product['id'],
        'product_id': product['product']?['id'],
        'product_name': product['product']?['name'] ?? '',
        'product_description': product['product']?['description'] ?? '',
        'product_imgUrl': product['product']?['imgUrl'] ?? '',
        'product_category_id': product['product']?['category']?['id'],
        'product_category_name': product['product']?['category']?['name'] ?? '',
        'store_id': product['store']?['id'],
        'store_name': product['store']?['name'] ?? '',
        'store_address': product['store']?['address'] ?? '',
        'store_imgUrl': product['store']?['imgUrl'] ?? '',
        'store_location_id': product['store']?['location']?['id'],
        'store_location_latitude': product['store']?['location']?['latitude'],
        'store_location_longitude': product['store']?['location']?['longitude'],
        'store_location_timestamp': product['store']?['location']?['timestamp'],
        'store_brandId': product['store']?['brandId'],
        'brand_id': product['brand']?['id'],
        'brand_name': product['brand']?['name'] ?? '',
        'brand_imageUrl': product['brand']?['imageUrl'] ?? '',
        'price': product['price'],
        'previous_price': product['previousPrice'],
        'quantity_in_stock': product['quantityInStock'],
        'productUrl': product['productUrl'] ?? '',
        'dateAdded': product['dateAdded'] ?? '',
        'dateModified': product['dateModified'] ?? '',
      },
      conflictAlgorithm: ConflictAlgorithm.replace,
    );
  }

  /// Retrieves a list of products from the 'recently_viewed' table,
  /// ordered so that the most recent items appear first.
  Future<List<Map<String, dynamic>>> getRecentlyViewedItems() async {
    final db = await database;
    return await db.query('recently_viewed', orderBy: 'rowid DESC');
  }

  /// Removes a product from the 'recently_viewed' table using its id.
  Future<void> deleteRecentlyViewedItem(int id) async {
    final db = await database;
    await db.delete('recently_viewed', where: 'id = ?', whereArgs: [id]);
  }
}
