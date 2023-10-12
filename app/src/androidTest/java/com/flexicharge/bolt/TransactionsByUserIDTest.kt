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
               "transactionID": 1,
                "connectorID": "55",
                "currentChargePercentage": 50,
                "kwhTransferred": 25.5,
                "pricePerKwh": 100,
                "price": 7728,
                "discount": "0",
                "startTimestamp": "123123123",
                "endTimestamp": "2223232323",
                },
                {
               "transactionID": 12,
                "connectorID": "42",
                "currentChargePercentage": 16,
                "kwhTransferred": 25.5,
                "pricePerKwh": 100,
                "price": 7728,
                "discount": "0",
                "startTimestamp": "123123123",
                "endTimestamp": "2223232323",
                }
            ]
        """.trimIndent()

        val mockResponse = createMockResponse(200, responseBody)
        mockWebServer.enqueue(mockResponse)

        // Act:
        val token = 222
        val response = apiService.transactionsByUserID("Bearer $token", userId)

        // Assert:
        assertNotNull(response)
        assertTrue(response.isSuccessful)

        val transactions = response.body()
        assertNotNull(transactions)
        assertEquals(2, transactions?.size)
        assertEquals(1, transactions?.get(0)?.transactionID)
        assertEquals(50, transactions?.get(0)?.currentChargePercentage)
    }

    @Test
    fun testTransactionsByUserIDEmptyResponse() = runBlocking {
        // Arrange:
        val userId = "123456"

        val responseBody = "[]"
        val mockResponse = createMockResponse(200, responseBody)
        mockWebServer.enqueue(mockResponse)

        // Act:
        val token = 222
        val response = apiService.transactionsByUserID("Bearer $token", userId)

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
        val token = 222
        val response = apiService.transactionsByUserID("Bearer $token", userId)

        // Assert:
        assertNotNull(response)
        assertEquals(404, response.code())
        assertNull(response.body()) // Since the user was not found, expect null for the response body
    }
}
