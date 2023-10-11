
## Reset Password Functionality
This documentation provides basic knowledge on how the user can reset their password.

## Implementation Details
1. To find the reset/recover password screen the user must go to acount settings -> change password -> i forgot my password.

2. The first step is similar to the [RegisterActivity](../app/src/main/java/com/flexicharge/bolt/activities/RegisterActivity.kt) and [VerifyActivity](../app/src/main/java/com/flexicharge/bolt/activities/VerifyActivity.kt) where the user enters their email and recives a confirmation code.

3.  The view switches to [ConfirmEmailActivity](../app/src/main/java/com/flexicharge/bolt/activities/ConfirmEmailActivity.kt) where the user can fill in the confirmation code as well as a new password and a repeted password.

4.  On confirming the new password, the function confrimResetPass gets called from [EntryManager](../app/src/main/java/com/flexicharge/bolt/activities/businessLogic/EntryManager.kt), which sends a POST request to the  /auth/frgot-password/{username} endpoint.

5. If the request is sucessfull the user gets transported to the login screen and their password is uppdated.  
