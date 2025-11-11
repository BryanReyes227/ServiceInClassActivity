package edu.temple.myapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    lateinit var timerBinder : TimerService.TimerBinder
    var isConnected = false

    private val handler = android.os.Handler(Looper.getMainLooper()) { msg ->
        countView.text = msg.what.toString()
        true
    }
    val serviceConnection = object : ServiceConnection{
        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
            timerBinder = service as TimerService.TimerBinder
            isConnected = true
            timerBinder.setHandler(handler)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isConnected = false
        }
    }

    private lateinit var countView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        val intent = Intent(this, TimerService::class.java)
        startService(intent)

        bindService(intent, serviceConnection, BIND_AUTO_CREATE)

        countView = findViewById(R.id.textView)

        findViewById<Button>(R.id.startButton).setOnClickListener {
            if (!isConnected) return@setOnClickListener

            when {
                !timerBinder.isRunning -> {
                    timerBinder.start(20)
                }
                !timerBinder.paused -> {
                    timerBinder.pause()
                }
                else -> {
                    timerBinder.pause()
                }
            }
        }
        
        findViewById<Button>(R.id.stopButton).setOnClickListener {
            timerBinder.stop()
            countView.text = "Stopped"
        }
    }

    override fun onDestroy() {
        unbindService(serviceConnection)
        super.onDestroy()
    }
}