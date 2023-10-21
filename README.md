# Documentation for FlexiCharge Native Android Application

## In depth documentation can be found in the folder [Docs](./Docs/)
### Or through these links:
* [RemoteObjects](./Docs/RemoteObjects.md)
* [LiveChargingMetricsListener](./Docs/LiveMetricsFeature.md)
* [RegisterActivity](./Docs/RegisterFeature.md)
* [Login Feature](./Docs/LoginFeature.md)
* [Reset Password Feature](./Docs/ResetPasswordFeature.md)
* [Ktlint](./Docs/KtlintFeature.md)
* [Charging process](./Docs/ChargingProcess.md)
* [Testing strategy](./Docs/TestingStrategy.md)

## Project Architecture

This project works with Android Studio Giraffe | 2022.3.1 and targets Android SDK version 33.

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
    * Example 2: A QR scanner activity is stored in ".../bolt/activities/QrActivity.kt"
      <br/>
* **Class names their files should be written in PascalCase**
    * See the rule below for an example.
* **Class files should be named after the class name. (enforced by Ktlint)**
    * Example: A class named TheExampleClass must be located in a file named "TheExampleClass.kt". If this is not the case your build will fail since this is enforced by Ktlint.

* **Camel casing is used for variable names.** <br/>
    * Example: A list of chargers is named chargerList
* **ID's in Layout view's are identified by context, type of view and then name, separated by underscores** <br/>
    * Example: A menu button in mainActivity would be named mainActivity_button_menu

## Commits
* Please commit your code frequently with relevant description for each associated change.

## Support

Create an issue on GitHub or contact either:
* Scrum Master emely.kara6@gmail.com
* Product Owner foal20ym@student.ju.se | alexander-forsanker2011@hotmail.com

