import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../../service/Environment.dart';
import 'NewPostPage.dart';
import 'PostDetailPage.dart';

/// Displays a list of posts and allows filtering by store.
/// Also provides a button to create a new post.
class PostsPage extends StatefulWidget {
  const PostsPage({Key? key}) : super(key: key);

  @override
  _PostsPageState createState() => _PostsPageState();
}

class _PostsPageState extends State<PostsPage> {
  // Flag to track whether posts are being loaded.
  bool isLoading = false;
  // List of posts fetched from the backend.
  List<dynamic> posts = [];
  // Mapping of store IDs to store names.
  Map<int, String> storeNames = {};
  // Currently selected store's ID to filter posts.
  int? selectedStoreId;

  @override
  void initState() {
    super.initState();
    // Fetch posts and stores when the page loads.
    _fetchPosts();
    _fetchStores();
  }

  /// Fetches posts from the API.
  /// Uses the stored JWT token for authentication.
  Future<void> _fetchPosts() async {
    setState(() => isLoading = true);
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String? jwtToken = prefs.getString('jwtToken');

    final response = await ApiService().get(
      "/posts",
      headers: {"Authorization": "Bearer $jwtToken"},
    );

    setState(() {
      isLoading = false;
      if (response["success"] == true) {
        posts = response["data"];
      } else {
        // Show an error if fetching posts fails.
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(response["message"] ?? "Failed to load posts")),
        );
      }
    });
  }

  /// Fetches the list of stores from the API based on the user's location.
  Future<void> _fetchStores() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String? jwtToken = prefs.getString('jwtToken');
    double latitude = prefs.getDouble('latitude') ?? 0.0;
    double longitude = prefs.getDouble('longitude') ?? 0.0;
    int radius = 10000000; // Adjust the radius as needed.

    final response = await ApiService().get(
      "/stores/all/$latitude/$longitude/$radius",
      headers: {"Authorization": "Bearer $jwtToken"},
    );

    if (response["success"] == true) {
      List<dynamic> stores = response["data"];
      Map<int, String> mapping = {};
      // Build a map of store IDs to store names.
      for (var store in stores) {
        if (store["id"] != null && store["name"] != null) {
          mapping[store["id"]] = store["name"];
        }
      }
      setState(() {
        storeNames = mapping;
      });
    } else {
      // Show an error if fetching stores fails.
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(response["message"] ?? "Failed to load stores")),
      );
    }
  }

  /// Deletes a post after confirming with the user.
  /// After deletion, the posts are re-fetched.
  Future<void> _deletePost(int postId) async {
    // Ask the user for confirmation before deletion.
    final bool? confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text("Delete Post"),
        content: const Text("Are you sure you want to delete this post?"),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text("Cancel"),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text("Delete"),
          ),
        ],
      ),
    );

    if (confirmed != true) return;

    SharedPreferences prefs = await SharedPreferences.getInstance();
    String? jwtToken = prefs.getString('jwtToken');

    final response = await ApiService().delete(
      "/posts/$postId",
      headers: {"Authorization": "Bearer $jwtToken"},
    );

    if (response["success"] == true) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Post deleted successfully")),
      );
      // Refresh posts after deletion.
      _fetchPosts();
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(response["message"] ?? "Failed to delete post")),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    // Filter posts based on the selected store.
    List<dynamic> filteredPosts = selectedStoreId != null
        ? posts.where((post) => post["storeId"] == selectedStoreId).toList()
        : [];

    return Scaffold(
      appBar: AppBar(
        title: const Text("Posts"),centerTitle: true,
        actions: [
          // Refresh button to re-fetch posts and stores.
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: () {
              _fetchPosts();
              _fetchStores();
            },
          )
        ],
      ),
      // Floating action button to navigate to the NewPostPage.
      floatingActionButton: FloatingActionButton(
        backgroundColor: Colors.blue,
        foregroundColor: Colors.white,
        onPressed: () async {
          final result = await Navigator.push(
            context,
            MaterialPageRoute(builder: (context) => const NewPostPage()),
          );
          // If a new post is created, refresh the list of posts.
          if (result == true) {
            _fetchPosts();
          }
        },
        child: const Icon(Icons.add),
      ),
      body: isLoading
      // Show a loading spinner if posts are being fetched.
          ? const Center(child: CircularProgressIndicator())
          : Column(
        children: [
          // Dropdown for selecting a store to filter posts.
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: DropdownButton<int>(
              hint: const Text("Select a store"),
              value: selectedStoreId,
              isExpanded: true,
              onChanged: (value) {
                setState(() {
                  selectedStoreId = value;
                });
              },
              items: storeNames.entries
                  .map((entry) => DropdownMenuItem<int>(
                value: entry.key,
                child: Text(entry.value),
              ))
                  .toList(),
            ),
          ),
          // Display either a prompt to select a store or the filtered posts.
          Expanded(
            child: selectedStoreId == null
                ? const Center(child: Text("Please select a store"))
                : filteredPosts.isEmpty
                ? const Center(
              child: Text("No posts found for the selected store"),
            )
                : ListView.builder(
              itemCount: filteredPosts.length,
              itemBuilder: (context, index) {
                final post = filteredPosts[index];
                final int storeId = post["storeId"] ?? 0;
                final String storeName = storeNames[storeId] ??
                    "Store #$storeId";
                return InkWell(
                  // Navigate to the PostDetailPage when a post is tapped.
                  onTap: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) =>
                            PostDetailPage(postId: post["id"]),
                      ),
                    );
                  },
                  child: Card(
                    margin: const EdgeInsets.all(8),
                    elevation: 3,
                    child: Column(
                      crossAxisAlignment:
                      CrossAxisAlignment.start,
                      children: [
                        // Display post image if available; else, a placeholder.
                        if (post["imgUrl"] != null &&
                            (post["imgUrl"] as String)
                                .isNotEmpty)
                          Image.network(
                            post["imgUrl"],
                            height: 200,
                            width: double.infinity,
                            fit: BoxFit.cover,
                            errorBuilder: (context, error, stackTrace) {
                              return Container(
                                height: 200,
                                color: Colors.grey,
                                child: const Center(
                                    child: Text("Image not available")),
                              );
                            },
                          )
                        else
                          Container(
                            height: 200,
                            color: Colors.grey,
                            child: const Center(child: Text("No image")),
                          ),
                        // Post title.
                        Padding(
                          padding: const EdgeInsets.all(8.0),
                          child: Text(
                            post["title"] ?? "No Title",
                            style: const TextStyle(
                                fontSize: 18,
                                fontWeight: FontWeight.bold),
                          ),
                        ),
                        // Post content snippet.
                        Padding(
                          padding: const EdgeInsets.symmetric(
                              horizontal: 8.0),
                          child: Text(
                            post["content"] ?? "No Content",
                            maxLines: 3,
                            overflow: TextOverflow.ellipsis,
                            style: const TextStyle(fontSize: 16),
                          ),
                        ),
                        // Display associated store name.
                        Padding(
                          padding: const EdgeInsets.all(8.0),
                          child: Text(
                            "Store: $storeName",
                            style: const TextStyle(
                                fontSize: 14,
                                fontStyle: FontStyle.italic),
                          ),
                        ),
                        // Display post creation time.
                        Padding(
                          padding: const EdgeInsets.symmetric(
                              horizontal: 8.0, vertical: 4.0),
                          child: Text(
                            "Posted at: ${post["createdAt"] ?? ""}",
                            style: const TextStyle(
                                fontSize: 12, color: Colors.grey),
                          ),
                        ),
                      ],
                    ),
                  ),
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}
