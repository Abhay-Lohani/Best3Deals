import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;

/// This class holds all the configuration values needed to interact with the backend API.
class ApiConfig {
  // Base URL for our API server.
  static const String baseUrl = "http://172.17.3.115:8080";

  // Endpoints for various authentication and file upload operations.
  static const String signUpUrl = "/auth/signup";
  static const String signInUrl = "/auth/login";
  static const String forgotPassword = "/auth/forgot-password";
  static const String verifyUrl = "/auth/verify";
  static const String reSendCodeUrl = "/auth/resend";
  static const String getUser = "/users/me";
  static const String putUser = "/users/update-profile";
  static const String uploadUrl = "/upload";

  // Default headers used for JSON content.
  static const Map<String, String> headers = {
    "Content-Type": "application/json"
  };
}

/// Service class for making API calls to the backend. It provides methods for POST, GET, PUT,
/// DELETE requests, and file uploads.
class ApiService {
  /// Makes a POST request to the given [url] with the provided [headers] and [body].
  /// Returns a Map indicating success or failure and includes the parsed data or error message.
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

      // Try to decode the response as JSON. If it fails, fall back to plain text.
      try {
        parsedData = json.decode(responseBody);
      } catch (e) {
        parsedData = responseBody;
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

  /// Sends a GET request to the specified [endpoint] and returns the decoded response.
  Future<Map<String, dynamic>> get(String endpoint, {Map<String, String>? headers}) async {
    final response = await http.get(Uri.parse("${ApiConfig.baseUrl}$endpoint"), headers: headers);
    return jsonDecode(response.body);
  }

  /// Sends a PUT request to the specified [endpoint] with the provided [body] and returns the decoded response.
  Future<Map<String, dynamic>> put(String endpoint, {Map<String, String>? headers, dynamic body}) async {
    final response = await http.put(Uri.parse("${ApiConfig.baseUrl}$endpoint"), headers: headers, body: body);
    return jsonDecode(response.body);
  }

  /// Sends a DELETE request to the specified [endpoint] with the provided [body] and returns the decoded response.
  Future<Map<String, dynamic>> delete(String endpoint, {Map<String, String>? headers, dynamic body}) async {
    final response = await http.delete(Uri.parse("${ApiConfig.baseUrl}$endpoint"), headers: headers, body: body);
    return jsonDecode(response.body);
  }

  /// Uploads an image file to the backend using a multipart request.
  /// The [imageFile] parameter should be a File object representing the image.
  Future<Map<String, dynamic>> uploadFile(File imageFile) async {
    var uri = Uri.parse("${ApiConfig.baseUrl}/upload");
    var stream = http.ByteStream(imageFile.openRead());
    var length = await imageFile.length();
    var request = http.MultipartRequest("POST", uri);
    var multipartFile = http.MultipartFile('file', stream, length,
        filename: imageFile.path.split('/').last);
    request.files.add(multipartFile);
    var response = await request.send();
    var responseString = await response.stream.bytesToString();
    return jsonDecode(responseString);
  }
}
