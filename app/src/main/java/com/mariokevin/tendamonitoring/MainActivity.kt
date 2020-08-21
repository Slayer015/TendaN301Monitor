package com.mariokevin.tendamonitoring

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadFragment(HomeFragment(),"home")
        bottomNavBar.setItemSelected(R.id.home,true)

        bottomNavBar.setOnItemSelectedListener { id->
            when(id){
                R.id.home -> loadFragment( HomeFragment(),"home")
            }
        }

    }

    private fun loadFragment(fragment:Fragment,tag:String){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer,fragment,tag)
            .commit()
    }

}