# BikeShare
## Android app created during the 4. semester course Mobile App Development 

* The App uses Google Sign-In so a SHA-1 fingerprint needs to be added to a Google API Console Project. 
* In addition the app uses Google Maps so a Google Maps API key is needed for seeing ride locations. 


### Assignment

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

### Key Functions

* Google Sign-In - Login using the Google Play Service API. If logging in for the first time a
BikeShare account with 1000 credits is made using the Google id as key for the RealmObject.
In the AccountActivity information can be seen in terms of email, username and Google profile
picture. Account credits can be reset to 1000 from the AccountActivity for testing purposes.

* Register bike - A bike can be registered by giving name and price per hour and an image
taken by the OpenCV camera. The camera detects circles using OpenCV image processing,
and could be further developed to detect ”bike like shapes” to maybe only allow images of
bikes (proof of concept). If no image is given the bike is assigned a default image of a bike.
The image taken by the camera is saved as a bitmap to cache and later converted to bytes
that can be saved to the realm database. To ensure the price per hour of the bike can only be
between 0 and 100 i used a InputFilter.

* See vacant bikes - This is done by querying the realm database for the bikes that are free.
A bike is set to be in use when a ride is started.

* Start ride - If a bike is free a ride can be started. The phone GPS is used to get the current
location of the device and the accompanied address. This is then saved as the starting location
of the ride. As a naive proof of concept, Bluetooth and discovering can also be enabled and
show close by devices. The idea would be that bikes would act as beacons with IDs having to
match with the bike you’re trying to unlock.

* End ride - The ride ending process is similar to starting process and also adds a end location.
A cost of the ride is also calculated based on the bike’s price per hour and the difference
between the start- and end date of the ride. If the logged in account doesn’t have enough
credits, the ride can’t be ended and a prompt is shown telling the missing amount. When
a ride has ended all information regarding it is shown, including a Google Maps view of the
start- and end location, the distance between the points and a link to the bike used.

