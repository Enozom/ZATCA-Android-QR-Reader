package com.enozom.poc.e_invoice.utils

import android.app.Activity
import android.util.Log
import android.view.ViewGroup
import com.enozom.poc.e_invoice.R
import com.google.android.material.snackbar.Snackbar

class SnackBarUtils private constructor() {
    private var mSnackBar: Snackbar? = null


    fun hideSnackBar() {
        Log.d(TAG, "hideSnackBar: $mSnackBar")
        if (mSnackBar != null) {
            mSnackBar!!.dismiss()
        }
    }

    //todo optimize
    fun showMessage(activity: Activity?, message: String) {
        try {
            Log.d(TAG, "showMessage: $message")
            val contentViewGroup = activity?.findViewById(android.R.id.content) as? ViewGroup
            var rootView = contentViewGroup?.getChildAt(0)

            if (rootView == null)
                rootView = activity?.window?.decorView?.rootView

            if (rootView != null && (mSnackBar == null || (mSnackBar != null && mSnackBar?.isShown == false))) {
                mSnackBar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
                mSnackBar!!.setAction(R.string.ok) { mSnackBar!!.dismiss() }
                mSnackBar!!.show()
            }

        } catch (ex: IllegalStateException) { // fragment not
            Log.d(TAG, "showMessage: exception")// attached to activity
        }
    }

    companion object {
        private const val TAG = "SnackBarUtils"
        private var mInstance: SnackBarUtils? = null

        val instance: SnackBarUtils
            get() {
                if (mInstance == null) {
                    mInstance =
                        SnackBarUtils()
                }
                return mInstance!!
            }
    }
}