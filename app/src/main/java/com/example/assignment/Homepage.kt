package com.example.assignment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.assignment.Community.Community
import com.example.assignment.DonationModule.DonationPage
import com.example.assignment.Resources.ResourcesList
import com.google.android.material.bottomnavigation.BottomNavigationView

class Homepage : AppCompatActivity() {
    private lateinit var botNav: BottomNavigationView
    private lateinit var profileName: TextView
    private lateinit var profilePosition: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        profileName = findViewById(R.id.nameText)
        profilePosition = findViewById(R.id.positionText)

        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val name = sharedPreferences.getString("lastName", "")
        val position = sharedPreferences.getString("position", "")

        profileName.setText(name)
        profilePosition.setText(position)
        botNav = findViewById(R.id.botNavigation)
        botNav.menu.findItem(R.id.homeMenu)?.isChecked = true
        botNav.setOnNavigationItemReselectedListener { item ->
            when (item.itemId) {
                R.id.homeMenu -> {
                    true
                }
                R.id.donationMenu -> {
                    val intent = Intent(this, DonationPage::class.java)
                    startActivity(intent)
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
            }
        }
    }
}