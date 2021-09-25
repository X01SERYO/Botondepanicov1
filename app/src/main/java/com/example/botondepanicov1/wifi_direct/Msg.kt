package com.example.botondepanicov1.wifi_direct

import java.io.Serializable

class Msg: Serializable {
    private val TAG = "Message"
    val TEXT_MESSAGE = 1
    val IMAGE_MESSAGE = 2
    val NEED_NAME = 3
    var AUDIO_MESSAGE = 4
    val NAME = 5
    val REQUEST = 6
    private val serialVersionUID = 6529685098267757690L
    val OUT = 99



    private var mType = 0
    private var mText: String? = null
    private lateinit var byteArray: ByteArray
    private var fileName: String? = null
    private var fileSize: Long = 0
    private var filePath: String? = null

    //// MARK: 16/06/2018 stores a record of all users this message been to


    //// MARK: 16/06/2018 stores a record of all users this message been to
    //Getters and Setters

    fun getmType(): Int {
        return mType
    }

    fun setmType(mType: Int) {
        this.mType = mType
    }

    fun getmText(): String? {
        return mText
    }

    fun setmText(mText: String?) {
        this.mText = mText
    }

    fun getByteArray(): ByteArray? {
        return byteArray
    }

    fun setByteArray(byteArray: ByteArray) {
        this.byteArray = byteArray
    }

    fun getFileName(): String? {
        return fileName
    }

    fun setFileName(fileName: String?) {
        this.fileName = fileName
    }

    fun getFileSize(): Long {
        return fileSize
    }

    fun setFileSize(fileSize: Long) {
        this.fileSize = fileSize
    }

    fun getFilePath(): String? {
        return filePath
    }

    fun setFilePath(filePath: String?) {
        this.filePath = filePath
    }


    fun Msg(type: Int, text: String?) {
        mType = type
        mText = text
    }
}