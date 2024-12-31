import SwiftUI
import KMPObservableViewModelSwiftUI
import Shared
import Combine
import KMPNativeCoroutinesCombine

/// I wanna support ios15 and SwiftUI Navigation sucks and doesn't support declarative navigation like compose navigation.
/// Data object comparison doesn't work on real is devices, only simulators for some reason
/// Hence exposing the name() string here instead of the kotlin sealed interface Route type
class NavigationViewModel: ObservableObject {
    @Published var currentScreen: String = ""
    
    private var cancellables = Set<AnyCancellable>()
    
    init(statePublisher: AnyPublisher<AppState, Error>) {
        statePublisher
            .assertNoFailure()
            .map { state in state.currentScreen.name()}
            .receive(on: DispatchQueue.main)
            .assign(to: &$currentScreen)
            
        
    }
}
