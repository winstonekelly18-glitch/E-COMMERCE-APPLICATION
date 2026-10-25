# ShopMpesa - Complete Android E-Commerce App with M-Pesa STK Push

ShopMpesa is a native Android retail store application built in **Java**, featuring Safaricom M-Pesa STK Push payment integration via a secure Python Flask REST API backend.

---

## Architecture Overview

- **Language:** Java (Android) | Python (Flask Backend)
- **UI:** Material Design 3 + XML Layouts
- **Architecture Pattern:** MVVM (Model-View-ViewModel) + Repository Pattern
- **Local DB:** Room Database (SQLite) for offline cart persistence
- **Central DB & Auth:** Firebase Firestore + Firebase Authentication
- **Backend API:** Python Flask REST API (holds Safaricom Daraja secrets)
- **Networking:** Retrofit 2 + OkHttp + Gson
- **Image Loading:** Glide

---

## Project Structure

```
ShopMpesa/
├── app/src/main/
│   ├── java/com/example/e_commerceapplication/
│   │   ├── ShopMpesaApp.java              # Application class (Theme initialization)
│   │   ├── MainActivity.java              # Product Catalogue & Search Grid
│   │   ├── activities/
│   │   │   ├── SplashActivity.java        # Session check & Role-based router
│   │   │   ├── LoginActivity.java         # Email & Biometric Sign In
│   │   │   ├── RegisterActivity.java      # Registration with Kenyan phone validation
│   │   │   ├── ForgotPasswordActivity.java# Reset password link trigger
│   │   │   ├── ProductDetailsActivity.java# Stock badge, carousel, reviews, wishlist
│   │   │   ├── CartActivity.java          # Room DB Cart manager
│   │   │   ├── CheckoutActivity.java      # Address, Nairobi fee, promo code & order creation
│   │   │   ├── MpesaPaymentActivity.java  # STK push initiation & 90s polling loop
│   │   │   ├── PaymentResultActivity.java # Success/Failure states & PDF receipt generator
│   │   │   ├── OrderHistoryActivity.java  # Past orders with status tabs
│   │   │   ├── OrderDetailsActivity.java  # Visual timeline stepper & receipt download
│   │   │   ├── ProfileActivity.java       # User profile editor & role display
│   │   │   ├── SettingsActivity.java      # Dark/Light/System theme & Biometric switch
│   │   │   ├── WishlistActivity.java      # Favorites screen
│   │   │   ├── ReviewsActivity.java       # Customer ratings & reviews
│   │   │   ├── AdminDashboardActivity.java# Today's sales, low stock cards, metrics
│   │   │   ├── AdminProductManagerActivity.java# Add/Edit/Delete products
│   │   │   └── AdminOrderManagerActivity.java  # Status change (Processing, Shipped, Completed)
│   │   ├── models/                        # User, Product, LocalCartItem, Order, OrderItem, Payment, etc.
│   │   ├── database/                      # Room AppDatabase, CartDao, LocalCartItem
│   │   ├── repository/                    # AuthRepository, ProductRepository, CartRepository, OrderRepository, PaymentRepository, AdminRepository
│   │   ├── viewmodels/                    # AuthViewModel, ProductViewModel, CartViewModel, OrderViewModel, PaymentViewModel, AdminViewModel
│   │   ├── network/                       # ApiService, RetrofitClient, DTOs
│   │   ├── utils/                         # ValidationUtils, ThemeUtils, PdfGenerator, BiometricHelper, NotificationHelper, Constants
│   │   └── services/                      # ShopFirebaseMessagingService (FCM)
│   └── res/
│       ├── values/                        # Light Theme & English Strings
│       ├── values-night/                  # Dark Theme Colors & Styles
│       └── values-sw/                     # Swahili Language Translations
├── backend/
│   ├── app.py                             # Flask server (/stkpush, /callback, /status, /simulate_callback)
│   ├── daraja.py                          # Safaricom OAuth, Base64 Password & STK Push request handler
│   ├── test_simulation.py                 # Simulation script for callback testing
│   ├── requirements.txt                   # Flask dependencies
│   └── .env.example                       # Environment secrets template
├── firestore.rules                        # Security rules restricting admin access & user permissions
└── proguard-rules.pro                     # Optimization & keeping data models
```

---

## Setup & Running Instructions

### 1. Flask Backend Setup
1. Navigate to the `backend/` folder:
   ```bash
   cd backend
   ```
2. Create virtual environment and install dependencies:
   ```bash
   python -m venv venv
   source venv/bin/activate  # On Windows: venv\Scripts\activate
   pip install -r requirements.txt
   ```
3. Copy `.env.example` to `.env` and fill in your Daraja credentials:
   ```bash
   cp .env.example .env
   ```
4. Start local Flask server:
   ```bash
   python app.py
   ```
5. For Daraja callback testing, expose port 5000 using ngrok:
   ```bash
   ngrok http 5000
   ```
   Update `DARAJA_CALLBACK_URL` in `.env` with your ngrok HTTPS URL.

### 2. Android App Setup
1. Open the project in Android Studio.
2. Ensure Firebase is linked and `google-services.json` is placed in `app/`.
3. In `RetrofitClient.java`, set `BASE_URL` to `http://10.0.2.2:5000/` for Android Emulator testing or your ngrok URL for physical device testing.
4. Sync Gradle and Run on an Android device or emulator (Min SDK 24+).

---

## Demonstration Answers to Demo Questions

1. **How is the total calculated?**
   - Unit price × quantity per item, added to delivery fee (KES 250 for Nairobi or 0 for Shop Collection), minus validated promo code discount.

2. **Why do Daraja credentials stay on the backend?**
   - To prevent decompilation attacks. Storing `Consumer Key`, `Consumer Secret`, and `Passkey` in the APK exposes them to reverse engineering. Keeping them on the backend guarantees secret isolation.

3. **Accepted Request vs. Completed Payment?**
   - An accepted request (`ResponseCode: 0`) means Safaricom accepted the request and sent the STK prompt to the handset. Payment is completed **only** when Safaricom posts a successful callback with `ResultCode: 0` and an M-Pesa receipt number.

4. **How is a callback matched to an order?**
   - The backend maps the unique `CheckoutRequestID` received in the callback payload to the `payments` document in Firestore, which stores `orderReference`.

5. **How are failed, delayed, or duplicate callbacks handled?**
   - **Failed/Cancelled:** Marked as `FAILED` or `CANCELLED` in `payments` and `orders` collections.
   - **Delayed:** The Android app polls `GET /api/mpesa/status/{orderRef}` every 5 seconds for up to 90s, updating the UI as soon as status changes.
   - **Duplicate:** Handled idempotently. If a callback for a `CheckoutRequestID` already marked `SUCCESS` arrives, the backend ignores it.

6. **How does stock update work?**
   - Stock is deducted **only** after payment success inside an atomic **Firestore Transaction**. It reduces `stockQuantity` and logs an `InventoryTransaction` document.

7. **How is admin access protected?**
   - Admin access is protected in two layers:
     1. **App Layer:** Role check (`role == "admin"`) on `SplashActivity`, `LoginActivity`, and `AdminDashboardActivity`.
     2. **Database Layer:** Firestore Security Rules enforce `isAdmin()` check for writing products or updating order status.

8. **How to deploy to production?**
   - Change `DARAJA_ENV` to `production` in `.env`.
   - Update `DARAJA_SHORTCODE` to production Paybill/Till number.
   - Deploy Flask backend to cloud host (AWS, Heroku, DigitalOcean) with SSL/TLS (HTTPS).
