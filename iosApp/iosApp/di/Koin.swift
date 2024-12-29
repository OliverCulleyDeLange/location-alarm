import Shared

func get<A: AnyObject>() -> A {
    return KoinProvider.companion.koin.get(objCClass: A.self) as! A
}

func get<A: AnyObject>(_ type: A.Type) -> A {
    return KoinProvider.companion.koin.get(objCClass: A.self) as! A
}

func get<A: AnyObject>(_ type: A.Type, qualifier: (any Koin_coreQualifier)? = nil, parameter: Any) -> A {
    return KoinProvider.companion.koin.get(objCClass: A.self, qualifier: qualifier, parameter: parameter) as! A
}
