package com.example.pertemuan10.presentation.sign_in // Perbaikan package proyek Anda

import androidx.lifecycle.ViewModel
import com.example.pertemuan10.data.SignInResult // Import dari package data proyek Anda
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignInViewModel : ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    // Fungsi untuk memproses hasil sign-in dari Google
    fun onSignInResult(result: SignInResult) {
        _state.update { it.copy(
            isSignInSuccessful = result.data != null,
            signInError = result.errorMessage
        ) }
    }

    // Fungsi untuk mereset status login (misal saat logout atau kembali ke login screen)
    fun resetState() {
        _state.update { SignInState() }
    }
}