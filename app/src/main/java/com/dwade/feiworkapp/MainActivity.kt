package com.dwade.feiworkapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    private lateinit var startTime: EditText
    private lateinit var endTime: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val pattern = DateTimeFormatter.ofPattern("MMM dd")
        val dayViewer: TextView = findViewById(R.id.businessDay)
        var currentDate = LocalDate.now()
        var businessDaysAdded = 0

        while (businessDaysAdded < 5) {
            currentDate = currentDate.plusDays(1)
            if (currentDate.dayOfWeek != DayOfWeek.SATURDAY && currentDate.dayOfWeek != DayOfWeek.SUNDAY) {
                businessDaysAdded++
            }
        }

        dayViewer.text = currentDate.format(pattern)

        startTime = findViewById(R.id.startTimeField)
        endTime = findViewById(R.id.endTimeField)

        val startTimeRadioGroup = findViewById<RadioGroup>(R.id.startTimeRadioGroup)
        val endTimeRadioGroup = findViewById<RadioGroup>(R.id.endTimeRadioGroup)

        val isEndTimePMSelected = endTimeRadioGroup.checkedRadioButtonId == R.id.endTimePM
        val isStartTimePMSelected = startTimeRadioGroup.checkedRadioButtonId == R.id.startTimePM

        val textView: TextView = findViewById(R.id.textView3)
        val hoursTitle: TextView = findViewById(R.id.textView2)

        val calcButton = findViewById<Button>(R.id.calculateButton)
        calcButton.setOnClickListener {
            val start = startTime.text
            val end = endTime.text
            val startLength = start.length
            val endLength = end.length

            //error if time too short
            if (startLength < 3 || endLength < 3) {
                textView.text = R.string.err_too_short.toString()
                return@setOnClickListener
            }

            //setting mid index
            val startMidIndex: Int = when(startLength) {
                3 -> 1
                else -> 2//only 4 digits possible
            }
            val endMidIndex: Int = when(endLength) {
            3 -> 1
            else -> 2
            }

            //parsing minutes and hours
            var startHour = start.substring(0,startMidIndex).toDouble()
            val startMinute = start.substring(startMidIndex).toDouble()
            var endHour = end.substring(0,endMidIndex).toDouble()
            val endMinute = end.substring(endMidIndex).toDouble()

            //format check, hour must be between 1 and 12
            if (startHour > 12 || startHour < 0 || endHour > 12 || endHour < 0) {
                textView.text = R.string.err_invalid_time.toString()
                return@setOnClickListener
            }

            //12hr to 24hr conversions
            startHour = when {
                startHour == 12.0 && !isStartTimePMSelected -> 0.0 // Midnight case: 12 AM to 00:00
                startHour == 12.0 && isStartTimePMSelected -> 12.0 // Noon case: 12 PM remains 12:00
                isStartTimePMSelected -> startHour + 12 // Afternoon/evening case: Add 12 for PM
                else -> startHour // Morning case: Hours stay the same
            }

            endHour = when {
                endHour == 12.0 && !isEndTimePMSelected -> 0.0 // Midnight case: 12 AM to 00:00
                endHour == 12.0 && isEndTimePMSelected -> 12.0 // Noon case: 12 PM remains 12:00
                isEndTimePMSelected -> endHour + 12 // Afternoon/evening case: Add 12 for PM
                else -> endHour // Morning case: Hours stay the same
            }

            val finalStartTime: Double = startHour + (startMinute / 60)// /60 for getting decimal minutes
            var finalEndTime: Double = endHour + (endMinute / 60)

            if (finalStartTime >= finalEndTime) finalEndTime += 24

            val finalTime = finalEndTime - finalStartTime
            hoursTitle.visibility = View.VISIBLE
            textView.text = finalTime.toString()
        }
    }

}