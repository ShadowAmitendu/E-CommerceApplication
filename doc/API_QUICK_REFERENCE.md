# ⚡ API Quick Reference Cheatsheet

Base URL: `http://localhost:8080`  
Credentials: Always include cookies (`withCredentials: true` / `credentials: 'include'`).

---

## 🔑 Authentication (`/api/users`)

| Method | Endpoint | Access | Body Payload | Success Response |
|---|---|---|---|---|
| `POST` | `/api/users/register` | Public | `{ "name", "email", "phone", "password", "role" }` | `201 Created` (`"Register Successfully"`) |
| `POST` | `/api/users/login` | Public | `{ "email", "password" }` | `200 OK` (User JSON + Cookie) |
| `GET` | `/api/users/me` | Authenticated | *None* | `200 OK` (`{ "id", "name", "email", "role" }`) |
| `POST` | `/api/users/logout` | Authenticated | *None* | `200 OK` (`"LoggedOut!"`) |

---

## 📦 Products (`/api/products`)

| Method | Endpoint | Access | Body Payload | Success Response |
|---|---|---|---|---|
| `GET` | `/api/products` | Public | *None* | `200 OK` (Array of Product JSON) |
| `GET` | `/api/products/{id}` | Public | *None* | `200 OK` (Product JSON) |
| `POST` | `/api/products` | **Admin** | `{ "id", "name", "quantity", "price" }` | `201 Created` (`"Added Successfully!"`) |
| `PUT` | `/api/products/{id}` | **Admin** | `{ "name", "quantity", "price" }` | `200 OK` (`"Updated Successfully!"`) |
| `DELETE` | `/api/products/{id}` | **Admin** | *None* | `200 OK` (`"Deleted Successfully!"`) |

---

## 🛒 Shopping Cart (`/api/cart`)

| Method | Endpoint | Access | Body Payload | Success Response |
|---|---|---|---|---|
| `GET` | `/api/cart` | Authenticated | *None* | `200 OK` (`{ items: [...], totalItems, totalAmount }`) |
| `POST` | `/api/cart/add` | Authenticated | `{ "productId": "PROD-1", "quantity": 1 }` | `200 OK` (Updated Cart JSON) |
| `PUT` | `/api/cart/{itemId}` | Authenticated | `{ "quantity": 3 }` | `200 OK` (Updated Cart JSON) |
| `DELETE` | `/api/cart/{itemId}` | Authenticated | *None* | `200 OK` (Updated Cart JSON) |
| `DELETE` | `/api/cart` | Authenticated | *None* | `200 OK` (`"Cart cleared successfully!"`) |
| `POST` | `/api/cart/checkout` | Authenticated | *None* | `201 Created` (Array of placed Order JSON) |

---

## 📋 Orders (`/api/orders`)

| Method | Endpoint | Access | Body Payload | Success Response |
|---|---|---|---|---|
| `POST` | `/api/orders` | Authenticated | `{ "productId": "PROD-1", "quantity": 1 }` | `201 Created` (Single Order JSON) |
| `GET` | `/api/orders` | Authenticated | *None* | `200 OK` (Array of user orders, or all if Admin) |
| `GET` | `/api/orders/{id}` | Authenticated | *None* | `200 OK` (Single Order JSON) |
| `DELETE` | `/api/orders/{id}` | Authenticated | *None* | `200 OK` (`"Deleted Successfully!"` + restocks inventory) |

---

## ⚠️ Error Codes Quick Map

- **`400 Bad Request`**: Missing parameters, invalid inputs, negative numbers.
- **`401 Unauthorized`**: Session missing/expired. Redirect to login.
- **`403 Forbidden`**: Admin access required, or user trying to view another user's order.
- **`404 Not Found`**: ID not found.
- **`409 Conflict`**: Stock not enough for requested quantity.
