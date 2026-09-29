# 🛍️ E-Commerce Backend — Frontend Integration & API Documentation

Welcome to the **E-Commerce Application** backend documentation! This guide is specifically prepared for the **Frontend Development Team** to enable rapid, seamless integration with our Spring Boot REST API.

---

## 📑 Table of Contents
1. [Project Overview & Architecture](#1-project-overview--architecture)
2. [Connection & Authentication (Critical for Frontend)](#2-connection--authentication-critical-for-frontend)
3. [User Roles & Permissions](#3-user-roles--permissions)
4. [Standard API Error Format](#4-standard-api-error-format)
5. [Complete API Reference](#5-complete-api-reference)
   - [Authentication & Users (`/api/users`)](#51-authentication--user-endpoints)
   - [Product Catalog (`/api/products`)](#52-product-catalog-endpoints)
   - [Shopping Cart (`/api/cart`)](#53-shopping-cart-endpoints)
   - [Orders & Transactions (`/api/orders`)](#54-order-management-endpoints)
   - [Customer Addresses (`/api/addresses`)](#55-address-management-endpoints)
6. [Ready-to-Use Frontend Code Snippets (Fetch & Axios)](#6-ready-to-use-frontend-code-snippets)
7. [End-to-End User Workflows](#7-end-to-end-user-workflows)

---

## 1. Project Overview & Architecture

### What is this project?
This application is a full-featured e-commerce backend built with **Java 17**, **Spring Boot**, **Spring Data JPA**, and **MySQL**. It handles user identity, role-based authorization, inventory control, shopping cart lifecycle, and transactional order fulfillment.

### Key Business Rules
1. **Product Inventory & Stock Tracking:**
   - Products maintain real-time inventory counts (`quantity`).
   - Adding to cart or checking out validates stock availability. If stock is exceeded, the server returns `409 Conflict`.
2. **Persistent Shopping Cart:**
   - Carts are stored in the database per user account.
   - Items added on one browser or device remain in the cart across sessions.
   - Adding an existing product automatically increments the quantity.
3. **Atomic Cart Checkout:**
   - When a user checks out their cart (`POST /api/cart/checkout`), the backend verifies stock for all items, deducts inventory, creates individual `Order` records, and clears the cart inside a single transactional boundary (`@Transactional`). If any item is out of stock, the entire checkout is aborted.
4. **Order Cancellation & Automatic Restocking:**
   - When an order is deleted or cancelled (`DELETE /api/orders/{id}`), the purchased quantity is **automatically refunded back into product stock**.

### Database Entity Relationships
```
              ┌───< (Many) [Address]
              │
[User] (1) ───┼───< (Many) [CartItem] >─── (Many) [Product]
              │                                      │
              └─────────< (Many) [Order]    >────────┘
```

---

## 2. Connection & Authentication (Critical for Frontend)

### Server Details
- **Base URL:** `http://localhost:8080`
- **Protocols:** RESTful JSON (`Content-Type: application/json`)

### CORS Configuration
Cross-Origin Resource Sharing is configured on the backend (`WebConfig.java`) to accept requests from:
- `http://localhost:*` (React, Next.js, Vite, Vue, Angular on any port, e.g., 3000, 5173, 8000).
- Allowed Methods: `GET`, `POST`, `PUT`, `DELETE`, `PATCH`, `OPTIONS`
- `AllowCredentials: true` (Required for cookie transmission)

### Session Cookie (`JSESSIONID`) Authentication
The backend uses standard Spring `HttpSession` cookie-based authentication.
1. When a user logs in (`POST /api/users/login`), the backend responds with a `Set-Cookie` header:
   ```http
   Set-Cookie: JSESSIONID=ABCD1234EF5678; Path=/; HttpOnly; SameSite=Lax
   ```
2. **Frontend Requirement:** The frontend client **MUST** configure all HTTP requests to send credentials.
   - In `fetch()`: pass `{ credentials: 'include' }`
   - In `axios`: set `axios.defaults.withCredentials = true;`

> ⚠️ **Important:** If `credentials: 'include'` is omitted in your frontend calls, the browser will strip the cookie and all protected endpoints (`/api/users/me`, `/api/cart`, `/api/orders`, `/api/addresses`) will return `401 Unauthorized`.

---

## 3. User Roles & Permissions

Users are assigned one of two roles during registration:
- **`USER`** (or `CUSTOMER`):
  - Can view products
  - Can manage their own shopping cart
  - Can place orders and view/cancel their own orders
  - Can view and update their profile (`GET|PUT /api/users/me`)
  - Can manage multiple delivery addresses with a default address (`/api/addresses`)
- **`ADMIN`**:
  - Full platform privileges
  - Can view all registered users platform-wide (`GET /api/users`)
  - Can create, update, and delete products in the catalog
  - Can view all orders placed across all users on the platform
  - Can cancel/restock any order

---

## 4. Standard API Error Format

Every error returned by the backend follows a uniform JSON structure defined in `GlobalExceptionHandler.java`:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Product ID is required ('productId')",
  "timestamp": "2026-09-28T21:30:00"
}
```

### Common HTTP Status Codes
| Status Code | Label | Cause | Frontend Action |
|---|---|---|---|
| `200 OK` | Success | Request completed successfully | Render data |
| `201 Created` | Created | Resource created (e.g. order placed, product added) | Show success toast |
| `400 Bad Request` | Bad Request | Validation failure (missing field, negative number) | Display `error.message` on input form |
| `401 Unauthorized` | Unauthorized | Session expired or not logged in | Redirect user to Login modal/page |
| `403 Forbidden` | Forbidden | Insufficient role (e.g. non-admin accessing admin route) | Show permission denied message |
| `404 Not Found` | Not Found | Item, product, or user does not exist | Show 404 / empty state |
| `409 Conflict` | Conflict | Insufficient inventory stock | Prompt user to reduce ordered quantity |
| `500 Server Error` | Server Error | Unhandled server exception | Show generic server error alert |

---

## 5. Complete API Reference

### 5.1 Authentication & User Endpoints

#### Register User
Creates a new customer or admin account.
- **URL:** `POST /api/users/register`
- **Access:** Public
- **Headers:** `Content-Type: application/json`
- **Request Body:**
  ```json
  {
    "name": "Jane Doe",
    "email": "jane@example.com",
    "phone": "9876543210",
    "password": "Password123",
    "role": "USER"
  }
  ```
  *(Role can be `"USER"` or `"ADMIN"`)*
- **Response:** `201 Created`
  ```text
  Register Successfully
  ```

#### Login User
Authenticates credentials and establishes a session cookie.
- **URL:** `POST /api/users/login`
- **Access:** Public
- **Headers:** `Content-Type: application/json`
- **Request Body:**
  ```json
  {
    "email": "jane@example.com",
    "password": "Password123"
  }
  ```
- **Response:** `200 OK` (Sets `JSESSIONID` cookie)
  ```json
  {
    "id": 1,
    "name": "Jane Doe",
    "email": "jane@example.com",
    "phone": "9876543210",
    "role": "USER"
  }
  ```

#### Get Current Logged-in Profile
Checks if an active session exists and returns the current user profile.
- **URL:** `GET /api/users/me`
- **Access:** Authenticated
- **Response `200 OK` (Logged In):**
  ```json
  {
    "id": 1,
    "name": "Jane Doe",
    "email": "jane@example.com",
    "role": "USER"
  }
  ```
- **Response `401 Unauthorized` (Not Logged In):**
  ```json
  {
    "status": 401,
    "error": "Unauthorized",
    "message": "Login required to view profile",
    "timestamp": "2026-09-28T21:30:00"
  }
  ```

#### Update Profile
Updates profile details (name, phone, password) for the currently logged-in user.
- **URL:** `PUT /api/users/me` *(alias: `/api/users/profile`)*
- **Access:** Authenticated
- **Headers:** `Content-Type: application/json`
- **Request Body:**
  ```json
  {
    "name": "Jane Smith",
    "phone": "9876543210",
    "currentPassword": "Password123",
    "newPassword": "NewPassword456"
  }
  ```
  *(All fields are optional. `currentPassword` is only required when changing `newPassword`)*
- **Response:** `200 OK`
  ```json
  {
    "id": 1,
    "name": "Jane Smith",
    "email": "jane@example.com",
    "phone": "9876543210",
    "role": "USER"
  }
  ```

#### Get All Users (Admin Only)
Retrieves all registered users across the platform. Accessible only by accounts with the `ADMIN` role.
- **URL:** `GET /api/users` *(aliases: `/api/users/all`, `/api/users/view`)*
- **Access:** Admin Only (`userRole == ADMIN`)
- **Response `200 OK`:**
  ```json
  [
    {
      "id": 1,
      "name": "Jane Smith",
      "email": "jane@example.com",
      "phone": "9876543210",
      "role": "USER"
    },
    {
      "id": 2,
      "name": "System Admin",
      "email": "admin@example.com",
      "phone": "1234567890",
      "role": "ADMIN"
    }
  ]
  ```
- **Response `403 Forbidden` (If non-admin):**
  ```json
  {
    "status": 403,
    "error": "Forbidden",
    "message": "Admin access only",
    "timestamp": "2026-09-28T21:30:00"
  }
  ```

#### Get User by ID
Retrieves details of an individual user by primary key ID.
- **URL:** `GET /api/users/{id}`
- **Path Variable:** `id` (e.g. `1`)
- **Access:** Authenticated (Owner of the account or Admin)
- **Response:** `200 OK`
  ```json
  {
    "id": 1,
    "name": "Jane Smith",
    "email": "jane@example.com",
    "phone": "9876543210",
    "role": "USER"
  }
  ```

#### Logout
Destroys the session on the server and unbinds stored user state.
- **URL:** `POST /api/users/logout` *(or `GET`)*
- **Access:** Authenticated
- **Response:** `200 OK`
  ```text
  LoggedOut!
  ```

---

### 5.2 Product Catalog Endpoints

#### Get All Products
Retrieves the entire catalog.
- **URL:** `GET /api/products` *(alias: `/api/products/view`)*
- **Access:** Public
- **Response:** `200 OK`
  ```json
  [
    {
      "id": "PROD-101",
      "name": "Wireless Noise-Canceling Headphones",
      "quantity": 25,
      "price": 149.99
    },
    {
      "id": "PROD-102",
      "name": "Mechanical Keyboard",
      "quantity": 12,
      "price": 89.99
    }
  ]
  ```

#### Get Product by ID
Retrieves a single product.
- **URL:** `GET /api/products/{id}`
- **Path Variable:** `id` (e.g. `PROD-101`)
- **Access:** Public
- **Response:** `200 OK`
  ```json
  {
    "id": "PROD-101",
    "name": "Wireless Noise-Canceling Headphones",
    "quantity": 25,
    "price": 149.99
  }
  ```

#### Create Product (Admin Only)
Adds a new product to the catalog.
- **URL:** `POST /api/products` *(alias: `/api/products/add`)*
- **Access:** Admin Only (`userRole == ADMIN`)
- **Headers:** `Content-Type: application/json`
- **Request Body:**
  ```json
  {
    "id": "PROD-103",
    "name": "Ergonomic Office Chair",
    "quantity": 10,
    "price": 199.99
  }
  ```
- **Response:** `201 Created`
  ```text
  Added Successfully!
  ```

#### Update Product (Admin Only)
Modifies existing product details.
- **URL:** `PUT /api/products/{id}` *(alias: `/api/products/update/{id}`)*
- **Path Variable:** `id`
- **Access:** Admin Only
- **Request Body:**
  ```json
  {
    "name": "Ergonomic Office Chair Pro",
    "quantity": 15,
    "price": 219.99
  }
  ```
- **Response:** `200 OK`
  ```text
  Updated Successfully!
  ```

#### Delete Product (Admin Only)
Removes a product from the database.
- **URL:** `DELETE /api/products/{id}` *(alias: `/api/products/delete/{id}`)*
- **Path Variable:** `id`
- **Access:** Admin Only
- **Response:** `200 OK`
  ```text
  Deleted Successfully!
  ```

---

### 5.3 Shopping Cart Endpoints

#### View Cart
Fetches the current user's cart, items, subtotals, and total price.
- **URL:** `GET /api/cart` *(alias: `/api/cart/view`)*
- **Access:** Authenticated
- **Response:** `200 OK`
  ```json
  {
    "items": [
      {
        "id": 1,
        "productId": "PROD-101",
        "productName": "Wireless Noise-Canceling Headphones",
        "unitPrice": 149.99,
        "quantity": 2,
        "subtotal": 299.98,
        "availableStock": 25
      },
      {
        "id": 2,
        "productId": "PROD-102",
        "productName": "Mechanical Keyboard",
        "unitPrice": 89.99,
        "quantity": 1,
        "subtotal": 89.99,
        "availableStock": 12
      }
    ],
    "totalItems": 3,
    "totalAmount": 389.97
  }
  ```

#### Add Item to Cart
Adds a product to the user's cart. If the product is already in the cart, it **automatically increments the existing quantity** and validates inventory stock.
- **URL:** `POST /api/cart/add` *(aliases: `/api/cart`, `/api/cart/items`)*
- **Access:** Authenticated
- **Headers:** `Content-Type: application/json`
- **Request Body:**
  ```json
  {
    "productId": "PROD-101",
    "quantity": 2
  }
  ```
- **Response:** `200 OK` (Returns the updated `CartResponse` object)

#### Update Item Quantity in Cart
Updates the quantity for a specific cart row (e.g. via `+` / `-` buttons).
- **URL:** `PUT /api/cart/{itemId}` *(aliases: `/api/cart/items/{itemId}`, `/api/cart/update/{itemId}`)*
- **Path Variable:** `itemId` (e.g. `1`)
- **Headers:** `Content-Type: application/json`
- **Request Body:**
  ```json
  {
    "quantity": 3
  }
  ```
  *(Note: Setting `quantity: 0` automatically removes the item from the cart).*
- **Response:** `200 OK` (Returns the updated `CartResponse` object)

#### Remove Item from Cart
Removes a specific item row from the cart.
- **URL:** `DELETE /api/cart/{itemId}` *(alias: `/api/cart/items/{itemId}`)*
- **Path Variable:** `itemId` (e.g. `1`)
- **Access:** Authenticated
- **Response:** `200 OK` (Returns the updated `CartResponse` object)

#### Clear Cart
Removes all items from the user's cart.
- **URL:** `DELETE /api/cart` *(alias: `/api/cart/clear`)*
- **Access:** Authenticated
- **Response:** `200 OK`
  ```text
  Cart cleared successfully!
  ```

#### Cart Checkout (Cart-to-Order)
Converts all items in the shopping cart into customer orders atomically:
1. Validates stock for all cart items.
2. Decrements product inventory counts.
3. Generates persistent `Order` records.
4. Clears the shopping cart.
- **URL:** `POST /api/cart/checkout`
- **Access:** Authenticated
- **Response:** `201 Created`
  ```json
  [
    {
      "id": 101,
      "quantity": 2,
      "orderDate": "2026-09-28T21:35:00",
      "product": {
        "id": "PROD-101",
        "name": "Wireless Noise-Canceling Headphones",
        "quantity": 23,
        "price": 149.99
      },
      "user": {
        "id": 1,
        "name": "Jane Doe",
        "email": "jane@example.com",
        "phone": "9876543210",
        "role": "USER"
      }
    }
  ]
  ```

---

### 5.4 Order Management Endpoints

#### Place Direct Order ("Buy Now")
Places an immediate single-product order without needing to use the cart.
- **URL:** `POST /api/orders` *(alias: `/api/orders/add`)*
- **Access:** Authenticated
- **Headers:** `Content-Type: application/json`
- **Request Body:**
  ```json
  {
    "productId": "PROD-101",
    "quantity": 1
  }
  ```
- **Response:** `201 Created` (Returns single `Order` JSON object)

#### Get Orders
- **URL:** `GET /api/orders` *(alias: `/api/orders/view`)*
- **Access:** Authenticated
- **Behavior:**
  - Standard user (`USER`): Returns **only orders placed by the current user**.
  - Admin (`ADMIN`): Returns **all orders across all users platform-wide**.
- **Response:** `200 OK` (Array of `Order` objects)

#### Get Single Order by ID
- **URL:** `GET /api/orders/{id}`
- **Path Variable:** `id` (e.g. `101`)
- **Access:** Authenticated (Owner of the order or Admin)
- **Response:** `200 OK` (Single `Order` object)

#### Cancel / Delete Order
Cancels an order and **automatically refunds the purchased quantity back into product inventory stock**.
- **URL:** `DELETE /api/orders/{id}` *(alias: `/api/orders/delete/{id}`)*
- **Path Variable:** `id` (e.g. `101`)
- **Access:** Authenticated (Owner of the order or Admin)
- **Response:** `200 OK`
  ```text
  Deleted Successfully!
  ```

---

### 5.5 Address Management Endpoints

Customers can store multiple delivery addresses. One address is designated as the **default address** (`isDefault: true`).

**Business Rules:**
- The first address created by a customer automatically becomes the default address.
- When an address is marked as default, any other existing default address for that user is automatically switched to non-default (`isDefault: false`).
- If a default address is deleted, the earliest remaining address is promoted to default automatically.

#### Get All Addresses
Retrieves all shipping addresses for the logged-in customer, ordered with the default address first.
- **URL:** `GET /api/addresses` *(alias: `/api/addresses/view`)*
- **Access:** Authenticated
- **Response:** `200 OK`
  ```json
  [
    {
      "id": 1,
      "userId": 1,
      "fullName": "Jane Doe",
      "phone": "9876543210",
      "street": "123 Elm Street, Apt 4B",
      "city": "Springfield",
      "state": "IL",
      "postalCode": "62701",
      "country": "USA",
      "default": true
    },
    {
      "id": 2,
      "userId": 1,
      "fullName": "Jane Doe (Office)",
      "phone": "9876543210",
      "street": "789 Corporate Plaza, Suite 100",
      "city": "Springfield",
      "state": "IL",
      "postalCode": "62704",
      "country": "USA",
      "default": false
    }
  ]
  ```

#### Add New Address
Creates a new address record. If `isDefault` is `true` or this is the user's first address, it becomes default.
- **URL:** `POST /api/addresses` *(alias: `/api/addresses/add`)*
- **Access:** Authenticated
- **Headers:** `Content-Type: application/json`
- **Request Body:**
  ```json
  {
    "fullName": "Jane Doe",
    "phone": "9876543210",
    "street": "123 Elm Street, Apt 4B",
    "city": "Springfield",
    "state": "IL",
    "postalCode": "62701",
    "country": "USA",
    "isDefault": true
  }
  ```
- **Response:** `201 Created` (Returns created `AddressResponse` object)

#### Get Single Address by ID
Retrieves details of a specific address.
- **URL:** `GET /api/addresses/{id}`
- **Path Variable:** `id` (e.g. `1`)
- **Access:** Authenticated (Owner of address or Admin)
- **Response:** `200 OK`

#### Update Address
Modifies fields of an existing address.
- **URL:** `PUT /api/addresses/{id}` *(alias: `/api/addresses/update/{id}`)*
- **Path Variable:** `id`
- **Access:** Authenticated (Owner or Admin)
- **Request Body:**
  ```json
  {
    "fullName": "Jane Doe",
    "phone": "9876543210",
    "street": "123 Elm Street, Suite 5C",
    "city": "Springfield",
    "state": "IL",
    "postalCode": "62701",
    "country": "USA",
    "isDefault": false
  }
  ```
- **Response:** `200 OK`

#### Set Default Address
Promotes an address to be the primary default address for the user, demoting any previously default address.
- **URL:** `PATCH /api/addresses/{id}/default` *(or `PUT /api/addresses/{id}/default`)*
- **Path Variable:** `id` (e.g. `2`)
- **Access:** Authenticated (Owner or Admin)
- **Response:** `200 OK` (Returns updated `AddressResponse` object with `default: true`)

#### Delete Address
Deletes an address record. If this address was default, the next remaining address becomes default.
- **URL:** `DELETE /api/addresses/{id}` *(alias: `/api/addresses/delete/{id}`)*
- **Path Variable:** `id`
- **Access:** Authenticated (Owner or Admin)
- **Response:** `200 OK`
  ```text
  Address deleted successfully!
  ```

---

## 6. Ready-to-Use Frontend Code Snippets

### Setup A: Using Vanilla JavaScript `fetch`
```javascript
const API_BASE_URL = 'http://localhost:8080';

// Universal request helper that automatically passes cookies and parses JSON/text
async function apiRequest(endpoint, options = {}) {
  const config = {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
    // CRITICAL: Required for session cookies (JSESSIONID) across requests
    credentials: 'include',
  };

  const response = await fetch(`${API_BASE_URL}${endpoint}`, config);
  const rawText = await response.text();
  
  let data;
  try {
    data = rawText ? JSON.parse(rawText) : null;
  } catch {
    data = rawText; // Handles plain text responses
  }

  if (!response.ok) {
    const errorMsg = data?.message || data || `HTTP error ${response.status}`;
    const error = new Error(errorMsg);
    error.status = response.status;
    error.data = data;
    throw error;
  }

  return data;
}

// Example API calls:
export const api = {
  // Auth & Profile
  login: (email, password) => apiRequest('/api/users/login', { method: 'POST', body: JSON.stringify({ email, password }) }),
  getProfile: () => apiRequest('/api/users/me', { method: 'GET' }),
  updateProfile: (profileData) => apiRequest('/api/users/me', { method: 'PUT', body: JSON.stringify(profileData) }),
  getAllUsers: () => apiRequest('/api/users', { method: 'GET' }), // Admin only
  logout: () => apiRequest('/api/users/logout', { method: 'POST' }),

  // Addresses
  getAddresses: () => apiRequest('/api/addresses', { method: 'GET' }),
  getAddressById: (id) => apiRequest(`/api/addresses/${id}`, { method: 'GET' }),
  addAddress: (addressData) => apiRequest('/api/addresses', { method: 'POST', body: JSON.stringify(addressData) }),
  updateAddress: (id, addressData) => apiRequest(`/api/addresses/${id}`, { method: 'PUT', body: JSON.stringify(addressData) }),
  setDefaultAddress: (id) => apiRequest(`/api/addresses/${id}/default`, { method: 'PATCH' }),
  deleteAddress: (id) => apiRequest(`/api/addresses/${id}`, { method: 'DELETE' }),

  // Products
  getProducts: () => apiRequest('/api/products'),
  
  // Cart
  getCart: () => apiRequest('/api/cart'),
  addToCart: (productId, quantity = 1) => apiRequest('/api/cart/add', { method: 'POST', body: JSON.stringify({ productId, quantity }) }),
  updateCartItem: (itemId, quantity) => apiRequest(`/api/cart/${itemId}`, { method: 'PUT', body: JSON.stringify({ quantity }) }),
  removeCartItem: (itemId) => apiRequest(`/api/cart/${itemId}`, { method: 'DELETE' }),
  checkoutCart: () => apiRequest('/api/cart/checkout', { method: 'POST' }),

  // Orders
  getOrders: () => apiRequest('/api/orders'),
  cancelOrder: (orderId) => apiRequest(`/api/orders/${orderId}`, { method: 'DELETE' }),
};
```

---

### Setup B: Using `axios`
```javascript
import axios from 'axios';

export const apiClient = axios.create({
  baseURL: 'http://localhost:8080',
  // CRITICAL: Tells Axios to send and receive cookies with cross-origin requests
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Response interceptor to handle session expiration (401)
apiClient.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response?.status === 401) {
      console.warn('Session expired or unauthorized. Redirect to login.');
      // Optional: window.location.href = '/login';
    }
    const message = error.response?.data?.message || error.message;
    return Promise.reject(new Error(message));
  }
);
```

---

## 7. End-to-End User Workflows

### Workflow 1: Customer Shopping Flow
```
1. Frontend calls GET /api/products               ──> Render catalog grid
2. Customer clicks "Add to Cart"
   Frontend calls POST /api/cart/add              ──> Updates cart count badge in navbar
3. Customer navigates to Cart page
   Frontend calls GET /api/cart                   ──> Renders items, item subtotals, and total
4. Customer adjusts quantity (+ / -)
   Frontend calls PUT /api/cart/{itemId}          ──> Updates row subtotal and total amount
5. Customer clicks "Place Order"
   Frontend calls POST /api/cart/checkout         ──> Cart emptied, stock decremented, orders created
6. Frontend navigates to My Orders page
   Frontend calls GET /api/orders                 ──> Displays order history
```

### Workflow 2: Order Cancellation & Inventory Refund
```
1. Customer clicks "Cancel Order" on My Orders page
2. Frontend calls DELETE /api/orders/{orderId}
3. Backend:
   - Restores purchased quantity back to the product stock in inventory
   - Removes the order record
4. Frontend shows success toast and refreshes order history
```
