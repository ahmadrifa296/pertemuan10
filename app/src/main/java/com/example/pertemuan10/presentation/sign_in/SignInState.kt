package com.example.pertemuan10.presentation.sign_in // Perbaikan package sesuai proyek Anda

/**
 * Data class untuk menampung status (state) dari proses login.
 */
data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)