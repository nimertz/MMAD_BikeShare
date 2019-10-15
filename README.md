# BikeShare
## Android app created during the course Mobile App Development



For Assignment #02, you should design and implement: a user interface and functionality to register bikes, to register rides, and manage an account associated with each bike. You are free to design the user interface to your own liking (and it may be completely different from the user interface, we have used in the first weeks of the course).

Functionality and User Interface

The cornerstone of BikeShare app is a database containing information about a number of bikes and rides.

* The (minimal) information related to a bike are: (i) ID (identification of the lock); (ii) type of bike; (iii) picture of the bike; and (iv) price per hour. You may extend this.
* The (minimal) information related to rides are: (i) start (location and time); (ii) end (location and time); and (iii) price for the ride. You may extend this.
Your solution must be able to register new bikes, start rides, end rides and make a (pseudo) payment for a ride.

Your app(s) must have a number of screens (activities/fragments) to enter and store data related to bikes and rides:

* Register a bike: register an ID, type, picture, price (and possibly more).
* Start a ride: start location and time, Id of bike, unlock the bike.
* End a ride: end location and time, locking the bike, calculation and storing the price.
* Find a vacant bike: a list of bike that can be rented.
* Check bike: get all information about a bike, status (in use/free), list of all rides and list of all payments.

You are not allowed to handle "real" money in the BikeShare app. Your solution must, therefore, provide access to an account (based on pseudo money). The cost for rides should be deducted from this account.

Minimal Requirements

You are encouraged to make a nice, user friendly, powerful, reliable and robust app implementing the functionality described above. However, be realistic about your programming experience and amount of time you have available when you decide what to include and how to implement it. For example, it is better to have a modest functionality that does not crash and do the job rather than including a lot of functionality that is unstable. As a minimum, your app must include:

* The four application screens (i.e. register, start, end and check).
* A local database on the Android device (SQLite or Realm).
* Use of a RecyclerView and Adapter.
* Use Android Location (GPS) to find the current position of your Android device.
* Use the camera of your Android device to take a picture (of a bike), store it in the database and display it.
