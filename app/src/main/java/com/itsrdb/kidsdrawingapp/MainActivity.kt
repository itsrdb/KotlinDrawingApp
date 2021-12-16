package com.itsrdb.kidsdrawingapp

import android.app.Dialog
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val drawingView = findViewById<com.itsrdb.kidsdrawingapp.DrawingView>(R.id.drawing_view)
        drawingView.setSizeForBrush(5.toFloat())

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
}