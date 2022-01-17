package com.enozom.poc.e_invoice

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.enozom.poc.e_invoice.utils.SnackBarUtils
import com.enozom.poc.e_invoice.utils.ZATCAQRCode
import com.enozom.poc.e_invoice.utils.customViews.ZATCAScannerView
import com.enozom.poc.e_invoice.utils.extensions.checkCameraPermission
import com.enozom.poc.e_invoice.utils.zatcaParser.ErrorType
import com.enozom.poc.e_invoice.utils.zatcaParser.OnParseCompleteCallback
import com.enozom.poc.e_invoice.utils.zatcaParser.ParseStatus
import com.enozom.poc.e_invoice.utils.zatcaParser.ZATCAParser

open class ZATCAScannerActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName

    private lateinit var requestMultiplePermissionsLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var getContentLauncher: ActivityResultLauncher<String>

    private lateinit var zatacSv: ZATCAScannerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zatca_scanner)
        setupScannerView()
        setCustomAttrFromIntent()
        initializePermissionsRequest()
        initializeContentLauncher()
        setClickListeners()
        requestMultiplePermissionsLauncher.launch(
            arrayOf(Manifest.permission.CAMERA)
        )
    }

    override fun onResume() {
        super.onResume()

        zatacSv.refreshPermissionHolder(checkCameraPermission())
    }

    override fun onPause() {
        super.onPause()
        zatacSv.pause()
    }

    private fun setCustomAttrFromIntent() {
        intent?.getIntExtra(TXT_COLOR_ID, -1)?.let {
            if (it != -1) {
                zatacSv.setTextColor(ContextCompat.getColor(zatacSv.context, it))
            }
        }
        intent?.getIntExtra(BTN_BK_COLOR_ID, -1)?.let {
            if (it != -1) {
                zatacSv.setPickImageBtnBKColor(it)
                zatacSv.setScanIndicator(it)
                zatacSv.setCamPermissionTxtColor(it)
            }
        }
        intent?.getIntExtra(SEP_COLOR, -1)?.let {
            if (it != -1) {
                zatacSv.setSeparatorColor(it)
            }
        }
    }


    /**
     *  initialize Barcode view and set to decode once if the scanned value wasn't correct it will try to scan indefinitely
     * */
    private fun setupScannerView() {
        zatacSv = findViewById<ZATCAScannerView>(R.id.zatca_sv)
        //starting with continues decoding cause th barcode view to lag
        zatacSv.decodeSingle { result ->
            Log.i(TAG, "barcodeResult: ${result.text}")
            result.text?.let {
                getQRCodeContent(qrCode = it) { status, result ->
                    when (status) {
                        ParseStatus.Success -> {
                            setResult(Activity.RESULT_OK, Intent().apply {
                                putExtra(ZATCA_BILL_INFO, result.data)
                            })
                            finish()
                        }
                        ParseStatus.Failure -> {
                            result.error?.errorMsg?.let {
                                showMessage(it)
                            }
                            if (result.error?.errorType == ErrorType.ZATCAQRCodeNotFound || result.error?.errorType == ErrorType.InvalidQRCode) {
                                zatacSv.decodeContinuous { result ->
                                    result.text?.let {
                                        getQRCodeContent(qrCode = it) { status, result ->
                                            when (status) {
                                                ParseStatus.Success -> {
                                                    setResult(Activity.RESULT_OK, Intent().apply {
                                                        putExtra(ZATCA_BILL_INFO, result.data)
                                                    })
                                                    finish()
                                                }
                                                ParseStatus.Failure -> {
                                                    result.error?.errorMsg?.let {
                                                        showMessage(it)
                                                    }
                                                    if (result.error?.errorType == ErrorType.ZATCAQRCodeNotFound || result.error?.errorType == ErrorType.InvalidQRCode) {
                                                    }
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initializePermissionsRequest() {
        requestMultiplePermissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                onPermissionResult(CAMERA_PERMISSION_CODE, permissions)
            }
    }

    /**
     *  initialize content launcher to read files from storage and provides a callback contains the selected file
     *  - Note: refer to manifest for required permissions
     * */
    private fun initializeContentLauncher() {
        getContentLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) {
                it?.let { uri ->
                    contentResolver?.let { cr ->
                        getQRCodeContent(uri = uri, cr = cr) { status, result ->
                            when (status) {
                                ParseStatus.Success -> {
                                    setResult(Activity.RESULT_OK, Intent().apply {
                                        putExtra(ZATCA_BILL_INFO, result.data)
                                    })
                                    finish()
                                }
                                ParseStatus.Failure -> {
                                    result.error?.errorMsg?.let {

                                    }
                                }
                            }
                        }
                    }
                }
            }
    }

    private fun setClickListeners() {
        findViewById<ImageView>(R.id.close_iv).setOnClickListener {
            finish()
        }
        zatacSv.pickImageBtn.setOnClickListener {
            getContentLauncher.launch("image/*")
        }
        zatacSv.camPermissionTv.setOnClickListener {
            requestMultiplePermissionsLauncher.launch(
                arrayOf(Manifest.permission.CAMERA)
            )
        }
    }

    private fun onPermissionResult(requestCode: Int, permissions: Map<String, Boolean>) {
        when (requestCode) {
            CAMERA_PERMISSION_CODE -> zatacSv.refreshPermissionHolder(permissions[Manifest.permission.CAMERA] == true)
        }
    }

    private fun showMessage(message: String) {
        val snackBarUtils = SnackBarUtils.instance
        snackBarUtils.showMessage(this, message)
    }

    /**
     * get ZATAC info from scanned QR code or from selected image
     * @param:qrCode : Scanned QR code from scanner
     *        uri : image path of the selected image
     *        cr : contentResolver
     *        onParseCompleteCallback : which returns status of the parsing operation ,result which contains the parsed ZATAC QR if a valid one was present
     *                                  and error which has a type and a string message
     *                                  ErrorTypes:
     *                                              FileNotFound,
     *                                              InvalidQRCode,
     *                                              QRCodeNotFound),
     *                                              ZATCAQRCodeNotFound
     *
     * */
    private fun getQRCodeContent(
        qrCode: String? = null,
        uri: Uri? = null,
        cr: ContentResolver? = null,
        onParseCompleteCallback: OnParseCompleteCallback<ZATCAQRCode>
    ) {
        if (qrCode != null) {
            ZATCAParser.getZATCABillInfoFromQRCode(qrCode, onParseCompleteCallback)
        } else if (uri != null && cr != null) {
            ZATCAParser.getZATCABillInfoFrom(uri, cr, onParseCompleteCallback)
        }
    }


    companion object {
        private const val CAMERA_PERMISSION_CODE = 59
        const val ZATCA_BILL_INFO = "zatca_bill_info"

        const val BTN_BK_COLOR_ID = "buttons_background_color_id"
        const val TXT_COLOR_ID = "text_color_id"
        const val SEP_COLOR = "separator_color"

        fun newIntent(
            context: Context?,
            @ColorRes buttonsBackgroundColorResID: Int? = null,
            @ColorRes textColorResID: Int? = null,
            @ColorRes separatorColorResID: Int? = null
        ): Intent {
            return Intent(context, ZATCAScannerActivity::class.java).apply {
                putExtra(BTN_BK_COLOR_ID, buttonsBackgroundColorResID)
                putExtra(TXT_COLOR_ID, textColorResID)
                putExtra(SEP_COLOR, separatorColorResID)
            }
        }
    }
}