# Location Alarm

- An app that sounds an alarm when you enter a geofence.
- Also an excuse to learn multiplatform app development with KMM.
- Also an excuse to play with fun things like maps, location, notifications, ios widgets, voice
  commands, watch apps, ya know, cool shit you wouldn't usually play with.

# Priority 1
- Check for memory leaks and performance issues esp around cancellation and lifetimes

## Priority 2

- M Tutorial
- L UI Tests?
- M Log snapshot tests - Check logs for given flow haven't changed
- L Allow searching for locations with dropdown results
- S Draw path of route while alarm enabled
- Apple watch app / Android watch app
- Alarm config (sound, vibration pattern)
- Recent geofences
  - Save most recent geofence location and radius
- Favourite geofences
  - Allow user to save the geofence to a quick select button
- Sign in and sync (sync favorite geofences as a most basic sync operation)
  - Yes totally not that useful, but a useful learning excersize

### Priority 3

- M Capture GPX file for alarm enabled session - to aid GPS issue debugging
- S Code quality
- Polygonal geofence areas
- Allow multiple geofences to be active
- Other actions when in geofence
- Inverse location alarm (anchor watch)
- Voice commands "Hey google, wake me when i get to woolwich
- Weekly notifications - caught up on x hours extra sleep this week
- S Firebase app distribution
- S Bug reporting