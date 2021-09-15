package com.example.getandposttoapiusingmvc.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.getandposttoapiusingmvc.R
import com.example.getandposttoapiusingmvc.model.dataclass.post.PostDataClassItem
import com.example.getandposttoapiusingmvc.view.CommentsActivity

class PostAdapter(var postArrayList: ArrayList<PostDataClassItem>, private var context: Context): RecyclerView.Adapter<PostAdapter.MyViewHolder>() {
    inner class MyViewHolder(var myView: View): RecyclerView.ViewHolder(myView) {
        private var instanceOfTitleTextView: TextView
        private var instanceOfBodyTextViewId: TextView

        init {
            instanceOfTitleTextView = myView.findViewById(R.id.titleTextViewId)
            instanceOfBodyTextViewId = myView.findViewById(R.id.bodyTextViewId)
        }

        fun bindingFunction(bindingFunctionList: PostDataClassItem){
            instanceOfTitleTextView.text = bindingFunctionList.title
            instanceOfBodyTextViewId.text = bindingFunctionList.body
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflatingVariable = LayoutInflater.from(parent.context).inflate(R.layout.post_list_item, parent,false)
        return MyViewHolder(inflatingVariable)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindingFunction(postArrayList[position])

        holder.myView.setOnClickListener {
            val intent = Intent(context, CommentsActivity::class.java)
            intent.putExtra("TITLE", postArrayList[position].title)
            intent.putExtra("POSTID", postArrayList[position].id.toString())
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return postArrayList.size
    }
}