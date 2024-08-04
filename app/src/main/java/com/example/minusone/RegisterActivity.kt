package com.example.minusone

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        val toLoginBtn = findViewById<Button>(R.id.toLogin)
        toLoginBtn.setOnClickListener {
            toLogin()
        }
    }

    private fun toLogin() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}