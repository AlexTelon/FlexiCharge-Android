## RegisterActivity
1. The [RegisterActivity](../app/src/main/java/com/flexicharge/bolt/activities/RegisterActivity.kt) asks for the users email, password and a repeated password. The user also has the option to inlude a first and last name which can also be added later with [NameAdressActivity](../app/src/main/java/com/flexicharge/bolt/activities/NameAdressActivity.kt)

2. Once the user provides all non-optional inputs and the inputs are valid (validation is done using the [Validator](../app/src/main/java/com/flexicharge/bolt/helpers/Validator.kt) class), a POST request is made to the endpoint "/auth/sign-up".

3. If the request is successful, the backend sends a confirmation code to the user's email and the app switces to the [VerifyActivity](../app/src/main/java/com/flexicharge/bolt/activities/VerifyActivity.kt)

4. When the user enters their verification code the registration is complete and the user gets transported to the [LoginActivity](..app/src/main/java/com/flexicharge/bolt/activities/LoginActivity.kt) where their login information should already be filled in.
