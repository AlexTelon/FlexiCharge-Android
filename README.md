# Documentation for FlexiCharge Native Android Application

## Code structure



## The application

### Configuration Files

1. Google Maps SDK API key
   * Enter a Google Maps SDK api-key in the file ./app/src/debug/res/values/google_maps_api.xml by replacing `PUT_API_KEY_HERE` with the actual key
2. Klarna testing account
   * Using klarna requires Klarna Playgrond sample data. Use these personal details (You will only need to enter email and zip code once, then it will be remembered inside the app):
     ![image-20211011145831799](./images/image-20211011145831799.png)

### Build Steps

1. Open project in Android Studio
2. Let gradle download all required modules
3. Compile and run application by clicking "Run"

### Using the app

1. Upon starting the app, grant required location permissions to continue to Home.

2. In home, the user can do a multitude of things...

   * Use the map to view FlexiChargers on the map.
     ![image-20211011152235679](./images/image-20211011152235679.png)

   * Tap ![image-20211011151837571](./images/image-20211011151837571.png) to "warp" to your location. 

   * Tap the ![image-20211011151855812](./images/image-20211011151855812.png) to access QR Scanner
     * Scan a QR code present on a charger to access ChargingPanel with preenterd ID

   * Press the ![image-20211011151821916](./images/image-20211011151821916.png) to access the ChargingPanel. 

     * In ChargingPanel a user can do a plethera of things.
       ![image-20211011153319900](./images/image-20211011153319900.png)

       * Tap ![image-20211011153117219](./images/image-20211011153117219.png)to see a list of all ChargePoints and their distance to you by

       * View available Chargers inside a ChargePoint by tapping on a ChargePoint. ![image-20211011152025404](./images/image-20211011152025404.png)

       * Charge an electric vehicle.

          1. Entera valid chargerID, obtained by either...

             * Entering a valid ChargerID.
               ![image-20211011152137316](./images/image-20211011152137316.png)
             * Scanning a QR Code 
             * Tapping a chargepoint in the chargepoint list and selecting one available charger.![image-20211011152100925](./images/image-20211011152100925.png)

          2. Tap  ![image-20211011152306659](./images/image-20211011152306659.png)to make a monetary reservation for a charger. (Follow through Klarna steps, testing account described above in readme)

          3. Once reservation is complete the charging process will begin.
             ![image-20211011152355229](./images/image-20211011152355229.png)

          4. To stop charging, either...

             * Wait for charge percentege to reach 100%

             * Tap ![image-20211011152407316](./images/image-20211011152407316.png) to manually stop charging.

          5. Upon finished charging a charging summary will be presented.
             ![image-20211011152445124](./images/image-20211011152445124.png)

### Known issues

## Support

Create an issue on GitHub or contact our Scrum Leader at jofi1617@student.ju.se

## License