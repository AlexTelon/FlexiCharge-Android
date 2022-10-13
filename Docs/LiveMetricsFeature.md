# Live charging metrics
Live charging metrics are implemented throught the use of WebSockets. The implementation can be found in the class [LiveChargingMetricsListener](../app/src/main/java/com/flexicharge/bolt/activities/businessLogic/LiveChargingMetricsListener.kt). To use it, create an instance of the class using the user's ID as the first argument to its constructor, and a callback function which is called when metrics have been received as its second argument. The callback passes a [WebSocketJsonResponse.LiveMetricsMessage](../app/src/main/java/com/flexicharge/bolt/api/flexicharge/WebSocketJsonMessage.kt) which contains all the metrics sent by the server. The ```LiveChargingMetricsListener``` instance will begin listening as soon as it's created.
## ```stopWhen()```
To make the listener stop listening on receiving a certain message, you can define a predicate and set it using the ```stopWhen()``` method.

## ```stop()```
The listener also implements the method ```stop()``` which simply closes its connection.