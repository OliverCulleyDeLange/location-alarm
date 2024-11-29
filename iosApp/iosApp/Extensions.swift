import Foundation

extension NSObject {
    func apply(_ block: (Self) -> Void) -> Self {
        block(self)
        return self
    }
}
