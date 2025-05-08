package com.example.check_answer

import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.check_answer.databinding.ActivityMainBinding
import java.text.DecimalFormat
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    //переменные для подсчёта статистики
    private var correctAnswers = 0 // количество правильных ответов
    private var wrongAnswers = 0 // количество неправильных ответов
    var rightAnswer = 0.0 //правильный ответ примера
    var userAnswer: Int? = 0//ответ, введённый пользователем
    var showCorrectAnswer = true //показываем ли правильный ответ пользователю
    var displayedAnswer = 0.0
    var isUserThinkAnswerIsRight = false //переменная, в которой записывается, считает ли пользователь, что ответ верный
    // переменные для времени
    private var startTime = 0L
    val listOfTime = mutableListOf<Long>() //список времени на каждый ответ
    private val timeFormat = DecimalFormat("#.##")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    //метод нажатия на кнопку СТАРТ
    fun btnStartClick(view: View) {
        binding.btnStart.isEnabled = false //кнопка СТАРТ становится недоступна
        binding.linearLayoutExample.setBackgroundColor(Color.WHITE)
        generateAnExample() //генерация примера и вывод на экран
        binding.btnRight.isEnabled = true
        binding.btnWrong.isEnabled = true
        startTime = SystemClock.elapsedRealtime() //вычисление начального времени в миллисекундах
    }

    //метод нажатия на кнопку ВЕРНО
    fun btnRightClick(view: View) {
        isUserThinkAnswerIsRight = true
        if (showCorrectAnswer) {//если пользователь угадал, что пример ВЕРНЫЙ
            binding.linearLayoutExample.setBackgroundColor(Color.GREEN)
            correctAnswers++ }
        else {//если пользователь не угадал, что пример ВЕРНЫЙ
            binding.linearLayoutExample.setBackgroundColor(Color.RED)
            wrongAnswers++ }
        checkAnswer()
        updateStats()
    }

    //метод нажатия на кнопку НЕВЕРНО
    fun btnWrongClick(view: View) {
        isUserThinkAnswerIsRight = false
        if (!showCorrectAnswer) {//если пользователь угадал, что пример НЕВЕРНЫЙ
            binding.linearLayoutExample.setBackgroundColor(Color.GREEN)
            correctAnswers++ }
        else {//если пользователь не угадал, что пример НЕВЕРНЫЙ
            binding.linearLayoutExample.setBackgroundColor(Color.RED)
            wrongAnswers++ }
        checkAnswer()
        updateStats()
    }

    //все дополнительные действия к проверке ответа
    fun checkAnswer() {
        val currentTime = SystemClock.elapsedRealtime() - startTime //находим время, за которое был дан текущий ответ
        listOfTime.add(currentTime) //добавляем в список время, за которое был дан текущий ответ
        binding.btnRight.isEnabled = false //кнопка ВЕРНО становится недоступна
        binding.btnWrong.isEnabled = false //кнопка НЕВЕРНО становится недоступна
        binding.btnStart.isEnabled = true //кнопка СТАРТ становится доступна
    }

    //метод генерации примера
    fun generateAnExample() {
        val operations = listOf("*","/","-","+") //список операций
        val operation = operations.random() //генерируем одну операцию из списка
        var firstOperand = Random.nextInt(10,100) //первый операнд
        var secondOperand = 0 //второй операнд
        if (operation == "-") secondOperand = Random.nextInt(1,firstOperand)
        else secondOperand = Random.nextInt(10,100)

        rightAnswer = when (operation) {
            "*" -> (firstOperand * secondOperand).toDouble()
            "/" -> firstOperand.toDouble() / secondOperand
            "-" -> (firstOperand - secondOperand).toDouble()
            "+" -> (firstOperand + secondOperand).toDouble()
            else -> 0.0
        }
        showCorrectAnswer = Random.nextBoolean()//генерируем, какой ответ мы покажем пользователю
        if (showCorrectAnswer) displayedAnswer = rightAnswer //показываем правильный ответ
        else displayedAnswer = generateWrongAnswer(rightAnswer, operation) //генерируем неправильный ответ

        //вывод примера на экран
        binding.txtFirstOperand.text = firstOperand.toString()
        binding.txtOperation.text = operation
        binding.txtSecondOperand.text = secondOperand.toString()
        if (operation == "/") binding.txtResult.text = "%.2f".format(displayedAnswer)//если операция деления, то показываем с двумя знаками после запятой
        else binding.txtResult.text = displayedAnswer.toInt().toString()//если не операция деления, то показываем целое число
    }

    //метод генерации неправильного ответа
    fun generateWrongAnswer(rightAnswer : Double, operation : String) : Double {
        val miss = when (operation) {
            "/" -> "%.2f".format(Random.nextDouble(0.1, 1.0)).toDouble()
            else -> Random.nextInt(1, 10).toDouble()
        }
        var isPlusToAnswer = Random.nextBoolean()//будем отнимать от правильного ответа или увеличивать
        return when (isPlusToAnswer) {
            true -> rightAnswer + miss
            false -> rightAnswer - miss
        }

    }
    //метод обновления статистики
    fun updateStats() {
        if (listOfTime.isNotEmpty()) {
            binding.txtMinTime.text = timeFormat.format(listOfTime.min().toDouble() / 1000) //минимальное время
            binding.txtMaxTime.text = timeFormat.format(listOfTime.max().toDouble() / 1000) //максимальное время
            binding.txtAvgTime.text = timeFormat.format(listOfTime.average() / 1000) //среднее время
        }
        binding.txtNumberRight.text = correctAnswers.toString() //количество правильных ответов
        binding.txtNumberWrong.text = wrongAnswers.toString() //количество неправильных ответов
        binding.txtAllExamples.text = (correctAnswers + wrongAnswers).toString() ////количество ответов
        binding.txtPercentageCorrectAnswers.text = "%.2f%%".format(correctAnswers.toDouble() / (wrongAnswers + correctAnswers).toDouble() * 100) //количество правильных ответов в процентах
    }
}