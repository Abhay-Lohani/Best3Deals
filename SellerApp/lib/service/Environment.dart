import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;

class ApiConfig {
  static const String baseUrl = "http://172.17.3.115:8080";
  static const String signUpUrl = "/auth/signup";
  static const String signInUrl = "/auth/login";
  static const String verifyUrl = "/auth/verify";
  static const String reSendCodeUrl = "/auth/resend";
  static const String uploadUrl = "/upload";
  static const String postProducts = "/products";
  static const String postStoreProducts = "/store-products";
  static const String putProducts = "/products";
  static const String putStoreProducts = "/store-products";
  static const String getProducts = "/products";
  static const String getStoreProducts = "/store-products";
  static const String deleteProducts = "/products";
  static const String deleteStoreProducts = "/store-products";
  static const String postStores = "/stores";
  static const String getFlyers = "/flyers";
  static const String postFlyers = "/flyers";
  static const String putFlyers = "/flyers";
  static const String deleteFlyers = "/flyers";
  static const String deleteFlyerProduct = "/flyers/product";
  static const Map<String, String> headers = {
    "Content-Type": "application/json"
  };
}

class ApiService {
  Future<Map<String, dynamic>> callApi({
    required String url,
    required Map<String, String> headers,
    required String body,
  }) async {
    final Uri apiUrl = Uri.parse(ApiConfig.baseUrl + url);

    try {
      final response = await http.post(
        apiUrl,
        headers: headers,
        body: body,
      );

      String responseBody = response.body;
      dynamic parsedData;

      try {
        parsedData = json.decode(responseBody);
      } catch (e) {
        parsedData = responseBody; // If parsing fails, treat as plain text
      }

      if (response.statusCode == 200) {
        print('API Call Successful: $parsedData');
        return {"success": true, "data": parsedData};
      } else {
        print('Error: ${response.statusCode}, $parsedData');
        return {"success": false, "error": parsedData ?? "Unknown error"};
      }
    } catch (e) {
      print('Error occurred: $e');
      return {"success": false, "error": "Network error, please try again."};
    }
  }

  // GET method
  Future<Map<String, dynamic>> get(String endpoint, {Map<String, String>? headers}) async {
    final response = await http.get(Uri.parse("${ApiConfig.baseUrl}$endpoint"), headers: headers);
    return jsonDecode(response.body);
  }

  // PUT method
  Future<Map<String, dynamic>> put(String endpoint, {Map<String, String>? headers, dynamic body}) async {
    final response = await http.put(Uri.parse("${ApiConfig.baseUrl}$endpoint"), headers: headers, body: body);
    return jsonDecode(response.body);
  }

  // DELETE method
  Future<Map<String, dynamic>> delete(String endpoint, {Map<String, String>? headers, dynamic body}) async {
    final response = await http.delete(Uri.parse("${ApiConfig.baseUrl}$endpoint"), headers: headers, body: body);
    return jsonDecode(response.body);
  }

  // Example for multipart upload (for image files)
  Future<Map<String, dynamic>> uploadFile(File imageFile) async {
    var uri = Uri.parse("${ApiConfig.baseUrl}/upload");
    var stream = http.ByteStream(imageFile.openRead());
    var length = await imageFile.length();
    var request = http.MultipartRequest("POST", uri);
    var multipartFile = http.MultipartFile('file', stream, length, filename: imageFile.path.split('/').last);
    request.files.add(multipartFile);
    var response = await request.send();
    var responseString = await response.stream.bytesToString();
    return jsonDecode(responseString);
  }

  static bool isValidImageUrl(String url)  {
    if (url.isEmpty) return false;
    // Try to parse the URL.
    final uri = Uri.tryParse(url);
    if (uri == null || (uri.scheme != 'http' && uri.scheme != 'https')) {
      return false;
    }else{
      return true;
    }
  }
}
