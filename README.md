<img width="368" alt="Screen Shot 2022-03-09 at 14 01 16" src="https://user-images.githubusercontent.com/7502465/157543042-6eeaed11-a987-44bf-84b2-bf9e2b65cfb1.png">

# Brook Health App Code Challenge - Ricardo Castillo
Check your Google Fit Last Month Readings from an external app

## Features
The functionality presents solutions to the different scenarios required to have the functionality.

#### [Timber](https://github.com/JakeWharton/timber).
Logger with a small, extensible API which provides utilities on Android’s normal Log. class.

#### [Cycler](https://github.com/square/cycler). 
API that allows to simplify the RecyclerView implementation.

#### [ThreeTenABP](https://github.com/JakeWharton/ThreeTenABP). 
Android library adapted to handle Date in an easier way.

###FragmentViewBindingDelegate
Eliminates boilerplate code when is about implementing view models.

## MVI - Model View Intend with Kotlin Flow
Model View Intend builds upon Unidirectional Data Flow, better organizing the UI and business logic into view states and creating a clear contract between UI and business logic with an interface.

- Ui Modularization : Define the final UI results emitted and rendered in an interface contract and view state.
- Scaling: Reuse code with clear organization.
- Unit test simply: Build tests by observing the existing view state results from the business logic.
- Kotlin Flow: Quick setup, lifecycle management, threading management, avoid nesting, open for customization and simple implementation.


<img width="686" alt="Screen Shot 2022-03-09 at 16 21 40" src="https://user-images.githubusercontent.com/7502465/157547814-5fcea9af-6572-4ce3-b332-980346b29bb4.png">


### Android tools
- ViewBinding
- ConstraintLayout
- Google Playservices
- Google Fit API
- Material Buttons
- Android Coroutines
- Kotlin
- Services
- Kotlin Extensions
- Permissions
- Notifications
- ViewModel
- Custom Views
- Lint


## Improvements
Definitely this app contains a lot of room for expansion. If time were not a problem, I would definitely add:

- Dependency Injection with Hilt
- Loading animation with Lottie
- Custom Icons for the app
- Transition animations when RecyclerView were filled.
- Cache support using Room
- Changed icons for vectors (SVG)
- Screensizes support (tablets)
