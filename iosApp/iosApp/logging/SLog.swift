import Shared

/// A thin wrapper around shared logging to provide syntactic sugar
class SLog {
    static func e(_ m: String) {
        Shared.SLog.companion.e(m: m)
    }
    static func w(_ m: String) {
        Shared.SLog.companion.w(m: m)
    }
    static func i(_ m: String) {
        Shared.SLog.companion.i(m: m)
    }
    static func d(_ m: String) {
        Shared.SLog.companion.d(m: m)
    }
    static func v(_ m: String) {
        Shared.SLog.companion.v(m: m)
    }
}
