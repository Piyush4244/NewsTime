package com.example.newstime.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newstime.databinding.ItemArticlePreviewBinding
import com.example.newstime.models.Article

class NewsListAdapter(private val fragment:Fragment) :ListAdapter<Article,NewsListAdapter.NewsViewHolder>(NewsComparator()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(
                ItemArticlePreviewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article=getItem(position)
        holder.bind(article!!)
    }

    class NewsComparator:DiffUtil.ItemCallback<Article>(){

        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url==newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem==newItem
        }
    }

    inner class NewsViewHolder(private val binding:ItemArticlePreviewBinding) :RecyclerView.ViewHolder(binding.root){

        fun bind(article: Article){
            binding.apply {
                Glide.with(fragment).load(article.urlToImage).into(ivArticleImage)
                tvSource.text=article.source?.name
                tvTitle.text=article.title
                tvDescription.text=article.description
                tvPublishedAt.text=article.publishedAt
                root.setOnClickListener {
                    onItemClickListener?.let { it(article) }
                }
            }
        }
    }

    private var onItemClickListener:((Article)->Unit)?=null

    fun setOnItemClickListener(listener:(Article)->Unit){
        onItemClickListener=listener
    }
}