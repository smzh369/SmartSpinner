package com.zerlings.smartspinner

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.zerlings.spinner.SmartSpinner
import com.zerlings.spinner.SmartSpinnerLayout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val spinner = findViewById<SmartSpinner>(R.id.spinner)
        spinner.setOnItemSelectedListener { view, index ->
            Toast.makeText(this, spinner.getItemAtPosition(index), Toast.LENGTH_SHORT).show()
        }
        spinner.setOnSpinnerResetListener {
            Toast.makeText(this, spinner.getSelectedItem(), Toast.LENGTH_SHORT).show()
        }
        val spinner_reset = findViewById<TextView>(R.id.spinner_reset)
        spinner_reset.setOnClickListener {
            spinner.reset()
        }
        spinner.setDataSource(arrayListOf("apple", "banana", "orange", "banana", "orange", "banana", "orange"))
        val spinnerLayout: SmartSpinnerLayout<PayType> = findViewById(R.id.spinner_layout)
        spinnerLayout.setAdapter(SmartSpinnerLayoutAdapter(arrayListOf(PayType("wechat", R.mipmap.wechat_icon),
            PayType("alipay", R.mipmap.withdraw_alipay),
            PayType("unipay", R.mipmap.withdraw_unipay))))
        spinnerLayout.setOnItemSelectedListener { view, index ->
            Toast.makeText(this, spinnerLayout.getItemAtPosition(index)?.title, Toast.LENGTH_SHORT).show()
        }
        spinnerLayout.setOnSpinnerResetListener {
            Toast.makeText(this, spinnerLayout.getSelectedItem()?.title, Toast.LENGTH_SHORT).show()
        }
        val spinner_layout_reset = findViewById<TextView>(R.id.spinner_layout_reset)
        spinner_layout_reset.setOnClickListener {
            spinnerLayout.reset()
        }
    }
}
