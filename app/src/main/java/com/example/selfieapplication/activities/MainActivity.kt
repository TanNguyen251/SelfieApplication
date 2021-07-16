package com.example.selfieapplication.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.selfieapplication.R
import com.example.selfieapplication.adapters.ImageAdapter
import com.example.selfieapplication.models.Image
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var storage: FirebaseStorage
    private var imageList: ArrayList<Image> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun init(){
        storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference.child(UploadActivity.STORAGE_IMAGE)
        val listAllTask: Task<ListResult> = storageReference.listAll()
        listAllTask.addOnCompleteListener{
            val itemList: List<StorageReference> = it.result!!.items

            itemList.forEachIndexed { _, item ->
                item.downloadUrl.addOnSuccessListener { uri ->
                    imageList.add(Image(uri.toString()))
                }.addOnCompleteListener{
                    main_rv_image_list.adapter = ImageAdapter(this, imageList)
                    main_rv_image_list.layoutManager = GridLayoutManager(this, 2)
                }
            }
        }
        main_fab_add.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v){
            main_fab_add -> {
                startActivity(Intent(this, UploadActivity::class.java))
            }
        }
    }
}