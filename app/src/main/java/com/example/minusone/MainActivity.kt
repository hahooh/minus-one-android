package com.example.minusone

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.minusone.caller.Apicaller

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val loginButton = findViewById<Button>(R.id.loginButton)
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        loginButton.setOnClickListener {
            this.login(emailInput.toString(), passwordInput.toString())
        }
    }

    private fun login(email: String, password: String) {
        Log.i("email!", email)
        Log.i("password!", password)
        val caller = Apicaller("http://10.0.2.2:8080/v1")
        val body = mutableMapOf<String,String>()
        body["email"] = email
        body["password"] = password
        caller.post("/login", body)
    }
}