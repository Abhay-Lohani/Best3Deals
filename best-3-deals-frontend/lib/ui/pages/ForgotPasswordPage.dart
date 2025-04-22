import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:frontend_ui/ui/pages/SignInPage.dart';
import '../../service/Environment.dart';

/// A screen that allows users to request a password reset.
/// The user enters their email, and if the request is successful,
/// they will receive a reset password link via email.
class ForgotPasswordPage extends StatefulWidget {
  const ForgotPasswordPage({Key? key}) : super(key: key);

  @override
  _ForgotPasswordPageState createState() => _ForgotPasswordPageState();
}

class _ForgotPasswordPageState extends State<ForgotPasswordPage> {
  // Controller to capture the email input.
  final TextEditingController _emailController = TextEditingController();
  // Flag to indicate whether the app is processing the reset request.
  bool _isLoading = false;

  /// Handles the password reset process.
  /// It sends a request to the API with the user's email.
  /// Upon success, it notifies the user and redirects to the SignInPage.
  Future<void> _resetPassword() async {
    setState(() {
      _isLoading = true;
    });

    // Prepare the request body with the entered email.
    String signInBody = jsonEncode({
      "email": _emailController.text.trim(),
    });

    // Call the API endpoint for password reset.
    final response = await ApiService().callApi(
      url: ApiConfig.forgotPassword,
      headers: ApiConfig.headers,
      body: signInBody,
    );

    setState(() {
      _isLoading = false;
    });

    // If the API indicates a failure, show an error message.
    if (!response["success"]) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(
            response["error"] ?? "Something went wrong. Please try again.",
          ),
        ),
      );
    } else {
      // Notify the user that the reset link has been sent.
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text("Reset password link sent to your email"),
        ),
      );
      // Redirect the user back to the SignInPage.
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(builder: (context) => const SignInPage()),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Forgot Password'),
        centerTitle: true,
      ),
      // Display a loader when processing; otherwise, show the form.
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            const SizedBox(height: 12),
            // Email input field.
            TextField(
              controller: _emailController,
              decoration:
              _buildInputDecoration("Please enter your email address"),
            ),
            const SizedBox(height: 16),
            // Button to submit the reset password request.
            ElevatedButton(
              onPressed: _resetPassword,
              child: const Text('Reset Password'),
            ),
          ],
        ),
      ),
    );
  }

  /// Builds a consistent style for input fields.
  InputDecoration _buildInputDecoration(String label) {
    return InputDecoration(
      labelText: label,
      filled: true,
      fillColor: Colors.white.withOpacity(0.7),
      border: OutlineInputBorder(
        borderRadius: BorderRadius.circular(30.0),
        borderSide: const BorderSide(
          color: Colors.black,
        ),
      ),
      enabledBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(30.0),
        borderSide: BorderSide(
          color: Colors.black.withOpacity(0.5),
        ),
      ),
      focusedBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(30.0),
        borderSide: const BorderSide(
          color: Colors.black,
        ),
      ),
      errorStyle: const TextStyle(color: Colors.red),
    );
  }
}
