package com.enozom.poc.e_invoice.utils.zatcaParser

import android.content.ContentResolver
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.enozom.poc.e_invoice.utils.ZATCAQRCode
import com.enozom.poc.e_invoice.utils.extensions.decodeTLVToSet
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.io.FileNotFoundException

class ZATCAParser {

    companion object {
        /**
         * parse scanned code to retrieve the ZATCA bill info and returns the parsed values
         * @param: scannedQRCode : String
         * @return: ZATCA parsed from QR code
         * */
        fun getZATCABillInfoFromQRCode(
            scannedQRCode: String?,
            onParseCompleteCallback: OnParseCompleteCallback<ZATCAQRCode>
        ) {
            scannedQRCode?.let { qrCode ->
                //convert Base64 code to byteArray
                Base64.decode(qrCode, Base64.DEFAULT)?.let { data ->
                    try {
                        //convert byteArray to set of TLV values
                        val tlvSet = data.decodeTLVToSet()
                        //take action based on values
                        if (tlvSet.size == 5) {
                            onParseCompleteCallback.onParseComplete(
                                status = ParseStatus.Success,
                                ParseResult(
                                    data = ZATCAQRCode(
                                        tlvSet.elementAt(0),
                                        tlvSet.elementAt(1),
                                        tlvSet.elementAt(2),
                                        tlvSet.elementAt(3),
                                        tlvSet.elementAt(4)
                                    )
                                )
                            )

                        } else {
                            onParseCompleteCallback.onParseComplete(
                                ParseStatus.Failure,
                                ParseResult<ZATCAQRCode>(
                                    error = ParseError(
                                        errorMsg = "Not a ZATCA qr code try to use getTLVFromQRCode",
                                        errorType = ErrorType.ZATCAQRCodeNotFound
                                    )
                                )
                            )
                        }

                    } catch (e: IndexOutOfBoundsException) {
                        //not valid QR code scanned
                        Log.e("ZATCA parser", e.localizedMessage ?: "")
                        onParseCompleteCallback.onParseComplete(
                            ParseStatus.Failure,
                            ParseResult<ZATCAQRCode>(
                                error = ParseError(
                                    errorMsg = "Scanned QR code is not valid",
                                    errorType = ErrorType.InvalidQRCode
                                )
                            )
                        )
                    } catch (e: Exception) {
                        Log.e("ZATCA parser", e.localizedMessage ?: "")
                    }
                }
            }
        }

        fun getTLVFromQRCode(
            scannedQRCode: String?,
            onParseCompleteCallback: OnParseCompleteCallback<Set<String>>
        ) {
            scannedQRCode?.let { qrCode ->
                //convert Base64 code to byteArray
                Base64.decode(qrCode, Base64.DEFAULT)?.let { data ->
                    try {
                        //convert byteArray to set of TLV values
                        onParseCompleteCallback.onParseComplete(
                            ParseStatus.Success,
                            ParseResult(data = data.decodeTLVToSet())
                        )
                    } catch (e: IndexOutOfBoundsException) {
                        //not valid QR code scanned
                        Log.e("ZATCA parser", e.localizedMessage ?: "")
                        onParseCompleteCallback.onParseComplete(
                            ParseStatus.Failure,
                            ParseResult(
                                error = ParseError(
                                    errorMsg = "Scanned QR code is not valid",
                                    errorType = ErrorType.InvalidQRCode
                                )
                            )
                        )
                    } catch (e: Exception) {
                        Log.e("ZATCA parser", e.localizedMessage ?: "")
                    }
                }
            }
        }

        /**
         * extract QR code from provided image then parse the found QR - if any- and logs the parsed values
         * @param: uri : imageUri
         * */
        fun getZATCABillInfoFrom(
            uri: Uri,
            contentResolver: ContentResolver,
            onParseCompleteCallback: OnParseCompleteCallback<ZATCAQRCode>
        ) {
            try {
                contentResolver.openInputStream(uri)?.let { inputStream ->
                    BitmapFactory.decodeStream(inputStream)?.let { bitmap ->
                        //get bitmap from read bytes from gallery
                        val width = bitmap.width
                        val height = bitmap.height
                        val pixels = IntArray(width * height)
                        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
                        bitmap.recycle()
                        val source = RGBLuminanceSource(width, height, pixels)
                        val bBitmap = BinaryBitmap(HybridBinarizer(source))
                        val reader = MultiFormatReader()
                        try {
                            //get QR code from image using ZXing scanner
                            reader.decode(bBitmap).text?.let { qrCode ->
                                //get values from QR code
                                getZATCABillInfoFromQRCode(qrCode, onParseCompleteCallback)
                            }

                        } catch (e: NotFoundException) {
                            Log.e("ZATCA parser", "decode exception", e)
                            onParseCompleteCallback.onParseComplete(
                                ParseStatus.Failure,
                                ParseResult<ZATCAQRCode>(
                                    error = ParseError(
                                        errorMsg = "No valid QR code was found in the selected image",
                                        errorType = ErrorType.QRCodeNotFound
                                    )
                                )
                            )
                        }
                    }
                }

            } catch (e: FileNotFoundException) {
                Log.e("ZATCA parser", "can not open file$uri", e)
                onParseCompleteCallback.onParseComplete(
                    ParseStatus.Failure,
                    ParseResult<ZATCAQRCode>(
                        error = ParseError(
                            errorMsg = "Selected image cannot be opened",
                            errorType = ErrorType.FileNotFound
                        )
                    )
                )
            }
        }
    }
}