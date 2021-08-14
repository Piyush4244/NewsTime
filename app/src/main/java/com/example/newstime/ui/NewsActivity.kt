package com.example.newstime.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.newstime.NewsApplication
import com.example.newstime.R
import com.example.newstime.databinding.ActivityNewsBinding
import com.example.newstime.db.NewsDatabase
import com.example.newstime.repository.NewsRepository

class NewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsBinding

    val viewModel:NewsViewModel by viewModels{NewsViewModelFactory(application,(application as NewsApplication).repository)}

    override fun onCreate(savedInstanceState: Bundle?) {
        println("newsActivity created")
        super.onCreate(savedInstanceState)
        binding= ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment=supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        val navController=navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }
}
