# RegisterActivity
The [RegisterActivity](../app/src/main/java/com/flexicharge/bolt/activities/RegisterActivity.kt) asks for an email, password and a repeated password. 
Once the user provides all three and they are valid (validation is done
using the [Validator](../app/src/main/java/com/flexicharge/bolt/helpers/Validator.kt) class), a POST request is made to the backend to store the data.
Once that is successful, the backend sends a confirmation code to the user's email and the app switces to the [VerifyActivity](../app/src/main/java/com/flexicharge/bolt/activities/VerifyActivity.kt) where the user can verify their account.
