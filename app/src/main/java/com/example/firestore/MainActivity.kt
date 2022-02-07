package com.example.firestore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firestore.databinding.ActivityMainBinding
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firestoreをインスタンス化
        val db = Firebase.firestore

        // ボタンを押したときの処理
        binding.addButton.setOnClickListener {

            // Taskをインスタンス化
            val task = Task(
                title = binding.titleEditText.text.toString(),
                name = binding.nameEditText.text.toString(),
            )

            db.collection("tasks")
                .add(task)
                .addOnSuccessListener { documentReference ->
                    Log.d("ADD_TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("ADD_TAG", "Error adding document", e)
                }
        }

        // RecyclerViewの設定
        val taskAdapter = TaskAdapter()
        binding.recyclerView.adapter = taskAdapter
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // アプリ起動時に、保存されているデータを取得する
        db.collection("tasks")
            .orderBy("createdAt")
            .get()
            .addOnSuccessListener { tasks ->
                val taskList = ArrayList<Task>()
                tasks.forEach { taskList.add(it.toObject(Task::class.java)) }
                taskAdapter.submitList(taskList)
            }
            .addOnFailureListener { exception ->
                Log.d("READ_TAG", "Error getting documents: ", exception)
            }
        // データの変更をリアルタイムでアプリに反映する
        db.collection("tasks")
            .orderBy("createdAt")
            .addSnapshotListener { tasks, e ->
            if (e != null) {
                Log.w("READ_TAG", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (tasks != null) {
                val taskList = ArrayList<Task>()
                tasks.forEach { taskList.add(it.toObject(Task::class.java)) }
                taskAdapter.submitList(taskList)
            } else {
                Log.d("READ_TAG", "Current data: null")
            }
        }
    }
}