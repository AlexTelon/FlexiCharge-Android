# Documentation for FlexiCharge Native Android Application

### 

## Code structure



## Running the aplication

### Configuration Files

1. Google Maps SDK API key
   * Enter a Google Maps SDK api-key in the file ./app/src/debug/res/values/google_maps_api.xml by replacing `PUT_API_KEY_HERE` with the actual key

### Build Steps

1. Open project in Android Studio
2. Let gradle download all required modules
3. Compile and run application by clicking "Run"

### Required Steps by user

1. Upon starting the app, grant location permissions to continue to the Map Activity.

2. Enter a valid charger ID to proceed with payment.

   * Charger ID's manually entered by tapping the large center button.

   * Obtain a valid chargerID by either:
     1. Entering a valid ChargerID.
     2. Scanning a QR Code
     3. Tapping a chargepoint in the chargepoint list and selecting one available charger.

3. Make a reservation by tapping the Klarna Button.

4. Once reservation is complete the charging process will begin.

5. To stop charging, either:

   1. Wait for charge percentege to reach 100%
   2. Tap Disconnect to manually stop charging.

6. Upon finished charging a charging summary will be presented.

### Known issues

## Support

Create an issue on GitHub or contact our Scrum Leader at jofi1617@student.ju.se

## License