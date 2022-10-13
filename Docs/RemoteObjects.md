# RemoteObject
## What are RemoteObjects?
* A ```RemoteObject``` is a local representation of one or several endpoints on the flexicharge API back end.
* For example, for the endpoint /transactions there is a class ```RemoteTransaction``` which inherits from ```RemoteObject<Transaction>```
* All functionality of the endpoint should be contained within a ```RemoteObject``` class.

## How do you implement a RemoteObject?
* Define a ```data class``` summarizing the information to be contained within the remote object.
* The ```data class``` ```Chargers``` for example contains the information returned by the API-call ```GET /chargers```.
* Make sure that the layout and the names of the fields in the ```data class``` matches its corresponding JSON object - provided by the backend team.
* Create a class inheriting from ```RemoteObject<T>```.
* For example, the class ```RemoteChargers``` inherits from ```RemoteObject<Chargers>```.
* Implement its abstract methods and properties. Android studio can help you in doing this, just hover over what's marked with red "squiggly lines" and perform the recommended action.
* Check the existing Remote____ classes for reference on how to implement API-calls.
* Don't forget to use ```withTimeout()``` when making API calls! Otherwise, calls that aren't responded to will never finish, which will slowly drain resources.

## How do you use a RemoteObject?
* All RemoteObjects implement the method ```refresh()``` which utilizes the abstract method ```retrieve()```.
* ```retrieve()``` should implement the most general ```GET``` request you can make to the end-point.
* ```refresh()``` in turn calls the onRefreshed callback which can be defined with the method ```setOnRefreshedCallback()```.
* All operations performed by a remote object should return a ```Job``` instance, which you can store in a variable.
* To perform an action after the API-call has been succesfully made, use the method ```Job.invokeOnCompletion()```.
* To check whether the API-call succeeded you can consult the property ```Job.isCancelled``` to ```return@invokeOnCompletion``` if it failed.

## Why use remote objects?
* They help separate business logic from the presentation layer.
* They standardize the way communication with the back end is done.
* They ensure that each API-call is only done in one place.