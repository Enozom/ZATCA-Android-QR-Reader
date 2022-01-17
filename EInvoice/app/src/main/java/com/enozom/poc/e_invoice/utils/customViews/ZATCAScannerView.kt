package com.enozom.poc.e_invoice.utils.customViews

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.enozom.poc.e_invoice.R
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.journeyapps.barcodescanner.camera.CameraSettings

class ZATCAScannerView : FrameLayout {
    private val TAG = this.javaClass.simpleName

    lateinit var pickImageBtn: AppCompatButton
    lateinit var camPermissionTv: TextView
    private lateinit var scanIndicator: View
    private lateinit var scanBv: BarcodeView
    private lateinit var permissionHolder: LinearLayout
    private lateinit var sepHolder: LinearLayout

    constructor(context: Context) : super(context) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initialize(attrs)
    }

    private fun initialize(attrs: AttributeSet? = null) {
        // Get attributes set on view
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.ZATCAScannerView)

        val textColor = attributes.getColor(
            R.styleable.ZATCAScannerView_zatca_textColor,
            ContextCompat.getColor(context, R.color.white)
        )
        val btnColor = attributes.getColor(
            R.styleable.ZATCAScannerView_zatca_btnBackgroundColor,
            ContextCompat.getColor(context, R.color.color398CFD)
        )
        val sepColorID = attributes.getResourceId(
            R.styleable.ZATCAScannerView_zatca_separatorColor,
            R.color.white
        )

        attributes.recycle()

        inflate(context, R.layout.layout_zatca_scanner, this)

        pickImageBtn = findViewById(R.id.scan_btn)
        camPermissionTv = findViewById(R.id.no_cam_permission_tv)
        scanIndicator = findViewById(R.id.scan_indicator)
        permissionHolder = findViewById(R.id.cam_permission_holder)
        sepHolder = findViewById(R.id.or_holder)

        setThemeBackgroundColor(btnColor)
        setTextColor(textColor)
        setSeparatorColor(sepColorID)
        setupScannerView()
    }

    private fun setThemeBackgroundColor(@ColorInt color: Int?) {
        color?.let {
            scanIndicator.setBackgroundColor(color)
            pickImageBtn.backgroundTintList = ColorStateList.valueOf(color)
            camPermissionTv.setTextColor(color)
            invalidate()
            requestLayout()
        }
    }

    fun setTextColor(@ColorInt color: Int?) {
        color?.let {
            findViewById<TextView>(R.id.scan_tv).setTextColor(color)
            findViewById<TextView>(R.id.no_cam_permission_title_tv).setTextColor(color)
            findViewById<TextView>(R.id.no_cam_permission_subtitle_tv).setTextColor(color)
            findViewById<TextView>(R.id.or_tv).setTextColor(color)
        }
    }

    fun setPickImageBtnBKColor(@ColorRes colorID: Int?) {
        colorID?.let {
            pickImageBtn.backgroundTintList = ContextCompat.getColorStateList(context, colorID)
            invalidate()
            requestLayout()
        }
    }

    fun getPickImageBtnBk(): Drawable? = pickImageBtn.background

    fun setCamPermissionTxtColor(@ColorRes colorID: Int?) {
        colorID?.let {
            camPermissionTv.setTextColor(ContextCompat.getColor(context, colorID))
            invalidate()
            requestLayout()
        }
    }

    fun getCamPermissionTxtColor(): Int? = camPermissionTv.currentTextColor

    fun setScanIndicator(@ColorRes colorID: Int?) {
        colorID?.let {
            scanIndicator.setBackgroundColor(ContextCompat.getColor(context, colorID))
            invalidate()
            requestLayout()
        }
    }

    fun getScanIndicator(): Drawable? = scanIndicator.background


    fun setSeparatorColor(@ColorRes colorID: Int?) {
        colorID?.let {
            findViewById<View>(R.id.divider).setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    it
                )
            )
            findViewById<View>(R.id.divider2).setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    it
                )
            )
            invalidate()
            requestLayout()
        }
    }

    /**
     *  initialize Barcode view and set to decode once if the scanned value wasn't correct it will try to scan indefinitely
     * */
    private fun setupScannerView() {
        scanBv = findViewById<BarcodeView>(R.id.scan_bv)
        scanBv.apply {
            cameraSettings = CameraSettings().apply {
                requestedCameraId = 0
                isAutoFocusEnabled = false
                focusMode = CameraSettings.FocusMode.CONTINUOUS
            }
            decoderFactory =
                DefaultDecoderFactory(listOf(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39))
        }
    }

    fun decodeSingle(barcodeCallback: BarcodeCallback) {
        scanBv.decodeSingle(barcodeCallback)
    }

    fun decodeContinuous(barcodeCallback: BarcodeCallback) {
        scanBv.decodeContinuous(barcodeCallback)
    }

    fun pause() {
        scanBv.pause()
    }

    fun resume() {
        scanBv.resume()
    }

    fun refreshPermissionHolder(isPermissionGranted: Boolean) {
        if (isPermissionGranted) {
            scanBv.resume()
            permissionHolder.visibility = View.GONE
            scanIndicator.visibility = View.VISIBLE
        } else {
            scanBv.pause()
            permissionHolder.visibility = View.VISIBLE
            scanIndicator.visibility = View.GONE
        }
    }
}