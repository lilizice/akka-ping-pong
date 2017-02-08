
Scala and Akka demo app.  As of this writing this app only does a ping-pong message pass in a loop using Akka remote actors.

Configuration:

POJO entrypoint on class com.appdynamics.akka220.appd.poc.actors.PingPongActor, method handleProbe.  This will be
the only BT in the app.