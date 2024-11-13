package com.duyth10.learnvocabulary

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

class QuizFragment : Fragment() {

    private lateinit var tvQuestion: TextView
    private lateinit var btnOption1: Button
    private lateinit var btnOption2: Button
    private lateinit var btnOption3: Button
    private lateinit var btnOption4: Button

    private var correctAnswersCount = 0
    private var incorrectAnswersCount = 0

    private val viewModel: VocabularyViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_quiz, container, false)


        viewModel.vocabularyList.observe(viewLifecycleOwner, Observer { updatedList ->
            val selectedVocabularyIds = arguments?.getIntegerArrayList("selectedVocabularyIds")
            if (selectedVocabularyIds != null) {
                viewModel.setSelectedVocabularies(selectedVocabularyIds)
                Log.d("selectedVocabularyIds", selectedVocabularyIds.toString())
            }
        })


        tvQuestion = view.findViewById(R.id.tvQuestion)
        btnOption1 = view.findViewById(R.id.btnOption1)
        btnOption2 = view.findViewById(R.id.btnOption2)
        btnOption3 = view.findViewById(R.id.btnOption3)
        btnOption4 = view.findViewById(R.id.btnOption4)

        viewModel.currentQuestion.observe(viewLifecycleOwner) { quizQuestion ->
            tvQuestion.text = quizQuestion.question ?: ""
            btnOption1.text = quizQuestion.options.getOrNull(0) ?: ""
            btnOption2.text = quizQuestion.options.getOrNull(1) ?: ""
            btnOption3.text = quizQuestion.options.getOrNull(2) ?: ""
            btnOption4.text = quizQuestion.options.getOrNull(3) ?: ""
        }

        btnOption1.setOnClickListener { checkAnswer(btnOption1.text.toString()) }
        btnOption2.setOnClickListener { checkAnswer(btnOption2.text.toString()) }
        btnOption3.setOnClickListener { checkAnswer(btnOption3.text.toString()) }
        btnOption4.setOnClickListener { checkAnswer(btnOption4.text.toString()) }

        viewModel.generateQuizQuestion()

        viewModel.navigateBack.observe(viewLifecycleOwner) { shouldNavigateBack ->
            if (shouldNavigateBack) {
                sendResultToMainFragment()


            }
        }

        return view
    }

    private fun checkAnswer(selectedAnswer: String) {
        val correctAnswer = viewModel.currentQuestion.value?.correctAnswer
        if (selectedAnswer == correctAnswer) {
            correctAnswersCount++
            Toast.makeText(context, "Correct!", Toast.LENGTH_SHORT).show()
        } else {
            incorrectAnswersCount++
            Toast.makeText(context, "Wrong answer!", Toast.LENGTH_SHORT).show()
        }
        viewModel.generateQuizQuestion()
    }

    private fun sendResultToMainFragment() {
        val resultBundle = Bundle().apply {
            putInt("correctAnswersCount", correctAnswersCount)
            putInt("incorrectAnswersCount", incorrectAnswersCount)
        }
        Log.d("correctAnswersCount", correctAnswersCount.toString())
        Log.d("incorrectAnswersCount", incorrectAnswersCount.toString())

        viewModel.onNavigatedBack()

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        val mainFragment = MainFragment()
        mainFragment.arguments = resultBundle
        transaction.replace(R.id.fragment_container, mainFragment)
        transaction.addToBackStack(null)
        transaction.commit()


    }
}
