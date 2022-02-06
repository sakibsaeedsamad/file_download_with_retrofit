package com.example.file_download_2

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Headers
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.app.DownloadManager
import android.os.StrictMode
import java.io.*
import com.example.file_download_2.new.Api
import com.example.file_download_2.new.ApiService


class MainActivity : AppCompatActivity() {
    val PERMISSIONS = listOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val PERMISSION_REQUEST_CODE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        button.setOnClickListener {
            downloadFile()
        }


    }
    private fun viewFile(uri: Uri) {
        application?.let { context ->
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val chooser = Intent.createChooser(intent, "Open with")

            if (intent.resolveActivity(context.packageManager) != null) {
                startActivity(chooser)
            } else {
                Toast.makeText(context, "No suitable application to open file", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun downloadFile() {
        val apiInterface: Api = ApiService.createService(Api::class.java)
        val call: Call<ResponseBody> = apiInterface.getFileType("134")
        call.enqueue(object : Callback<ResponseBody?> {
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if (response.isSuccessful) {

                    val headers: Headers = response.headers()
                    // get header value
                    val fileName: String = headers["File-Name"].toString()

                    Log.d("File-Name", "onResponse: $fileName ")
                    val folder = application?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)

                    val file = File(folder, fileName)
                    val writtenToDisk = writeResponseBodyToDisk(response.body()!!, file)
                    Log.d("File download was a success? ", writtenToDisk.toString())

                    if(writtenToDisk == true) {
                        val uri = application?.let {
                            FileProvider.getUriForFile(it, "com.example.file_download_2", file)
                        }

                        viewFile(uri!!);
                    }

                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {}
        })
    }

    private fun writeResponseBodyToDisk(body: ResponseBody, file:File): Boolean {

        return try {
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                val fileSize = body.contentLength()
                var fileSizeDownloaded: Long = 0
                inputStream = body.byteStream()
                outputStream = FileOutputStream(file)
                while (true) {
                    val read: Int = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream?.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                    //Log.d(TAG, "file download: $fileSizeDownloaded of $fileSize")
                }
                outputStream?.flush()
                true
            } catch (e: IOException) {
                e.printStackTrace()
                false
            } finally {
                if (inputStream != null) {
                    inputStream.close()
                }
                if (outputStream != null) {
                    outputStream.close()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }


//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        if (requestCode == PERMISSION_REQUEST_CODE && hasPermissions(application, PERMISSIONS)) {
//            downloadFile()
//        }
//    }
//    private fun hasPermissions(context: Context?, permissions: List<String>): Boolean {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null) {
//            return permissions.all { permission ->
//                ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
//            }
//        }
//
//        return true
//    }



}