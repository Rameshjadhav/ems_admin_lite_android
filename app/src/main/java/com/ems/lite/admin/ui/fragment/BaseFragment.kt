package com.ems.lite.admin.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Point
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.ems.lite.admin.databinding.LayoutNoInternetBinding
import com.ems.lite.admin.ui.activities.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseFragment : Fragment() {
    protected fun hideKeyboard(editText: EditText) {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    protected fun pickWhatsappPackageName(): String {

        val w4bPackageName = "com.whatsapp.w4b"
        val wpPackageName = "com.whatsapp"

        var packageName: String = w4bPackageName

        val pm = requireActivity().packageManager
        try {
            pm.getPackageInfo(
                w4bPackageName,
                PackageManager.GET_ACTIVITIES
            )
        } catch (exWhatsappForBusiness: PackageManager.NameNotFoundException) {
            try {
                pm.getPackageInfo(
                    wpPackageName,
                    PackageManager.GET_ACTIVITIES
                )
                packageName = wpPackageName
            } catch (exWhatsapp: PackageManager.NameNotFoundException) {
                packageName = wpPackageName
            }
        }

        return packageName
    }
    protected fun initNoInternet(
        noInternetLayout: LayoutNoInternetBinding, onClickListener: View.OnClickListener
    ) {
        noInternetLayout.onClickListener = onClickListener
    }

    fun showKeyboard(mActivity: Activity?) {
        (mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }

    protected fun showHideProgress(show: Boolean) {
        (requireActivity() as BaseActivity).showHideProgress(show)
    }

    @Suppress("DEPRECATION")
    fun getScreenWidth(): Int {
        val wm = requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size.x
    }

    companion object {
        // My Generic Check Permission Method
        fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
            if (context != null) {
                val hasNotPermission = permissions.find {
                    ActivityCompat.checkSelfPermission(
                        context, it
                    ) != PackageManager.PERMISSION_GRANTED
                }
                return (hasNotPermission == null)
            }
            return true
        }

        fun hasPermission(context: Context?, permission: String): Boolean {
            if (context != null) {
                return ActivityCompat.checkSelfPermission(
                    context, permission
                ) == PackageManager.PERMISSION_GRANTED
            }
            return true
        }
    }

}
