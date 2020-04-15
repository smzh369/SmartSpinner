package com.zerlings.smartspinner

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.zerlings.library.SmartSpinnerLayout
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
        val fruitList = ArrayList<Fruit>()
        fruitList.add(Fruit("apple", R.mipmap.wechat_icon))
        fruitList.add(Fruit("banana", R.mipmap.withdraw_alipay))
        fruitList.add(Fruit("orange", R.mipmap.withdraw_unipay))
        val spinnerLayout: SmartSpinnerLayout<Fruit> = findViewById(R.id.spinner_layout)
        spinnerLayout.setAdapter(SmartSpinnerLayoutAdapter(fruitList))
        spinnerLayout.setOnItemSelectedListener { view, index ->
            Toast.makeText(this, spinnerLayout.getItemAtPosition(index)?.title, Toast.LENGTH_SHORT).show()
        }
        spinnerLayout.setOnSpinnerResetListener {
            Toast.makeText(this, spinnerLayout.getSelectedItem()?.title, Toast.LENGTH_SHORT).show()
        }
        reset.setOnClickListener {
            spinnerLayout.reset()
        }
    }
}
