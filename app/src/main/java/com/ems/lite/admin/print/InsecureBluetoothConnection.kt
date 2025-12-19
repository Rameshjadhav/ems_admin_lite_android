package com.ems.lite.admin.print

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import java.io.IOException
import java.util.UUID

open class InsecureBluetoothConnection(device: BluetoothDevice) : BluetoothConnection(device) {

    @SuppressLint("MissingPermission")
    @Throws(IOException::class)
    override fun connect(): InsecureBluetoothConnection {
        if (this.isConnected) {
            return this
        }

        // Standard SPP UUID
        val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

        // 1. Create the INSECURE socket (This bypasses the strict pairing requirement)
        val socket = this.device.createInsecureRfcommSocketToServiceRecord(uuid)
        socket.connect()

        // 2. USE REFLECTION to set the 'socket' field (because it is private in the library)
        try {
            val socketField = BluetoothConnection::class.java.getDeclaredField("socket")
            socketField.isAccessible = true
            socketField.set(this, socket)
        } catch (e: Exception) {
            e.printStackTrace()
            // If reflection fails, we cannot proceed
            throw IOException("Failed to set socket via reflection.", e)
        }

        // 3. Set the output stream (Variable name is 'outputStream' in the library, not 'stream')
        this.outputStream = socket.outputStream
        this.data = ByteArray(0)

        return this
    }
}