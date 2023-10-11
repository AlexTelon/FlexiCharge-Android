# Charging Process

## Introduction

This document explains the process for a user to begin a charging process at an available charger and the pre-requirements for it.
Currently there is only one charger that is working, since that is the only one giving responses from backend, and that is the charger with the Id **100000**.

## Find the correct charger

There is currently four possible way to find the charger the user want to charge at, those four ways are :

1. **QR code:** The user has the possibility to use the camera and scan a Qr-code to get to the correct charger. To be able to use this feature the user has to grant permission for the app to use the camera. To test this function there is currently one generated qr-code that will lead the user to the correct charger.
![This is the qr-code](/images/QrCode100000.png "This Qr-code will send the user to the charger 100000")  

2. **Choose the Charger from the map:** The user has the option to find the charger they want to use from the map that is being displayed at the home screen. Then the user just have to click on the marker that represent the charger that should be used. The charger that can be tested is located at Jönköping university. And is called JU.

3. **Charger Identifier** Another way to find the charger is to enter the id manually, and to do that the user begin by pressing the Flexi-charge button in the bottom center of the screen <br>
![Flexi-charge button](/images/image-20211011151821916.png) <br>
When that button has been pressed the user will see this image on the screen <br>
 ![Charger Identifier](/images/image-20211011153319900.png) <br>
 There the user inputs the id of the charger they want to charge at (currently only "100000" available)

 4. **Chargers Near Me** The final way to find the charger is to use the chargers near me function. To do that the user start by pressing the same button as in step 3 (The Flexi-charge button in the bottom of the screen). Then the user press the button with the text "Charge Points near Me" to get a list of all charger with the closest being first. <br>
 ![Chargers near me](/images/ChargersNearMe.jpg) <br>
 Currently the available charger is at the charger point called "JU"

 ## Start the charging
 1. **Choose payment type:** When the user has chosen the correct charger, they then should choose the payment type they want to use (currently only Klarna available). When that has been chosen by pressing the Klarna button the "Continue" button should turned green <br>
 ![Payment Chosen](/images/PaymentChosen.jpg) <br><br>
 2. **Choose payment type in Klarna** When pressing the continue button the user should be sent to the Klarna layout and verify the payment. <br>
 The first screen the user will see is where they can choose between credit/debit card or bank transfer. There is a user that should be used for testing and with that the credit card option should be used.<br>
 ![Klarna choose payment](/images/KlarnaCardOrBank.jpg)<br><br>
 3. **Enter costumer details:** The next step is to enter the customer details for the user. The test person that can be used has the email **youremail@email.com** and the zipcode **12345** <br>
 ![Klarna costumer details](/images/TestDetails.jpg)<br><br>
 4. **Address warning:** Then the user should continue and get to a view where all the details is shown. Here a warning will be displayed that the address is not found
 but since this is a test person that is not a problem and the user can just press continue at this stage<br>
 ![Address not found](/images/AddressWarningKlarna.jpg)<br><br>
5. **Verify user details:** After that the user will have to verify the user details that has been entered and that is done by only pressing the verify button at the bottom<br>
![Verify user details](/images/KlarnaVerifyDetails.jpg)<br><br>
 6. **Enter card details** The final step of the Klarna process is to enter the card details of the user, here the user should also use the "demo details.
the card number that should be used is **4111 1111 1111 1111** the expire date should be any date in the future for example **12/25** and the CVC code should be **123**
then finally the user press the pay button and should then be sent back to our app. <br>
![Klarna card info](/images/KlarnaCardInformation.jpg)<br><br>
7. **Charging on going:** After that the user will have a bottom bar that display information about the on going charging. Currently the information that is being displayed will be updated every 5 seconds
and the data that being received from the API endpoint is randomized. Therefore the percentage could for example go from 78% to 12%. But the implementation is working so when correct data is being sent from backend that will be displayed correctly<br>
![Ongoing Charing](/images/ChargingOngoing.jpg)    <br><br>
8. **Charging summary:** The final view the user will be displayed is the charge summary, it will be visible after the "Stop charging" button has been pressed. The data here will be randomized by the backend as well. Therefore the data could seem wrong but it is working as expected. To remove the bottom dialog the user should press the red cross 
![Summary Image](/images/ChargingSummary.jpg)<br><br>

