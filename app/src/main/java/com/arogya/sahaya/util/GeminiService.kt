package com.arogya.sahaya.util

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content

class GeminiService {

    private val textModel = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = Constants.GEMINI_API_KEY
    )

    private val visionModel = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = Constants.GEMINI_API_KEY
    )

    suspend fun getHealthTips(vitalsContext: String): String {
        val prompt = """
            You are a friendly health assistant for an elderly rural Indian patient.
            Based on these recent vitals: $vitalsContext
            Give 3 simple, actionable health tips in easy-to-understand language.
            Keep each tip to 1–2 sentences. Use numbered list format.
            Focus on diet, exercise, and medication adherence relevant to their readings.
        """.trimIndent()
        return try {
            val text = textModel.generateContent(prompt).text?.trim().orEmpty()
            if (text.isNotBlank()) text else "Unable to generate tips right now."
        } catch (e: Exception) {
            "Unable to generate tips: ${e.message}"
        }
    }

    suspend fun parsePrescription(image: Bitmap): String {
        val prompt = """
            This is a medical prescription image. Extract ALL medicines listed.
            For each medicine, return ONLY this format (one per line):
            MEDICINE: <name> | DOSAGE: <dosage> | SLOTS: <Morning/Afternoon/Night>
            If slots are not clear, guess based on typical usage.
            Do not add any explanation — only the formatted lines.
        """.trimIndent()
        return try {
            val inputContent = content {
                image(image)
                text(prompt)
            }
            val text = visionModel.generateContent(inputContent).text?.trim().orEmpty()
            if (text.isNotBlank()) text else "Could not read prescription."
        } catch (e: Exception) {
            "Error reading prescription: ${e.message}"
        }
    }
}