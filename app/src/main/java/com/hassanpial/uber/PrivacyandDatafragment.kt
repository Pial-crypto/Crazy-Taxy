package com.hassanpial.uber

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PrivacyandDatafragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PrivacyandDatafragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
     var view= inflater.inflate(R.layout.fragment_privacyand_datafragment, container, false)

        view.findViewById<Button>(R.id.manageyouraccount).setOnClickListener(){
            startActivity(Intent(requireContext(),EditProfile::class.java))
        }
        // Set OnClickListener for each TextView
        view.findViewById<TextView>(R.id.data_collection_title)?.setOnClickListener {
            // Handle click for Data Collection and Usage
            //onDataCollectionClicked()
            startActivity(Intent(requireContext(),Datacollentionandusage::class.java))
        }

        view.findViewById<TextView>(R.id.data_access_title)?.setOnClickListener {
            // Handle click for Data Access and Deletion
            //onDataAccessClicked()
            startActivity(Intent(requireContext(),DataCollectionandDeletion::class.java))
        }

        view.findViewById<TextView>(R.id.privacy_policy_title)?.setOnClickListener {
            // Handle click for Privacy Policy and Terms of Service
            //o/nPrivacyPolicyClicked()
            startActivity(Intent(requireContext(),termsandpolicy::class.java))
        }

        view.findViewById<TextView>(R.id.feedback_support_title)?.setOnClickListener {
            // Handle click for Feedback and Support
            //onFeedbackSupportClicked()
            startActivity(Intent(requireContext(),FeedbackandSupport::class.java))
        }

        view.findViewById<TextView>(R.id.security_measures_title)?.setOnClickListener {
            // Handle click for Security Measures
            //onSecurityMeasuresClicked()
            startActivity(Intent(requireContext(),securitymeasures::class.java))
        }

        view.findViewById<TextView>(R.id.data_usage_analytics_title)?.setOnClickListener {
            // Handle click for Data Usage Analytics
           // onDataUsageAnalyticsClicked()
            startActivity(Intent(requireContext(),DataUsageAnalytics::class.java))
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PrivacyandDatafragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PrivacyandDatafragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}