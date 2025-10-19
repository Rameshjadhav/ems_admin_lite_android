package com.ems.lite.admin.common.ui.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.View
import com.ems.lite.admin.R
import com.ems.lite.admin.utils.CustomProgressDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseBottomSheetDialogFragment : BottomSheetDialogFragment(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    fun showHideProgress(show: Boolean) {
        if (show) {
            CustomProgressDialog.showProgressDialog(requireActivity())
        } else {
            CustomProgressDialog.dismissProgressDialog()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener {
                val bottomSheet =
                    findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                BottomSheetBehavior.from(bottomSheet).apply {
                    setState(BottomSheetBehavior.STATE_EXPANDED)
                }
            }
        }
    }
}
