package com.itsrdb.kidsdrawingapp

import android.app.Dialog
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.get

class MainActivity : AppCompatActivity() {

    private var mImageButtonCurrentPaint: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val drawingView = findViewById<com.itsrdb.kidsdrawingapp.DrawingView>(R.id.drawing_view)
        drawingView.setSizeForBrush(5.toFloat())

        val ll_paint_colors = findViewById<LinearLayout>(R.id.ll_paint_colors)
        mImageButtonCurrentPaint = ll_paint_colors[1] as ImageButton    //Black
        mImageButtonCurrentPaint!!.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.pallet_selectedl)
        )

        val brushBtn = findViewById<ImageButton>(R.id.ib_brush)
        brushBtn.setOnClickListener{
            showBrushSizeChooserDialog()
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