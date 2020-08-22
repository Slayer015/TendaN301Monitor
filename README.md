## Unofficial Tenda N301 Monitor App
<img align="right" src="./Preview/Main.gif" width="250"/>
I've never programmed in Kotlin so I decided to make something quick and useful for me that serves as practice. Officially, the Tenda N301 router, doesn't have a mobile app to manage all the stuff related with it, and yeah, it obviouslly has its own local webpage (minimalistic and responsive) to do it, but I said... Why not an app?

### Features
- See current upload and download speed of your network
- See all devices connected to the router
- And that's all.. It's just a simple consult app

The code is probably not the most optimal and contains bad practices of mine, but it works! There is some bug thing related with the motion layout and the recycler view that makes the app to crash, maybe someday I'll investigate about it.

### Operation
The app is ready to operate with routers without password, if it isn't your case, you have to download the source code and make your own build by changing the next line in HomeFragment.kt:
```kotlin
tenda = TendaAPI("your_pass_here", activity!!)
```
I made some kind of API writen as well in Kotlin, you just need to provide the router password and the actual activity, by itself it gets the device gateway (where it is supposed to be the router connected), after that, it makes a POST request to http://$__ROUTERIP/login/Auth, if the response header contains the cookie session, you're in! otherwise you aren't and you get notified. In case there is no a Tenda N301 or the response is unexpected, you'll get notified as well. Don't know what happen if other Tenda model tries to connect.
<br><br>
To get all the information about the connected devices I use
```kotlin
tenda!!.getOnlineDevices()
```
To get current router status I use 
```kotlin
tenda!!.getDeviceStatistics()
```
Those methods should be executed not in the main thread or wont work (Android rules), they returns a JSONObject. I execute getOnlineDevice() every 5 seconds (the official web does this by default) if I reduce this time eventually weird things will happend with the json returned by the router and you'll need to restart it.

### Other stuff
- App design inspired in [Parsa Aghaei's](https://dribbble.com/shots/7157806-Internet-Network-Monitoring-App) dribbble
- TendaAPI inspired in [Talha Balaj's](https://github.com/talhabalaj/tenda-n301-api-python) API made in Python

### Third party libraries
- [Gson](https://github.com/google/gson)
- [OkHttp](https://github.com/square/okhttp)
- [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)
- [Chip Navigation Bar](https://github.com/ismaeldivita/chip-navigation-bar)
- Icons designed by [Freepik](https://www.flaticon.es/autores/freepik) and [Pixel perfect](https://www.flaticon.es/autores/pixel-perfect) from [flaticon](https://www.flaticon.es)
