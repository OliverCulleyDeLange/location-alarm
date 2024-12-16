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
    fileprivate func getText(_ context: ActivityViewContext<LocationAlarmWidgetAttributes>) -> String {
        if (context.state.alarmTriggered) {
            return "You have reached your destination!"
        } else if (context.state.distanceToAlarm != nil) {
            return "Distance to alarm: \(context.state.distanceToAlarm ?? "")"
        } else {
            return "Location alarm active!"
        }
    }
    
    var body: some WidgetConfiguration {
        ActivityConfiguration(for: LocationAlarmWidgetAttributes.self) { context in
            // Live Activity UI
            HStack {
                Image("Icon")
                    .resizable()
                    .frame(width: 24, height: 24)
                    .foregroundStyle(.primary)
                Spacer().frame(width: 8)
                Text("\(getText(context))")
                    .multilineTextAlignment(.center)
                    .fontWeight(.bold)
                    .foregroundStyle(.primary)
                    
                if (context.state.alarmTriggered){
                    Spacer().frame(width: 16)
                    Link(destination: URL(string: "uk.co.oliverdelange.locationalarm://action/stop_alarm")!) {
                        Text("Stop Alarm")
                    }
                }
            }
            .activityBackgroundTint(context.state.alarmTriggered ? Color.orange : nil)
        } dynamicIsland: { context in
            // Expanded UI
            DynamicIsland {
                DynamicIslandExpandedRegion(.leading) {
                    LocationAlarmIcons(alarmTriggered: context.state.alarmTriggered)
                }
                DynamicIslandExpandedRegion(.bottom) {
                    Text("\(getText(context))")
                }
            }
            // Compact UI
            compactLeading: {
                LocationAlarmIcons(alarmTriggered: context.state.alarmTriggered)
            }
            compactTrailing: {
                let text = context.state.alarmTriggered ? "Arrived!" :
                    "\(context.state.distanceToAlarm ?? "Active")"
                Text("\(text)")
                    .foregroundStyle(Color(context.state.alarmTriggered ? .orange : .accentColor))
                    .padding(EdgeInsets(top: 0, leading: 0, bottom: 0, trailing: 4))
            }
            // Minimal UI
            minimal: {
                Image("LocationAlarmIcon")
                    .resizable()
                    .frame(width: 20, height: 20)
                    .foregroundStyle(context.state.alarmTriggered ? Color.orange : Color.green)
            }
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
        LocationAlarmWidgetAttributes.ContentState(distanceToAlarm: nil, alarmTriggered: false)
    }
    
    fileprivate static var withDistance: LocationAlarmWidgetAttributes.ContentState {
        LocationAlarmWidgetAttributes.ContentState(distanceToAlarm: "100", alarmTriggered: false)
    }
    
    fileprivate static var withDistanceTriggered: LocationAlarmWidgetAttributes.ContentState {
        LocationAlarmWidgetAttributes.ContentState(distanceToAlarm: "100", alarmTriggered: true)
    }
}

#Preview("Active", as: .content, using: LocationAlarmWidgetAttributes.preview) {
    LocationAlarmWidgetLiveActivity()
} contentStates: {
    LocationAlarmWidgetAttributes.ContentState.withDistance
    LocationAlarmWidgetAttributes.ContentState.empty
}

#Preview("Triggered", as: .content, using: LocationAlarmWidgetAttributes.preview) {
    LocationAlarmWidgetLiveActivity()
} contentStates: {
    LocationAlarmWidgetAttributes.ContentState.withDistanceTriggered
}
