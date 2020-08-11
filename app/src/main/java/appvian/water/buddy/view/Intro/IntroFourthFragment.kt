package appvian.water.buddy.view.Intro

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import appvian.water.buddy.R
import appvian.water.buddy.view.MainActivity
import appvian.water.buddy.viewmodel.IntroViewModel
import kotlinx.android.synthetic.main.intro_fourth_fragment.view.*


class IntroFourthFragment : Fragment() {
    private lateinit var introViewModel: IntroViewModel
    lateinit var nametext : TextView
    lateinit var tagetamounttext : TextView
    lateinit var cmtext : TextView


    companion object {
        fun newInstance() = IntroFourthFragment()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.intro_fourth_fragment, container, false)
        nametext = view.intro_fourth_name
        tagetamounttext = view.intro_fourth_Targetamount
        cmtext = view.cmtext
        view.startbtn.setOnClickListener {
            startActivity(Intent(activity as IntroActivity, MainActivity::class.java))
        }
        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        introViewModel =
            ViewModelProvider(activity!!).get<IntroViewModel>(IntroViewModel::class.java)

        introViewModel.nameliveText.observe(viewLifecycleOwner,
        Observer<Any> { o -> nametext.text = o!!.toString() })

        introViewModel.kgliveText.observe(viewLifecycleOwner,
            Observer<Any> { o -> tagetamounttext.text = o!!.toString() })

        introViewModel.heightliveText.observe(viewLifecycleOwner,
            Observer<Any> { o -> cmtext.text = o!!.toString() })

    }


}