# QA ✅❌

## What is this?

- Manual tests to complete before releasing. Copy this file into the [qa](./qa) directory and rename
  with the version number and platform like `QA_1.0.0_android.md`
- Ideally replace these with automated UI tests eventually.
- Acts as a complete list of everything the app should do.

Copy me, paste me ✅ ❌

## Location Permissions

Location permissions are request on first app start. The map screen doesn't show unless they're
granted.

- Fresh app install
    - [ ] I see location permissions rationale
    - I tap 'allow location access'
        - [ ] I see location permission dialog
        - I tap 'allow once' (IOS) / 'Only this time' (Android)
            - [ ] I see the map screen
            - [ ] IOS ONLY: I see the map screen - I force close and reopen app - I see location
              permissions rationale
        - [ ] I tap 'allow while using app' - I see the map screen
        - [ ] I tap 'don't allow' - I see location permissions denied screen
- [ ] Location permissions denied previously - I see location permissions denied screen
- [ ] Deny location permissions - Manually enable via settings - Reopen app (don't close) - I
  see the map screen

## Notification Permissions

Notification permissions are requested the first time the alarm is enabled.

- [ ] Fresh app install - On map screen - Tap enable alarm - I see notification permissions dialog
- [ ] Fresh app install - On map screen - Tap enable alarm - Tap 'Allow' - Alarm is enabled
- [ ] Fresh app install - On map screen - Tap enable alarm - Tap 'Don't Allow - I see a notification
  permissions denied message - 'Enable alarm' button is disabled
- [ ] Notification permissions denied previously - Open app - On map screen - I see notification
  permissions denied message - 'Enable alarm' button is disabled
- [ ] ANDROID ONLY: Notification permissions denied previously - Open app - On map screen - I see
  notification permissions denied message - Tap 'Allow notification permissions' - System dialog
  appears
    - [ ] Tap 'Allow' - notification permissions denied message disappears
  - [ ] Tap 'Don't Allow' - 'Allow notification permissions' button disappears and is replaced by
    instruction to go to settings
- [ ] Notification permissions denied previously - Manually enable in settings - Reopen app (don't
  close) - On map
  screen - 'Enable alarm' works as expected

## Set Geofence

- [ ] Map screen - geofence moves with location updates
- [ ] Map screen - Tap somewhere on the map - geofence moves to tap location
- [ ] Map screen - Tap somewhere on the map - geofence stops moving with location updates
- [ ] Map screen - Tap radius scrubber - arrows animate up and down
- [ ] Map screen - Drag radius scrubber up - Radius increases - Geofence gets bigger
- [ ] Map screen - Drag radius scrubber down - Radius decreases - Geofence gets smaller
- [ ] Map screen - Drag radius scrubber down a lot - Radius decreases with minimum 10m - Geofence
  gets smaller until minimum 10m

## Location Alarm

Assuming notification permissions are enabled.
See [Notification Permissions](#notification-permissions).

### Set alarm

- Map screen - Tap enable alarm
    - [ ] Alarm is enabled (Button says 'Disable Alarm') - Distance to alarm is shown
    - [ ] Persistent Notification (Android) / Live Activity (ios) is shown in notification drawer
      with distance to alarm shown
    - [ ] When my location updates - Then distance to alarm is updated in persistent notification /
      live activity

### Alarm Triggered

#### App in Foreground

- Map screen - Tap enable alarm - Location updates to within geofence
    - [ ] Alarm sounds & vibrates
    - [ ] Alert is shown with option to 'Stop Alarm'
    - [ ] Persistent notification becomes bright color and gives option to stop alarm
    - [ ] Tap stop alarm in alert UI - Alarm stops, persistent notification is dismissed
  - [ ] Tap stop alarm in persistent notification - App opens (ios only), alarm stops, persistent
      notification is dismissed

#### App in Background

- Map screen - Tap enable alarm - **Background app** - Location updates to within geofence
    - [ ] Alarm sounds & vibrates
    - [ ] A notification is shown to draw attention to the alarm being triggered which gives the
      option to stop alarm
    - [ ] Persistent notification in notification drawer becomes bright color and gives option to
      stop alarm
  - [ ] Tap stop alarm in persistent notification - App opens (ios only), alarm stops, persistent
      notification is dismissed

#### Phone locked

- Map screen - Tap enable alarm - **Lock app** - Location updates to within geofence
    - [ ] Alarm sounds & vibrates
    - [ ] Persistent notification on lock screen becomes bright color and gives option to stop alarm
  - [ ] Tap stop alarm in persistent notification - App opens (ios only), alarm stops, persistent
      notification is dismissed

#### Update Alarm Geofence while alarm enabled

- [ ] Map screen - Tap enable alarm - Increase radius until my location is within geofence - Alarm
  sounds & vibrates
- [ ] Map screen - Tap enable alarm - Tap my location on the map - Alarm sounds & vibrates

### Edge cases

- Location alarm works as in [alarm triggered](#alarm-triggered) when:
    - [ ] I have battery saver mode enabled (Requires real world testing)
    - [ ] My phone is on silent
    - [ ] My phone is on do not disturb
- [ ] IOS: When i have headphones connected, the alarm is heard through the headphones

### Platform specific

- [ ] Android system alarm volume is respected
- [ ] IOS Dynamic Island works on iPhone 14Pro and above
    - [ ] Minimal icon is white when active, and orange when triggered
    - [ ] Compact view shows icon in white and green check when active, and orange warning triangle
      when
      triggered
    - [ ] Compact Text says distance when active and 'Arrived!' when triggered
    - [ ] Expanded view shows icon in white and green check when active, and orange warning triangle
      when triggered
    - [ ] Expanded Text says distance when active and 'You have reached your destination!' when
      triggered
- [ ] Android emulator crap check:
  Permissions, location updates, alarm triggers from locked phone
    - [ ] Android 12 (31)
    - [ ] Android 13 (33)
    - [ ] Android 14 (34)
  - [ ] Android 15 (35) (Pixel 8)
    - [ ] Android 16 (36) (preview)
- [ ] IOS simulator crap check:
  Permissions, location updates, alarm triggers from locked phone
    - [ ] IOS 15
    - [ ] IOS 16
    - [ ] IOS 17
    - [ ] IOS 18

## Fly to current location

- [ ] Map screen - Tap location icon bottom left - Current location (blue dot) will become centered
- [ ] Map screen - Tap location icon bottom left - Current location (blue dot) will become
  centered - Tap again before new location comes through - Current location is centered
    - Regression: This ensures the fly to functionality works even if the location hasn't updated.

## Dev tooling

- [ ] ANDROID ONLY: Press volume buttons UP DOWN UP DOWN to toggle debug mode
- [ ] Map screen - Tap debug screen button - Debug screen appears
    - Logs tab
        - [ ] Logs are visible and update as new logs come in
        - [ ] New logs are visible at the bottom of the screen (auto scroll to bottom)
        - [ ] Scroll up in the logs - The auto scroll to bottom stops
        - [ ] Scroll up in the logs - Scroll down to the bottom - The auto scroll to bottom restarts
    - Gps tab
        - [ ] Timestamps of all location updates are visible and update as new updates come in
        - [ ] Auto scroll works as in the Logs tab
    - Tools tab
        - [ ] Crash button crashes the app
            - [ ] I see the crash in Firebase Crashlytics