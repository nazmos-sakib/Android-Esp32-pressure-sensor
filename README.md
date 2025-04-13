# Android-Esp32-pressure-sensor
# Android Esp32 BluetoothChat App 📱
This Android app connects to an ESP-32 microcontroller via Classical Bluetooth (SPP) and facilitates message exchange. The ESP-32 acts as a bridge between serial communication and Bluetooth, sending and receiving data as byte streams. This project highlights the use of Jetpack Compose for the UI, Dagger Hilt for dependency injection, and Kotlin Coroutines with StateFlow to handle asynchronous operations and state management.

![kotlin] ![Jetpack] ![apiLevel]

## Table of Contents

* [Overview](#bluetooth-chat)
* [Features](#features)
* [Demo](#demo)
* [Setup](#setup)
* [Design Patterns & Architecture](#design-patterns--architecture)
* [Tech Stack](#tech-stack)
* [Contributing](#contributing)


## Features

  * [ESP-32 Bluetooth Communication](): Connect to an ESP-32 via Classical Bluetooth (SPP).
  * [Serial Data Transfer](): Send and receive serial data as byte streams.
  * [Device Discovery](): Scan for nearby Bluetooth devices.
  * [Real-Time Data Transfer](): Exchange sensor data between Android and ESP-32.
  * [Room Database](): Sensored data are stored with Room Database.
  * [Jetpack Compose UI](): Modern, declarative UI with Compose.
  * [Dagger Hilt for DI](): Simplifies dependency injection in Android.
  * [Kotlin Coroutines & StateFlow](): Manage asynchronous data flow and state updates.
  * [MVVM & Clean Architecture](): Maintainable, scalable, and testable code structure.


## Demo
<div style="display: flex; align: left;">
    <img  src="app/src/main/res/raw/ss_1.jpg" alt="Image 1" style=" border: 2px solid #ccc; border-radius: 8px; align: left;" width="400px" height="500px">
    <img  src="app/src/main/res/raw/ss_2.jpg" alt="Image 1" style=" border: 2px solid #ccc; border-radius: 8px; align: left;" width="300px" height="500px">
    <img  src="app/src/main/res/raw/ss_3.jpg" alt="Image 1" style=" border: 2px solid #ccc; border-radius: 8px; align: left;" width="300px" height="500px">
</div>


<div style="display: flex; align: left;">
    <img  src="app/src/main/res/raw/ss_4.jpg" alt="Image 1" style=" border: 2px solid #ccc; border-radius: 8px; align: left;" width="300px" height="500px">
    <img  src="app/src/main/res/raw/ss_5.jpg" alt="Image 1" style=" border: 2px solid #ccc; border-radius: 8px; align: left;" width="300px" height="500px">
    <img  src="app/src/main/res/raw/ss_6.jpg" alt="Image 1" style=" border: 2px solid #ccc; border-radius: 8px; align: left;" width="300px" height="500px">
</div>

## Android Build

> compileSdk 34 <br>
> minSdk 26 <br>
> targetSdk 34 <br>
> Java version 17

    
## Permissions    
   ```XML
   <uses-permission android:name="android.permission.BLUETOOTH" android:maxSdkVersion="30"/>
    <uses-permission android:name="android.permission.BLUETOOTH_SCAN" android:usesPermissionFlags="neverForLocation"
        tools:targetApi="s" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
    <uses-permission
        android:name="android.permission.ACCESS_FINE_LOCATION"
        tools:ignore="CoarseFineLocation" />
    <uses-feature android:name="android.hardware.bluetooth" />
   ```


## Setup

To run this project, follow these steps:

### ESP 32 setup
Arduino IDE was used to program ESP 32 microcontroller
1. find the right port and esp controller model in the IDE
2. insert the folloing code
   ```c
   
    #include "BluetoothSerial.h"

    String device_name = "naz's-esp";

    // Check if Bluetooth is available
    #if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
    #error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
    #endif

    // Check Serial Port Profile
    #if !defined(CONFIG_BT_SPP_ENABLED)
    #error Serial Port Profile for Bluetooth is not available or not enabled. It is only available for the ESP32 chip.
    #endif

    BluetoothSerial SerialBT;

    #define BODY 34
    #define HEAD 35

    void setup() {
      Serial.begin(115200);
      SerialBT.begin(device_name);  //Bluetooth device name
      //SerialBT.deleteAllBondedDevices(); // Uncomment this to delete paired devices; Must be called after begin
      Serial.printf("The device with name \"%s\" is started.\nNow you can pair it with Bluetooth!\n", device_name.c_str());
    }

    void loop() {
      uint16_t headReading = analogRead(HEAD);
      uint16_t bodyReading = analogRead(BODY);


      if (headReading > 150 || bodyReading > 150) {

        //String res = headReading;
        //outgoingReadings.bodyReading = bodyReading;

      

        //SerialBT.write(String(headReading).c_str());
        //SerialBT.println(headReading); 
        SerialBT.println(String(headReading) + "," + String(bodyReading));

        updateDisplay(headReading, bodyReading);

        Serial.println("--------------------");
      }

      
      delay(100);
    }


    void updateDisplay(int headReading, int boadyReading) {
      Serial.print("HEAD sensor value = ");
      Serial.println(headReading);  // print the raw analog reading

      if (headReading > 100 && headReading < 1500)  // from 100 to 1499
        Serial.println("headReading -> light touch");
      else if (headReading >= 1500 && headReading < 2500)  // from 1500 to 2499
        Serial.println("headReading -> light squeeze");
      else if (headReading >= 2500 && headReading < 3500)  // from 2500 to 3499
        Serial.println("headReading -> medium squeeze");
      else if (headReading >= 3500)  // from 3500 to 5000
        Serial.println("headReading -> big squeeze");


      Serial.print("BODY sensor value = ");
      Serial.println(boadyReading);  // print the raw analog reading

      if (boadyReading > 100 && boadyReading < 1500)  // from 100 to 1499
        Serial.println("boadyReading -> light touch");
      else if (boadyReading >= 1500 && boadyReading < 2500)  // from 1500 to 2499
        Serial.println("boadyReading -> light squeeze");
      else if (boadyReading >= 2500 && boadyReading < 3500)  // from 2500 to 3499
        Serial.println("boadyReading -> medium squeeze");
      else if (boadyReading >= 3500)  // from 3500 to 5000
        Serial.println("boadyReading -> big squeeze");
    }
   ```
3. Upload the code to the microcontroller

### Android app Setup
1. Clone the repository:
```bash
git clone https://github.com/nazmos-sakib/Android-Esp32-pressure-sensor.git
```
2. Open the project in Android Studio.
3. Build the project:
   * Ensure the required SDK versions are installed.
   * The app targets Android SDK 34 and uses Kotlin 1.5.1. and Java version 17
4. Run the app:
   * Install the app on your Android device.
   * Ensure Bluetooth is enabled.
   * Use the app to scan for and connect to your ESP-32 device.



## Design Patterns & Architecture

### MVVM (Model-View-ViewModel)

The MVVM (Model-View-ViewModel) pattern is used in this project to cleanly separate the user interface (UI) from the business logic, making the app easier to maintain and test. Here's how each layer contributes to the app:

* **Model**: Contains the data-related logic, such as device discovery and Bluetooth connection handling. This layer interacts with the Android Bluetooth Adapter API to manage Bluetooth devices and connections.
* **ViewModel**: Acts as the bridge between the Model and the View. It holds UI-related data that survives configuration changes (like screen rotations). In this app, the ViewModel manages:
  
   * Device scanning results.
The list of paired devices.
Connection statuses and messaging logic.
The ViewModel uses LiveData to expose data to the View, allowing the app to respond to changes without requiring manual updates.
   
   * View: This represents the UI, including the activities and fragments. The View observes the ViewModel for changes and updates the UI accordingly. It doesn’t handle any business logic, making it easier to keep the UI layer clean and focused only on presenting the data.
This approach ensures that the app’s business logic is decoupled from the UI, which makes the code more maintainable and testable.


### Clean Architecture

The project is built following Clean Architecture principles, which helps to ensure a clear separation of concerns. Clean Architecture divides the codebase into multiple layers, allowing for a modular and scalable application. Here’s how Clean Architecture is applied in this project:

1. **Domain Layer**: This is the core of the application. It contains the business logic, including entities (such as Bluetooth device data) and use cases (e.g., "scan for devices," "connect to a device"). The domain layer is independent of any frameworks, UI, or data sources, making it reusable and testable.

2. **Data Layer**: The data layer contains repositories that implement the business logic defined in the domain layer. It manages data retrieval and communication with external systems (in this case, the Android Bluetooth Adapter API). By using repositories, the data layer abstracts away the details of the data sources, making the domain layer completely unaware of how data is fetched or stored.

3. **Presentation Layer**: This includes the ViewModels and UI components. The ViewModel communicates with the domain layer to retrieve or manipulate data and then provides this data to the UI layer. The UI layer (View) observes the ViewModel and updates itself automatically when the data changes, thanks to LiveData.


## Tech Stack

   * [**MVVM** Architecture](): Used for separating concerns between the UI and business logic.
   * [**Kotlin**](): Primary programming language.
   * [**Android Bluetooth Adapter**](): For device discovery, pairing, and message transfer.
   * [**Android Studio**](): Development environment.
   * [**Coroutines**](): For managing asynchronous Bluetooth operations.
   * [**Jetpack compose**](): UI is implement with jetpack composer.
   * [**Room Database**](): Store sensor data.
   * [**Dagger-Hilt**](): provides a standard way for dependency injection into the application.
   * [**Bluetooth Adapter API**](): Used for Bluetooth communication.
   * [**ESP 32 microcontroller**](): Used to create a bridge between serial communication and Classical Bluetooth.
   * [**C Programming**](): Used for programming the ESP-32, allowing it to communicate with the Android app over Bluetooth and manage serial data transfer.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request or open an Issue if you find any bugs or have feature requests.











[apiLevel]: https://img.shields.io/badge/API-26+-brightgreen.svg
[kotlin]: https://img.shields.io/badge/Kotlin-purple
[Jetpack]: https://img.shields.io/badge/Jetpack-green
