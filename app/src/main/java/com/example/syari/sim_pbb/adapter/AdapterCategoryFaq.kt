package com.example.syari.sim_pbb.adapter

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.syari.sim_pbb.R
import kotlinx.android.synthetic.main.activity_adapter_category_faq.*

class AdapterCategoryFaq : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adapter_category_faq)

        kategoriFaq.setOnClickListener({
            if (test.visibility == View.GONE){
                test.visibility == View.VISIBLE
            }else{
                test.visibility == View.GONE
            }
        })
    }

//    fun clickCategory(view: View){
//        if (test.visibility == View.GONE){
//            test.visibility == View.VISIBLE
//        }else{
//            test.visibility == View.GONE
//        }
//    }
}
