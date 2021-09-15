package com.example.getandposttoapiusingmvc.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.getandposttoapiusingmvc.R
import com.example.getandposttoapiusingmvc.connectivity.ConnectivityLiveData
import com.example.getandposttoapiusingmvc.model.api.ApiInterface
import com.example.getandposttoapiusingmvc.model.constant.Constants
import com.example.getandposttoapiusingmvc.model.dataclass.post.commentsdataclass.CommentsDataClassItem
import com.example.getandposttoapiusingmvc.view.adapter.CommentsAdapter
import com.example.getandposttoapiusingmvc.view.listconverter.ConvertingCommentsListToArrayList.convertedCommentListToArrayList
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CommentsActivity : AppCompatActivity() {
    /**
     * Create instance of view model, connectivityLivedata, connectivityText, PostAdapter, backButton,
     * Recyclerview, commentsAdapter, commentsButton, all edit textView
     */
    private lateinit var connectivityLiveData: ConnectivityLiveData
    private lateinit var connectivityText: TextView
    private lateinit var instanceOfRecyclerView: RecyclerView
    private lateinit var instanceOfPostFromPostActivityTextView: TextView
    private lateinit var instanceOfNameEditTextView: TextInputEditText
    private lateinit var instanceOfBodyEditTextView: TextInputEditText
    private lateinit var instanceOfPostCommentsButton: Button
    private lateinit var instanceOfTextInputLayout: TextInputLayout
    private lateinit var instanceOfTextInputLayout2: TextInputLayout
    private lateinit var instanceOfCommentsDataClass : CommentsDataClassItem

    private val instanceOfCommentsAdapter by lazy { CommentsAdapter(convertedCommentListToArrayList as ArrayList<CommentsDataClassItem>) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        /**
         * Implement action back button
         */
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Comments"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)


        /**
         * Grab title and Id of post sent in by intent from main/post activity/adapter
         */
        val passedPostFromPostActivity = intent.getStringExtra("TITLE")
        val passedPostIdFromPostActivity = intent.getStringExtra("POSTID")
        /**
         * Convert the postIdPassedFromPostActivity or Adapter to string cos it came as string?
         *
         */
        val convertPostIdToString = passedPostIdFromPostActivity.toString()

        /**
         * Grab Id of postsTextView, backButton in comments activity, Recyclerview in comments
         * activity
         */
        instanceOfPostFromPostActivityTextView = findViewById(R.id.postFromPostActivityId)
        instanceOfRecyclerView = findViewById(R.id.commentsRecyclerViewId)
        instanceOfNameEditTextView = findViewById(R.id.commentNameId)
        instanceOfBodyEditTextView = findViewById(R.id.commentBodyId)
        instanceOfPostCommentsButton = findViewById(R.id.postCommentsButtonInCommentsActivityId)
        instanceOfTextInputLayout = findViewById(R.id.textInputLayout)
        instanceOfTextInputLayout2 = findViewById(R.id.textInputLayout2)

        /**
         * Set up comments recyclerview
         */
        instanceOfRecyclerView.layoutManager = LinearLayoutManager(this)
        instanceOfRecyclerView.setHasFixedSize(true)

        /**
         * Assign the created post text view space to the title text coming from postActivity
         */
        instanceOfPostFromPostActivityTextView.text = passedPostFromPostActivity

        /**
         * Grab view by ID for connectivityLivedata, connectivityText, recyclerView
         */
        connectivityLiveData = ConnectivityLiveData(application)
        connectivityText = findViewById(R.id.commentsConnectivityTextId)

        /**
         * Invite connectivityLivedata to observe network connection
         */
        connectivityLiveData.observe(this, { isAvailable ->
            when (isAvailable) {
                true -> {
                    connectivityText.visibility = View.GONE
                    instanceOfRecyclerView.visibility = View.VISIBLE
                    instanceOfNameEditTextView.visibility = View.VISIBLE
                    instanceOfBodyEditTextView.visibility = View.VISIBLE
                    instanceOfPostCommentsButton.visibility = View.VISIBLE
                    instanceOfTextInputLayout2.visibility = View.VISIBLE
                    instanceOfTextInputLayout.visibility = View.VISIBLE
                }
                false -> {
                    connectivityText.visibility = View.VISIBLE
                    instanceOfRecyclerView.visibility = View.GONE
                    instanceOfNameEditTextView.visibility = View.GONE
                    instanceOfBodyEditTextView.visibility = View.GONE
                    instanceOfPostCommentsButton.visibility = View.GONE
                    instanceOfTextInputLayout2.visibility = View.GONE
                    instanceOfTextInputLayout.visibility = View.GONE
                }
            }
        })

        getCommentsInCommentsActivity(convertPostIdToString)

        /**
         * Using addTextChangeListener to enable post button when input fields are not empty
         */

        instanceOfBodyEditTextView.addTextChangedListener(addCommentsOnButtonClick)
        instanceOfNameEditTextView.addTextChangedListener(addCommentsOnButtonClick)

        instanceOfPostCommentsButton.setOnClickListener {
            functionPushCommentsToApi()
            functionClearText()
        }

//        functionPushCommentsToApi()
    }

    private fun getCommentsInCommentsActivity(convertPostIdFromNullStringToString:String) {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level= HttpLoggingInterceptor.Level.BODY
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

        /**
         * Creating an instance of retrofit builder
         */
        val instanceOfRetrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .build()
            .create(ApiInterface::class.java)

        val getCommentsFromRetrofitBuilder = instanceOfRetrofitBuilder.getCommentsInInterfacePerPost(convertPostIdFromNullStringToString)

        /**
         * Using enqueue to get data from the api on a new thread
         */
        getCommentsFromRetrofitBuilder.enqueue(object : Callback<List<CommentsDataClassItem>?> {
            override fun onResponse(call: Call<List<CommentsDataClassItem>?>, response: Response<List<CommentsDataClassItem>?>) {
            var commentsReceivedInOnResponseInEnqueue = response.body()!!

            convertedCommentListToArrayList= commentsReceivedInOnResponseInEnqueue
            instanceOfCommentsAdapter.notifyDataSetChanged()
            instanceOfRecyclerView.adapter = instanceOfCommentsAdapter
        }
            override fun onFailure(call: Call<List<CommentsDataClassItem>?>, t: Throwable) {
                Toast.makeText(this@CommentsActivity, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun functionPushCommentsToApi(){

        val interceptor = HttpLoggingInterceptor()
        interceptor.level= HttpLoggingInterceptor.Level.BODY
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val instanceOfRetrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .build()
            .create(ApiInterface::class.java)

        val generatePostId = (0..10000).random()
        val generatedId = (0..10000000).random()
        val grabbedNameInComments = instanceOfNameEditTextView.text.toString()
        val defaultEmail = "seunawonugba@gmail.com"
        val grabbedBodyInComments = instanceOfBodyEditTextView.text.toString()


        val usersComment = CommentsDataClassItem(grabbedBodyInComments, defaultEmail, generatedId, grabbedNameInComments, generatePostId)
        instanceOfCommentsDataClass = usersComment
        instanceOfCommentsAdapter.commentsArrayList.add(instanceOfCommentsDataClass)
        instanceOfCommentsAdapter.notifyDataSetChanged()
        instanceOfRecyclerView.adapter = instanceOfCommentsAdapter


        val pushedCommeToApi = instanceOfRetrofitBuilder.pushCommentsToApiInRepository(instanceOfCommentsDataClass)

        pushedCommeToApi.enqueue(object : Callback<CommentsDataClassItem?> { override fun onResponse(call: Call<CommentsDataClassItem?>, response: Response<CommentsDataClassItem?>) {
            Log.d("POSTCOMMENTS", "Body: ${response.body().toString()}")
            Log.d("POSTCOMMENTS", "Code: ${response.code()}")
            Log.d("POSTCOMMENTS", "Message: ${response.message()}")
        }
            override fun onFailure(call: Call<CommentsDataClassItem?>, t: Throwable) {
                Toast.makeText(this@CommentsActivity, t.message, Toast.LENGTH_LONG).show()
            }
        })


    }

    private val addCommentsOnButtonClick: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val grabbedBodyInComments: String = instanceOfBodyEditTextView.text.toString().trim()
            val grabbedNameInComments: String = instanceOfNameEditTextView.text.toString().trim()
            instanceOfPostCommentsButton.isEnabled = grabbedBodyInComments.isNotEmpty() && grabbedNameInComments.isNotEmpty()
        }
        override fun afterTextChanged(s: Editable) {}
    }

    private fun functionClearText() {
        instanceOfNameEditTextView.text = null
        instanceOfBodyEditTextView.text = null
    }
}