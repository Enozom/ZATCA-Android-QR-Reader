package com.enozom.poc.e_invoice.utils.zatcaParser

fun interface OnParseCompleteCallback<T> {
    fun onParseComplete(status: ParseStatus, result : ParseResult<T>)
}