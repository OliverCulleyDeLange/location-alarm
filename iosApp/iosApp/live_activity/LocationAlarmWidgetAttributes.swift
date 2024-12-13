//
//  LocationAlarmWidgetAttributes.swift
//  iosApp
//
//  Created by Oliver de Lange on 13/12/2024.
//  Copyright © 2024 orgName. All rights reserved.
//


import ActivityKit
import Combine
import Foundation

//FIXME this is duplicated in LocationAlarmWidget
// Doesn't compile when it doesn't exist in both places
struct LocationAlarmWidgetAttributes: ActivityAttributes {
    public struct ContentState: Codable, Hashable {
        // Dynamic stateful properties about your activity go here!
        var distanceToAlarm: String
    }
}
