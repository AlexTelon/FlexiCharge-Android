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

class UpdateUserInfoTest {
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
    fun testUpdateUserInfoSuccess() = runBlocking {
        // Arrange: Create a valid authorization header and a UserFullDetails object
        val authorizationHeader = "Bearer $token"
        val userFullDetails = UserFullDetails("")

        // Mock a successful response with updated user information
        val responseBody = """
            {
                "firstName": "John",
                "lastName": "Doe"
            }
        """.trimIndent()

        val mockResponse = createMockResponse(200, responseBody)
        mockWebServer.enqueue(mockResponse)

        // Act: Make an API request to update user information
        val response = apiService.updateUserInfo(authorizationHeader, userFullDetails)

        // Assert: Check that the response indicates a successful update
        assertNotNull(response)
        assertTrue(response.isSuccessful)

        val updatedUserDetails = response.body()
        assertNotNull(updatedUserDetails)
        assertEquals("John", updatedUserDetails?.firstName)
        assertEquals("Doe", updatedUserDetails?.lastName)
    }

    @Test
    fun testUpdateUserInfoFailure() = runBlocking {
        // Arrange: Create a valid authorization header and a UserFullDetails object
        val authorizationHeader = "Bearer $token"
        val userFullDetails = UserFullDetails()

        // Mock a failure response with an error message
        val errorMessage = "Update failed due to invalid data"
        val mockResponse = createMockResponse(400, errorMessage)
        mockWebServer.enqueue(mockResponse)

        // Act: Make an API request to update user information
        val response = apiService.updateUserInfo(authorizationHeader, userFullDetails)

        // Assert: Check that the response indicates a failure and contains the error message
        assertNotNull(response)
        assertFalse(response.isSuccessful)
        assertEquals(400, response.code())

        val errorResponseBody = response.errorBody()?.string()
        assertNotNull(errorResponseBody)
        assertTrue(errorResponseBody?.contains(errorMessage) == true)
    }

    @Test
    fun testUnauthorizedUpdate() = runBlocking {
        // Arrange:
        val unauthorizedToken = "invalid_token"
        val authorizationHeader = "Bearer $unauthorizedToken"
        val userFullDetails = UserFullDetails()

        val mockUnauthorizedResponse = MockResponse().setResponseCode(401)
        mockWebServer.enqueue(mockUnauthorizedResponse)

        // Act:
        val response = apiService.updateUserInfo(authorizationHeader, userFullDetails)

        // Assert:
        assertNull(response.body())
        assertEquals(401, response.code())
    }

    @Test
    fun testMissingFieldsInUpdateResponse() = runBlocking {
        // Arrange:
        val authorizationHeader = "Bearer $token"
        val userFullDetails = UserFullDetails()

        // Mock a response with missing fields
        val responseBody = """
        {
            "firstName": "UpdatedFirstName"
        }
        """.trimIndent()

        val mockResponse = createMockResponse(200, responseBody)
        mockWebServer.enqueue(mockResponse)

        // Act:
        val response = apiService.updateUserInfo(authorizationHeader, userFullDetails)

        // Assert:
        assertNotNull(response)
        assertTrue(response.isSuccessful)

        val updatedUserDetails = response.body()
        assertNotNull(updatedUserDetails)
        assertEquals("UpdatedFirstName", updatedUserDetails?.firstName)
        assertNull(updatedUserDetails?.city)
    }
}
