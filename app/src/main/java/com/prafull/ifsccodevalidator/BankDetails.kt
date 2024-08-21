package com.prafull.ifsccodevalidator

import com.google.gson.annotations.SerializedName

data class BankDetails(
    @SerializedName("ADDRESS") val address: String?,
    @SerializedName("BANK") val bank: String?,
    @SerializedName("BANKCODE") val bankCode: String?,
    @SerializedName("BRANCH") val branch: String?,
    @SerializedName("CENTRE") val centre: String?,
    @SerializedName("CITY") val city: String?,
    @SerializedName("CONTACT") val contact: String?,
    @SerializedName("DISTRICT") val district: String?,
    @SerializedName("IFSC") val ifsc: String?,
    @SerializedName("IMPS") val imps: Boolean?,
    @SerializedName("ISO3166") val iso3166: String?,
    @SerializedName("MICR") val micr: String?,
    @SerializedName("NEFT") val neft: Boolean?,
    @SerializedName("RTGS") val rtgs: Boolean?,
    @SerializedName("STATE") val state: String?,
    @SerializedName("SWIFT") val swift: Any,
    @SerializedName("UPI") val upi: Boolean?
)