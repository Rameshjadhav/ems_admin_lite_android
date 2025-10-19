package com.ems.lite.admin.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.os.Build
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.AdapterView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ems.lite.admin.R
import com.ems.lite.admin.model.response.Error
import com.ems.lite.admin.ui.activities.BaseActivity
import com.google.gson.Gson
import okhttp3.ResponseBody
import org.aviran.cookiebar2.CookieBar
import java.lang.reflect.Type
import java.net.SocketTimeoutException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToLong


object CommonUtils {
    const val API_KEY = "D3F27F967649F9C34CD9BA771DFED"
    const val SECRET_KEY = "j5Ox0cMj1ySioRDPM0A1Uh6t5yP651a3"
    const val CURRENCY_CODE = "Rs."

    fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    @Suppress("DEPRECATION")
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun showToast(activity: Activity?, message: String?) {
        if (activity != null && !activity.isFinishing && !message.isNullOrEmpty()) {
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
        }
    }

    fun isValidEmail(email: String): Boolean {
        val emailMatcher =
            Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})\$")
                .matcher(email)
        return !TextUtils.isEmpty(email) && emailMatcher.find()
    }

    fun isValidPassword(password: String): Boolean {
        val pattern: Pattern
        //        val specialCharacters = "-@%\\[\\}+'!/#$^?:;,\\(\"\\)~`.*=&\\{>\\]<_"
        val passwordExp =
            "^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{8,20}$"
        pattern = Pattern.compile(passwordExp)
        val matcher: Matcher = pattern.matcher(password)
//        return if (matcher.matches()) {
//            val patternSP: Pattern
//            val matcherSP: Matcher
//            val passwordRegExp =
//                "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[$specialCharacters])(?=\\S+$).{8,20}$"
//            patternSP = Pattern.compile(passwordRegExp)
//            matcherSP = patternSP.matcher(password)
//            !matcherSP.matches()
//        } else {
        return matcher.matches()
//        }

    }

    fun isAlphaNumeric(password: String): Boolean {
        val emailExpression = "[A-Za-z0-9]+"
        val p = Pattern.compile(emailExpression)
        val m = p.matcher(password)
        return !TextUtils.isEmpty(password) && m.find()
    }

    fun <T> getStringToModel(json: String?, clazz: Class<T>?): Any {
        val gson = Gson()
        return gson.fromJson(json, clazz as Type?)
    }

    fun showErrorMessage(activity: Activity?, error: Error?) {
        showErrorMessage(activity, error?.message)
    }

    fun showErrorMessage(activity: Activity?, message: String?) {
        showTopMessage(activity, message, R.color.red, true)
    }

    private fun showTopMessage(
        activity: Activity?, message: String?, @ColorRes bgColor: Int, isError: Boolean
    ) {
        if (activity != null && !activity.isFinishing && !message.isNullOrEmpty()) {
            CookieBar.build(activity as BaseActivity)
                .setTitle(activity.getString(R.string.app_name))
                .setTitleColor(R.color.white)
                .setBackgroundColor(bgColor)
                .setMessage(message)
                .setIcon(0)
//                .setMessageSize(15)
//                .setMessageGravity(Gravity.CENTER_HORIZONTAL)
//                .setMessageFont(ResourcesCompat.getFont(activity, R.font.poppins_regular))
                .setIcon(if (isError) R.drawable.icon_error else R.drawable.icon_success)
                .setDuration(2000)
                .show()
        }
    }
    fun updatePasswordView(editText: EditText, show: Boolean) {
        editText.transformationMethod =
            if (show) null else PasswordTransformationMethod.getInstance()
        editText.setSelection(editText.text.toString().length)
    }

    /**
     * Null safe comparison of two objects.
     *
     * @return true if the objects are identical.
     */
    fun objectEquals(o1: Any?, o2: Any?): Boolean {
        if (o1 == null && o2 == null) {
            return true
        }
        return if (o1 == null) {
            false
        } else o1 == o2
    }

    fun isTimeOutError(t: Throwable?): Boolean {
        return t is SocketTimeoutException
    }

    fun getErrorResponse(error: ResponseBody?): Error {
        val gson = Gson()
        var baseResponse = Error()
        baseResponse.message = ""
        try {
            baseResponse = gson.fromJson(error!!.charStream(), Error::class.java)
            baseResponse.message = baseResponse.message
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return baseResponse
    }

    fun roundDouble(d: Double, point: Int): String {
        var tempValue = d
        val factor = 10.0.pow(point.toDouble()).toLong()
        tempValue *= factor
        val temp = tempValue.roundToLong().toDouble() / factor
        val longValue = temp.toLong()
        val difference = temp - longValue
        var value = longValue.toString()
        if (abs(difference) > 0) {
            value = temp.toString()
        }
        return value

    }

    fun getRoundDoubleWithCurrency(value: Double, digit: Int): String {
        var digits = digit
        val amount1 = roundDouble(value, digit)
        if (!amount1.contains(".")) {
            digits = 0
        }
        val localeForCurrency = Locale("en", "us")
        val formatter =
            NumberFormat.getCurrencyInstance(localeForCurrency) as DecimalFormat
        formatter.maximumFractionDigits = digits
//        val currencySymbol =
//            Currency.getInstance(localeForCurrency)
//                .getSymbol(localeForCurrency)
//        return formatter.format(value).replace(currencySymbol, "$currencySymbol ")
        return formatter.format(value)
    }

    fun getImage(context: Context, ImageName: String?): Drawable? {
        return try {
            ContextCompat.getDrawable(
                context,
                context.resources
                    .getIdentifier(ImageName, "drawable", context.packageName)
            )
        } catch (e: java.lang.Exception) {
            ContextCompat.getDrawable(
                context, context.resources.getIdentifier("flag_00", "drawable", context.packageName)
            )
        }
    }

    fun setBgColor(mContext: Context, background: Drawable, color: Int) {
        when (background) {
            is ShapeDrawable -> {
                // cast to 'ShapeDrawable'
                val shapeDrawable: ShapeDrawable = background
                shapeDrawable.paint.color = ContextCompat.getColor(mContext, color)
            }
            is GradientDrawable -> {
                // cast to 'GradientDrawable'
                val gradientDrawable: GradientDrawable = background
                gradientDrawable.setColor(ContextCompat.getColor(mContext, color))
            }
            is ColorDrawable -> {
                // alpha value may need to be set again after this call
                val colorDrawable: ColorDrawable = background
                colorDrawable.color = ContextCompat.getColor(mContext, color)
            }
        }
    }

    fun dpToPx(dp: Int): Float {
        return (dp * Resources.getSystem().displayMetrics.density)
    }

    fun getCurrencySymbol(currencyCode: String): String {
        return try {
            val currency = Currency.getInstance(currencyCode)
            currency.symbol
        } catch (e: java.lang.Exception) {
            currencyCode
        }
    }
    fun getMd5For(s: String): String {
        try {
            // Create MD5 Hash
            val digest = MessageDigest.getInstance("SHA-256")
            digest.update(s.toByteArray())
            val messageDigest = digest.digest()

            // Create Hex String

            val hexString = StringBuffer()
            for (i in messageDigest.indices) {
                var hexValue = Integer.toHexString(0xFF and messageDigest[i].toInt())
                if (hexValue.length == 1) {
                    hexValue = "0$hexValue"
                }
                hexString.append(hexValue)
            }
            Log.v("", "SHA-256 for '$s'  is: $hexString")
            return hexString.toString()

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        return ""
    }
    fun isValidMobile(mobile: String): Boolean {
        val matcher =
            Pattern.compile("^[0-9]{10}")
                .matcher(mobile)
        return !TextUtils.isEmpty(mobile) && matcher.find()
    }
    fun updateDisabledPositionInSpinner(
        context: Context, parent: AdapterView<*>?, position: Int, disabledPosition: Int
    ) {
        var color = R.color.primary_text_color
        if (position == disabledPosition) {
            color = R.color.et_hint_color
        }
        (parent?.getChildAt(0) as TextView).setTextColor(
            ContextCompat.getColor(context, color)
        )
    }

//    fun isWorkScheduled(context: Context, tag: String): Boolean {
//        val instance = WorkManager.getInstance(context)
//        val statuses = instance.getWorkInfosByTag(tag)
//        return try {
//            var running = false
//            val workInfoList = statuses.get()
//            for (workInfo in workInfoList) {
//                val state = workInfo.state
//                running =
//                    !state.isFinished //(state == WorkInfo.State.RUNNING) or (state == WorkInfo.State.ENQUEUED)
//            }
//            running
//        } catch (e: ExecutionException) {
//            e.printStackTrace()
//            false
//        } catch (e: InterruptedException) {
//            e.printStackTrace()
//            false
//        }
//    }
}