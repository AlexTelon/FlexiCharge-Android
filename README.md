# Documentation for FlexiCharge Native Android Application

## In depth documentation can be found in the folder [Docs](./Docs/)
### Or through these links:
   * [RemoteObjects](./Docs/RemoteObjects.md)
   * [LiveChargingMetricsListener](./Docs/LiveMetricsFeature.md)
   * [RegisterActivity](./Docs/RegisterFeature.md)
   * [Login Feature](./Docs/LoginFeature.md)
   * [Reset Password Feature](./Docs/ResetPasswordFeature.md)


## Project Architecture

  This branch works with Android Studio 2020.3.1, uses Android SDK 33 and supports Android 10 and above.

## The application

### Configuration Files

1. **Google Maps SDK API key**
   Enter a Google Maps SDK api-key in the file ./app/src/debug/res/values/google_maps_api.xml by replacing `PUT_API_KEY_HERE` with the actual key

2. **Klarna testing account**
   Using klarna requires Klarna Playgrond sample data. Use these personal details (You will only need to enter email and zip code once, then it will be remembered inside the app):
   **Personal Details used for Bank Transfer**
   
     ![image-20211011145831799](./images/image-20211011145831799.png)
   
   **Credit Card Details** <br/>
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

<br/>

# Contributing Guidelines

Want to contribute to this repository? Please follow the following guidelines.

## Missing Features or bugs?
If you want to report a bug or suggest a new feature, please submit an issue and follow the templates down below: 

For features:
* Description
* Tasks
* Requirements
* Acceptance Test

For Bugs: 
* When did the bug occur?
* How did the bug occur?
* How can another developer reproduce the bug? Be VERY detailed and explain every click and action required
  * This step is **MANDATORY!** The bug must be reproducable with a step-by-step guide.

## Creating a pull request

* When you have work that you want to merge, create a pull request.
* The title of your pull request should be the same as you branch name but without camelCase.
  * For example a branch with name "bug/createLoginScreen" translates to "Bug/create login screen".
* Assign yourself as the Assigne.
* Assign 1...* squad members for the review
* Select the appropiate label.
* Tag the related issue in the description and explain the purpose of your pull request.
* After receiving and addressing comments or requested changes, you should re-request a review to notify the reviewers. 

## Coding Rules
* **Class files are categorized into different packages, depending on their application.** <br/>
  * Example 1: A map helper class is stored in ".../bolt/helper/MapHelper.kt" <br/>
  * Example 2: A QR scanner activity is stored in ".../bolt/activities/QrActivity.kt" <br/>

* **Camel casing is used for variable names.** <br/>
  * Example: A list of chargers is named chargerList
* **Snake casing is used for class file names**
* **ID's in Layout view's are identified by context, type of view and then name, separated by underscores** <br/>
    * Example: A menu button in mainActivity would be named mainActivity_button_menu

## Commits
* Please commit your code frequently with relevant description for each associated change.

## Support

Create an issue on GitHub or contact either:
* Scrum Master emely.kara6@gmail.com
* Product Owner foal20ym@student.ju.se | alexander-forsanker2011@hotmail.com

