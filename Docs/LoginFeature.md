## Login Functionality
This documentation provides basic knowledge on how the login function works.

## Implementation Detail 

The login functionality is implemented by adapting a layer called EntryManager (called business logic for logical purposes). 
The login flow process steps: 
1. LoginActivity calls the function signIn(username, password) [EntryManager](../app/src/main/java/com/flexicharge/bolt/activities/businessLogic/EntryManager.kt). 
2. EntryManger communicates with the ApiInterface to send a POST request to "/auth/sign-in", which containins a body(username, password). 
 [ApiInterface file](../app/src/main/java/com/flexicharge/bolt/api/flexicharge/ApiInterface.kt).
4. The server send back a response.
5. The response is rendered. 
6. signIn function invokes the callback.
7. Act accordingly.

![The figure Illustrates the flow of the login process.](../images/login_flow.png)
