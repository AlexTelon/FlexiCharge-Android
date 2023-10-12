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

class InitTransactionTest {

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
    fun testInitTransactionSuccess() = runBlocking {
        // Arrange:
        val transactionSession = TransactionSession(100000, "klarna")

        val responseBody = """
            {
                "klarnaClientToken": "123456",
                "klarnaSessionID": "4840232",
                "transactionID" : "9999"
            }
        """.trimIndent()

        val mockResponse = createMockResponse(200, responseBody)
        mockWebServer.enqueue(mockResponse)

        val token = 23123123
        // Act:
        val response = apiService.initTransaction("Bearer $token ", transactionSession)

        // Assert:
        assertNotNull(response)
        assertTrue(response.isSuccessful)

        val transactionDetails = response.body()
        assertNotNull(transactionDetails)
        assertEquals("123456", transactionDetails?.klarnaClientToken)
        assertEquals("9999", transactionDetails?.transactionID)
    }

    @Test
    fun testInitTransactionFailure() = runBlocking {
        // Arrange:
        val transactionSession = TransactionSession(100000, "klarna")

        val errorMessage = "Transaction initialization failed due to invalid data"
        val mockResponse = createMockResponse(400, errorMessage)
        mockWebServer.enqueue(mockResponse)

        val token = 23123123
        // Act:
        val response = apiService.initTransaction("Bearer $token ", transactionSession)

        // Assert:
        assertNotNull(response)
        assertFalse(response.isSuccessful)
        assertEquals(400, response.code())

        val errorResponseBody = response.errorBody()?.string()
        assertNotNull(errorResponseBody)
        assertTrue(errorResponseBody?.contains(errorMessage) == true)
    }

    @Test
    fun testMissingFieldsInInitTransactionResponse() = runBlocking {
        // Arrange:
        val transactionSession = TransactionSession(100000, "klarna")

        val responseBody = """
            {
                "klarnaClientToken": "123456"
            }
        """.trimIndent()

        val mockResponse = createMockResponse(200, responseBody)
        mockWebServer.enqueue(mockResponse)

        val token = 23123123
        // Act:
        val response = apiService.initTransaction("Bearer $token ", transactionSession)

        // Assert:
        assertNotNull(response)
        assertTrue(response.isSuccessful)

        val transactionDetails = response.body()
        assertNotNull(transactionDetails)
        assertEquals("123456", transactionDetails?.klarnaClientToken)
    }

    @Test
    fun testUnauthorizedInitTransaction() = runBlocking {
        // Arrange:
        val transactionSession = TransactionSession(100000, "klarna")

        val mockResponse = createMockResponse(401, "")
        mockWebServer.enqueue(mockResponse)

        val token = 23123123
        // Act:
        val response = apiService.initTransaction("Bearer $token ", transactionSession)

        // Assert:
        assertNotNull(response)
        assertEquals(401, response.code())
    }

    @Test
    fun testEmptyInitTransactionResponse() = runBlocking {
        // Arrange: Create a valid TransactionSession object
        val transactionSession = TransactionSession(100000, "klarna")

        // Mock an empty JSON object response body
        val responseBody = "{}"
        val mockResponse = createMockResponse(200, responseBody)
        mockWebServer.enqueue(mockResponse)

        val token = 23123123
        // Act:
        val response = apiService.initTransaction("Bearer $token ", transactionSession)

        val transaction = response.body()

        // Assert: Ensure that the response is handled gracefully (e.g., return null or handle as needed)
        assertNotNull(response)
        assertTrue(response.isSuccessful)
    }
}
