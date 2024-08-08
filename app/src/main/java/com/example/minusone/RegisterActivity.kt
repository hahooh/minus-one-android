package com.example.minusone

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.minusone.caller.getAuthService

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        val toLoginBtn = findViewById<Button>(R.id.toLogin)
        toLoginBtn.setOnClickListener {
            toLogin()
        }

        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val nameInput = findViewById<EditText>(R.id.nameInput)
        val registerBtn = findViewById<Button>(R.id.registerButton)
        registerBtn.setOnClickListener {
            register(emailInput.text.toString(), passwordInput.text.toString(), nameInput.text.toString())
        }
    }

    private fun toLogin() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun register(email: String, password: String, name: String) {
        getAuthService().register(applicationContext, email, password, name) { msg ->
            Log.i("this is message", msg.toString())
            when(msg) {
                null, "" -> {
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                }
                else -> {
                    val errMsg = findViewById<TextView>(R.id.errorMessage)
                    errMsg.text = when(msg) {
                        "duplicated key" -> "Already joined!"
                        else -> msg
                    }
                    errMsg.visibility = TextView.VISIBLE
                }
            }
        }
    }
}