# QA

## What is this?

- Manual tests to complete before releasing. Copy this file into the [qa](./qa) directory and rename
  with the version number like `QA_1.0.0.md`
- Ideally replace these with automated UI tests eventually.
- Acts as a complete list of everything the app should do.

Copy me, paste me ✅ ❌

## Location Permissions

Location permissions are request on first app start. The map screen doesn't show unless they're
granted.

- Fresh app install
    - ✅ I see location permissions rationale
    - I tap 'allow location access'
        - ✅ I see location permission dialog
        - I tap 'allow once' (IOS) / 'Only this time' (Android)
            - ✅ I see the map screen
            - ❌ I see the map screen - I force close and reopen app - I see location permissions
              rationale
                - `Potentially i don't understand how the once permission works on android`
      - ✅ I tap 'allow while using app' - I see the map screen
        - ✅ I tap 'don't allow' - I see location permissions denied message
- ✅ Location permissions denied previously - I see location permissions rationale
- ✅ Location permissions denied previously - I tap 'allow location access' - I Deny - I see location
  permissions denied message
- ✅ Deny location permissions - Manually enable via settings - Reopen app (don't force close) - I
  see the map screen

## Notification Permissions

Notification permissions are requested the first time the alarm is enabled.

- ✅Fresh app install - On map screen - Tap enable alarm - I see notification permissions dialog
- ✅Fresh app install - On map screen - Tap enable alarm - Tap 'Allow' - Alarm is enabled
- ✅Fresh app install - On map screen - Tap enable alarm - Tap 'Don't Allow - I see a notification
  permissions denied message - 'Enable alarm' button is disabled
- ✅ Notification permissions denied previously - Open app - On map screen - I see notification
  permissions denied message - 'Enable alarm' button is disabled
- ✅ Notification permissions denied previously - Manually enable in settings - Open app - On map
  screen - 'Enable alarm' works as expected

## Set Geofence

- ✅ Map screen - geofence moves with location updates
- ✅ Map screen - Tap somewhere on the map - geofence moves to tap location
- ✅ Map screen - Tap somewhere on the map - geofence stops moving with location updates
- ✅ Map screen - Tap radius scrubber - arrows animate up and down
- ✅ Map screen - Drag radius scrubber up - Radius increases - Geofence gets bigger
- ✅ Map screen - Drag radius scrubber down - Radius decreases - Geofence gets smaller
- ✅ Map screen - Drag radius scrubber down a lot - Radius decreases with minimum 10m - Geofence
  gets smaller until minimum 10m

## Location Alarm

Assuming notification permissions are enabled.
See [Notification Permissions](#notification-permissions).

### Set alarm

- Map screen - Tap enable alarm
    - ✅ Alarm is enabled (Button says 'Disable Alarm') - Distance to alarm is shown
    - ✅ Persistent Notification (Android) / Live Activity (ios) is shown in notification drawer
      with distance to alarm shown
    - ✅ When my location updates - Then distance to alarm is updated in persistent notification /
      live activity

### Alarm Triggered

#### App in Foreground

- Map screen - Tap enable alarm - Location updates to within geofence
    - ✅ Alarm sounds & vibrates
    - ✅ Alert is shown with option to 'Stop Alarm'
    - ✅ Persistent notification becomes bright color and gives option to stop alarm
    - ✅ Tap stop alarm in alert UI - Alarm stops, persistent notification is dismissed
    - ✅ Tap stop alarm in persistent notification - App opens, alarm stops, persistent
      notification
      is dismissed

#### App in Background

- Map screen - Tap enable alarm - **Background app** - Location updates to within geofence
    - ✅ Alarm sounds & vibrates
    - ❌ A notification is shown to draw attention to the alarm being triggered which gives the
      option to stop alarm
        - `No notification pops down`
      - `Notification updates when alarm is disabled and radius changes `
    - ✅ Persistent notification in notification drawer becomes bright color and gives option to
      stop alarm
    - ✅Tap stop alarm in persistent notification - App opens, alarm stops, persistent
      notification is dismissed

#### Phone locked

- Map screen - Tap enable alarm - **Lock app** - Location updates to within geofence
    - ✅ Alarm sounds & vibrates
    - ✅ Persistent notification on lock screen becomes bright color and gives option to stop alarm
    - ✅ Tap stop alarm in persistent notification - App opens, alarm stops, persistent
      notification
      is dismissed

#### Update Alarm Geofence while alarm enabled

- ✅ Map screen - Tap enable alarm - Increase radius until my location is within geofence - Alarm
  sounds & vibrates
- ✅ Map screen - Tap enable alarm - Tap my location on the map - Alarm sounds & vibrates

### Edge cases

- Location alarm works as in [alarm triggered](#alarm-triggered) when:
    - ✅ I have battery saver mode enabled (Not tested with extreme as i don't think i should support
      that)
    - ✅ My phone is on silent
- ✅ My device (emulator) doesn't have internet - I open the app and enable the alarm - I see 'Alarm
  active' in place of distance to alarm
    -
    `Ideally we should disable the enable alarm button until users location is known and there's a geofence. Added to TODO P1`

### Platform specific

- ✅ Android system alarm volume is respected
- [ ] Android emulator crap check:
    - [ ] Android 12 (31)
    - [ ] Android 13 (33)
    - [ ] Android 14 (34)
    - [ ] Android 15 (35)
    - [ ] Android 16 (36) (preview)

## Fly to current location

- ✅ Map screen - Tap location icon bottom left - Current location (blue dot) will become centered
- ✅ Map screen - Tap location icon bottom left - Current location (blue dot) will become
  centered - Tap again before new location comes through - Current location is centered
    - Regression: This ensures the fly to functionality works even if the location hasn't updated.
