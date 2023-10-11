package com.flexicharge.bolt

import com.flexicharge.bolt.api.flexicharge.ApiInterface
import junit.framework.TestCase.*
import kotlinx.coroutines.runBlocking
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TransactionsByUserIDTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiInterface

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiInterface::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    private fun createMockResponse(responseCode: Int, responseBody: String): MockResponse {
        return MockResponse()
            .setResponseCode(responseCode)
            .setBody(responseBody)
    }

    @Test
    fun testTransactionsByUserIDSuccess() = runBlocking {
        // Arrange:
        val userId = "123456"

        val responseBody = """
            [
                {
                    "chargerID": 1,
                    "klarna_consumer_token": "token1",
                    "currentChargePercentage": 50,
                    "isKlarnaPayment": true,
                    "kwhTransfered": 25.5,
                    "meterStart": 100,
                    "paymentConfirmed": true,
                    "paymentID": "payment1",
                    "pricePerKwh": "0.12",
                    "session_id": "session1",
                    "timestamp": 1633524900,
                    "transactionID": 1,
                    "userID": "123456"
                },
                {
                    "chargerID": 2,
                    "klarna_consumer_token": "token2",
                    "currentChargePercentage": 75,
                    "isKlarnaPayment": false,
                    "kwhTransfered": 30.0,
                    "meterStart": 200,
                    "paymentConfirmed": false,
                    "paymentID": "payment2",
                    "pricePerKwh": "0.15",
                    "session_id": "session2",
                    "timestamp": 1633525900,
                    "transactionID": 2,
                    "userID": "123456"
                }
            ]
        """.trimIndent()

        val mockResponse = createMockResponse(200, responseBody)
        mockWebServer.enqueue(mockResponse)

        // Act:
        val response = apiService.transactionsByUserID(userId)

        // Assert:
        assertNotNull(response)
        assertTrue(response.isSuccessful)

        val transactions = response.body()
        assertNotNull(transactions)
        assertEquals(2, transactions?.size)
        assertEquals(1, transactions?.get(0)?.transactionID)
        assertEquals(true, transactions?.get(0)?.isKlarnaPayment)
    }

    @Test
    fun testTransactionsByUserIDEmptyResponse() = runBlocking {
        // Arrange:
        val userId = "123456"

        val responseBody = "[]"
        val mockResponse = createMockResponse(200, responseBody)
        mockWebServer.enqueue(mockResponse)

        // Act:
        val response = apiService.transactionsByUserID(userId)

        // Assert:
        assertNotNull(response)
        assertTrue(response.isSuccessful)

        val transactions = response.body()
        assertNotNull(transactions)
        assertTrue(transactions?.isEmpty() == true)
    }

    @Test
    fun testTransactionsByUserIDNotFound() = runBlocking {
        // Arrange:
        val userId = "nonexistent123"

        val mockResponse = createMockResponse(404, "User not found")
        mockWebServer.enqueue(mockResponse)

        // Act: Make an API request to retrieve transactions by user ID
        val response = apiService.transactionsByUserID(userId)

        // Assert:
        assertNotNull(response)
        assertEquals(404, response.code())
        assertNull(response.body()) // Since the user was not found, expect null for the response body
    }
}
