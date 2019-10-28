package com.klnm.houseofconversation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.klnm.houseofconversation.common.DoFileUpload
import kotlinx.android.synthetic.main.activity_gallery.*

class GalleryActivity : AppCompatActivity() {

    private val GALLERY_CODE = 999

    private var selectImage = mutableListOf<String>()

    val tagName = "GalleryActivity"

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_upload_image -> {

                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

                startActivityForResult(Intent.createChooser(intent, "다중선택은 포토"), GALLERY_CODE)

               //message.setText(R.string.title_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                //message.setText(R.string.title_dashboard)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        Log.d(tagName, "onActivityResult Call resultCode : $requestCode $resultCode $data")

        super.onActivityResult(requestCode, resultCode, data)
        selectImage.clear()

        var imageList = mutableListOf<String>()
        if(resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GALLERY_CODE -> {

                    data?.let {

                        if (data.clipData == null ){
                            Log.d(tagName, "null====" + data.data.toString())

                            val realPath = getRealPathFromURI(data.data)

                            imageList.add(realPath)

                        } else {
                            var clipData = data.clipData
                            clipData?.let {
                                    clip ->

                                Log.d(tagName, "count ${clip.itemCount}")

                                for(index in 0..clip.itemCount -1){

                                    Log.d(tagName, "index ${index}")

                                    val realPath = getRealPathFromURI(clip.getItemAt(index).uri)

                                    imageList.add(realPath)
                                }

                            }

                        }
                    }
                }
            }
        }


        for(image in imageList){
            Log.d(tagName, "Main image : $image")


            //fragments.add(PhotoFragment.newInstance(image))
        }

        if(imageList.size > 0){

            val upload = DoFileUpload()
            upload.httpMultipartUpload("", "01083553999", imageList)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }


    fun getRealPathFromURI(contentUri: Uri) : String {
        //val column_index = 0

        val proj = arrayOf<String>(MediaStore.Images.Media.DATA)
        var cursor = contentResolver.query(contentUri, proj, null, null, null)

        var imageStr = ""
        if(cursor != null) {
            while (cursor.moveToNext()) {

                imageStr = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                Log.d(tagName, "getRealPathFromURI image : $imageStr  Uri : ${contentUri}")

            }
            cursor.close()
        }

        return imageStr

    }
}
