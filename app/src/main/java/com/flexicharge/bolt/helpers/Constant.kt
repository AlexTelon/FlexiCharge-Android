package com.flexicharge.bolt.helpers

object StatusCode {
    const val ok = 200
    const val created = 201
    const val badRequest = 400
    const val notFound = 404
    const val unAuthorized = 401
}

object TextInputType {
    const val isEmail = "is email"
    const val isPassword = "is password"
    const val isConfirmationCode = "is conformation code"
    const val isTooLong = "is too long"
    const val isName = "is name"
    const val isPostCode = "is postcode"
    const val isAddress = "is address"
    const val isTown = "is Town"
    const val isPhoneNumber = "is phone number"
}

object LoginChecker {
    var LOGGED_IN = false
}