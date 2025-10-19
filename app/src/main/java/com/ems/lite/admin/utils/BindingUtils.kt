package com.ems.lite.admin.utils

import android.annotation.SuppressLint
import android.text.Spannable
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.ems.lite.admin.R
import java.util.*

@BindingAdapter("android:visibility")
fun hideVisibilityIfNull(view: View, value: String?) {
    view.visibility = if (value.isNullOrEmpty()) View.GONE else View.VISIBLE
}

@BindingAdapter("android:visibility")
fun hideVisibility(view: View, value: Boolean) {
    view.visibility = if (value) View.VISIBLE else View.GONE
}

@BindingAdapter("android:visibility")
fun hideVisibilityIfNull(view: View, value: Double?) {
    view.visibility = if (value == null || value == 0.0) View.GONE else View.VISIBLE
}

@BindingAdapter("android:text")
fun setFloatText(view: View, value: Float?) {
    if (view is TextView) {
        view.text = if (value != null) {
            if (value % 1 == 0f) {
                String.format(Locale.US, "%,.0f", value)
            } else {
                "$value"
            }
        } else "0"
    }
}

@BindingAdapter(value = ["prefix_message", "rating", "postfix_message"], requireAll = false)
fun setRatingText(view: View, prefix: String?, value: Float?, postFix: String?) {
    val prefixMessage = if (!prefix.isNullOrEmpty()) prefix else ""
    val postFixMessage = if (!postFix.isNullOrEmpty()) postFix else ""
    val message = StringBuilder()
    message.append(prefixMessage)
    if (value != null) {
        if (value % 1 == 0f) {
            message.append(String.format(Locale.US, "%,.0f", value))
        } else {
            message.append("$value")
        }
    } else message.append("0")
    message.append(postFixMessage)
    if (view is TextView) {
        view.text = message
    }
}

@BindingAdapter(value = ["prefix_message", "review", "postfix_message"], requireAll = false)
fun setReviewText(view: View, prefix: String?, value: Long?, postFix: String?) {
    val prefixMessage = if (!prefix.isNullOrEmpty()) prefix else ""
    val postFixMessage = if (!postFix.isNullOrEmpty()) postFix else ""
    val message = StringBuilder()
    message.append(prefixMessage)
    message.append(if (value != null) "$value" else "0")
    message.append(postFixMessage)
    if (view is TextView) {
        view.text = message
    }
}

@BindingAdapter("url", "place_holder", "load_type")
fun loadImagePicasso(
    imageView: ImageView, url: String?, @IdRes placeHolder: Int, type: ImageSetter.LoadImageType?
) {
    if (type != null && type == ImageSetter.LoadImageType.ROUND) {
        ImageSetter.loadRoundedImage(url, placeHolder, imageView)
    } else {
        ImageSetter.loadImage(url, placeHolder, imageView)
    }
}

@BindingAdapter(value = ["country_code"], requireAll = false)
fun setFormattedCountryCode(view: View, countryCode: String?) {
    val formattedCode = if (!countryCode.isNullOrEmpty()) {
        "+$countryCode"
    } else {
        ""
    }
    if (view is TextView)
        view.text = formattedCode
    else if (view is EditText)
        view.setText(formattedCode)
}

@BindingAdapter(
    value = ["label_message", "label_color", "separator", "value", "value_color"],
    requireAll = false
)
fun formattedText(
    view: View, label: String?, labelColor: Int = R.color.secondary_text_color,
    separator: String = " : ", value: String?, valueColor: Int = R.color.primary_text_color
) {
    val labelMessage = if (!label.isNullOrEmpty()) label else ""
    val valueMessage = if (!value.isNullOrEmpty()) value else ""
    val message = StringBuilder()
    message.append(labelMessage)
    message.append(separator)
    message.append(valueMessage)
    val span = Spannable.Factory.getInstance().newSpannable(message)
    span.setSpan(
        ForegroundColorSpan(ContextCompat.getColor(view.context, labelColor)),
        0, labelMessage.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    span.setSpan(
        ForegroundColorSpan(ContextCompat.getColor(view.context, valueColor)),
        message.length - valueMessage.length, message.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    if (view is TextView) {
        view.text = span
    }
}
@BindingAdapter(value = ["date", "in_format", "out_format"], requireAll = true)
fun setDate(view: View, date: String?, inFormat: String?, outFormat: String?) {
    val dateString: String =
        if (!date.isNullOrEmpty() && !inFormat.isNullOrEmpty() && !outFormat.isNullOrEmpty()) {
            DateFormatter.getFormattedDate(inFormat, date, outFormat)
        } else if (!date.isNullOrEmpty()) {
            date
        } else {
            "-"
        }
    if (view is TextView) {
        view.text = dateString
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter(
    value = ["prefix_message", "value", "num_fraction", "postfix_message", "ignore_fraction", "required_currency"],
    requireAll = false
)
fun setFormattedPrice(
    view: TextView, prefix: String?, value: Double?, numberOfFraction: Int?,
    postFix: String?, ignoreFraction: Boolean, requiredCurrency: Boolean = true
) {
    val prefixMessage =
        if (!prefix.isNullOrEmpty()) "$prefix" else if (requiredCurrency) "${CommonUtils.CURRENCY_CODE} " else ""
    val postFixMessage = postFix ?: ""
    if (value != null && numberOfFraction != null) {
        view.text = if (value % 1 == 0.0 && ignoreFraction) {
            "$prefixMessage${String.format(Locale.US, "%,.0f", value)}$postFixMessage"
        } else {
            "$prefixMessage${
                String.format(Locale.US, "%,.${numberOfFraction}f", value)
            }$postFixMessage"
        }
    }
}
@BindingAdapter("onClick")
fun onClick(view: View, onClick: () -> Unit) {
    view.setOnClickListener {
        onClick()
    }
}
