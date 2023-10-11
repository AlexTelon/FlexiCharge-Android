## Live charging metrics
Live charging metrics are currently implemented throught the use of a continuously polling GET request /transactions/{transactionId} that fetches live data every few seconds. 
1. The use of the endpoint can be found in the class [RemoteTransaction](../app/src/main/java/com/flexicharge/bolt/activities/businessLogic/RemoteTransaction.kt) as a function named retrive(). 

2. In [MainActivity](../app/src/main/java/com/flexicharge/bolt/activities/MainActivity.kt), retrive() is then being called from the function setupChargingInProgressDialog()



## ```stopChargingProcess()```
1. To stop the current transaction we call on the stop() function from [RemoteTransaction](../app/src/main/java/com/flexicharge/bolt/activities/businessLogic/RemoteTransaction.kt) which uses the PUT endpoint transactions/stop/{transactionId}.

2. stop() is then being called from our stopChargingProcess() in [MainActivity](../app/src/main/java/com/flexicharge/bolt/activities/MainActivity.kt) 

## Future implementations
The live charging metrics was originally retrived with the use of WebSockets which should be the case for future implementations since it is a more optimal way of handling data. 
