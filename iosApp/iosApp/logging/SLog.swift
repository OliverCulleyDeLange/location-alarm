import Shared

/// A thin wrapper around shared logging to provide syntactic sugar
class SLog {
    static func e(_ m: String) {
        Shared.SLog.shared.e(m: m)
    }
    static func w(_ m: String) {
        Shared.SLog.shared.w(m: m)
    }
    static func i(_ m: String) {
        Shared.SLog.shared.i(m: m)
    }
    static func d(_ m: String) {
        Shared.SLog.shared.d(m: m)
    }
    static func v(_ m: String) {
        Shared.SLog.shared.v(m: m)
    }
}
