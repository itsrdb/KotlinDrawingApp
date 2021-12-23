package com.itsrdb.kidsdrawingapp

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.Image
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private var mImageButtonCurrentPaint: ImageButton? = null
    private val getResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                if(data!!.data != null){
                    val customPhoto = findViewById<ImageView>(R.id.iv_background)
                    customPhoto.visibility = View.VISIBLE
                    customPhoto.setImageURI(data.data)
                }else{
                    Toast.makeText(this, "Incorrect file type", Toast.LENGTH_SHORT).show()
                }
            }
        }

    companion object{
        private const val STORAGE_PERMISSION_CODE = 1
    }

    private inner class BitmapAsyncTask(val mBitmap: Bitmap?): AsyncTask<Any, Void, String>(){

        private lateinit var mProgressDialog: Dialog

        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog()
        }

        override fun doInBackground(vararg params: Any?): String {
            var result = ""
            if(mBitmap != null){
                try{
                    val bytes = ByteArrayOutputStream()
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)
                    val f = File(externalCacheDir!!.absoluteFile.toString()
                            +File.separator+"DrawingApp_"+System.currentTimeMillis()/1000+".png")
                    val fO = FileOutputStream(f)
                    fO.write(bytes.toByteArray())
                    fO.close()
                    result = f.absolutePath
                }catch (e: Exception){
                    result = ""
                    e.printStackTrace()
                }
            }
            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            cancelProgressDialog()
            if(result!!.isNotEmpty()){
                Toast.makeText(this@MainActivity, "File Saved Successfully", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this@MainActivity, "Something went wrong, try again", Toast.LENGTH_SHORT).show()
            }

            MediaScannerConnection.scanFile(
                this@MainActivity, arrayOf(result), null) { path, uri ->
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.putExtra(
                    Intent.EXTRA_STREAM,
                    uri
                )
                shareIntent.type =
                    "image/jpeg"
                startActivity(
                    Intent.createChooser(
                        shareIntent,
                        "Share"
                    )
                )
            }
        }

        private fun showProgressDialog(){
            mProgressDialog = Dialog(this@MainActivity)
            mProgressDialog.setContentView(R.layout.dialog_custom_progress)
            mProgressDialog.show()
        }

        private fun cancelProgressDialog(){
            mProgressDialog.dismiss()
        }

    }

    private fun requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE).toString())){
            Toast.makeText(this, "Need Permission", Toast.LENGTH_SHORT).show()
        }
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isReadStorageAllowed(): Boolean{
        val result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)

        return result == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val drawingView = findViewById<com.itsrdb.kidsdrawingapp.DrawingView>(R.id.drawing_view)
        drawingView.setSizeForBrush(5.toFloat())

        val customPhoto = findViewById<ImageView>(R.id.iv_background)
        customPhoto.visibility = View.INVISIBLE

        val ll_paint_colors = findViewById<LinearLayout>(R.id.ll_paint_colors)
        mImageButtonCurrentPaint = ll_paint_colors[1] as ImageButton    //Black
        mImageButtonCurrentPaint!!.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.pallet_selectedl)
        )

        val brushBtn = findViewById<ImageButton>(R.id.ib_brush)
        brushBtn.setOnClickListener{
            showBrushSizeChooserDialog()
        }

        val imageBtn = findViewById<ImageButton>(R.id.ib_gallery)
        imageBtn.setOnClickListener {
            if(isReadStorageAllowed()){
                val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                getResult.launch(pickPhotoIntent)
            }else{
                requestStoragePermission()
            }
        }

        val undoBtn = findViewById<ImageButton>(R.id.ib_undo)
        undoBtn.setOnClickListener {
            drawingView.onClickUndo()
        }

        val saveBtn = findViewById<ImageButton>(R.id.ib_save)
        saveBtn.setOnClickListener {
            if(isReadStorageAllowed()){
                val flView = findViewById<FrameLayout>(R.id.fl_drawing)
                BitmapAsyncTask(getBitmapFromView(flView)).execute()
            }else{
                requestStoragePermission()
            }
        }
    }

    private fun getBitmapFromView(view: View) : Bitmap {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if(bgDrawable != null){
            bgDrawable.draw(canvas)
        }else{
            canvas.drawColor(Color.WHITE)
        }

        view.draw(canvas)
        return returnedBitmap
    }

    private fun showBrushSizeChooserDialog(){
        val brushDialog = Dialog(this)
        brushDialog.setTitle("Brush Size")
        brushDialog.setContentView(R.layout.dialog_brush_size)
        Toast.makeText(this, "button works", Toast.LENGTH_SHORT).show()

        val smallBtn = brushDialog.findViewById<ImageButton>(R.id.ib_small_btn)
        smallBtn.setOnClickListener {
            val drawingView = findViewById<com.itsrdb.kidsdrawingapp.DrawingView>(R.id.drawing_view)
            drawingView.setSizeForBrush(5.toFloat())
            brushDialog.dismiss()
        }

        val medBtn = brushDialog.findViewById<ImageButton>(R.id.ib_medium_btn)
        medBtn.setOnClickListener {
            val drawingView = findViewById<com.itsrdb.kidsdrawingapp.DrawingView>(R.id.drawing_view)
            drawingView.setSizeForBrush(10.toFloat())
            brushDialog.dismiss()
        }

        val largeBtn = brushDialog.findViewById<ImageButton>(R.id.ib_large_btn)
        largeBtn.setOnClickListener {
            val drawingView = findViewById<com.itsrdb.kidsdrawingapp.DrawingView>(R.id.drawing_view)
            drawingView.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()
        }
        brushDialog.show()

    }

    fun paintClicked(view: View){
        if(view != mImageButtonCurrentPaint){
            val imageBtn = view as ImageButton
            val colorTag = imageBtn.tag.toString()
            val drawingView = findViewById<com.itsrdb.kidsdrawingapp.DrawingView>(R.id.drawing_view)
            drawingView.setColor(colorTag)

            //mImageButtonCurrentPaint = ll_paint_colors[0] as ImageButton    //Black
            imageBtn.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.pallet_selectedl)
            )
            mImageButtonCurrentPaint!!.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.pallet_normal)
            )
            mImageButtonCurrentPaint = view
        }
    }
}