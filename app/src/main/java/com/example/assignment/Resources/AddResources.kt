package com.example.assignment.Resources

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.assignment.R
import com.example.assignment.databinding.ActivityAddResourcesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.Random

class AddResources : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etDesc: EditText
    private lateinit var btnUpload: Button
    private lateinit var btnSaveData: Button
    private lateinit var fileUpload: TextView //textview to display the file path that i selected

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private lateinit var storageRef: StorageReference

    private val PICK_FILE_REQUEST_CODE = 123
    private var selectedFileUri: Uri? = null //store the selected file URI

    private lateinit var addResourceBinding: ActivityAddResourcesBinding
    private lateinit var SqlHelper: SQLiteHelper



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addResourceBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_resources)
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val name = sharedPreferences.getString("lastName", "")
        val email = sharedPreferences.getString("email", "")

        etTitle = addResourceBinding.titleInput
        etDesc = addResourceBinding.inputDescription
        btnUpload = addResourceBinding.uploadBtn
        btnSaveData = addResourceBinding.addBtn
        fileUpload = addResourceBinding.inputFilePath

        firebaseAuth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance().reference
        dbRef = FirebaseDatabase.getInstance().getReference("Resources")

        btnSaveData.setOnClickListener {
            if(isNetworkAvailable()){
                val key = storeInFirebase(name!!,email!!)
                Log.i("CheckKeyValue", key.toString())
                storeInDatabase(name, email, key)
            }else{
                Toast.makeText(
                    this,
                    "Please ensure that you have internet connection.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        btnUpload.setOnClickListener {
            openFilePicker()
        }
        SqlHelper = SQLiteHelper(this)

    }

    private fun storeInDatabase(name: String, email: String, key: String?) {
        val dbHelper = SQLiteHelper(this)
        // add shared preferences name
        val resTitle = etTitle.text.toString()
        val resDesc = etDesc.text.toString()
        val resource =
            ResourceModel(key.toString(), name, email, resTitle, selectedFileUri.toString(), resDesc, email)
        Log.i("ResourceModelSaveChecked", resource.toString())
        val status = dbHelper.insertResources(resource)
        //check insert success or not success
        if(status){
            Toast.makeText(this, "Resource Added...", Toast.LENGTH_SHORT).show()
            finish()
        }else{
            Toast.makeText(this, "Record not saved",Toast.LENGTH_SHORT).show()
        }

    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf" //specific the type for PDF files
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            selectedFileUri = data.data

            //display the selected file URI in the Textview
            fileUpload.text = selectedFileUri.toString()
        }
    }

    private fun storeInFirebase(name:String, email:String): String? {
        //getting values
        val resTitle = etTitle.text.toString()
        val resDesc = etDesc.text.toString()

        val resourceKey = dbRef.push().key
        if (selectedFileUri != null) {
//            //Upload the selected file to Firebase Storage
//            val fileRef = storageRef.child("uploads/${selectedFileUri!!.lastPathSegment}")
//            val uploadTask: UploadTask = fileRef.putFile(selectedFileUri!!)
//
//            uploadTask.addOnSuccessListener { taskSnapshot ->
//                // File uploaded successfully, get the download URL
//                fileRef.downloadUrl.addOnSuccessListener { downloadUri ->
//                    //create a hashmap to store resource data

            //push the data to the realtime database
            val resource = ResourceModel(resourceKey!!, name, email, resTitle, selectedFileUri.toString(), resDesc, email)

            if (resourceKey != null) {
                dbRef.child(resourceKey).setValue(resource)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this,
                                "Resource saved successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()

                            etTitle.text.clear()
                            etDesc.text.clear()
                            selectedFileUri = null // Reset the selected file URI

                        } else {
                            Toast.makeText(this, "Resource save failed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }
        }
        return resourceKey
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
        }




}