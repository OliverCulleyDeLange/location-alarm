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
            let text = context.state.distanceToAlarm != nil ?
                "Distance to alarm: \(context.state.distanceToAlarm ?? "")" :
                "Location alarm active!"
            VStack {
                Text("\(text)")
            }
            .activityBackgroundTint(Color.cyan)
            .activitySystemActionForegroundColor(Color.black)
            
        } dynamicIsland: { context in
            DynamicIsland {
                DynamicIslandExpandedRegion(.leading) {
                    LocationAlarmIcons()
                }
                // Feels like overload of info given its repeated in the expanded section
//                DynamicIslandExpandedRegion(.trailing) {
//                    let text = context.state.distanceToAlarm != nil ?
//                        "\(context.state.distanceToAlarm ?? "")m" :
//                        "Active!"
//                    Text("\(text)")
//                }
                DynamicIslandExpandedRegion(.bottom) {
                    let text = context.state.distanceToAlarm != nil ?
                        "Distance to alarm: \(context.state.distanceToAlarm ?? "")m" :
                        "Location alarm active"
                    Text("\(text)")
                }
            } compactLeading: {
                LocationAlarmIcons()

            } compactTrailing: {
                let text = context.state.distanceToAlarm != nil ?
                    "\(context.state.distanceToAlarm ?? "")m" :
                    "Active!"
                Text("\(text)")
            } minimal: {
                Image(systemName: "location")
                    .resizable()
                    .frame(width: 24, height: 24)
                    .foregroundStyle(.primary)
            }
//            .widgetURL(URL(string: "http://www.apple.com"))
//            .keylineTint(Color.red)
        }
    }
}

extension LocationAlarmWidgetAttributes {
    fileprivate static var preview: LocationAlarmWidgetAttributes {
        LocationAlarmWidgetAttributes()
    }
}

extension LocationAlarmWidgetAttributes.ContentState {
    fileprivate static var empty: LocationAlarmWidgetAttributes.ContentState {
        LocationAlarmWidgetAttributes.ContentState(distanceToAlarm: nil)
    }
    
    fileprivate static var withValue: LocationAlarmWidgetAttributes.ContentState {
        LocationAlarmWidgetAttributes.ContentState(distanceToAlarm: "100")
    }
}

#Preview("Notification", as: .content, using: LocationAlarmWidgetAttributes.preview) {
    LocationAlarmWidgetLiveActivity()
} contentStates: {
    LocationAlarmWidgetAttributes.ContentState.withValue
    LocationAlarmWidgetAttributes.ContentState.empty
}

struct LocationAlarmIcons: View {
    var body: some View {
        HStack {
            Image(systemName: "location")
                .resizable()
                .frame(width: 24, height: 24)
                .foregroundStyle(.primary)
            Image(systemName: "alarm")
                .resizable()
                .frame(width: 24, height: 24)
                .foregroundStyle(.primary)
            Image(systemName: "checkmark.circle.fill")
                .resizable()
                .frame(width: 24, height: 24)
                .foregroundStyle(.green)
                .padding(2)
        }
    }
}
