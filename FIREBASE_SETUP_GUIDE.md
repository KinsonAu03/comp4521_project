# Firebase Setup Guide

## ✅ What's Already Done

1. ✅ **Root `build.gradle.kts`**: Added Google Services plugin version 4.4.4
2. ✅ **App `build.gradle.kts`**: Added Google Services plugin and Firebase dependencies
3. ✅ **Firebase BOM**: Updated to version 34.5.0
4. ✅ **Firebase Analytics**: Added to dependencies
5. ✅ **Firebase Messaging**: Added to dependencies

## 📋 Firebase Setup Steps

### Step 1: Verify `google-services.json` File

Your project already has a `google-services.json` file in `app/google-services.json`. 

**To verify it's correct:**
1. Open the file and check it contains:
   - Your project number
   - Package name: `com.group22.weatherForecastApp`
   - Valid Firebase configuration

**If you need to download/update it:**
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project
3. Click the gear icon ⚙️ → **Project Settings**
4. Scroll to **Your apps** section
5. Find your Android app (or create one with package name: `com.group22.weatherForecastApp`)
6. Download `google-services.json`
7. Replace the existing file in `app/google-services.json`

### Step 2: Add SHA-1 and SHA-256 Fingerprints to Firebase

**Why?** Firebase needs your app's SHA fingerprints for:
- Google Sign-In (if you plan to use authentication)
- App verification and security
- Dynamic Links (if you plan to use them)

**Your Debug Keystore Fingerprints:**
- **SHA-1**: `AE:2A:48:7F:1C:EB:44:42:04:7D:7B:89:E4:88:EE:52:2B:F9:C4:F9`
- **SHA-256**: `A7:E1:E8:F4:70:91:22:0C:52:E8:52:9C:C7:87:F7:55:1E:18:E4:D1:3D:9E:67:4A:00:54:5C:61:62:77:F9:5D`

**How to add them:**
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project: **comp4521project-46ca7**
3. Click the gear icon ⚙️ → **Project Settings**
4. Scroll down to **Your apps** section
5. Find your Android app (package: `com.group22.weatherForecastApp`)
6. Click **Add fingerprint** button
7. Add both SHA-1 and SHA-256 fingerprints (copy-paste from above)
8. Click **Save**

**Note:** If you plan to release the app, you'll also need to add the SHA fingerprints from your release keystore later.

### Step 3: Sync Gradle

1. In Android Studio, click **File → Sync Project with Gradle Files**
2. Or click **Sync Now** if you see a banner
3. Wait for the sync to complete

### Step 4: Verify Firebase is Working

After syncing, Firebase should be ready. You can verify by:

1. **Check for build errors**: Look at the Build output
2. **Initialize Firebase in code** (optional, for testing):

```kotlin
// In your Application class or MainActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.initialize

// Initialize (usually automatic, but you can verify)
Firebase.initialize(context)
```

## 🔥 Firebase Services Configured

### 1. Firebase Analytics ✅
- Automatically tracks app usage
- View reports in Firebase Console

### 2. Firebase Cloud Messaging ✅
- Ready for push notifications
- Will be used for weather alerts

## 📱 Testing Firebase Setup

### Test Analytics:
1. Run your app
2. Navigate around
3. Check Firebase Console → Analytics after a few hours

### Test Cloud Messaging (later):
1. Go to Firebase Console → Cloud Messaging
2. Send a test notification
3. Your app should receive it (after implementing notification handling)

## 🐛 Troubleshooting

### If you get "Plugin with id 'com.google.gms.google-services' not found":
- ✅ Already fixed - we added it to root `build.gradle.kts`

### If you get "google-services.json not found":
- Verify the file exists at `app/google-services.json`
- Re-download from Firebase Console if needed

### If build fails:
1. Clean project: **Build → Clean Project**
2. Rebuild: **Build → Rebuild Project**
3. Invalidate caches: **File → Invalidate Caches**

## ✅ You're All Set!

Once Gradle sync completes successfully, Firebase is fully configured. You can now:
- Start using Firebase Analytics
- Implement push notifications for weather alerts
- Access other Firebase services as needed

---

**Next Steps:**
1. ✅ Add SHA-1 and SHA-256 fingerprints to Firebase Console (see Step 2 above)
2. Sync Gradle in Android Studio
3. Verify no build errors
4. Start building your weather app features!

