# Documentation for FlexiCharge Native Android Application

## Code structure



## The application

### Configuration Files

1. Google Maps SDK API key
  * Enter a Google Maps SDK api-key in the file ./app/src/debug/res/values/google_maps_api.xml by replacing `PUT_API_KEY_HERE` with the actual key
3. Klarna testing account
  * Using klarna requires Klarna Playgrond sample data. Use these personal details (You will only need to enter email and zip code once, then it will be remembered inside the app):
     
  ![image-20211011145831799](./images/image-20211011145831799.png)

### Build Steps

1. Open project in Android Studio
2. Let gradle download all required modules
3. Compile and run application by clicking "Run"

### Using the app
1. Upon starting the app, grant required location permissions to continue to Home.
2. In home, the user can do a multitude of things...
---
* Use the map to view FlexiChargers on the map.

  ![image-20211011152235679](./images/image-20211011152235679.png)
* Tap ![image-20211011151837571](./images/image-20211011151837571.png) to "warp" to your location. 
* Tap ![image-20211011151855812](./images/image-20211011151855812.png) to access FlexiCharger QR Scanner
* Tap ![image-20211011151821916](./images/image-20211011151821916.png) to access the ChargingPanel. 
  
#### To charge
   ![image-20211011153319900](./images/image-20211011153319900.png) 
   ![image-20211011152137316](./images/image-20211011152137316.png)
1. Enter a valid charger ID pin in the ChargingPanel, a chargerID can be obtained by:
   * Using map to find GREEN chargers
   * Scanning a QR Code present on a FlexiCharger
   * Tapping a chargepoint from the "chargepoints near me" list and selecting one available charger.

     ![image-20211011152100925](./images/image-20211011152100925.png)
2. Tap ![image-20211011152306659](./images/image-20211011152306659.png) to make a monetary reservation for a charger. (Follow through Klarna steps, testing account described above in readme)
3. Once reservation is complete the charging process will begin automatically.

   ![image-20211011152355229](./images/image-20211011152355229.png)
4. To stop charging, either..
   * Wait for charge percentege to reach 100%
   * Tap ![image-20211011152407316](./images/image-20211011152407316.png) to manually stop charging.
5. Upon finished charging a charging summary will be presented as such.

   ![image-20211011152445124](./images/image-20211011152445124.png)
---
### Known issues

## Support

Create an issue on GitHub or contact our Scrum Leader at jofi1617@student.ju.se

## License
