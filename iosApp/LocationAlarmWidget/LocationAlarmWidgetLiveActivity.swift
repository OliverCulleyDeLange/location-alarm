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
                let text = context.state.distanceToAlarm != nil ?
                    "Distance to alarm: \(context.state.distanceToAlarm ?? "")" :
                    "Location alarm active!"
                DynamicIslandExpandedRegion(.leading) {
                    Image(systemName: "location")
                        .resizable()
                        .frame(width: 24, height: 24)
                        .foregroundStyle(.primary)
                }
                DynamicIslandExpandedRegion(.trailing) {
                    HStack {
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
                DynamicIslandExpandedRegion(.bottom) {
                    Text("\(text)")
                }
            } compactLeading: {
                Image(systemName: "location")
                    .resizable()
                    .frame(width: 24, height: 24)
                    .foregroundStyle(.primary)
            } compactTrailing: {
                HStack {
                    Image(systemName: "alarm")
                        .resizable()
                        .frame(width: 24, height: 24)
                        .foregroundStyle(.primary)
                    Image(systemName: "checkmark.circle.fill")
                        .resizable()
                        .frame(width: 24, height: 24)
                        .foregroundStyle(.green)
                }
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
        LocationAlarmWidgetAttributes.ContentState(distanceToAlarm: "100m")
    }
}

#Preview("Notification", as: .content, using: LocationAlarmWidgetAttributes.preview) {
    LocationAlarmWidgetLiveActivity()
} contentStates: {
    LocationAlarmWidgetAttributes.ContentState.empty
    LocationAlarmWidgetAttributes.ContentState.withValue
}
