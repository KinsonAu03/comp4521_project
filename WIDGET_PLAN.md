# Widget Implementation Plan

## Overview
This document outlines the detailed plan for implementing weather widgets on the home screen. The grid system uses a 6×4 layout (6 columns × 4 rows = 24 grid units).

---

## Grid System Specifications

### Grid Layout
- **Total Grid Units**: 24 (6 columns × 4 rows)
- **Column Width**: Each column = 1/6 of screen width
- **Row Height**: Each row = 1/4 of available vertical space (excluding top bar)
- **Widget Positioning**: Widgets can span multiple columns and rows
- **Spacing**: 8dp padding between widgets

### Widget Size Constraints
- **Minimum Size**: 1×1 (1 column × 1 row)
- **Maximum Size**: 6×4 (full screen)
- **Common Sizes**:
  - Small: 2×2, 3×2, 4×1
  - Medium: 4×2, 4×3, 6×2
  - Large: 4×4, 6×3, 6×4

---

## Widget Catalog

### 1. Current Weather Widget
**Priority**: High (Core Widget)

**Purpose**: Display current weather conditions for the selected location

**Available Sizes**:
- Small (2×2): Temperature + icon only
- Medium (4×2): Temperature + icon + condition + feels like
- Large (4×4): Full details with all metrics

**Data Displayed**:
- Current temperature (°C/°F)
- Weather condition icon
- Weather description (e.g., "Sunny", "Partly Cloudy")
- "Feels like" temperature
- Location name
- Last updated timestamp

**Large Size Additional Data**:
- Humidity percentage
- Wind speed and direction
- Pressure
- UV Index
- Visibility

**API Data Source**:
```kotlin
response.current {
    temp, feels_like, weather[].main, weather[].description, 
    weather[].icon, humidity, wind_speed, wind_deg, 
    pressure, uvi, visibility, dt
}
```

**Interaction**:
- Tap to navigate to Weather Details screen
- Long press to resize/remove

---

### 2. Hourly Forecast Widget
**Priority**: High (Core Widget)

**Purpose**: Show weather forecast for the next 24-48 hours

**Available Sizes**:
- Medium (4×2): Horizontal scrollable list, 12 hours visible
- Large (6×3): Horizontal scrollable list, 24 hours visible

**Data Displayed**:
- Time (hour)
- Temperature
- Weather icon
- Precipitation probability (if > 0%)
- Wind speed (optional in small view)

**API Data Source**:
```kotlin
response.hourly[] {
    dt, temp, weather[].icon, pop, wind_speed
}
```

**Interaction**:
- Horizontal scroll to see more hours
- Tap hour to see detailed forecast
- Long press to resize/remove

---

### 3. Daily Forecast Widget
**Priority**: High (Core Widget)

**Purpose**: Show 7-day weather forecast

**Available Sizes**:
- Medium (4×3): List view, 7 days
- Large (6×4): Expanded view with more details

**Data Displayed**:
- Day name (Mon, Tue, etc.)
- Date
- High/Low temperature
- Weather icon
- Precipitation probability
- Brief condition description

**Large Size Additional Data**:
- Sunrise/Sunset times
- UV Index
- Wind speed
- Humidity

**API Data Source**:
```kotlin
response.daily[] {
    dt, temp.min, temp.max, temp.day, temp.night,
    weather[].icon, weather[].main, pop, uvi,
    wind_speed, humidity
}
```

**Interaction**:
- Vertical scroll to see all days
- Tap day to expand details
- Long press to resize/remove

---

### 4. Weather Alerts Widget
**Priority**: Medium

**Purpose**: Display active weather alerts and warnings

**Available Sizes**:
- Small (4×1): Alert count badge + brief message
- Medium (4×2): List of active alerts with severity indicators

**Data Displayed**:
- Alert count badge (if alerts exist)
- Alert event name (e.g., "Severe Thunderstorm Warning")
- Alert severity indicator (color-coded)
- Alert start/end time
- Brief description

**API Data Source**:
```kotlin
response.alerts[] {
    sender_name, event, start, end, description
}
```

**Visual Design**:
- Red background for severe alerts
- Yellow/Orange for warnings
- Collapsible when no alerts
- Badge showing count of active alerts

**Interaction**:
- Tap to expand and see full alert details
- Swipe to dismiss (optional)
- Long press to resize/remove

---

### 5. Today's Weather Summary Widget
**Priority**: Medium

**Purpose**: Comprehensive summary of today's weather conditions

**Available Sizes**:
- Medium (4×2): Compact summary
- Large (6×3): Detailed summary with charts

**Data Displayed**:
- Current temperature and condition
- Today's high/low
- Hourly temperature chart (line graph)
- Precipitation timeline
- Wind conditions
- Sunrise/Sunset times

**API Data Source**:
```kotlin
response.current + response.hourly[0-23] + response.daily[0]
```

**Interaction**:
- Tap to see full details
- Long press to resize/remove

---

### 6. UV Index Widget
**Priority**: Low

**Purpose**: Display current and forecasted UV Index

**Available Sizes**:
- Small (2×2): Current UV index with indicator
- Medium (3×2): Current + daily forecast

**Data Displayed**:
- Current UV Index
- UV Index level (Low, Moderate, High, Very High, Extreme)
- Color-coded indicator
- Daily UV Index forecast (optional)

**API Data Source**:
```kotlin
response.current.uvi
response.daily[].uvi
```

**Visual Design**:
- Color gradient: Green (Low) → Yellow → Orange → Red (Extreme)
- Progress bar or circular indicator

---

### 7. Wind Conditions Widget
**Priority**: Low

**Purpose**: Display wind speed, direction, and forecast

**Available Sizes**:
- Small (2×2): Current wind with compass
- Medium (4×2): Current + hourly wind forecast

**Data Displayed**:
- Wind speed (km/h, m/s, or mph)
- Wind direction (compass direction + degrees)
- Wind gust speed (if available)
- Wind direction indicator (arrow/compass)

**API Data Source**:
```kotlin
response.current.wind_speed, wind_deg
response.hourly[].wind_speed, wind_deg
```

**Visual Design**:
- Compass rose showing wind direction
- Animated arrow indicating direction
- Speed displayed prominently

---

### 8. Air Quality Widget (Future)
**Priority**: Low (Requires additional API)

**Purpose**: Display air quality index if available

**Note**: OpenWeatherMap One Call API 3.0 may include air quality data. Check API response for availability.

**Available Sizes**:
- Small (2×2): AQI number + status
- Medium (4×2): AQI + breakdown by pollutant

---

### 9. Sunrise/Sunset Widget
**Priority**: Low

**Purpose**: Display sunrise and sunset times

**Available Sizes**:
- Small (2×1): Times only
- Medium (3×2): Times + sun position indicator

**Data Displayed**:
- Sunrise time
- Sunset time
- Day length
- Current sun position (optional)

**API Data Source**:
```kotlin
response.current.sunrise, sunset (if available)
response.daily[].sunrise, sunset
```

---

### 10. Precipitation Widget
**Priority**: Low

**Purpose**: Show precipitation forecast and probability

**Available Sizes**:
- Small (2×2): Current precipitation + probability
- Medium (4×2): Hourly precipitation chart

**Data Displayed**:
- Current precipitation (if raining)
- Precipitation probability (pop)
- Hourly precipitation forecast
- Chart showing precipitation over time

**API Data Source**:
```kotlin
response.hourly[].pop
response.current.rain, snow (if available)
```

---

## Widget Implementation Priority

### Phase 1 (MVP - Core Widgets)
1. ✅ Current Weather Widget (Small, Medium, Large)
2. ✅ Hourly Forecast Widget (Medium, Large)
3. ✅ Daily Forecast Widget (Medium, Large)

### Phase 2 (Enhanced Features)
4. Weather Alerts Widget (Small, Medium)
5. Today's Weather Summary Widget (Medium, Large)

### Phase 3 (Additional Widgets)
6. UV Index Widget
7. Wind Conditions Widget
8. Sunrise/Sunset Widget
9. Precipitation Widget

### Phase 4 (Future Enhancements)
10. Air Quality Widget (if API supports)
11. Weather Map Widget
12. Favorite Locations Quick Switch Widget

---

## Widget Layout System

### Grid Calculation
```kotlin
// Grid unit calculations
val columnWidth = screenWidth / 6
val rowHeight = availableHeight / 4

// Widget dimensions
widgetWidth = columnWidth * widgetPosition.width
widgetHeight = rowHeight * widgetPosition.height
```

### Widget Positioning
```kotlin
data class WidgetPosition(
    val column: Int,  // 0-5
    val row: Int,     // 0-3
    val width: Int,   // 1-6
    val height: Int   // 1-4
)
```

### Example Layouts

**Layout 1: Balanced**
- Current Weather (4×2) - Top left
- Hourly Forecast (6×2) - Below current weather
- Daily Forecast (6×2) - Bottom

**Layout 2: Compact**
- Current Weather (2×2) - Top left
- Hourly Forecast (4×2) - Top right
- Daily Forecast (6×2) - Bottom

**Layout 3: Detailed**
- Current Weather (4×4) - Left side
- Hourly Forecast (2×2) - Top right
- Daily Forecast (2×2) - Bottom right

---

## Data Flow

### Widget Data Source
1. **Repository** fetches data from API
2. **Database** stores cached data
3. **ViewModel** provides data to widgets
4. **Widget Composables** display data

### Widget State Management
- Each widget can have its own ViewModel or share a common one
- Widget positions and sizes stored in database
- Widget visibility and order managed by user preferences

---

## Technical Implementation Notes

### Widget Component Structure
```kotlin
@Composable
fun CurrentWeatherWidget(
    size: WidgetSize,
    locationId: Long,
    viewModel: WeatherViewModel,
    modifier: Modifier = Modifier
) {
    // Fetch data based on size
    // Render appropriate UI
}
```

### Widget Sizing
- Use `Modifier.size()` with calculated dimensions
- Responsive to screen size changes
- Support both portrait and landscape

### Widget Customization
- Store widget configuration in database
- Support drag-and-drop (future)
- Support resize handles (future)
- Support remove/add widgets (future)

---

## Testing Strategy

### Unit Tests
- Widget data parsing
- Widget size calculations
- Widget position validation

### UI Tests
- Widget rendering at different sizes
- Widget interaction (tap, long press)
- Widget layout in different screen sizes

### Integration Tests
- Widget data flow from API to UI
- Widget persistence (save/load layout)
- Widget updates on data refresh

---

## Next Steps

1. ✅ Implement grid system (6×4 layout)
2. ✅ Add API response logging
3. ✅ Create widget plan document
4. Implement Current Weather Widget (all sizes)
5. Implement Hourly Forecast Widget
6. Implement Daily Forecast Widget
7. Add widget customization UI
8. Add widget persistence
9. Implement drag-and-drop (future)
10. Add widget to Android home screen (Glance widgets)

---

## Notes

- All widget sizes are flexible and can be adjusted based on UX testing
- Widget colors and styling should follow Material Design 3 guidelines
- Widgets should be accessible (support TalkBack, high contrast)
- Widgets should handle loading states and errors gracefully
- Consider offline mode - show cached data when API unavailable

