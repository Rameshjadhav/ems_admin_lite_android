package com.ems.lite.admin.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.HomeActivityBinding
import com.ems.lite.admin.ui.activities.ImagePicker.Companion.hasPermissions
import com.ems.lite.admin.ui.fragment.HomeTabFragment
import com.ems.lite.admin.utils.AnimationsHandler
import com.ems.lite.admin.utils.Prefs
import com.google.android.material.tabs.TabLayout

class HomeActivity : BaseActivity() {
    companion object {
        fun startActivity(activity: Activity) {
            activity.startActivity(Intent(activity, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            })
            activity.finish()
            AnimationsHandler.playActivityAnimation(
                activity, AnimationsHandler.Animations.RightToLeft
            )
        }
    }

    private lateinit var binding: HomeActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.home_activity)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.app_name))
        binding.toolbarLayout.toolbarRoot.setBackgroundResource(android.R.color.transparent)
        initClickListener()
        setTabIcons()
        init()
    }

    private fun init() {
        syncTables()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            && !hasPermissions(this, *BLUETOOTH_PERMISSIONS_11)
        ) {
            ActivityCompat.requestPermissions(this, BLUETOOTH_PERMISSIONS_11, 1)
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S
            && !hasPermissions(this, *BLUETOOTH_PERMISSIONS)
        ) {
            ActivityCompat.requestPermissions(this, BLUETOOTH_PERMISSIONS, 1)
        } else {
            initDefaultPrinter()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                initDefaultPrinter()
            } else {
                Toast.makeText(this, "Permissions denied. Cannot proceed.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
    @SuppressLint("MissingPermission")
    private fun initDefaultPrinter() {
        val bluetoothDevicesList = BluetoothPrintersConnections().list ?: arrayOf()
        val printerName = Prefs.printerName
        selectedPrinter =
            bluetoothDevicesList.filterNotNull().find { it.device?.name == printerName }
    }

    private fun initClickListener() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        loadHomeFragment()
                    }
                }
            }
        })
    }

    private fun loadHomeFragment() {
        setToolBarTitle(getString(R.string.home))
        replaceFragment(HomeTabFragment.onNewInstance())
    }

    private fun setTabIcons() {
        binding.tabLayout.tabIconTint = null
        binding.tabLayout.getTabAt(0)?.apply {
            setIcon(R.drawable.home_tab_selector)
            text = getString(R.string.home)
        }
        loadHomeFragment()
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }

    fun changeTab(position: Int) {
        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
    }
}