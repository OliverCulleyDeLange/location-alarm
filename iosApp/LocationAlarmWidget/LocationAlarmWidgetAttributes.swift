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

struct LocationAlarmWidgetAttributes: ActivityAttributes {
    public struct ContentState: Codable, Hashable {
        var distanceToAlarm: String
    }
}
