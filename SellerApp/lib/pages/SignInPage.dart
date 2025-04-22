import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:sellerapp/service/DatabaseHelper.dart';
import 'package:sellerapp/service/Environment.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'EnterStoreDetailsPage.dart';
import 'HomePage.dart';

class SignInPage extends StatefulWidget {
  const SignInPage({Key? key}) : super(key: key);

  @override
  _SignInPageState createState() => _SignInPageState();
}

class _SignInPageState extends State<SignInPage> {
  final TextEditingController _usernameController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  bool _isLoading = false;

  Future<void> _signIn() async {
    setState(() {
      _isLoading = true;
    });

    String signInBody = jsonEncode({
      "email": _usernameController.text.trim(),
      "password": _passwordController.text,
      "userType": "CUSTOMER"
    });

    try {
      final response = await ApiService().callApi(
        url: ApiConfig.signInUrl,
        headers: ApiConfig.headers,
        body: signInBody,
      );

      if (!response["success"]) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
              content: Text(
                  response["error"] ?? "Login failed. Please try again.")),
        );
      } else {
        final SharedPreferences prefs = await SharedPreferences.getInstance();
        await prefs.setString('jwtToken', response['data']['token']);
        await DatabaseHelper().saveUserLogin(_usernameController.text.trim());
        Navigator.pushReplacement(
          context,
          MaterialPageRoute(builder: (context) => const EnterStoreDetailsPage()),
        );
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Error during sign in: $e")),
      );
    } finally {
      setState(() {
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Login'),
        centerTitle: true,
      ),
      body: Stack(
        children: [
          Padding(
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
              ],
            ),
          ),
          // Loader overlay
          if (_isLoading)
            Container(
              color: Colors.black.withOpacity(0.5),
              child: const Center(
                child: CircularProgressIndicator(),
              ),
            ),
        ],
      ),
    );
  }
}

class InputSection extends StatelessWidget {
  final TextEditingController usernameController;
  final TextEditingController passwordController;
  const InputSection(
      {Key? key,
        required this.usernameController,
        required this.passwordController})
      : super(key: key);

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
