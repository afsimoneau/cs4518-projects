package com.example.cs4518_project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import java.util.*

class DetailFragment : Fragment() {
    private lateinit var teamBScore: TextView
    private lateinit var teamAScore: TextView
    private lateinit var teamAText: TextView
    private lateinit var teamBText: TextView

    private lateinit var resetButt: Button
    private lateinit var historyButt: Button
    private lateinit var saveButt: Button

    private lateinit var history: History

    private var historyRepository: HistoryRepository = HistoryRepository.get()

    interface Callbacks {
        fun onClickNewGameFromDetail()
        fun onClickHistoryFromDetail()
    }

    private var callbacks: Callbacks? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    private val historyDetailViewModel: HistoryDetailViewModel by lazy {
        ViewModelProvider(requireActivity()).get(HistoryDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        history = History()
        val historyId = arguments?.getSerializable("history_id") as UUID
        historyDetailViewModel.loadHistory(historyId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        container?.removeAllViews()
        val view = inflater.inflate(R.layout.fragment_detail, container, false)

        findViews(view)

        setOnClickListeners(view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        historyDetailViewModel.historyLiveData.observe(viewLifecycleOwner, { history ->
            history.let {
                this.history = history
                updateUI()
            }
        })
    }

    private fun updateUI() {
        teamAText.text = history.teamAName
        teamBText.text = history.teamBName
        teamAScore.text = history.teamAScore.toString()
        teamBScore.text = history.teamBScore.toString()
    }

    private fun setOnClickListeners(view: View) {
        resetButt.setOnClickListener {
            Log.d(this::class.java.toString(), "newGameButt")

            callbacks?.onClickNewGameFromDetail()
        }

        historyButt.setOnClickListener {
            Log.d(this::class.java.toString(), "historyButt")

            callbacks?.onClickHistoryFromDetail()
        }

        saveButt.setOnClickListener {
            historyRepository.updateHistory(history)
            historyDetailViewModel.loadHistory(history.id)
            updateUI()
        }
    }

    private fun findViews(view: View) {
        teamAScore = view.findViewById(R.id.teamAScore)
        teamBScore = view.findViewById(R.id.teamBScore)
        teamAText = view.findViewById(R.id.teamAText)
        teamBText = view.findViewById(R.id.teamBText)

        historyButt = view.findViewById(R.id.historyButt_detail)
        saveButt = view.findViewById(R.id.saveButt_detail)
        resetButt = view.findViewById(R.id.resetButt_detail)
    }

    companion object {
        fun newInstance(historyId: UUID): DetailFragment {
            val args = Bundle().apply {
                putSerializable("history_id", historyId)
            }
            return DetailFragment().apply { arguments = args }
        }
    }

    override fun onStop() {
        super.onStop()
        historyDetailViewModel.saveHistory(history)
    }
}