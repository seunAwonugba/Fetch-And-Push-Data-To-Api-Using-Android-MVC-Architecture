package com.example.getandposttoapiusingmvc.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import com.example.getandposttoapiusingmvc.R
import com.google.android.material.textfield.TextInputEditText

class AddNewPostActivity : AppCompatActivity() {
    private lateinit var instanceOfTitleTextView: TextInputEditText
    private lateinit var instanceOfBodyTextView: TextInputEditText
    private lateinit var instanceOfShareButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_post)

        /**
         * Set action bar, to enable on back press
         */

        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "New Post"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)

        /**
         * Find Id of views
         */

        instanceOfBodyTextView = findViewById(R.id.bodyTextViewInShareActivityId)
        instanceOfShareButton = findViewById(R.id.sharePostButtonId)
        instanceOfTitleTextView = findViewById(R.id.titleTextViewInShareActivityId)

        instanceOfBodyTextView.addTextChangedListener(addCommentsOnButtonClick)
        instanceOfTitleTextView.addTextChangedListener(addCommentsOnButtonClick)

        instanceOfShareButton.setOnClickListener {
            grabUsersInputAndPassToPostActivity()
            functionClearText()
        }
    }

    private fun functionClearText() {
        instanceOfTitleTextView.text = null
        instanceOfBodyTextView.text = null
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }



    private val addCommentsOnButtonClick: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val grabBodyInPostActivity: String = instanceOfBodyTextView.text.toString().trim()
            val grabTitleInPostActivity: String = instanceOfTitleTextView.text.toString().trim()
            instanceOfShareButton.isEnabled = grabBodyInPostActivity.isNotEmpty() && grabTitleInPostActivity.isNotEmpty()
        }
        override fun afterTextChanged(s: Editable) {}
    }

    private fun grabUsersInputAndPassToPostActivity(){

        val titleToPassFromPostActivity = instanceOfTitleTextView.text
        val bodyToPassFromPostActivity = instanceOfBodyTextView.text

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("PASSED_TITLE", titleToPassFromPostActivity.toString())
        intent.putExtra("PASSED_BODY", bodyToPassFromPostActivity.toString())

        startActivity(intent)
    }
}