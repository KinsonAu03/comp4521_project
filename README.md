# Weather Forecast App

An Android weather forecast application built with Jetpack Compose, providing real-time weather data, forecasts, and location management.

## Features

### Core Features
- **Current Weather Display**
  - Temperature with "feels like" temperature
  - Weather condition with icons
  - Humidity, wind speed, atmospheric pressure
  - UV index and visibility
  - Pull-to-refresh functionality

- **Weather Forecasts**
  - Hourly forecast (24-48 hours)
  - Daily forecast (7-14 days)
  - Detailed weather information for each time period

- **Weather Alerts**
  - Display of active weather alerts
  - Alert details with severity indicators

- **Location Management**
  - Auto-detect current location using GPS
  - Search locations by city name
  - Save up to 5 favorite locations
  - Switch between saved locations
  - Delete and manage locations

- **Settings & Customization**
  - Temperature unit: Celsius (°C) or Fahrenheit (°F)
  - Wind speed unit: km/h, m/s, or mph
  - Theme: Light, Dark, or System default
  - Real-time preference updates throughout the app

- **Data Persistence**
  - Room database for local data storage
  - Weather data caching for offline access
  - Location data persistence
  - Fast data loading from cache

## Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel) with Repository pattern
- **Database**: Room (SQLite)
- **Networking**: Retrofit + OkHttp
- **API**: OpenWeatherMap API
- **Location Services**: Google Play Services Location
- **State Management**: StateFlow/Flow
- **Async Operations**: Kotlin Coroutines
- **Dependency Injection**: Manual (ViewModels via `viewModel()`)

## Project Structure

```
app/src/main/java/com/group22/weatherForecastApp/
├── data/
│   ├── database/          # Room database entities and DAOs
│   ├── WeatherApi.kt     # Retrofit API interface
│   ├── WeatherRepository.kt
│   ├── LocationService.kt
│   └── PreferencesManager.kt
├── ui/
│   ├── screens/          # Compose screens
│   ├── components/       # Reusable UI components
│   ├── theme/            # App theming
│   └── viewmodel/        # ViewModels
└── navigation/           # Navigation routes and animations
```

## Setup

### Prerequisites
- Android Studio (latest version)
- Android SDK 24+ (minimum)
- OpenWeatherMap API key

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd comp4521_project
```

2. Add your OpenWeatherMap API key:
   - Create a `local.properties` file in the root directory (if it doesn't exist)
   - Add your API key:
   ```
   OPEN_WEATHER_API_KEY=your_api_key_here
   ```

3. Open the project in Android Studio and sync Gradle

4. Build and run the app

## Usage

1. **First Launch**: The app will request location permissions. You can grant permission for auto-detection or skip and add locations manually.

2. **View Weather**: The home screen displays current weather, alerts, and forecast summaries. Tap any card to view detailed information.

3. **Manage Locations**: 
   - Go to Settings → Location Manager
   - Search for a city or use "Get Current Location"
   - Add locations as favorites
   - Set a location as active to view its weather

4. **Customize Settings**: 
   - Navigate to Settings from the bottom navigation
   - Change temperature units, wind speed units, or theme
   - Changes apply immediately throughout the app

5. **Refresh Data**: 
   - Pull down on the home screen or tap the refresh button
   - Weather data will be fetched from the API and cached locally

## Permissions

The app requires the following permissions:
- `INTERNET`: For fetching weather data from the API
- `ACCESS_FINE_LOCATION`: For auto-detecting your current location
- `ACCESS_COARSE_LOCATION`: For approximate location detection

## API

This app uses the [OpenWeatherMap API](https://openweathermap.org/api). You'll need to:
1. Sign up for a free account at openweathermap.org
2. Generate an API key
3. Add it to your `local.properties` file

## Architecture

The app follows MVVM architecture with a Repository pattern:

- **UI Layer**: Jetpack Compose screens and components
- **ViewModel Layer**: Manages UI state and business logic
- **Repository Layer**: Abstracts data sources (API and Database)
- **Data Layer**: Room database and Retrofit API client

## Database Schema

- **LocationEntity**: Stores location information (name, coordinates, favorites)
- **WeatherDataEntity**: Stores weather data (current, hourly, daily forecasts)

## Future Enhancements

Potential features for future development:
- Home screen widgets (Android widget system)
- Push notifications for weather alerts
- Share weather data functionality
- Offline mode indicator
- Weather maps and radar

## License

This project is developed for COMP4521 Mobile Application Development course.

## Authors

- Au Chi Kin (20949848) - Leader
- HO, Wai Yin (20960969)

---

**Note**: This is a course project. Some features mentioned in the proposal documents (widgets, notifications, sharing) are planned but not yet implemented in the current version.

