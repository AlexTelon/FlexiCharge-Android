package com.flexicharge.bolt.api.flexicharge

import junit.framework.TestCase.*
import kotlinx.coroutines.runBlocking
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StartTransactionTest {

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
    fun testStartTransactionSuccess() = runBlocking {
        // Arrange:
        val transactionId = 123

        val responseBody = """
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
                "transactionID": 123,
                "userID": "123456"
            }
        """.trimIndent()

        val mockResponse = createMockResponse(200, responseBody)
        mockWebServer.enqueue(mockResponse)

        // Act:
        val response = apiService.startTransaction(transactionId)

        // Assert:
        assertNotNull(response)
        assertTrue(response.isSuccessful)

        val startedTransaction = response.body()
        assertNotNull(startedTransaction)
        assertEquals(123, startedTransaction?.transactionID)
        assertEquals(1, startedTransaction?.chargerID)
        assertEquals("token1", startedTransaction?.klarna_consumer_token)
        assertEquals(50, startedTransaction?.currentChargePercentage)
        assertTrue(startedTransaction?.isKlarnaPayment ?: false)
    }

    @Test
    fun testStartTransactionNotFound() = runBlocking {
        // Arrange:
        val transactionId = 999

        val mockResponse = createMockResponse(404, "Transaction not found")
        mockWebServer.enqueue(mockResponse)

        // Act:
        val response = apiService.startTransaction(transactionId)

        // Assert:
        assertNotNull(response)
        assertEquals(404, response.code())
        assertNull(response.body())
        assertFalse(response.isSuccessful)
        assertEquals("Client Error", response.message())
    }

    @Test
    fun testStartTransactionServerError() = runBlocking {
        // Arrange:
        val transactionId = 456

        val mockResponse = createMockResponse(500, "Internal Server Error")
        mockWebServer.enqueue(mockResponse)

        // Act:
        val response = apiService.startTransaction(transactionId)

        // Assert:
        assertNotNull(response)
        assertEquals(500, response.code())
        assertNull(response.body())
    }
}