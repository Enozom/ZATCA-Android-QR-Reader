package com.enozom.poc.e_invoice.utils.extensions

import android.util.Log
import java.nio.charset.StandardCharsets

/**
 * converts TLV ByteArray to set of values
 * @param : no parameters
 * @throws IndexOutOfBoundsException if it's used to parse a QR code which is not a TLV
 * @return : set of strings containing values of the TLV
 *
 * */
fun ByteArray.decodeTLVToSet(): Set<String> {
    val dataSet = mutableSetOf<String>()
    try {
        if (this.size > 2 && this.sliceArray(0 until this[1].toInt()).isNotEmpty()) {
            //has atleast one TLV
            var tagIndex = 0
            var length = this[1].toInt()
            while (tagIndex + length + 1 < this.lastIndex) {
                var value = String(
                    this.slice((tagIndex + 2)..(tagIndex + length + 1)).toByteArray(),
                    StandardCharsets.UTF_8
                )
                dataSet.add(value)
                tagIndex += length + 2
                length = this[tagIndex + 1].toInt()
            }
            dataSet.add(
                String(
                    this.slice((tagIndex + 2)..(tagIndex + length + 1)).toByteArray(),
                    StandardCharsets.UTF_8
                )
            )
        }
    } catch (e: IndexOutOfBoundsException) {
        Log.e("ByteArrayUtils", e.localizedMessage ?: "")
        throw e
    } catch (e: Exception) {
        Log.e("ByteArrayUtils", e.localizedMessage ?: "")
    }
    return dataSet
}