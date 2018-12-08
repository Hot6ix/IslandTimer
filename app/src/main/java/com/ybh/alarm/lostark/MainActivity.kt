package com.ybh.alarm.lostark

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ybh.alarm.lostark.fragments.MasterFragment

class MainActivity : AppCompatActivity() {

    private var mMasterFragment = MasterFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(R.id.fragment_container, mMasterFragment).commit()
        }
    }
}
