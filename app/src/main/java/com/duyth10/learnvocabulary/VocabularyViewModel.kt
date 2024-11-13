package com.duyth10.learnvocabulary

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VocabularyViewModel(application: Application) : AndroidViewModel(application) {

    private val vocabularyDao = VocabularyDatabase.getDatabase(application).vocabularyDao()
    val vocabularyList: LiveData<List<Vocabulary>> = vocabularyDao.getAllVocabulary()

    private val _currentQuestion = MutableLiveData<QuizQuestion>()
    val currentQuestion: LiveData<QuizQuestion> get() = _currentQuestion

    private val selectedVocabularies = mutableListOf<Vocabulary>()

    private val usedQuestions = mutableListOf<Vocabulary>()

    private val _navigateBack = MutableLiveData<Boolean>()
    val navigateBack: LiveData<Boolean> get() = _navigateBack

    fun addVocabulary(vocabulary: Vocabulary) {
        viewModelScope.launch {
            vocabularyDao.insert(vocabulary)
        }
    }

    fun updateVocabulary(vocabulary: Vocabulary) {
        viewModelScope.launch {
            vocabularyDao.update(vocabulary)
        }
    }

    fun deleteVocabulary(vocabulary: Vocabulary) {
        viewModelScope.launch {
            vocabularyDao.delete(vocabulary)
        }
    }

    fun generateQuizQuestion() {
        CoroutineScope(Dispatchers.IO).launch {
            if (selectedVocabularies.isNotEmpty()) {
                val unusedVocabularies = selectedVocabularies.filter { it !in usedQuestions }

                if (unusedVocabularies.isNotEmpty()) {
                    // Lấy ngẫu nhiên một từ vựng chưa sử dụng
                    val randomVocabulary = unusedVocabularies.random()
                    usedQuestions.add(randomVocabulary)

                    val question = "What is the meaning of '${randomVocabulary.eng}'?"
                    val correctAnswer = randomVocabulary.vie

                    val wrongAnswers = selectedVocabularies.filter { it.eng != randomVocabulary.eng }
                        .shuffled()
                        .take(3)
                        .map { it.vie }

                    val options = (wrongAnswers + correctAnswer).shuffled()

                    _currentQuestion.postValue(QuizQuestion(question, correctAnswer, options))
                } else {
                    _navigateBack.postValue(true)
                }
            }
        }
    }


    fun setSelectedVocabularies(selectedIds: List<Int>) {
        viewModelScope.launch {
            val vocabularies = vocabularyList.value?.filter { selectedIds.contains(it.id) } ?: listOf()
            Log.d("vocabulariesVMD",vocabularies.toString())
            selectedVocabularies.clear()
            selectedVocabularies.addAll(vocabularies)
            usedQuestions.clear()
            generateQuizQuestion()
        }
    }

    fun onNavigatedBack() {
        _navigateBack.value = false
    }


}
