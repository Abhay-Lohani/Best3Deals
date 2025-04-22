// Function to hide all lists
function hideAllLists() {
    document.getElementById('userList').style.display = 'none';
    document.getElementById('flyerList').style.display = 'none';
    document.getElementById('productList').style.display = 'none';
    document.getElementById('storeList').style.display = 'none';
    document.getElementById('postList').style.display = 'none';
    document.getElementById('commentList').style.display = 'none';
}

// Function to handle login
async function login(event) {
    event.preventDefault();
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    const response = await fetch('/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ email, password })
    });

    if (response.ok) {
        const data = await response.json();
        const token = data.token;

        // Decode the JWT token to get the user's roles
        const decodedToken = decodeJwtToken(token);
        if (!decodedToken || !decodedToken.roles) {
            alert('Invalid token or roles not found');
            return;
        }

        // Check if the user has the ADMIN role
        const isAdmin = decodedToken.roles.includes("ROLE_ADMIN");
        if (!isAdmin) {
            alert('Only ADMIN users can access this portal');
            return;
        }

        // Store token and roles in localStorage
        localStorage.setItem('token', token);
        localStorage.setItem('roles', JSON.stringify(decodedToken.roles));

        // Hide login form and show dashboard
        document.getElementById('loginForm').style.display = 'none';
        document.getElementById('dashboard').style.display = 'block';
    } else {
        alert('Login failed');
    }
}


// Function to check token expiry
function checkTokenExpiry() {
    const token = localStorage.getItem('token');
    const tokenExpiry = localStorage.getItem('tokenExpiry');

    if (!token || !tokenExpiry) {
        logout();
        return;
    }

    if (Date.now() > parseInt(tokenExpiry)) {
        logout();
    }
}

// Function to handle logout
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('tokenExpiry');
    document.getElementById('loginForm').style.display = 'block';
    hideAllLists();
    document.getElementById('dashboard').style.display = 'none';
}

// Function to clear token on browser/tab close
window.addEventListener('beforeunload', () => {
    localStorage.removeItem('token');
    localStorage.removeItem('tokenExpiry');
});

// Check token expiry when the page loads
window.addEventListener('load', () => {
    checkTokenExpiry();
});

function decodeJwtToken(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const payload = JSON.parse(atob(base64));
        return payload;
    } catch (error) {
        console.error("Failed to decode JWT token:", error);
        return null;
    }
}

// Function to load the dashboard
function loadDashboard() {
    hideAllLists();
    document.getElementById('dashboard').style.display = 'block';
}

// Function to load the user list
async function loadUserList() {
    const token = localStorage.getItem('token');
    if (!token) {
        alert('Please login first');
        document.getElementById('loginForm').style.display = 'block';
        return;
    }

    hideAllLists();
    const response = await fetch('/admin/users', {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (response.ok) {
        const users = await response.json();
        const userTableBody = document.querySelector('#usersTable tbody');
        userTableBody.innerHTML = '';
        users.forEach(user => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${user.id || ''}</td>
                <td>${user.firstName || ''}</td>
                <td>${user.lastName || ''}</td>
                <td>${user.email || ''}</td>
                <td>${user.phoneNumber || ''}</td>
                <td>${user.address || ''}</td>
                <td>${user.enabled ? 'Yes' : 'No'}</td>
                <td>${user.userType || ''}</td></td>
                <td>
                    <button class="edit" onclick="editUser(${user.id})">Edit</button>
                    <button onclick="deleteUser(${user.id})">Delete</button>
                </td>
            `;
            userTableBody.appendChild(row);
        });
        document.getElementById('userList').style.display = 'block';
    } else {
        alert('Failed to fetch users');
    }
}

// Function to delete a user
async function deleteUser(userId) {
    const token = localStorage.getItem('token');
    const response = await fetch(`/admin/users/${userId}`, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (response.ok) {
        alert('User deleted successfully');
        loadUserList();
    } else {
        alert('Failed to delete user');
    }
}

// Function to edit a user
async function editUser(userId) {
    const newFirstName = prompt('Enter new first name:');
    const newLastName = prompt('Enter new last name:');
    const newPhoneNumber = prompt('Enter new phone number:');
    const newAddress = prompt('Enter new address:');

    if (newFirstName && newLastName && newPhoneNumber && newAddress) {
        const token = localStorage.getItem('token');
        const response = await fetch(`/admin/users/${userId}`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                firstName: newFirstName,
                lastName: newLastName,
                phoneNumber: newPhoneNumber,
                address: newAddress
            })
        });

        if (response.ok) {
            alert('User updated successfully');
            loadUserList();
        } else {
            alert('Failed to update user');
        }
    } else {
        alert('All fields are required');
    }
}

// Function to load flyers
async function loadFlyers() {
    const token = localStorage.getItem('token');
    if (!token) {
        alert('Please login first');
        document.getElementById('loginForm').style.display = 'block';
        return;
    }

    hideAllLists();
    const response = await fetch('/flyers', {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (response.ok) {
        const result = await response.json();
        const flyers = result.data;
        const flyerTableBody = document.querySelector('#flyersTable tbody');
        flyerTableBody.innerHTML = '';

        flyers.forEach(flyer => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${flyer.id || ''}</td>
                <td>${flyer.name || ''}</td>
                <td>${flyer.description || ''}</td>
                <td>${flyer.storeId !== null ? flyer.storeId : ''}</td>
                <td>
                    <button onclick="deleteFlyer(${flyer.id})">Delete</button>
                </td>
            `;
            flyerTableBody.appendChild(row);
        });

        document.getElementById('flyerList').style.display = 'block';
    } else {
        alert('Failed to fetch flyers');
    }
}
// Function to delete a flyer
async function deleteFlyer(flyerId) {
    const token = localStorage.getItem('token');
    const response = await fetch(`/flyers/${flyerId}`, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (response.ok) {
        alert('Flyer deleted successfully');
        loadFlyers();
    } else {
        alert('Failed to delete flyer');
    }
}

// Function to load products
async function loadProducts() {
    const token = localStorage.getItem('token');
    if (!token) {
        alert('Please login first');
        document.getElementById('loginForm').style.display = 'block';
        return;
    }

    hideAllLists();
    const response = await fetch('/products', {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (response.ok) {
        const result = await response.json();
        const products = result.data;
        const productTableBody = document.querySelector('#productsTable tbody');
        productTableBody.innerHTML = '';

        products.forEach(product => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${product.id || ''}</td>
                <td>${product.name || ''}</td>
                <td>${product.price || ''}</td>
                <td>${product.description || ''}</td>
                <td>${product.quantityInStock || ''}</td>
                <td>${product.categoryId !== null ? product.categoryId : ''}</td>
                <td>${product.storeId || ''}</td>
                <td>
                    <button onclick="deleteProduct(${product.id})">Delete</button>
                </td>
            `;
            productTableBody.appendChild(row);
        });

        document.getElementById('productList').style.display = 'block';
    } else {
        alert('Failed to fetch products');
    }
}
// Function to delete a product
async function deleteProduct(productId) {
    const token = localStorage.getItem('token');
    const response = await fetch(`/products/${productId}`, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (response.ok) {
        alert('Product deleted successfully');
        loadProducts();
    } else {
        alert('Failed to delete product');
    }
}


// Function to load stores
async function loadStores() {
    const token = localStorage.getItem('token');
    if (!token) {
        alert('Please login first');
        document.getElementById('loginForm').style.display = 'block';
        return;
    }

    hideAllLists();
    const response = await fetch('/stores', {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (response.ok) {
        const result = await response.json();
        const stores = result.data;
        const storeTableBody = document.querySelector('#storesTable tbody');
        storeTableBody.innerHTML = '';

        stores.forEach(store => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${store.id || ''}</td>
                <td>${store.name || ''}</td>
                <td>${store.address || ''}</td>
                <td>${store.location ? store.location.latitude : ''}, ${store.location ? store.location.longitude : ''}</td>
                <td>${store.brandId !== null ? store.brandId : ''}</td>
                <td>
                    <button onclick="deleteStore(${store.id})">Delete</button>
                </td>
            `;
            storeTableBody.appendChild(row);
        });

        document.getElementById('storeList').style.display = 'block';
    } else {
        alert('Failed to fetch stores');
    }
}
// Function to delete a store
async function deleteStore(storeId) {
    const token = localStorage.getItem('token');
    const response = await fetch(`/stores/${storeId}`, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (response.ok) {
        alert('Store deleted successfully');
        loadStores();
    } else {
        alert('Failed to delete store');
    }
}

// Function to load posts
async function loadPosts() {
    const token = localStorage.getItem('token');
    if (!token) {
        alert('Please login first');
        document.getElementById('loginForm').style.display = 'block';
        return;
    }

    hideAllLists();
    const response = await fetch('/posts', {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (response.ok) {
        const result = await response.json();
        const posts = result.data;
        const postTableBody = document.querySelector('#postsTable tbody');
        postTableBody.innerHTML = '';

        posts.forEach(post => {
            const row = document.createElement('tr');
            row.innerHTML = `
            <td>${post.id || ''}</td>
            <td>${post.title || ''}</td>
            <td>${post.content || ''}</td>
            <td>${post.userId || ''}</td>
            <td>${post.storeId || ''}</td>
            <td>
                <button onclick="deletePost(${post.id})">Delete</button>
                <button onclick="loadComments(${post.id})">View Comments</button>
            </td>
        `;
            postTableBody.appendChild(row);
        });

        document.getElementById('postList').style.display = 'block';
    } else {
        alert('Failed to fetch posts');
    }
}

// Function to load comments for a specific post
async function loadComments(postId) {
    const token = localStorage.getItem('token');
    if (!token) {
        alert('Please login first');
        document.getElementById('loginForm').style.display = 'block';
        return;
    }

    const response = await fetch(`/posts/comments/all/${postId}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (response.ok) {
        const result = await response.json();
        const comments = result.data;
        const commentTableBody = document.querySelector('#commentsTable tbody');
        commentTableBody.innerHTML = '';

        comments.forEach(comment => {
            const row = document.createElement('tr');
            row.innerHTML = `
            <td>${comment.id || ''}</td>
            <td>${comment.content || ''}</td>
            <td>${comment.userId || ''}</td>
            <td>${comment.postId || ''}</td>
            <td>
                <button onclick="deleteComment(${comment.id})">Delete</button>
            </td>
        `;
            commentTableBody.appendChild(row);
        });

        document.getElementById('commentList').style.display = 'block';
    } else {
        alert('Failed to fetch comments');
    }
}

// Function to delete a post
async function deletePost(postId) {
    const token = localStorage.getItem('token');
    const response = await fetch(`/posts/${postId}`, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (response.ok) {
        alert('Post deleted successfully');
        loadPosts();
    } else {
        alert('Failed to delete post');
    }
}

// Function to delete a comment
async function deleteComment(commentId) {
    const token = localStorage.getItem('token');
    if (!token) {
        alert('Please login first');
        document.getElementById('loginForm').style.display = 'block';
        return;
    }

    // Fetch the comment
    const commentResponse = await fetch(`/posts/comments/${commentId}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (!commentResponse.ok) {
        alert('Failed to fetch comment details');
        return;
    }

    const comment = await commentResponse.json();
    const postId = comment.postId;

    // Delete the comment
    const deleteResponse = await fetch(`/posts/comments/${commentId}`, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (deleteResponse.ok) {
        alert('Comment deleted successfully');
        loadComments(postId);
    } else {
        alert('Failed to delete comment');
    }
}

// Function to show the create user form
function showCreateUserForm() {
    document.getElementById('createUserForm').style.display = 'block';
}

// hide the create user form
function hideCreateUserForm() {
    document.getElementById('createUserForm').style.display = 'none';
}

// handle form submission
async function createUser(event) {
    event.preventDefault();

    const firstName = document.getElementById('firstName').value;
    const lastName = document.getElementById('lastName').value;
    const email = document.getElementById('email_newuser').value;
    const password = document.getElementById('password_newuser').value;
    const phoneNumber = document.getElementById('phoneNumber').value;
    const address = document.getElementById('address').value;
    const userType = document.getElementById('userType').value;

    const userData = {
        firstName,
        lastName,
        email,
        password,
        phoneNumber,
        address,
        userType
    };

    // Send a POST request to the API
    try {
        const response = await fetch('/auth/signup', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userData)
        });

        if (response.ok) {
            alert('User created successfully!');
            hideCreateUserForm(); // Hide the form after successful creation
            loadUserList(); // Refresh the user list
        } else {
            const errorData = await response.json();
            alert(`Failed to create user: ${errorData.message || 'Unknown error'}`);
        }
    } catch (error) {
        console.error('Error creating user:', error);
        alert('An error occurred while creating the user.');
    }
}