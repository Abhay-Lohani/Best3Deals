import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../../db/DatabaseHelper.dart';
import '../../service/Environment.dart';
import 'DashboardPage.dart';

/// This page allows a new user to sign up for an account.
/// It validates input fields, sends a signup request, and then (optionally)
/// displays a verification code field if the API indicates that a code is needed.
class SignUpPage extends StatefulWidget {
  const SignUpPage({super.key});

  @override
  _SignUpPageState createState() => _SignUpPageState();
}

class _SignUpPageState extends State<SignUpPage> {
  final _formKey = GlobalKey<FormState>();
  // Controllers for each text field.
  final TextEditingController _firstNameController = TextEditingController();
  final TextEditingController _lastNameController = TextEditingController();
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  final TextEditingController _verificationCodeController = TextEditingController();

  // Flag to show the verification code field after a successful signup.
  bool _showVerificationCodeField = false;
  // Flag to indicate when a network request is in progress.
  bool _isLoading = false;

  /// Returns a consistent InputDecoration for text fields.
  InputDecoration _buildInputDecoration(BuildContext context, String label) {
    return InputDecoration(
      labelText: label,
      filled: true,
      fillColor: Colors.white.withOpacity(0.7),
      border: OutlineInputBorder(
        borderRadius: BorderRadius.circular(30.0),
      ),
      enabledBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(30.0),
        borderSide: BorderSide(
          color: Theme.of(context).primaryColor.withOpacity(0.5),
        ),
      ),
      focusedBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(30.0),
        borderSide: BorderSide(
          color: Theme.of(context).primaryColor,
        ),
      ),
      errorStyle: const TextStyle(color: Colors.red),
    );
  }

  /// Validates that a field is not empty.
  String? _validateNotEmpty(String? value, String fieldName) {
    if (value == null || value.trim().isEmpty) {
      return '$fieldName cannot be empty';
    }
    return null;
  }

  /// Validates that the email is correctly formatted.
  String? _validateEmail(String? value) {
    String? emptyCheck = _validateNotEmpty(value, 'Email');
    if (emptyCheck != null) return emptyCheck;
    final emailRegex = RegExp(r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$');
    if (!emailRegex.hasMatch(value!.trim())) {
      return 'Enter a valid email address';
    }
    return null;
  }

  /// Validates that the password meets certain criteria.
  String? _validatePassword(String? value) {
    String? emptyCheck = _validateNotEmpty(value, 'Password');
    if (emptyCheck != null) return emptyCheck;
    if (value!.length < 8) {
      return 'Password must be at least 8 characters';
    }
    if (!RegExp(r'[A-Z]').hasMatch(value)) {
      return 'Password must contain an uppercase letter';
    }
    if (!RegExp(r'[a-z]').hasMatch(value)) {
      return 'Password must contain a lowercase letter';
    }
    if (!RegExp(r'[0-9]').hasMatch(value)) {
      return 'Password must contain a number';
    }
    if (!RegExp(r'[!@#$%^&*(),.?":{}|<>]').hasMatch(value)) {
      return 'Password must contain a special character';
    }
    return null;
  }

  /// Attempts to sign up the user by sending the details to the API.
  Future<void> _signUp() async {
    if (_formKey.currentState?.validate() ?? false) {
      // Only proceed if the form is valid.
      setState(() {
        _isLoading = true;
      });

      String signUpBody = jsonEncode({
        "firstName": _firstNameController.text.trim(),
        "lastName": _lastNameController.text.trim(),
        "email": _emailController.text.trim(),
        "password": _passwordController.text,
        "userType": "CUSTOMER"
      });

      final response = await ApiService().callApi(
        url: ApiConfig.signUpUrl,
        headers: ApiConfig.headers,
        body: signUpBody,
      );

      setState(() {
        _isLoading = false;
        // Show verification code field if the API call is successful.
        _showVerificationCodeField = response["success"];
      });

      if (!response["success"]) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(response["error"].toString() ?? "Signup failed. Please try again.")),
        );
      }
    }
  }

  /// Resends the verification code to the user's email.
  Future<void> _reSendVerificationCode() async {
    setState(() {
      _isLoading = true;
    });

    final response = await ApiService().callApi(
      url: ApiConfig.reSendCodeUrl + "?email=${_emailController.text.trim()}",
      headers: ApiConfig.headers,
      body: "",
    );

    setState(() {
      _isLoading = false;
    });

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(response["error"] ?? "Verification code sent successfully!")),
    );
  }

  /// Verifies the code entered by the user.
  Future<void> _verifyCode() async {
    setState(() {
      _isLoading = true;
    });

    String verifyBody = jsonEncode({
      "email": _emailController.text.trim(),
      "verificationCode": _verificationCodeController.text
    });

    final response = await ApiService().callApi(
      url: ApiConfig.verifyUrl,
      headers: ApiConfig.headers,
      body: verifyBody,
    );

    if (response["success"]) {
      _signIn();
    } else {
      setState(() {
        _isLoading = false;
      });
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(response["error"] ?? "Invalid verification code.")),
      );
    }
  }

  /// Signs the user in by sending a sign-in request and navigating to the dashboard.
  Future<void> _signIn() async {
    String signInBody = jsonEncode({
      "email": _emailController.text.trim(),
      "password": _passwordController.text,
      "userType": "CUSTOMER"
    });

    final response = await ApiService().callApi(
      url: ApiConfig.signInUrl,
      headers: ApiConfig.headers,
      body: signInBody,
    );

    if (!response["success"]) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(response["error"] ?? "Login failed. Please try again.")),
      );
    } else {
      final SharedPreferences prefs = await SharedPreferences.getInstance();
      await prefs.setString('jwtToken', response['data']['token']);
      await DatabaseHelper().saveUserLogin(_emailController.text.trim());
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(builder: (context) => const DashboardPage()),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      // Use the app theme for styling the AppBar.
      appBar: AppBar(
        title: const Text('Sign Up'),
        centerTitle: true,
      ),
      body: Stack(
        children: [
          SingleChildScrollView(
            child: Container(
              padding: const EdgeInsets.all(16.0),
              child: Form(
                key: _formKey,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    TextFormField(
                      controller: _firstNameController,
                      decoration: _buildInputDecoration(context, 'First Name'),
                      validator: (value) => _validateNotEmpty(value, 'First Name'),
                    ),
                    const SizedBox(height: 12),
                    TextFormField(
                      controller: _lastNameController,
                      decoration: _buildInputDecoration(context, 'Last Name'),
                      validator: (value) => _validateNotEmpty(value, 'Last Name'),
                    ),
                    const SizedBox(height: 12),
                    TextFormField(
                      controller: _emailController,
                      decoration: _buildInputDecoration(context, 'Email'),
                      validator: _validateEmail,
                    ),
                    const SizedBox(height: 12),
                    TextFormField(
                      controller: _passwordController,
                      obscureText: true,
                      decoration: _buildInputDecoration(context, 'Password'),
                      validator: _validatePassword,
                    ),
                    const SizedBox(height: 20),
                    SizedBox(
                      width: MediaQuery.of(context).size.width * 0.5,
                      height: 56,
                      child: ElevatedButton(
                        onPressed: _signUp,
                        child: const Text('Sign Up', style: TextStyle(fontSize: 18)),
                      ),
                    ),
                    // If verification is needed, show the verification code input and actions.
                    if (_showVerificationCodeField) ...[
                      const SizedBox(height: 20),
                      Row(
                        children: [
                          Expanded(
                            child: TextFormField(
                              controller: _verificationCodeController,
                              decoration: _buildInputDecoration(context, 'Verification Code'),
                            ),
                          ),
                          const SizedBox(width: 10),
                          ElevatedButton(
                            onPressed: _reSendVerificationCode,
                            child: const Text('Resend Code'),
                          ),
                        ],
                      ),
                      const SizedBox(height: 10),
                      ElevatedButton(
                        onPressed: _verifyCode,
                        child: const Text('Verify Code'),
                      ),
                    ],
                  ],
                ),
              ),
            ),
          ),
          if (_isLoading)
            Container(
              color: Colors.black.withOpacity(0.3),
              child: const Center(child: CircularProgressIndicator()),
            ),
        ],
      ),
    );
  }
}
