import SwiftUI

struct RadiusScrubber: View {
    @State var radiusMeters: Int32
    var onRadiusChanged: (Int32) -> Void = {_ in}
    
    @State private var radiusAtDragStart: CGFloat?
    
    @State private var offsetUp: CGFloat = 0
    @State private var offsetDown: CGFloat = 0
    
    var body: some View {
        ScrollViewReader { scrollView in
            VStack {
                Icon(iconName: "arrow.up")
                    .offset(y: offsetUp)
                    .animation(.easeInOut, value: offsetUp)
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
                    .onTapGesture {
                        Task {
                            await animateHelper()
                        }
                    }
            }
            Icon(iconName: "arrow.down")
                .offset(y: offsetDown)
        }
    }

    func animateHelper() async {
        withAnimation(.easeInOut(duration: 0.3)) {
            offsetUp = -100
        }
        try? await sleepFor(milliseconds: 300)
        withAnimation(.easeInOut(duration: 0.2)) {
            offsetUp = 0
        }
        
        withAnimation(.easeInOut(duration: 0.3)) {
            offsetDown = 100
        }
        try? await sleepFor(milliseconds: 300)
        withAnimation(.easeInOut(duration: 0.2)) {
            offsetDown = 0
        }
    }
}

struct Icon: View {
    let iconName: String
    var body: some View {
        Image(systemName: iconName)
            .resizable()
            .scaledToFit()
            .foregroundColor(Color(.primaryContainer))
            .frame(width: 24)
    }
}

struct RadiusScroller_Previews: PreviewProvider {
    static var previews: some View {
        RadiusScrubber(radiusMeters: 500).frame(width: 100)
    }
}
