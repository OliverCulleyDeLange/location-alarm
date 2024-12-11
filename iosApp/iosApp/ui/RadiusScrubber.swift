import SwiftUI

struct RadiusScrubber: View {
    @State var radiusMeters: Int32
    var onRadiusChanged: (Int32) -> Void = {_ in}
    
    @State private var radiusAtDragStart: CGFloat?
    
    var body: some View {
        ScrollViewReader { scrollView in
            VStack {
                Text("Radius:")
                    .foregroundStyle(.secondary)
                    .fontWeight(.bold)
                Text("\(radiusMeters)")
                    .font(.title)
                    .fontWeight(.black)
            }.padding(4)
                .foregroundStyle(Color(.primary))
                .background(Color(.primaryContainer))
                .cornerRadius(8)
                .padding(8)
                .gesture(DragGesture()
                .onChanged { change in
                    print("Change \(change.translation.height)")
                    if radiusAtDragStart == nil {
                        radiusAtDragStart = Optional(CGFloat(radiusMeters))
                    }
                    // *5 just to increase the rate of change a bit
                    radiusMeters = Int32(radiusAtDragStart! - (change.translation.height * 5))
                    onRadiusChanged(Int32(radiusMeters))
                    
                }
                .onEnded {_ in
                    radiusAtDragStart = nil
                }
            )
        }
    }
}

struct RadiusScroller_Previews: PreviewProvider {
    static var previews: some View {
        RadiusScrubber(radiusMeters: 500).frame(width: 100)
    }
}
