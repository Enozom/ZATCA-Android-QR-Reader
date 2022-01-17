package com.enozom.poc.e_invoice.utils.zatcaParser

data class ParseResult<T>(
    val data: T? = null,
    val error: ParseError? = null
)

data class ParseError(
    val errorMsg: String? = null,
    val errorType: ErrorType? = null
)
