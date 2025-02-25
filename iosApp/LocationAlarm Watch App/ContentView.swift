//
//  ContentView.swift
//  LocationAlarm Watch App
//
//  Created by Oliver de Lange on 18/02/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct ContentView: View {
   
    
    var body: some View {
        VStack {
            Button("Enable Alarm", action: {
                SLog.w("TEST")
                WatchSessionManager.shared.enableAlarm()
            })
        }
        .padding()
    }
}

#Preview {
    ContentView()
}
