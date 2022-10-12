# Documentation for FlexiCharge Native Android Application

## Project Architecture
* ### RemoteObject
    #### What are "RemoteObjects"?
    * All endpoints on the backend that is of importance to the application should have a corresponding class inheriting from ```RemoteObject```.
    * For example, for the endpoint "/transactions" there is a class ```RemoteTransaction``` which inherits from ```RemoteObject<Transaction>```
    * All functionality of the endpoint should be contained within its ```RemoteObject``` class.
    #### How do you implement a RemoteObject?
    * Define a ```data class``` summarizing the information to be contained within the remote object.
    * Make sure that the layout and the names of the fields in the ```data class``` matches its corresponding JSON object, provided by the backend team.
    * The ```data class``` ```Chargers``` for example contains the information returned by the API-call ```GET /chargers```.
    * Create a class inheriting from ```RemoteObject<T>```.
    * For example, the class ```RemoteChargers``` inherits from ```RemoteObject<Chargers>```.
    * Implement its abstract methods and properties (android studio can help you in doing this, just hover over what's red and perform the recommended action.)
    * Check the existing Remote____ classes for reference on how to implement API-calls.
    * Don't forget to use ```withTimeout()``` when making API calls!
    #### How do you use a RemoteObject?
    * All RemoteObjects implement the method ```refresh()``` which utilizes the abstract method ```retrieve()```.
    * ```retrieve()``` should implement the most basic /GET request you can make to the end-point.
    * ```refresh()``` in turn calls the onRefreshed callback which can be defined with the method ```setOnRefreshedCallback()```.
    * All operations performed by a remote object should return a ```Job```instance, which you can store in a variable.
    * To perform an action after the API-call has been succesfully made, use the method ```Job.invokeOnCompletion()```.
    * To check whether the API-call succeeded you can consult the property ```Job.isCancelled``` to ```return@invokeOnCompletion``` if it failed.

  This branch works with Android Studio 2020.3.1, uses Android SDK 30 and supports Android 10 and above.

## The application

### Configuration Files

1. **Google Maps SDK API key**
   Enter a Google Maps SDK api-key in the file ./app/src/debug/res/values/google_maps_api.xml by replacing `PUT_API_KEY_HERE` with the actual key

2. **Klarna testing account**
   Using klarna requires Klarna Playgrond sample data. Use these personal details (You will only need to enter email and zip code once, then it will be remembered inside the app):
   **Personal Details used for Bank Transfer**
   
     ![image-20211011145831799](./images/image-20211011145831799.png)
   
   **Credit Card Details**
   ![image-20211013123905119](./images/image-20211013123905119.png)

### Build Steps

1. Open project in Android Studio
2. Let gradle download all required modules
3. Compile and run application by clicking "Run"


### Using the app

1. When starting the app, grant required location permissions to continue to Home Screen.
2. In home, the user can do a multitude of things...
   * Use the map to view FlexiChargers on the **map**.![image-20211011152235679](./images/image-20211011152235679.png)
   * Tap ![image-20211011151837571](./images/image-20211011151837571.png) to "warp" to **your location**. 
   * Tap ![image-20211011151855812](./images/image-20211011151855812.png) to access FlexiCharger **QR Scanner**
   * Tap ![image-20211011151821916](./images/image-20211011151821916.png) to access the **ChargingPanel**.   ![image-20211011153319900](./images/image-20211011153319900.png) 

#### To charge

   ![image-20211011152137316](./images/image-20211011152137316.png)

1. Enter a valid ChargerID pin in the ChargingPanel, ChargerID's can be obtained by:
   * Tapping green markers on the map.
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
## Contributing Guidelines

Want to contribute to this repository? Please follow the following guidelines.

### Missing Features or bugs?

If you want to report a bug or suggest a new feature, please submit an issue.

### Creating a pull request

* When you have work that you want to merge, create a pull request.
* When your pull request is created, tag it with an issue.

### Coding Rules

* **Class files are categorized into different packages, depending on their application.**
  Example 1: A map helper class is stored in ".../bolt/helper/MapHelper.kt"
  Example 2: A QR scanner activity is stored in ".../bolt/activities/QrActivity.kt"

* **Camel casing is used for variable names.**
  Example: A list of chargers is named chargerList
* **Snake casing is used for class file names**
* **ID's in Layout view's are identified by context, type of view and then name, separated by underscores**
  Example: A menu button in mainActivity would be named mainActivity_button_menu

### Commits

* Please commit your code frequently with relevant description for each associated change.

## Support

Create an issue on GitHub or contact our Scrum Leader at jofi1617@student.ju.se

## Register Activity 
self explanatory, the user registers using email and password, gets a conformation code and 
verifies it in the Verify Activity 