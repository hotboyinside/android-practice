package com.example.myapplication

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        binding.openActivityButton.setOnClickListener {
            val text = validatedText() ?: return@setOnClickListener
            startActivity(Intent(this, SecondActivity::class.java).apply {
                putExtra(SecondActivity.EXTRA_TEXT, text)
            })
        }
        binding.dialButton.setOnClickListener {
            val text = validatedText() ?: return@setOnClickListener
            val number = text.replace(Regex("[\\s()\\-]"), "")
            if (!number.matches(Regex("\\+?[0-9]{3,15}"))) {
                binding.inputText.error = getString(R.string.invalid_phone)
                binding.inputText.requestFocus()
                return@setOnClickListener
            }
            launchExternal(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null)))
        }
        binding.shareButton.setOnClickListener {
            val text = validatedText() ?: return@setOnClickListener
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            launchExternal(Intent.createChooser(sendIntent, getString(R.string.share_chooser)))
        }
    }

    private fun validatedText(): String? {
        val text = binding.inputText.text.toString()
        if (text.isBlank()) {
            binding.inputText.error = getString(R.string.empty_input)
            binding.inputText.requestFocus()
            return null
        }
        binding.inputText.error = null
        return text
    }

    private fun launchExternal(intent: Intent) {
        try {
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, R.string.no_handler, Toast.LENGTH_LONG).show()
        }
    }
}
