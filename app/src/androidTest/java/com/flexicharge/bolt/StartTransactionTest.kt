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
                "transactionID": 1,
                "connectorID": "55",
                "currentChargePercentage": 50,
                "kwhTransferred": 25.5,
                "pricePerKwh": 100,
                "price": 7728,
                "discount": "0",
                "startTimestamp": "123123123",
                "endTimestamp": "2223232323",

            }
        """.trimIndent()

        val mockResponse = createMockResponse(200, responseBody)
        mockWebServer.enqueue(mockResponse)

        // Act:
        val token = 2222
        val response = apiService.transactionStart("Bearer $token", transactionId)

        // Assert:
        assertNotNull(response)
        assertTrue(response.isSuccessful)

        val startedTransaction = response.body()
        assertNotNull(startedTransaction)
        assertEquals(7728, startedTransaction?.price)
        assertEquals(1, startedTransaction?.transactionID)
        assertEquals("55", startedTransaction?.connectorID)
        assertEquals(50, startedTransaction?.currentChargePercentage)
    }

    @Test
    fun testStartTransactionNotFound() = runBlocking {
        // Arrange:
        val transactionId = 999

        val mockResponse = createMockResponse(404, "Transaction not found")
        mockWebServer.enqueue(mockResponse)

        val token = 2222
        val response = apiService.transactionStart("Bearer $token", transactionId)

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

        val token = 2222
        val response = apiService.transactionStart("Bearer $token", transactionId)

        // Assert:
        assertNotNull(response)
        assertEquals(500, response.code())
        assertNull(response.body())
    }
}