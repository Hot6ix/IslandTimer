package com.ybh.lostark.islandtimer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ybh.lostark.islandtimer.fragments.MasterFragment

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
