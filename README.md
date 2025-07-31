# compose_flight_and_exchange_info

This is a Jetpack Compose-based app that displays flight and exchange rate information.
 - Written in Kotlin (of course)
 - Uses MVVM architecture
 - Displays flight information from Taoyuan International Airport
 - Shows exchange rate data with calculator functionality
 - Supports screen orientation changes
 - Supports light/dark theme switching

## Dev env

 - Windows 11
 - Android Studio Narwhal Patch 1
 - Android SDK version 36
 - JDK: 21.0.6(Android Studio Enbeded JVM)
 - Gradle: 8.14.3
 - Kotlin: 2.2.0
 - Compose: 2025.07.00

## References

 - [Government Open Data Platform - Taoyuan International Airport Real-time Flights](https://data.gov.tw/dataset/26194)
 - [Transportation Data Exchange - Public Transport - Aviation](https://tdx.transportdata.tw/api-service/swagger/basic/eb87998f-2f9c-4592-8d75-c62e5b724962#/Air/AirApi_Flight_2014)
 - [Free Currency Conversion API](https://freecurrencyapi.com/)

## Libraries

 - [kotlin-csv](https://github.com/jsoizo/kotlin-csv)
 - [ThreeTen Android Backport](https://github.com/JakeWharton/ThreeTenABP)
 - [retrofit](https://github.com/square/retrofit)
 - [okhttp](https://github.com/square/okhttp)

## Todos
- [ ] Add loading indicator
- [ ] Add unit tests
- [ ] Improve BottomSheet UI
- [ ] Integrate official API access token and renewal mechanism
- [ ] Refactor `RemoteDataSource` to wrap responses in `Response` for HTTP code handling
- [ ] Beautify flight info cards
- [ ] Remove unused `CsvDataSource`