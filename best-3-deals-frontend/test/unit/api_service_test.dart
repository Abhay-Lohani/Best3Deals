import 'package:flutter_test/flutter_test.dart';
import 'package:frontend_ui/service/Environment.dart';

void main() {
  group('ApiService Tests', () {
    final apiService = ApiService();

    test('Call API with invalid endpoint returns failure', () async {
      final response = await apiService.callApi(
        url: "/invalid_endpoint",
        headers: ApiConfig.headers,
        body: "{}",
      );
      expect(response["success"], false);
    });
  });
}
