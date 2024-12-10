# BarCodeQRCodeScannerCreator

**BarCodeQRCodeScannerCreator** is an Android application that enables users to scan and generate QR codes and barcodes. The app also stores scanning history and favorites using the Room database for easy access. It integrates several libraries for efficient performance, modern UI elements, and enhanced user experience.

## Key Features
- **QR & Barcode Scanning**: Scan various barcode formats (UPC, EAN, Code128) and QR codes using your device’s camera.
- **QR & Barcode Generation**: Create QR codes and barcodes from URLs, text, or other data inputs.
- **History & Favorites**: Store scanning history and favorite items using **Room** database for easy management and retrieval.
- **Lottie Animations**: Enhance the user experience with smooth and engaging animations.
  
## Libraries Used
The app utilizes several popular libraries to deliver its functionality:

- **ZXing (Zebra Crossing)**: For barcode and QR code scanning and generation.
  - `com.journeyapps:zxing-android-embedded:3.6.0`
  - `com.google.zxing:core:3.3.3`
- **Lottie**: To add beautiful animations to the app.
  - `com.airbnb.android:lottie:3.4.0`
- **Room Database**: For local data storage of scanning history and favorites.
  - `androidx.room:room-runtime:2.2.6`
- **Glide**: For image loading and caching.
  - `com.github.bumptech.glide:glide:4.11.0`
- **ViewPagerIndicator**: To implement smooth page navigation.
  - `com.github.JakeWharton:ViewPagerIndicator:2.4.1`
- **Material Design**: For modern UI components.
  - `com.google.android.material:material:1.4.0-alpha02`

## Installation

### Prerequisites
Ensure that you have the following installed on your system:
- [Android Studio](https://developer.android.com/studio) (version 4.0 or higher)
- Android SDK
- A connected Android device or an emulator for testing

### Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/BarCodeQRCodeScannerCreator.git
Open the project in Android Studio.
Sync the project with Gradle files by clicking File > Sync Project with Gradle Files.
Build and run the app on an emulator or physical device.
Usage
Scan QR & Barcodes: Use the built-in scanner to scan QR codes and barcodes.
Generate QR Codes & Barcodes: Input your data and generate a QR code or barcode.
Store Data: Add items to your favorites and view your scanning history, all stored in a local Room database.
License
This project is licensed under the MIT License - see the LICENSE file for details.

Acknowledgements
ZXing for barcode scanning and generation.
Lottie for smooth animations.
Room for local data storage.
Glide for image loading.

