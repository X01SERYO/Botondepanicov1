package com.example.botondepanicov1.wifi_direct

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder
import java.net.NetworkInterface
import java.text.SimpleDateFormat
import java.util.*

class Codificator {

    private val TAG = "COD"

    fun byteArrayToBitmap(b: ByteArray): Bitmap? {
        Log.v(TAG, "Convert byte array to image (bitmap)")
        return BitmapFactory.decodeByteArray(b, 0, b.size)
    }

    fun saveByteArrayToFile(context: Context, msg: Msg) {
        Log.v(TAG, "Save byte array to file")
        when (msg.getmType()) {

            msg.AUDIO_MESSAGE-> msg.setFilePath(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)!!.absolutePath + "/" + msg.getFileName())
        }
        val file: File = File(msg.getFilePath())
        if (file.exists()) {
            file.delete()
        }
        try {
            val fos = FileOutputStream(file.path)
            fos.write(msg.getByteArray())
            fos.close()
            Log.v(TAG, "Write byte array to file DONE !")
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e(TAG, "Write byte array to file FAILED !")
        }
    }


    @Throws(IOException::class)
    fun objToByte(tcpPacket: Msg?): ByteArray? {
        val byteStream = ByteArrayOutputStream()
        val objStream = ObjectOutputStream(byteStream)
        objStream.flush()
        objStream.writeObject(tcpPacket as Any?)
        objStream.close()
        return byteStream.toByteArray()
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    fun byteToObj(bytes: ByteArray?): Msg? {
        val byteStream = ByteArrayInputStream(bytes)
        val objStream = ObjectInputStream(byteStream)
        objStream.close()
        return objStream.readObject() as Msg
    }

    fun dateToString(date: Date?): String? {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        return formatter.format(date)
    }


    fun getMacAddr(): String? {
        try {
            val all: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            for (nif in all) {
                if (!nif.name.equals("wlan0", ignoreCase = true)) continue
                val macBytes = nif.hardwareAddress ?: return ""
                val res1 = StringBuilder()
                for (b in macBytes) {
                    res1.append(String.format("%02X:", b))
                }
                if (res1.length > 0) {
                    res1.deleteCharAt(res1.length - 1)
                }
                return res1.toString()
            }
        } catch (ex: Exception) {
        }
        return "02:00:00:00:00:00"
    }

    fun macToString(macAdd: String): String? {
        Log.d("sgk", "macToString() macAdd:$macAdd")
        var temp = "SG" ///// S G K /////
        Log.d("sgk", macAdd)
        val stringTokenizer = StringTokenizer(macAdd, ":")
        while (stringTokenizer.hasMoreTokens()) {
            temp += stringTokenizer.nextToken()
        }
        Log.d("sgk", "macToString() temp:$temp")
        return temp
    }

}