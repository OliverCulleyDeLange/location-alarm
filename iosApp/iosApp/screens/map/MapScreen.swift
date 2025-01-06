import SwiftUI
import Shared
import KMPObservableViewModelSwiftUI
@_spi(Experimental) import MapboxMaps

struct MapScreen: View {
    
    @StateViewModel private var viewModel: MapViewModel = get()
    
    var body: some View {
        MapScreenContent(
            state: viewModel.state, onEvent: { viewModel.onEvent(uiEvent: $0) }
        )
        .alert(isPresented: .constant(viewModel.state.shouldShowAlarmAlert)) {
            Alert(
                title: Text("Wakey Wakey"),
                message: Text("You have reached your destination."),
                dismissButton: .default(Text("Stop Alarm")){
                    viewModel.onEvent(uiEvent: UserEventTappedStopAlarm())
                }
            )
        }
        .onAppear {
            SLog.d("Map did appear")
            viewModel.onEvent(uiEvent: UiResultMapShown())
        }
        .onDisappear{
            SLog.d("Map did dissapear")
            viewModel.onEvent(uiEvent: UiResultMapNotShown())
        }
    }
}
