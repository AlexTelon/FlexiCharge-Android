package com.flexicharge.bolt

import com.flexicharge.bolt.api.flexicharge.ApiInterface
import com.flexicharge.bolt.api.flexicharge.Credentials
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import junit.framework.TestCase.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GetUserInfoTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiInterface
    private val email = "kofap47986@viicard.com"
    private val pass = "Test123!"
    private val credentials = Credentials(email, pass)
    private val token = getToekn()

    private fun getToekn() {
        runBlocking {
            val login = RetrofitInstance.flexiChargeApi.signIn(credentials)

            return@runBlocking login.body()?.accessToken
        }
    }

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
    fun testSuccessfulResponse() = runBlocking {
        // Arrange: Set up a response with valid data
        val authorizationHeader = "Bearer $token"
        val responseBody = """
        {
            "firstName": "John",
            "lastName": "Doe"
        }
        """.trimIndent()
        val mockSignInResponse = createMockResponse(200, responseBody)
        mockWebServer.enqueue(mockSignInResponse)

        // Act: Make an API request
        val response = apiService.getUserInfo(authorizationHeader)

        val userFullDetails = response.body()

        // Assert: Check that the response contains the expected data
        assertNotNull(userFullDetails)
        assertEquals("John", userFullDetails?.firstName)
        assertEquals("Doe", userFullDetails?.lastName)
    }

    @Test
    fun testMissingFieldsInResponse() = runBlocking {
        // Arrange: Set up a response with missing fields
        val authorizationHeader = "Bearer $token"
        val responseBody = """
        {
            "firstName": "John"
        }
        """.trimIndent()
        val mockSignInResponse = createMockResponse(200, responseBody)
        mockWebServer.enqueue(mockSignInResponse)

        // Act
        val response = apiService.getUserInfo(authorizationHeader)

        val userFullDetails = response.body()

        // Assert: Ensure that the missing fields are handled gracefully
        assertNotNull(userFullDetails)
        assertNull(userFullDetails?.lastName)
    }

    @Test
    fun testUnauthorizedToken() = runBlocking {
        // Arrange: Use an unauthorized token
        val unauthorizedToken = "invalid_token"

        val mockUnauthorizedResponse = MockResponse().setResponseCode(401)
        mockWebServer.enqueue(mockUnauthorizedResponse)

        // Act: Make an API request with the unauthorized token
        // Act: Make an API request
        val response = apiService.getUserInfo(unauthorizedToken)

        val userFullDetails = response.body()

        // Assert: Check that the response status code is 401 Unauthorized
        assertNull(userFullDetails)
    }

    @Test
    fun testServerErrorResponse() = runTest {
        val mockErrorResponse = MockResponse().setResponseCode(500)

        mockWebServer.enqueue(mockErrorResponse)

        val response = apiService.getUserInfo("Bearer $token")

        assertEquals(500, response.code())
    }
}
