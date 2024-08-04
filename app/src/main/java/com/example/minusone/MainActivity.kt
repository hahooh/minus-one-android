package com.example.minusone

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.minusone.caller.getAuthService

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        if(!isUserLoggedIn()) {
            return toRegister()
        }

        val loginButton = findViewById<Button>(R.id.loginButton)
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        loginButton.setOnClickListener {
            this.login(emailInput.text.toString(), passwordInput.text.toString())
        }

        val toRegisterBtn = findViewById<Button>(R.id.toRegister)
        toRegisterBtn.setOnClickListener {
            this.toRegister()
        }
    }

    private fun toRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun login(email: String, password: String) {
        getAuthService().login(context = applicationContext, email, password)
    }

    private fun isUserLoggedIn(): Boolean {
        return !getAuthService().getToken(context = applicationContext).isNullOrEmpty()
    }
}