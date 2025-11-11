package edu.temple.myapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {

    lateinit var timerBinder : TimerService.TimerBinder
    var isConnected = false

    val serviceConnection = object : ServiceConnection{
        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
            timerBinder = service as TimerService.TimerBinder
            isConnected = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isConnected = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var paused = false
        var running = false

        val intent = Intent(this, TimerService::class.java)
        startService(intent)

        bindService(intent, serviceConnection, BIND_AUTO_CREATE)

        val count = findViewById<>(R.id.textView)

        findViewById<Button>(R.id.startButton).setOnClickListener {
            paused = timerBinder.paused
            running = timerBinder.isRunning
            if(!running){
                timerBinder.start(20)

            }
            else if(!paused and running){
                timerBinder.pause()
            }
            else if(running and paused){
                timerBinder.pause()
            }
        }
        
        findViewById<Button>(R.id.stopButton).setOnClickListener {
            if(running){
                timerBinder.stop()
                running = false
            }
        }
    }

    override fun onDestroy() {
        unbindService(serviceConnection)
        super.onDestroy()
    }
}