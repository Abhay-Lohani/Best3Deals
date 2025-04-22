import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'ForgotPasswordPage.dart';
import 'SignUpPage.dart';
import '../../db/DatabaseHelper.dart';
import '../../service/Environment.dart';
import 'DashboardPage.dart';

/// The sign-in page for the app.
/// It provides fields for the user to enter their email/username and password,
/// and navigates to either the Dashboard, Forgot Password, or Sign Up pages.
class SignInPage extends StatefulWidget {
  const SignInPage({Key? key}) : super(key: key);

  @override
  _SignInPageState createState() => _SignInPageState();
}

class _SignInPageState extends State<SignInPage> {
  // Controllers for the email/username and password fields.
  final TextEditingController _usernameController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  // Flag to indicate if the sign-in process is loading.
  bool _isLoading = false;

  /// Attempts to sign in the user by sending credentials to the API.
  Future<void> _signIn() async {
    setState(() {
      _isLoading = true;
    });

    String signInBody = jsonEncode({
      "email": _usernameController.text.trim(),
      "password": _passwordController.text,
      "userType": "CUSTOMER"
    });

    final response = await ApiService().callApi(
      url: ApiConfig.signInUrl,
      headers: ApiConfig.headers,
      body: signInBody,
    );

    setState(() {
      _isLoading = false;
    });

    if (!response["success"]) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(response["error"] ?? "Login failed. Please try again.")),
      );
    } else {
      final SharedPreferences prefs = await SharedPreferences.getInstance();
      await prefs.setString('jwtToken', response['data']['token']);
      await DatabaseHelper().saveUserLogin(_usernameController.text.trim());
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(builder: (context) => const DashboardPage()),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Login'),
        centerTitle: true,
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            const SizedBox(height: 12),
            InputSection(
              usernameController: _usernameController,
              passwordController: _passwordController,
            ),
            const SizedBox(height: 16),
            SignInButton(onPressed: _signIn),
            const SizedBox(height: 16),
            const ForgotPasswordAndCreateAccountLinks(),
          ],
        ),
      ),
    );
  }
}

/// A widget that encapsulates the input fields for username and password.
class InputSection extends StatelessWidget {
  final TextEditingController usernameController;
  final TextEditingController passwordController;
  const InputSection({Key? key, required this.usernameController, required this.passwordController}) : super(key: key);

  /// Returns a consistent style for text field input decoration.
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

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        TextField(
          controller: usernameController,
          decoration: _buildInputDecoration('Username or Email'),
        ),
        const SizedBox(height: 16),
        TextField(
          controller: passwordController,
          obscureText: true,
          decoration: _buildInputDecoration('Password'),
        ),
      ],
    );
  }
}

/// A button for signing in.
class SignInButton extends StatelessWidget {
  final VoidCallback onPressed;
  const SignInButton({Key? key, required this.onPressed}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return ElevatedButton(
      onPressed: onPressed,
      child: const Text(
        'Sign In',
        style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
      ),
    );
  }
}

/// Provides links to the Forgot Password and Create Account pages.
class ForgotPasswordAndCreateAccountLinks extends StatelessWidget {
  const ForgotPasswordAndCreateAccountLinks({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        TextButton(
          onPressed: () {
            Navigator.push(
              context,
              MaterialPageRoute(builder: (context) => const ForgotPasswordPage()),
            );
          },
          child: const Text('Forgot Password?'),
        ),
        TextButton(
          onPressed: () {
            Navigator.push(
              context,
              MaterialPageRoute(builder: (context) => const SignUpPage()),
            );
          },
          child: const Text('Create Account'),
        ),
      ],
    );
  }
}
