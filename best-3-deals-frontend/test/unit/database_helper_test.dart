import 'package:flutter_test/flutter_test.dart';
import 'package:frontend_ui/db/DatabaseHelper.dart';
import 'package:sqflite/sqflite.dart';
import 'package:path/path.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  group('DatabaseHelper Tests', () {
    final dbHelper = DatabaseHelper();

    setUp(() async {
      // Delete the existing database before each test.
      var databasesPath = await getDatabasesPath();
      String path = join(databasesPath, 'user_database.db');
      await deleteDatabase(path);
      // Initialize the database.
      await dbHelper.database;
    });

    test('Save user login and check logged in state', () async {
      await dbHelper.saveUserLogin("test@example.com");
      bool loggedIn = await dbHelper.isUserLoggedIn();
      expect(loggedIn, true);
    });

    test('Logout user', () async {
      await dbHelper.saveUserLogin("test@example.com");
      await dbHelper.logoutUser();
      bool loggedIn = await dbHelper.isUserLoggedIn();
      expect(loggedIn, false);
    });
  });
}
