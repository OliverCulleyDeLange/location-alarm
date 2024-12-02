import SwiftUI

struct RadiusScroller: View {
    @State var radiusMeters: Int32
    var minRadius: Int32 = 1
    var maxRadius: Int32 = 1000
    var onRadiusChanged: (Int32) -> Void = {_ in}
    
    @State private var scrolledTo: Int32?
    
    var body: some View {
        ScrollViewReader { scrollView in
            ZStack {
                Text("\(radiusMeters)")
                
                ScrollView(.vertical){
                    LazyVStack(alignment: .trailing, spacing: 10) {
                        ForEach(minRadius...maxRadius, id: \.self) { radius in
                            HStack{
                                Spacer()
                                ZStack(alignment: .trailing) {
                                    let width = radius % 10 == 0 ? 50 : radius % 5 == 0 ? 25 : 10
                                    Rectangle().frame(height: 3).containerRelativeFrame(.horizontal, count: 100, span: width, spacing: 0)
                                }
                            }.id(radius)
                        }
                    }.scrollTargetLayout()
                }
                .scrollPosition(id: $scrolledTo, anchor: UnitPoint.center)
                .onChange(of: scrolledTo) {
                    print("----> scrolledTo: \(scrolledTo)")
                    if let unwrapped = scrolledTo {
                        radiusMeters = unwrapped
                        onRadiusChanged(unwrapped)
                    }
                }
                .onAppear{
                    print("----> OnAppear: \(radiusMeters)")
                    scrollView.scrollTo(radiusMeters, anchor: UnitPoint.center)
                }
            }
        }
    }
}

struct RadiusScroller_Previews: PreviewProvider {
    static var previews: some View {
        RadiusScroller(radiusMeters: 500).frame(width: 100).border(.pink)
    }
}
