# Dicoding Events - The Dicoding's Android Fundamental Course Project

## Overview
An Android application to display events from the Dicoding Events API, featuring both upcoming and past events with detailed information and navigation capabilities.

## Core Features

### Event Lists with Bottom Navigation
- Two fragment menus:
  - Active/upcoming events
  - Past events

- Each event item displays:
  - Event image (`imageLogo/mediaCover`)
  - Event name

### Event Details
- Comprehensive event information:
  - Event image
  - Event name
  - Organizer name (`ownerName`)
  - Event time (`beginTime`)
  - Remaining quota (`quota - registrant`)
  - Event description
  - Registration link button

### Loading States
- Loading indicators for all API data fetching operations

## Optional Enhancements

### Home Fragment
- Horizontal RecyclerView/Carousel showing:
  - Up to 5 active events
  - Up to 5 past events

### Search Functionality
- SearchBar/SearchView implementation
- Real-time event search using API endpoint

### Architecture Components
- ViewModel implementation
- State preservation during orientation changes
- Offline data persistence

### Error Handling
- Network error messages
- API failure handling
- User-friendly error states

### Code Quality
- Maximum 10 warnings in Code Inspection
- Proper code organization

## Technical Requirements

- Fragment implementation
- Bottom Navigation
- Network requests with JSON parsing
- ViewModel & LiveData usage
- Proper loading state management
- Error handling implementation
