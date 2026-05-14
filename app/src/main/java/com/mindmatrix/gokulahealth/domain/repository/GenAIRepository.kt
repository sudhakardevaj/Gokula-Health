package com.mindmatrix.gokulahealth.domain.repository

import javax.inject.Inject
import javax.inject.Singleton

interface GenAIRepository {
    suspend fun getHealthSuggestions(cattleId: Int, trend: List<Float>): String
}

@Singleton
class GenAIRepositoryImpl @Inject constructor() : GenAIRepository {
    override suspend fun getHealthSuggestions(cattleId: Int, trend: List<Float>): String {
        // TODO: Mark with TODO comment showing where real Anthropic/OpenAI API call goes
        // This is a mock implementation
        return "Possible causes: Mastitis (check for swelling/pain in udder),\n" +
                "Feed Quality Change (review recent feed batch),\n" +
                "Heat Stress (ensure shade and water availability).\n" +
                "Recommended Action: Consult your veterinarian within 24 hours."
    }
}
