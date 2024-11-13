package com.duyth10.learnvocabulary

data class QuizQuestion(
    val question: String,
    val correctAnswer: String,
    val options: List<String>
)