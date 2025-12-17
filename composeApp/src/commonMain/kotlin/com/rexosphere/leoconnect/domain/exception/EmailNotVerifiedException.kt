package com.rexosphere.leoconnect.domain.exception

/**
 * Exception thrown when a user tries to sign in with an unverified email address.
 * This is a special case that should be handled to show the verification pending screen.
 */
class EmailNotVerifiedException(message: String) : Exception(message)
