package com.example.cinechips.repository

import com.example.cinechips.model.User
import com.example.cinechips.model.Movie
import com.example.cinechips.model.Booking
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FirebaseRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: DatabaseReference = FirebaseDatabase.getInstance().reference

    // Login user
    fun loginUser(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    // Register user
    fun registerUser(user: User, password: String, callback: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(user.email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    db.child("users").child(auth.currentUser!!.uid).setValue(user)
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    // Add movie (admin)
    fun addMovie(movie: Movie, callback: (Boolean, String?) -> Unit) {
        val key = db.child("movies").push().key
        if (key == null) {
            callback(false, "Could not generate key")
            return
        }
        movie.id = key
        db.child("movies").child(key).setValue(movie)
            .addOnCompleteListener { task ->
                callback(task.isSuccessful, task.exception?.message)
            }
    }

    // Get all movies
    fun getMovies(callback: (List<Movie>) -> Unit) {
        db.child("movies").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val movies = snapshot.children.mapNotNull { it.getValue(Movie::class.java) }
                callback(movies)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    // Book movie
    fun bookMovie(booking: Booking, callback: (Boolean, String?) -> Unit) {
        val key = db.child("bookings").push().key
        if (key == null) {
            callback(false, "Could not generate key")
            return
        }
        booking.id = key
        db.child("bookings").child(key).setValue(booking)
            .addOnCompleteListener { task ->
                callback(task.isSuccessful, task.exception?.message)
            }
    }

    // Check if user is admin
    fun isAdmin(uid: String, callback: (Boolean) -> Unit) {
        db.child("users").child(uid).child("isAdmin")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val admin = snapshot.getValue(Boolean::class.java) ?: false
                    callback(admin)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false)
                }
            })
    }
}