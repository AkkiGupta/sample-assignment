package assignment.sample.coding.presentation.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import assignment.sample.coding.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        bottomNavigationView.setupWithNavController(navHostFragment.findNavController())

        navHostFragment.findNavController()
            .addOnDestinationChangedListener { _, destination, arguments ->
                when(destination.id) {
                    R.id.mapFragment -> {
                        arguments?.let {
                            if(it.size() > 0)
                                bottomNavigationView.visibility = View.GONE
                            else bottomNavigationView.visibility = View.VISIBLE
                        }
                    }
                    else ->
                        bottomNavigationView.visibility = View.VISIBLE
                }
            }
    }
}