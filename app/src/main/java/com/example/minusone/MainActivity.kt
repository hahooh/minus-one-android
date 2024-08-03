package com.example.minusone

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.minusone.caller.Authservice

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val loginButton = findViewById<Button>(R.id.loginButton)
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        loginButton.setOnClickListener {
            this.login(emailInput.text.toString(), passwordInput.text.toString())
        }
    }

    private fun login(email: String, password: String) {
        val authService = Authservice()
        authService.login(context = applicationContext, email, password)
    }
}