package com.harman.vsimakov2.CAPP.ui.tools

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.harman.vsimakov2.CAPP.R

class LogoutFragment : Fragment() {

    private lateinit var logoutViewModel: LogoutViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        logoutViewModel =
            ViewModelProviders.of(this).get(LogoutViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_logout, container, false)
        val textView: TextView = root.findViewById(R.id.text_tools)
        logoutViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}