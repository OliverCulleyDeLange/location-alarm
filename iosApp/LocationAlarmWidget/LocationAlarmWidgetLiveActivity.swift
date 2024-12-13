//
//  LocationAlarmWidgetLiveActivity.swift
//  LocationAlarmWidget
//
//  Created by Oliver de Lange on 13/12/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import ActivityKit
import WidgetKit
import SwiftUI

struct LocationAlarmWidgetLiveActivity: Widget {
    var body: some WidgetConfiguration {
        ActivityConfiguration(for: LocationAlarmWidgetAttributes.self) { context in
            // Lock screen/banner UI goes here
            VStack {
                Text("Distance to alarm: \(context.state.distanceToAlarm)")
            }
            .activityBackgroundTint(Color.cyan)
            .activitySystemActionForegroundColor(Color.black)

        } dynamicIsland: { context in
            DynamicIsland {
                // Expanded UI goes here.  Compose the expanded UI through
                // various regions, like leading/trailing/center/bottom
                DynamicIslandExpandedRegion(.leading) {
                    Text("Leading")
                }
                DynamicIslandExpandedRegion(.trailing) {
                    Text("Trailing")
                }
                DynamicIslandExpandedRegion(.bottom) {
                    Text("Bottom \(context.state.distanceToAlarm)")
                    // more content
                }
            } compactLeading: {
                Text("L")
            } compactTrailing: {
                Text("T \(context.state.distanceToAlarm)")
            } minimal: {
                Text(context.state.distanceToAlarm)
            }
            .widgetURL(URL(string: "http://www.apple.com"))
            .keylineTint(Color.red)
        }
    }
}

extension LocationAlarmWidgetAttributes {
    fileprivate static var preview: LocationAlarmWidgetAttributes {
        LocationAlarmWidgetAttributes()
    }
}

extension LocationAlarmWidgetAttributes.ContentState {
    fileprivate static var smiley: LocationAlarmWidgetAttributes.ContentState {
        LocationAlarmWidgetAttributes.ContentState(distanceToAlarm: "😀")
     }
     
     fileprivate static var starEyes: LocationAlarmWidgetAttributes.ContentState {
         LocationAlarmWidgetAttributes.ContentState(distanceToAlarm: "🤩")
     }
}

#Preview("Notification", as: .content, using: LocationAlarmWidgetAttributes.preview) {
   LocationAlarmWidgetLiveActivity()
} contentStates: {
    LocationAlarmWidgetAttributes.ContentState.smiley
    LocationAlarmWidgetAttributes.ContentState.starEyes
}
