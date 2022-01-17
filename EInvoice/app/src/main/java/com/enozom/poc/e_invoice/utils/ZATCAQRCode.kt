package com.enozom.poc.e_invoice.utils

import java.io.Serializable

data class ZATCAQRCode(
    val companyName: String? = null,
    val VATNumber: String? = null,
    val dateOfInvoice: String? = null,
    val invoiceTotal: String? = null,
    val VATTotal: String? = null
) : Serializable
