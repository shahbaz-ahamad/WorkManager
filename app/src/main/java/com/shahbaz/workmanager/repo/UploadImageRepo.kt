package com.shahbaz.workmanager.repo

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class UploadImageRepo(
    private val firebaseStorage: FirebaseStorage,
) {

    suspend fun uploadFile(
        fileUri: String,
        onProgress: suspend (progress: Int) -> Unit,
    ) {
        val storageRef: StorageReference =
            firebaseStorage.reference.child("Upload/${UUID.randomUUID()}")

        val uploadTask = storageRef.putFile(Uri.parse(fileUri))

        uploadTask.addOnProgressListener { taskSnapshot ->
            val progress =
                (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
            CoroutineScope(Dispatchers.IO).launch {
                onProgress(progress)
            }
        }.await() // Wait for the task to complete

        uploadTask.addOnFailureListener {
            throw it
        }.await() // Wait for failure or success

        uploadTask.addOnSuccessListener {
            // Handle success
        }.await()
    }

}