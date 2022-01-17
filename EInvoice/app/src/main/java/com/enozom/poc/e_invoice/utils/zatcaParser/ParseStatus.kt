package com.enozom.poc.e_invoice.utils.zatcaParser

enum class ParseStatus(val value:Int){
    Success(0),
    Failure(1)
}

enum class ErrorType(val value: Int){
    FileNotFound(0),
    InvalidQRCode(1),
    QRCodeNotFound(2),
    ZATCAQRCodeNotFound(3)
}