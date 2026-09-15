package com.pandora.app.core.ai

import com.pandora.app.core.security.KeystoreSecretManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

data class AiOrganizationSuggestion(
    val suggestedFolders: List<String>,
    val suggestedTags: List<String>,
    val executiveSummary: String,
    val reasoning: String
)

@Singleton
class GeminiClient @Inject constructor(
    private val keystoreSecretManager: KeystoreSecretManager
) {
    fun hasApiKey(): Boolean {
        return !keystoreSecretManager.getGeminiApiKey().isNullOrBlank()
    }

    suspend fun generateOrganizationProposal(
        title: String,
        content: String,
        availableFolders: List<String>,
        availableTags: List<String>
    ): Result<AiOrganizationSuggestion> = withContext(Dispatchers.IO) {
        val apiKey = keystoreSecretManager.getGeminiApiKey()
        if (apiKey.isNullOrBlank()) {
            return@withContext Result.failure(IllegalStateException("No Gemini API key configured. Please set your BYOK key in Settings."))
        }

        try {
            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"
            val prompt = """
                You are Pandora AI Librarian, a thoughtful personal knowledge assistant.
                Analyze the following saved content and suggest relevant folders, tags, and a concise 1-2 sentence executive summary.
                
                Content Title: $title
                Content Text: $content
                
                Available Folders in User's Vault: ${availableFolders.joinToString(", ")}
                Available Tags: ${availableTags.joinToString(", ")}
                
                Respond ONLY with a valid JSON object matching this schema:
                {
                   "suggestedFolders": ["folder1", "folder2"],
                   "suggestedTags": ["tag1", "tag2"],
                   "executiveSummary": "Concise summary of the knowledge item...",
                   "reasoning": "Why these folders and tags fit best..."
                }
            """.trimIndent()

            val requestBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                }
                put("contents", contents)
                put("generationConfig", JSONObject().apply {
                    put("response_mime_type", "application/json")
                })
            }

            val responseText = executePostRequest(endpoint, requestBody.toString())
            val responseJson = JSONObject(responseText)
            val candidates = responseJson.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val contentObj = candidates.getJSONObject(0).getJSONObject("content")
                val parts = contentObj.getJSONArray("parts")
                val rawJsonText = parts.getJSONObject(0).getString("text")

                val parsed = JSONObject(rawJsonText)
                val foldersArray = parsed.optJSONArray("suggestedFolders") ?: JSONArray()
                val tagsArray = parsed.optJSONArray("suggestedTags") ?: JSONArray()

                val folders = mutableListOf<String>()
                for (i in 0 until foldersArray.length()) folders.add(foldersArray.getString(i))

                val tags = mutableListOf<String>()
                for (i in 0 until tagsArray.length()) tags.add(tagsArray.getString(i))

                Result.success(
                    AiOrganizationSuggestion(
                        suggestedFolders = folders,
                        suggestedTags = tags,
                        executiveSummary = parsed.optString("executiveSummary", ""),
                        reasoning = parsed.optString("reasoning", "")
                    )
                )
            } else {
                Result.failure(Exception("Empty response from Gemini API"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateItemSummary(
        itemTitle: String,
        itemContent: String,
        itemType: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = keystoreSecretManager.getGeminiApiKey()
        if (apiKey.isNullOrBlank()) {
            val preview = itemContent.ifBlank { itemTitle }
            val fallbackSummary = "📌 **Artifact Overview ($itemType)**\n\n${preview.take(300)}${if (preview.length > 300) "..." else ""}\n\n💡 *Tip: Add your Gemini API key in Settings to unlock deep neural summaries, key takeaways, and interactive AI Q&A.*"
            return@withContext Result.success(fallbackSummary)
        }

        try {
            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"
            val prompt = """
                You are Pandora AI Copilot. Summarize the following $itemType from the user's personal vault.
                Title: $itemTitle
                Content: $itemContent
                
                Provide:
                1. Executive Summary (2-3 sentences)
                2. Key Takeaways (bullet points)
                3. Actionable Insights or Connections
                
                Keep the tone elegant, concise, and structured.
            """.trimIndent()

            val requestBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        })
                    })
                }
                put("contents", contents)
            }

            val responseText = executePostRequest(endpoint, requestBody.toString())
            val responseJson = JSONObject(responseText)
            val candidates = responseJson.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val contentObj = candidates.getJSONObject(0).getJSONObject("content")
                val parts = contentObj.getJSONArray("parts")
                val text = parts.getJSONObject(0).getString("text")
                Result.success(text)
            } else {
                Result.failure(Exception("Empty response from Gemini"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendScopedChatMessage(
        itemTitle: String,
        itemContent: String,
        conversationHistory: List<Pair<String, String>>, // (user/model, text)
        userMessage: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = keystoreSecretManager.getGeminiApiKey()
        if (apiKey.isNullOrBlank()) {
            val contentLower = itemContent.lowercase()
            val queryWords = userMessage.lowercase().split(" ").filter { it.length > 3 }
            val matchFound = queryWords.any { contentLower.contains(it) }

            val response = if (matchFound) {
                "Based on this artifact, here is what was found regarding your question:\n\n\"${itemContent.take(200)}...\"\n\n💡 *Note: You are using local extraction. Configure your Gemini API key in Settings for full natural language conversation.*"
            } else {
                "I am analyzing \"$itemTitle\".\n\nContent excerpt:\n${itemContent.take(150)}...\n\n💡 *Tip: Add your Gemini API key in Settings to chat freely and extract deep insights.*"
            }
            return@withContext Result.success(response)
        }

        try {
            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"

            val contentsArray = JSONArray()
            
            // System instructions context
            val systemContext = "You are Pandora AI Copilot. You are discussing a specific item in the user's personal vault titled '$itemTitle'. Answer the user's questions thoughtfully, accurately, and concisely based on this content.\nItem Content:\n$itemContent"

            contentsArray.put(JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().apply {
                    put(JSONObject().apply { put("text", systemContext) })
                })
            })
            contentsArray.put(JSONObject().apply {
                put("role", "model")
                put("parts", JSONArray().apply {
                    put(JSONObject().apply { put("text", "Understood. I will answer all questions accurately and concisely based on '$itemTitle'.") })
                })
            })

            for ((role, text) in conversationHistory) {
                contentsArray.put(JSONObject().apply {
                    put("role", if (role.lowercase() == "user") "user" else "model")
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", text) })
                    })
                })
            }

            contentsArray.put(JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().apply {
                    put(JSONObject().apply { put("text", userMessage) })
                })
            })

            val requestBody = JSONObject().apply {
                put("contents", contentsArray)
            }

            val responseText = executePostRequest(endpoint, requestBody.toString())
            val responseJson = JSONObject(responseText)
            val candidates = responseJson.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val contentObj = candidates.getJSONObject(0).getJSONObject("content")
                val parts = contentObj.getJSONArray("parts")
                val text = parts.getJSONObject(0).getString("text")
                Result.success(text)
            } else {
                Result.failure(Exception("No response text from Gemini"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun executePostRequest(urlStr: String, jsonBody: String): String {
        val url = URL(urlStr)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        conn.doOutput = true
        conn.doInput = true
        conn.connectTimeout = 15000
        conn.readTimeout = 20000

        OutputStreamWriter(conn.outputStream, Charsets.UTF_8).use { writer ->
            writer.write(jsonBody)
            writer.flush()
        }

        val responseCode = conn.responseCode
        val stream = if (responseCode in 200..299) conn.inputStream else conn.errorStream
        val response = BufferedReader(InputStreamReader(stream, Charsets.UTF_8)).use { reader ->
            reader.readText()
        }

        if (responseCode !in 200..299) {
            throw Exception("Gemini API Error ($responseCode): $response")
        }
        return response
    }
}
