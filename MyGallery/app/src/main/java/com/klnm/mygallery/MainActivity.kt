package com.klnm.mygallery

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import org.json.JSONArray
import kotlin.concurrent.timer


class MainActivity : AppCompatActivity() {

    private val REQUEST_READ_EXTERNAL_STORAGE = 1000
    private val GALLERY_CODE = 999

    private var selectImage = mutableListOf<String>()

    private var fragments = ArrayList<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageSelectBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            //intent.data = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            //intent.type = android.provider.MediaStore.Images.Media.CONTENT_TYPE
            startActivityForResult(Intent.createChooser(intent, "다중선택은 '포토'를 선택하세요"), GALLERY_CODE)

        }

        startBtn.setOnClickListener {
            loadImagePath()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        Log.d("Main image", "onActivityResult Call resultCode : $requestCode $resultCode $data")

        super.onActivityResult(requestCode, resultCode, data)

        val tagName = "onActivityResult"

        fragments.clear()

        selectImage.clear()


        var imageList = mutableListOf<String>()
        if(resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GALLERY_CODE -> {

                    data?.let {

                        if (data.clipData == null ){
                            Log.d(tagName, data.data.toString())
                            imageList.add(data.data.toString())

                        } else {
                            var clipData = data.clipData
                            clipData?.let {
                                clip ->

                                Log.d(tagName, "count ${clip.itemCount}")

                                for(index in 0..clip.itemCount -1){

                                    Log.d(tagName, "index ${index}")

                                    imageList.add(clip.getItemAt(index).uri.toString())
                                }

                            }

                        }
                    }
                }
            }
        }

        for(image in imageList){
            Log.d("Main image", "Main image : $image")

            fragments.add(PhotoFragment.newInstance(image))
        }

        if(fragments.size > 0){
            displayPhotos()
            saveImagePath(imageList)
        }
    }

    private fun displayPhotos(){

        val adapter = MyPagerAdapter(supportFragmentManager)
        adapter.updateFragment(fragments)
        viewPager.adapter = adapter

        timer(period = 3000) {
            runOnUiThread {
                if(viewPager.currentItem < adapter.count -1 ){
                    viewPager.currentItem = viewPager.currentItem + 1
                } else {
                    viewPager.currentItem = 0
                }
            }
        }

    }

    private fun saveImagePath(imagePathList : MutableList<String>){
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = pref.edit()

        var jsonArray : JSONArray = JSONArray()
        imagePathList?.let {
            for(path in it) {
                jsonArray.put(path)
            }
            editor.putString("imagePathList", jsonArray.toString()).apply()

        }
    }

    private fun loadImagePath() {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val json = pref.getString("imagePathList", null)

        json?.let {

            val jArray = JSONArray(json)
            for (i in 0 until jArray.length()) {

                val data = jArray.optString(i)
                fragments.add(PhotoFragment.newInstance(data))

            }
            displayPhotos()
        }
    }

    fun getRealPathFromURI(contentUri: Uri) {
        //val column_index = 0

        val proj = arrayOf<String>(MediaStore.Images.Media.DATA)
        var cursor = contentResolver.query(contentUri, proj, null, null, null)

        if(cursor != null) {
            while (cursor.moveToNext()) {

                val imageStr = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                Log.d("Main image", "Main image : $imageStr")

                fragments.add(PhotoFragment.newInstance(imageStr))

            }
            cursor.close()
        }

    }


    fun imagePermission() : Boolean{
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                alert("사진정보") {
                    yesButton {
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            REQUEST_READ_EXTERNAL_STORAGE
                        )
                    }
                    noButton { }
                }.show()


            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_READ_EXTERNAL_STORAGE
                )
            }
        } else {

            return true
            //getAllPhotos()
        }

        return false
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_READ_EXTERNAL_STORAGE -> {
                if((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getAllPhotos()
                } else {
                    toast("권한거부")
                }
                return
            }
        }
    }

    private fun getAllPhotos(){
        val cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            , null
            , null
            , null
            , MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC")


        val fragments = ArrayList<Fragment>()
        if(cursor != null) {
            while (cursor.moveToNext()) {
                val uri = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                Log.d("MainActivity", uri)
                fragments.add(PhotoFragment.newInstance(uri))
            }
            cursor.close()
        }

        val adapter = MyPagerAdapter(supportFragmentManager)
        adapter.updateFragment(fragments)
        viewPager.adapter = adapter

        timer(period = 3000) {
            runOnUiThread {
                if(viewPager.currentItem < adapter.count -1 ){
                    viewPager.currentItem = viewPager.currentItem + 1
                } else {
                    viewPager.currentItem = 0
                }
            }
        }

    }
}
