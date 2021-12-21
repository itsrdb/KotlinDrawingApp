package com.itsrdb.kidsdrawingapp

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
import android.net.Uri
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