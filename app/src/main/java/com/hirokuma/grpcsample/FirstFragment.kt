package com.hirokuma.grpcsample

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.hirokuma.grpcsample.databinding.FragmentFirstBinding
import io.grpc.ManagedChannelBuilder
import io.grpc.examples.helloworld.GreeterGrpcKt
import io.grpc.examples.helloworld.helloRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val channel = let {
        val builder = ManagedChannelBuilder.forAddress("192.168.0.50", 50051)
        // builder.useTransportSecurity() // https
        builder.usePlaintext() // http
        builder.executor(Dispatchers.IO.asExecutor()).build()
    }
    private val greeter = GreeterGrpcKt.GreeterCoroutineStub(channel)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    private suspend fun sayHello(name: String): String {
        val request = helloRequest { this.name = name }
        val response = greeter.sayHello(request)
        return response.message
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            val scope = CoroutineScope(Dispatchers.Default)
            scope.launch(Dispatchers.Main) {
                val text = try {
                    val response = sayHello("hello")
                    response
            //                    findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
                } catch (e: Exception) {
                    e.message ?: "Unknown Error"
                }
                val text1 = view.findViewById<TextView>(R.id.textview_first)
                text1.text = text
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        channel.shutdownNow()
        _binding = null
    }
}