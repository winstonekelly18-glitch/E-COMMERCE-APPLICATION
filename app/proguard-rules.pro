# Preserve Data Models for Firebase Firestore & Gson Serialization
-keep class com.example.e_commerceapplication.models.** { *; }
-keep class com.example.e_commerceapplication.network.** { *; }
-keep class com.example.e_commerceapplication.database.** { *; }

# Keep Retrofit & OkHttp
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# Keep Room generated classes
-keep class * extends androidx.room.RoomDatabase