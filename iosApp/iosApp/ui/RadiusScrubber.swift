import SwiftUI

struct RadiusScrubber: View {
    @State var radiusMeters: Int32
    var minRadius: Int32 = 10
    var onRadiusChanged: (Int32) -> Void = {_ in}
    
    @State private var radiusAtDragStart: CGFloat?
    
    var body: some View {
        ScrollViewReader { scrollView in
            VStack {
                Text("Radius:")
                Text("\(radiusMeters)")
                    .gesture(DragGesture()
                        .onChanged { change in
                            print("Change \(change.translation.height)")
                            if radiusAtDragStart == nil {
                                radiusAtDragStart = Optional(CGFloat(radiusMeters))
                            }
                            // *2 just to increase the rate of change a bit
                            var newRadius = Int32(radiusAtDragStart! - (change.translation.height * 5))
                            if newRadius < minRadius { newRadius = minRadius }
                            radiusMeters = newRadius
                            onRadiusChanged(Int32(radiusMeters))
                            
                        }
                        .onEnded {_ in
                            radiusAtDragStart = nil
                        }
                    )
                
            }
        }
    }
}

struct RadiusScroller_Previews: PreviewProvider {
    static var previews: some View {
        RadiusScrubber(radiusMeters: 500).frame(width: 100).border(.pink)
    }
}
