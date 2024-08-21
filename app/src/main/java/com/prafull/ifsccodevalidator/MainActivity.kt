package com.prafull.ifsccodevalidator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prafull.ifsccodevalidator.ui.theme.IFSCCodeValidatorTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.IOException

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IFSCCodeValidatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                    TopAppBar(title = {
                        Text(text = "IFSC Code Validator")
                    })
                }) { innerPadding ->
                    MainScreen(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val value = rememberSaveable {
        mutableStateOf("")
    }
    val scope = rememberCoroutineScope()
    var bankDetails by rememberSaveable {
        mutableStateOf<BankDetails?>(null)
    }
    var loading by rememberSaveable {
        mutableStateOf(false)
    }
    var networkIssue by rememberSaveable {
        mutableStateOf(false)
    }
    var errorText by rememberSaveable {
        mutableStateOf("")
    }
    LazyColumn(
        modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            OutlinedTextField(
                value = value.value, onValueChange = {
                    value.value = it
                }, colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.surfaceDim,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceDim,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceDim,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceDim
                ), trailingIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                loading = true
                                val response = IFSCCodeValidator.getBankDetails(value.value)
                                loading = false
                                when (response) {
                                    is Response.Success -> {
                                        bankDetails = response.data
                                        networkIssue = false
                                    }

                                    is Response.Error -> {
                                        bankDetails = null
                                        if (response.exceptions is Exceptions.NetworkIssue) {
                                            networkIssue = true
                                            errorText = "Network Issue"
                                        } else {
                                            networkIssue = false
                                            if (response.message != null) {
                                                errorText = response.message
                                                return@withContext
                                            }
                                            errorText = "Unknown Issue"
                                        }
                                    }
                                }
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null
                        )
                    }
                }, shape = RoundedCornerShape(25.dp)
            )
        }
        item {
            if (loading) {
                CircularProgressIndicator()
            } else {
                if (bankDetails != null) {
                    Column(Modifier.fillMaxSize()) {
                        TextItem(field = "Address", value = bankDetails?.address ?: "Not Found")
                        TextItem(field = "Bank", value = bankDetails?.bank ?: "Not Found")
                        TextItem(field = "Bank Code", value = bankDetails?.bankCode ?: "Not Found")
                        TextItem(field = "Branch", value = bankDetails?.branch ?: "Not Found")
                        TextItem(field = "Centre", value = bankDetails?.centre ?: "Not Found")
                        TextItem(field = "Contact", value = bankDetails?.contact ?: "Not Found")
                        TextItem(field = "District", value = bankDetails?.district ?: "Not Found")
                        TextItem(field = "IFSC", value = bankDetails?.ifsc ?: "Not Found")
                        TextItem(
                            field = "IMPS",
                            value = bankDetails?.imps?.toString() ?: "Not Found"
                        )
                        TextItem(field = "ISO3166", value = bankDetails?.iso3166 ?: "Not Found")
                        TextItem(field = "MICR", value = bankDetails?.micr ?: "Not Found")
                        TextItem(
                            field = "NEFT",
                            value = bankDetails?.neft?.toString() ?: "Not Found"
                        )
                        TextItem(
                            field = "RTGS",
                            value = bankDetails?.rtgs?.toString() ?: "Not Found"
                        )
                        TextItem(field = "State", value = bankDetails?.state ?: "Not Found")
                        TextItem(
                            field = "SWIFT",
                            value = bankDetails?.swift?.toString() ?: "Not Found"
                        )
                        TextItem(field = "UPI", value = bankDetails?.upi?.toString() ?: "Not Found")
                    }
                } else {
                    if (networkIssue) {
                        Text(
                            text = "No internet",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 22.sp
                        )
                    } else {
                        Text(text = errorText)
                    }
                }
            }
        }
    }
}

@Composable
fun TextItem(field: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = field, modifier = Modifier.weight(.45f))
        Text(text = ":", modifier = Modifier.weight(.1f))
        Text(text = value, modifier = Modifier.weight(.45f))
    }
}

object IFSCCodeValidator {
    private val api = Retrofit.Builder().baseUrl("https://ifsc.razorpay.com/")
        .addConverterFactory(GsonConverterFactory.create()).build().create<ApiService>()

    suspend fun getBankDetails(ifsc: String): Response<BankDetails> {
        return try {
            val response = api.getBankDetails(ifsc.uppercase())
            if (response.isSuccessful) {
                if (response.body() == null) {
                    Response.Error(exceptions = Exceptions.UnknownIssue)
                }
                Response.Success(data = response.body()!!)
            } else {
                Response.Error(exceptions = Exceptions.UnknownIssue, message = response.message())
            }
        } catch (e: IOException) {
            Response.Error(exceptions = Exceptions.NetworkIssue)
        } catch (e: HttpException) {
            Response.Error(exceptions = Exceptions.ServerIssue)
        } catch (e: Exception) {
            Response.Error(exceptions = Exceptions.UnknownIssue)
        }
    }
}

sealed interface Exceptions {
    data object NetworkIssue : Exceptions
    data object ServerIssue : Exceptions
    data object UnknownIssue : Exceptions
}

interface ApiService {

    @GET("/{ifsc}")
    suspend fun getBankDetails(@Path("ifsc") ifsc: String): retrofit2.Response<BankDetails>
}

sealed class Response<out T> {
    data class Success<out T>(val data: T) : Response<T>()
    data class Error(val exceptions: Exceptions, val message: String? = null) : Response<Nothing>()
}