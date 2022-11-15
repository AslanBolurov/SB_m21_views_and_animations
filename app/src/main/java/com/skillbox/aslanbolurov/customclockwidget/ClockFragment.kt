package com.skillbox.aslanbolurov.customclockwidget

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.skillbox.aslanbolurov.customclockwidget.databinding.FragmentClockBinding
import java.text.SimpleDateFormat

private const val TAG = "ClockFragment"

class ClockFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private var _binding:FragmentClockBinding?=null
    val binding get() = _binding!!

    private val viewModel:ClockFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding= FragmentClockBinding.inflate(inflater,container,false)

        return binding.root
    }

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ${SimpleDateFormat("HH:mm:ss").format(viewModel.time)}")
        binding.myWidget.time=viewModel.time
        binding.myWidget.timerJob?.start()
        binding.myWidget.execute()

    }


    @SuppressLint("SimpleDateFormat")
    override fun onDestroyView() {
        super.onDestroyView()
        binding.myWidget.timerJob?.cancel()
        Log.d(TAG, "onDestroyView: ${SimpleDateFormat("HH:mm:ss").format(viewModel.time)}")
        viewModel.time=binding.myWidget.time
        _binding=null
    }


}