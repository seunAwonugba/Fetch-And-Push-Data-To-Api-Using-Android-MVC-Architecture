package com.example.getandposttoapiusingmvc.view

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.getandposttoapiusingmvc.view.listconverter.ConvertingPostListToArrayList.convertedPostListToArrayList
import com.example.getandposttoapiusingmvc.R
import com.example.getandposttoapiusingmvc.connectivity.ConnectivityLiveData
import com.example.getandposttoapiusingmvc.model.constant.Constants.Companion.BASE_URL
import com.example.getandposttoapiusingmvc.model.dataclass.post.PostDataClassItem
import com.example.getandposttoapiusingmvc.model.api.ApiInterface
import com.example.getandposttoapiusingmvc.model.constant.Constants
import com.example.getandposttoapiusingmvc.model.dataclass.post.commentsdataclass.CommentsDataClassItem
import com.example.getandposttoapiusingmvc.view.adapter.PostAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    /**
     * Create an instance of recyclerView, connectivityLiveData, connectivityText and lazily initialise the adapter
     */
    private lateinit var instanceOfRecyclerView: RecyclerView
    private lateinit var connectivityLiveData: ConnectivityLiveData
    private lateinit var connectivityText: TextView
    private lateinit var instanceOfPostDataClass : PostDataClassItem
    private lateinit var instanceOfPostAdapter: PostAdapter
    private lateinit var instanceOfFloatingButton: FloatingActionButton




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**
         * grab ID of recyclerview, connectivityLivedata and connectivity text and floatingButton
         */
        instanceOfRecyclerView = findViewById(R.id.recyclerViewId)
        connectivityLiveData = ConnectivityLiveData(application)
        connectivityText = findViewById(R.id.connectivityTextId)
        instanceOfFloatingButton = findViewById(R.id.floatingActionButtonId)

        instanceOfPostAdapter = PostAdapter(arrayListOf(), this)



        /**
         * Set up the recyclerview
         */
        instanceOfRecyclerView.layoutManager = LinearLayoutManager(this)
        instanceOfRecyclerView.setHasFixedSize(true)

        if(Intent.ACTION_SEARCH == intent.action){
            handleIntent(intent)
        } else{
            getPostInPostActivity()
        }

        /**
         * Invite connectivityLivedata to observe network connection
         */
        connectivityLiveData.observe(this, { isAvailable ->
            when (isAvailable) {
                true -> {
                    connectivityText.visibility = View.GONE
                    instanceOfRecyclerView.visibility = View.VISIBLE
                }
                false -> {
                    connectivityText.visibility = View.VISIBLE
                    instanceOfRecyclerView.visibility = View.GONE
                }
            }
        })

        instanceOfFloatingButton.setOnClickListener { toAddNewPostActivity() }

        receiveDataPassedFromShareButton()



    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        // Verify the action and get the query
        if (Intent.ACTION_SEARCH == intent?.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                doMySearch(query)
            }
        }

    }

    private fun doMySearch(query: String) {
        val filtered = convertedPostListToArrayList.filter { it.body.contains(query,false) || it.title.contains(query, false) }
        instanceOfPostAdapter = PostAdapter(filtered as ArrayList<PostDataClassItem>, this@MainActivity)
        instanceOfPostAdapter.notifyDataSetChanged()
        instanceOfRecyclerView.adapter = instanceOfPostAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)

        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }

        return true
    }

    private fun getPostInPostActivity() {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level= HttpLoggingInterceptor.Level.BODY
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

        /**
         * Creating an instance of retrofit builder
         */
        val instanceOfRetrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(client)
            .build()
            .create(ApiInterface::class.java)

        val getPostFromRetrofitBuilder = instanceOfRetrofitBuilder.getPostFromApiInInterface()

        /**
         * Using enqueue to get data from the api on a new thread
         */

        getPostFromRetrofitBuilder.enqueue(object : Callback<List<PostDataClassItem>?> {
            override fun onResponse(call: Call<List<PostDataClassItem>?>, response: Response<List<PostDataClassItem>?>) {
                val postReceivedInOnResponseInEnqueue = response.body()!!
                convertedPostListToArrayList = postReceivedInOnResponseInEnqueue
                instanceOfPostAdapter = PostAdapter(postReceivedInOnResponseInEnqueue as ArrayList<PostDataClassItem>,this@MainActivity)
                instanceOfPostAdapter.notifyDataSetChanged()
                instanceOfRecyclerView.adapter = instanceOfPostAdapter
            }

            override fun onFailure(call: Call<List<PostDataClassItem>?>, t: Throwable) {
                Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun toAddNewPostActivity() {
        val intent = Intent(this, AddNewPostActivity::class.java )
        startActivity(intent)
    }

    private fun receiveDataPassedFromShareButton(){

        val interceptor = HttpLoggingInterceptor()
        interceptor.level= HttpLoggingInterceptor.Level.BODY
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val instanceOfRetrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .build()
            .create(ApiInterface::class.java)

        val generatedUserId = (0..10000).random()
        val generatedId = (0..10000000).random()
        val receivedTitle = intent.getStringExtra("PASSED_TITLE")
        val receivedBody = intent.getStringExtra("PASSED_BODY")


        if(receivedTitle != null && receivedBody != null){
            val usersPost = PostDataClassItem(receivedBody, generatedId, receivedTitle, generatedUserId)
            instanceOfPostDataClass = usersPost
            instanceOfPostAdapter.postArrayList.add(instanceOfPostDataClass)
            instanceOfPostAdapter.notifyDataSetChanged()
            instanceOfRecyclerView.adapter = instanceOfPostAdapter
            val pushPostToApi = instanceOfRetrofitBuilder.pushPostsToApiInRepository(instanceOfPostDataClass)

            pushPostToApi.enqueue(object : Callback<PostDataClassItem?> { override fun onResponse(call: Call<PostDataClassItem?>, response: Response<PostDataClassItem?>) {
                Log.d("PUSHPOST", "Body: ${response.body().toString()}")
                Log.d("PUSHPOST", "Code: ${response.code()}")
                Log.d("PUSHPOST", "Message: ${response.message()}")
            }

                override fun onFailure(call: Call<PostDataClassItem?>, t: Throwable) {
                    Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_LONG).show()
                }
            })
        }


    }
}