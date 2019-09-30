package com.example.syari.sim_pbb.menu_fragment


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.syari.sim_pbb.menu_activity.DetailCekNopActivity
import com.example.syari.sim_pbb.adapter.PageContentAdapter

import com.example.syari.sim_pbb.R
import kotlinx.android.synthetic.main.fragment_cek_nop.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class CekNopFragment : Fragment() {

    private lateinit var pageContentAdapter: PageContentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cek_nop, container, false)

//        pageContentAdapter = PageContentAdapter(childFragmentManager)
//        pageContentAdapter!!.addFragment(DataPajakFragment(),"Data Wajib Pajak")
//        pageContentAdapter!!.addFragment(PbbFragment(),"PBB")
//        view.content.adapter = pageContentAdapter
//        view.tabContent.setupWithViewPager(view.content)

        view.btnGoCekNop.setOnClickListener({
            val intent = Intent(view.context, DetailCekNopActivity::class.java)
            var nop = view.inputNop.text.toString()
            var tahun = view.tahunNop.text.toString()
            intent.putExtra("nop",nop)
            intent.putExtra("tahun",tahun)

            startActivity(intent)
        })

        view.btnResetCekNop.setOnClickListener({
            view.inputNop.text.clear()
            view.tahunNop.text.clear()
        })

        return view
    }


}
