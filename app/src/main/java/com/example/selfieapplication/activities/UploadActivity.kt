package com.example.selfieapplication.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.selfieapplication.R
import com.google.firebase.storage.FirebaseStorage
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_upload.*
import java.io.ByteArrayOutputStream
import java.util.*

class UploadActivity : AppCompatActivity(), View.OnClickListener {

    companion object{
        const val CAMERA_REQUEST_CODE = 1
        const val GALLERY_REQUEST_CODE = 2
        const val STORAGE_IMAGE = "images"
    }
    private lateinit var storage: FirebaseStorage
    private lateinit var imageUri: Uri
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)



        init()
    }

    private fun init(){
        upload_button_camera.setOnClickListener(this)
        upload_button_gallery.setOnClickListener(this)
        upload_button_upload.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v){
            upload_button_camera -> {
                requestCameraPermission()
            }
            upload_button_gallery -> {
                requestGalleryPermission()
            }
            upload_button_upload -> {
                uploadImage()
                startActivityForResult(Intent(this@UploadActivity, MainActivity::class.java), 3)
                finish()
            }
        }
    }

    private fun requestCameraPermission(){
        Dexter.withContext(this)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object: PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    openCamera()
                    Log.d("abc", "Camera Permission Allowed")
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Log.d("abc","Camera Permission Denied")
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }

            }).check()
    }

    private fun requestGalleryPermission(){
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    //check if all permissions are granted
                    if(p0!!.areAllPermissionsGranted()){
                        openGallery()
                    }

                    //
                    if(p0.isAnyPermissionPermanentlyDenied){
                        Log.d("abc", "A permission is denied")
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }

            }).onSameThread().check()
    }

    fun openCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    fun openGallery(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun uploadImage(){
        storage = FirebaseStorage.getInstance()

        val formatter = java.text.SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val storageReference = storage.getReference("$STORAGE_IMAGE/$fileName")
        // Get the data from an ImageView as bytes
        upload_image_view.isDrawingCacheEnabled = true
        upload_image_view.buildDrawingCache()
        val bitmap = (upload_image_view.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = storageReference.putBytes(data)
        uploadTask.addOnFailureListener {
            Log.d("abc",it.message.toString())
        }.addOnSuccessListener {
            Log.d("abc",it.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                CAMERA_REQUEST_CODE ->{
                    bitmap = data?.extras?.get("data") as Bitmap
                    upload_image_view.setImageBitmap(bitmap)
                }

                GALLERY_REQUEST_CODE ->{
                    imageUri = data?.data!!
                    upload_image_view.setImageURI(imageUri)
                }
            }
        }
    }
}