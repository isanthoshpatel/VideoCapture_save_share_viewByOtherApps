package com.example.videocapture_save_share_viewbyotherapps

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.provider.MediaStore
import android.util.Size
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.VideoView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toIcon
import androidx.core.net.toFile
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    var granted = false
    var savedUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissions()
        bt_takeVideo.setOnClickListener {
            takeVideo()
        }


        bt_share.setOnClickListener {
            if (savedUri != null) {
                Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "video/*"
                    putExtra(Intent.EXTRA_STREAM, savedUri)
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    startActivity(Intent.createChooser(this, "select app to share"))
                }


            }
        }


        bt_view.setOnClickListener {


            if (savedUri != null) {
                Intent().apply {
                    action = Intent.ACTION_VIEW
                    type = "video/*"
                    putExtra(Intent.EXTRA_STREAM, savedUri)
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    startActivity(Intent.createChooser(this, "select app to to view"))
                }
            }
        }


    }

    fun permissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            granted = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.size >= 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                granted = true
            } else {
                permissions()
            }
        }
    }

    fun takeVideo() {
        if (granted) {
            Intent().apply {
                action = MediaStore.ACTION_VIDEO_CAPTURE
                startActivityForResult(this, 1)
            }
        }
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            var uri = data!!.data as Uri
            savedUri = uri
            vv.setVideoURI(uri)
            var mediaController = android.widget.MediaController(this)
            vv.setMediaController(mediaController)
            mediaController.setAnchorView(vv)
            vv.start()

        }
    }

}
