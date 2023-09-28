package com.example.assignment.DonationModule

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.assignment.Community.Community
import com.example.assignment.Homepage
import com.example.assignment.R
import com.example.assignment.Resources.ResourcesList
import com.example.assignment.UserManagement.studentPanel
import com.google.android.material.bottomnavigation.BottomNavigationView

class DonationPage : AppCompatActivity() {

    private lateinit var botNav: BottomNavigationView
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donation_page)

        val donateBtn:Button =findViewById(R.id.donate)
        val homeBtn : ImageButton = findViewById(R.id.homeBtn)

        donateBtn.setOnClickListener{
        val i = Intent(this, DonationForm::class.java)
            startActivity(i)
        }

        val viewRecord = findViewById<Button>(R.id.recordBtn)
        viewRecord.setOnClickListener{
            val i = Intent(this, DonationRecord::class.java)
            startActivity(i)
        }
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email", "")
        homeBtn.setOnClickListener{
            val i = Intent(this,studentPanel::class.java)
            i.putExtra("email",email)
            startActivity(i)
        }
        botNav = findViewById(R.id.botNav)
        botNav.menu.findItem(R.id.donationMenu)?.isChecked = true
        botNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeMenu -> {
                    val intent = Intent(this, Homepage::class.java)
                    startActivity(intent)
                    true
                }

                R.id.donationMenu -> {
                    true
                }

                R.id.resourceMenu -> {
                    val intent = Intent(this, ResourcesList::class.java)
                    startActivity(intent)
                    true
                }

                R.id.communityMenu -> {
                    val intent = Intent(this, Community::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }
}