package it.unipd.dei.pseaiproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import it.unipd.dei.pseaiproject.databinding.FragmentBottomSheetBinding

class BottomSheetFragment: BottomSheetDialogFragment() {

    //TODO CAPIRE SE SERVE IL NULL OPPURE NO
    // Classe generata automaticamente basata sul nome del file xml
    private var _binding: FragmentBottomSheetBinding? = null

    // Fornisce un accesso non nullable a _binding
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.closeButton.setOnClickListener {
            this.dismiss()
        }
    }

    // Resetto _binding per evitare memory leaks
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}