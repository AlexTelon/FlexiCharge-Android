## Login Functionality/ Implementation Detail 

The login functionality is implemented by adapting a layer called EntryManager (called business logic for logical purposes). 
The process to login flows as following: 
1. LoginActivity calls the function signIn(username, password) [LiveChargingMetricsListener](../app/src/main/java/com/flexicharge/bolt/activities/businessLogic/EntryManager.kt). 
2. EntryManger communicates with the ApiInterface to send a POST request to "/auth/sign-in", [ApiInterface file](../app/src/main/java/com/flexicharge/bolt/api/flexicharge/ApiInterface.kt).
3. A POST request is sent to the endpoint "/auth/sign-in". 
4. The server send back a response.
5. The response is rendered. 
6. signIn function invokes the callback.
7. Act accordingly.

![The figure Illustrates the flow of the login process.](../images/login_flow.png)