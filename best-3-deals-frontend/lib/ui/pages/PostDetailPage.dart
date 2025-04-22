import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../../service/Environment.dart';
import 'package:http/http.dart' as http;

/// Displays the details of a single post along with its comments.
/// Allows users to add new comments, delete existing comments, and even delete the post.
class PostDetailPage extends StatefulWidget {
  final int postId;
  const PostDetailPage({Key? key, required this.postId}) : super(key: key);

  @override
  _PostDetailPageState createState() => _PostDetailPageState();
}

class _PostDetailPageState extends State<PostDetailPage> {
  // Indicates whether data is currently being loaded.
  bool isLoading = false;
  // Holds the post details retrieved from the API.
  Map<String, dynamic>? postData;
  // List of comments for the post.
  List<dynamic> comments = [];
  // Controller for the comment text field.
  final TextEditingController _commentController = TextEditingController();
  // Mapping of user IDs to their full names, used for displaying comment authors.
  Map<int, String> userNames = {};

  @override
  void initState() {
    super.initState();
    // Fetch user data to map user IDs to names.
    _fetchUsers();
    // Fetch the post details and its comments.
    _fetchPost();
  }

  /// Fetches all users so that we can display a full name for each comment.
  Future<void> _fetchUsers() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String? jwtToken = prefs.getString('jwtToken');
    final response = await http.get(
      Uri.parse("${ApiConfig.baseUrl}/users/all"),
      headers: {"Authorization": "Bearer $jwtToken"},
    );
    var responseBody = jsonDecode(response.body);
    if (responseBody != null) {
      // Build a mapping of user IDs to "firstName lastName".
      Map<int, String> mapping = {};
      for (var user in responseBody) {
        if (user["id"] != null && user["firstName"] != null && user["lastName"] != null) {
          final int userId = int.tryParse(user["id"].toString()) ?? 0;
          if (userId != 0) {
            mapping[userId] = "${user["firstName"]} ${user["lastName"]}";
          }
        }
      }
      setState(() {
        userNames = mapping;
      });
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(responseBody["message"] ?? "Failed to load users")),
      );
    }
  }

  /// Fetches the post details and its associated comments.
  Future<void> _fetchPost() async {
    setState(() => isLoading = true);
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String? jwtToken = prefs.getString('jwtToken');

    // Retrieve the post details.
    final response = await ApiService().get(
      "/posts/${widget.postId}",
      headers: {"Authorization": "Bearer $jwtToken"},
    );

    if (response["success"] == true) {
      postData = response["data"];
      // Once the post is fetched, retrieve its comments.
      final commentsResp = await ApiService().get(
        "/posts/comments/all/${widget.postId}",
        headers: {"Authorization": "Bearer $jwtToken"},
      );
      if (commentsResp["success"] == true) {
        comments = commentsResp["data"];
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(commentsResp["error"] ?? "Failed to load comments")),
        );
      }
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(response["error"] ?? "Failed to load post")),
      );
    }
    setState(() => isLoading = false);
  }

  /// Prompts the user to confirm deletion of the post.
  /// If confirmed, the post is deleted and the user is navigated back.
  Future<void> _deletePost() async {
    final bool? confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text("Delete Post"),
        content: const Text("Are you sure you want to delete this post?"),
        actions: [
          TextButton(
              onPressed: () => Navigator.pop(context, false),
              child: const Text("Cancel")
          ),
          TextButton(
              onPressed: () => Navigator.pop(context, true),
              child: const Text("Delete")
          ),
        ],
      ),
    );

    if (confirmed != true) return;

    SharedPreferences prefs = await SharedPreferences.getInstance();
    String? jwtToken = prefs.getString('jwtToken');

    final response = await ApiService().delete(
      "/posts/${widget.postId}",
      headers: {"Authorization": "Bearer $jwtToken"},
    );

    if (response["success"] == true) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Post deleted successfully")),
      );
      Navigator.pop(context, true); // Go back and refresh the posts list.
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(response["message"] ?? "Failed to delete post")),
      );
    }
  }

  /// Prompts the user to confirm deletion of a comment.
  /// If confirmed, the comment is deleted and the post details are refreshed.
  Future<void> _deleteComment(int commentId) async {
    final bool? confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text("Delete Comment"),
        content: const Text("Are you sure you want to delete this comment?"),
        actions: [
          TextButton(
              onPressed: () => Navigator.pop(context, false),
              child: const Text("Cancel")
          ),
          TextButton(
              onPressed: () => Navigator.pop(context, true),
              child: const Text("Delete")
          ),
        ],
      ),
    );

    if (confirmed != true) return;

    SharedPreferences prefs = await SharedPreferences.getInstance();
    String? jwtToken = prefs.getString('jwtToken');

    final response = await ApiService().delete(
      "/posts/comments/$commentId",
      headers: {"Authorization": "Bearer $jwtToken"},
    );

    if (response["success"] == true) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Comment deleted successfully")),
      );
      _fetchPost(); // Refresh the post and comments.
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(response["message"] ?? "Failed to delete comment")),
      );
    }
  }

  /// Adds a new comment to the post.
  /// After a successful addition, the comment input field is cleared and the post is refreshed.
  Future<void> _addComment() async {
    if (_commentController.text.trim().isEmpty) return;
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String? jwtToken = prefs.getString('jwtToken');

    final body = {
      "content": _commentController.text.trim(),
      "postId": widget.postId,
    };

    final response = await ApiService().callApi(
      url: "/posts/comments",
      headers: {
        "Content-Type": "application/json",
        "Authorization": "Bearer $jwtToken",
      },
      body: jsonEncode(body),
    );

    if (response["success"] == true) {
      _commentController.clear();
      _fetchPost(); // Refresh the post and comments to include the new comment.
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(response["error"] ?? "Failed to add comment")),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Post Details"),centerTitle: true,
        actions: [
          // Delete button in the app bar to remove the post.
          IconButton(
            icon: const Icon(Icons.delete),
            onPressed: _deletePost,
          ),
        ],
      ),
      body: isLoading
      // Show a loading indicator while data is being fetched.
          ? const Center(child: CircularProgressIndicator())
      // If no post data is available, show a message.
          : postData == null
          ? const Center(child: Text("No post data"))
      // Otherwise, display the post details.
          : SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Display the post image if available; otherwise, a placeholder is shown.
              if (postData?["imgUrl"] != null && (postData?["imgUrl"] as String).isNotEmpty)
                Image.network(
                  postData?["imgUrl"],
                  height: 200,
                  width: double.infinity,
                  fit: BoxFit.cover,
                  errorBuilder: (context, error, stackTrace) {
                    return Container(
                      height: 200,
                      color: Colors.grey,
                      child: const Center(child: Text("Image not available")),
                    );
                  },
                )
              else
                Container(
                  height: 200,
                  color: Colors.grey,
                  child: const Center(child: Text("No image")),
                ),
              // Display the post title.
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: Text(
                  postData?["title"] ?? "No Title",
                  style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                ),
              ),
              // Display a snippet of the post content.
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 8.0),
                child: Text(
                  postData?["content"] ?? "No Content",
                  maxLines: 3,
                  overflow: TextOverflow.ellipsis,
                  style: const TextStyle(fontSize: 16),
                ),
              ),
              const SizedBox(height: 8),
              // Show the post creation date.
              Text("Created At: ${postData!["createdAt"] ?? ""}"),
              const Divider(height: 32),
              // Comments section header.
              const Text("Comments", style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
              const SizedBox(height: 8),
              // If there are no comments, show a placeholder.
              comments.isEmpty
                  ? const Text("No comments yet.")
              // Otherwise, build a list of comment cards.
                  : ListView.builder(
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                itemCount: comments.length,
                itemBuilder: (context, index) {
                  final comment = comments[index];
                  // Retrieve the commenter's full name using the userNames mapping.
                  final int userId = comment["userId"] ?? 0;
                  final String fullName = userNames[userId] ?? userId.toString();
                  return Card(
                    margin: const EdgeInsets.symmetric(vertical: 4),
                    child: ListTile(
                      title: Text(comment["content"] ?? ""),
                      subtitle: Text("By: $fullName"),
                      trailing: IconButton(
                        icon: const Icon(Icons.delete, color: Colors.red),
                        onPressed: () => _deleteComment(comment["id"]),
                      ),
                    ),
                  );
                },
              ),
              const SizedBox(height: 16),
              // Input field for adding a new comment.
              Row(
                children: [
                  Expanded(
                    child: TextField(
                      controller: _commentController,
                      decoration: const InputDecoration(
                        hintText: "Add a comment...",
                      ),
                    ),
                  ),
                  IconButton(
                    icon: const Icon(Icons.send),
                    onPressed: _addComment,
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}
