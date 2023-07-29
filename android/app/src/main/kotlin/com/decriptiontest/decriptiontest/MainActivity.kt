package com.decriptiontest.decriptiontest

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Environment
import android.util.Log
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.security.MessageDigest
import java.util.Arrays
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class MainActivity: FlutterActivity() {

    private val CHANNEL = "samples.flutter.dev/battery"


    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
            // Note: this method is invoked on the main thread.
                call, result ->
            if (call.method == "getBatteryLevel") {
                val batteryLevel = getBatteryLevel()

                if (batteryLevel != -1) {
                    result.success(batteryLevel)
                    Log.e("TAG", "getBatteryLevel")
                } else {
                    result.error("UNAVAILABLE", "Battery level not available.", null)
                }
            }else if (call.method == "changeColor") {
                changeColor(call, result)
            }else if (call.method == "decrypt") {
                decrypt(call, result)
            } else {
                result.notImplemented()
            }
        }

    }

    private fun getBatteryLevel():Int{
        val batteryLevel: Int
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        } else {
            val intent = ContextWrapper(applicationContext).registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            batteryLevel = intent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100 / intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        }

        return batteryLevel
    }

    private fun changeColor(call: MethodCall, result: MethodChannel.Result) {
        var color = call.argument<String>("color");
        result.success(color);
    }

    private fun decrypt(call: MethodCall, result: MethodChannel.Result){
        var salt = call.argument<String>("salt");
        var path = call.argument<String>("path");
        var filename = call.argument<String>("filename");

        val dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        Log.e("TAG123", ""+dir?.absolutePath)
        if (dir !=null && !dir.exists()) {
            dir.mkdirs()
        }
        val p = dir?.absolutePath + File.separator + filename
        if(salt!=null && path !=null && filename!=null){
            _dec(salt, filename, p, result)
        }
    }

    fun _dec(key:String, data: String, path: String, result: MethodChannel.Result) {
        try {
            var length: Long = 0
            val buf = ByteArray(8192)
            val mainname: String = data
            val datakey = mainname.replace(".mp4.enc", "")
            Log.d("asdja", """ $key $datakey """.trimIndent())
            val datakeye = key.toString() + datakey
            var key = datakeye.toByteArray(charset("UTF-8"))
            val sha = MessageDigest.getInstance("SHA-1")
            key = sha.digest(key)
            key = Arrays.copyOf(key, 16)
            Log.d("asdja", """ $datakey $key  """.trimIndent())
            val secretKey = SecretKeySpec(key, "AES")
            length = File(path).length()
            val iv = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
            val ivspec = IvParameterSpec(iv)
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec)
            val inputStream: InputStream = FileInputStream(path)
            //File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(),"/didme_be.mp4");
            val file = File(applicationContext.cacheDir.toString() + File.separator, "didme_be.mp4")
            val outputStream: OutputStream = CipherOutputStream(FileOutputStream(file), cipher)
            var count = 0
            val buffer = ByteArray(1024)
            var total: Long = 0
            while (inputStream.read(buffer).also { count = it } != -1) {
                total += count.toLong()
                Log.e("TAG123","progress " + (total * 100 / length).toInt())
                outputStream.write(buffer, 0, count)
            }
            outputStream.close()
            inputStream.close()

            result.success(null);
        } catch (e: Exception) {
            println("Error while dencrypting: $e")
            result.error("ERROR", e.message, null)
        }

        Log.e("TAG123", "Decripted")

    }
}
