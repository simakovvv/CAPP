package com.harman.vsimakov2.CAPP.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import com.harman.vsimakov2.CAPP.R

class ApplicationsFragment : Fragment(), TabLayout.OnTabSelectedListener {

    private lateinit var applicationsViewModel: ApplicationsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        applicationsViewModel =
            ViewModelProviders.of(this).get(ApplicationsViewModel::class.java)
        return inflater.inflate(R.layout.fragment_applications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val textView: TextView = view.findViewById(R.id.text_applications)
        val tabLayout: TabLayout = view.findViewById(R.id.tab_bar_applications)
        applicationsViewModel.text.observe(this, Observer {
            textView.text = it
        })
        tabLayout.addOnTabSelectedListener(this)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onTabReselected(p0: TabLayout.Tab?) {
        // Nothing
    }

    override fun onTabUnselected(p0: TabLayout.Tab?) {
        // Nothing
    }

    override fun onTabSelected(p0: TabLayout.Tab?) {
        when (p0?.text) {
            getString(R.string.library) ->  applicationsViewModel.setText( getString(R.string.library))
            getString(R.string.installed) ->  applicationsViewModel.setText( getString(R.string.installed))
            getString(R.string.updates) ->  applicationsViewModel.setText( getString(R.string.updates))
        }
    }
}