package com.zerlings.smartspinner

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        spinner.setOnItemSelectedListener { view, index ->
            Toast.makeText(this, spinner.getItemAtPosition(index), Toast.LENGTH_SHORT).show()
        }
        spinner.setOnSpinnerResetListener {
            Toast.makeText(this, spinner.getSelectedItem(), Toast.LENGTH_SHORT).show()
        }
        reset.setOnClickListener {
            val strList = ArrayList<CharSequence>()
            strList.add("apple")
            strList.add("apple")
            strList.add("apple")
            spinner.setDataSource(strList)
        }
    }
}
