package com.apklis.plugin

import android.content.Context
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import cu.uci.android.apklis_license_validator.ApklisLicenseValidator
import cu.uci.android.apklis_license_validator.ApklisLicenseValidator.LicenseCallback
import cu.uci.android.apklis_license_validator.ApklisLicenseValidator.LicenseError

@CapacitorPlugin(name = "ApklisLicense")
class ApklisLicensePlugin : Plugin() {

    private var validator: ApklisLicenseValidator? = null

    override fun load() {
        validator = ApklisLicenseValidator()
    }

    @PluginMethod
    fun verifyLicense(call: PluginCall) {
        val packageName = call.getString("packageName")
        
        if (packageName.isNullOrBlank()) {
            call.reject("packageName is required")
            return
        }

        validator?.verifyCurrentLicense(context, packageName, object : LicenseCallback {
            override fun onSuccess(response: Map<String, Any>) {
                val result = JSObject()
                result.put("paid", response["paid"] as? Boolean ?: false)
                result.put("license", response["license"] as? String ?: "")
                result.put("username", response["username"] as? String ?: "")
                
                val error = response["error"] as? String
                if (error != null) {
                    result.put("error", error)
                }
                
                val statusCode = response["status_code"] as? Int
                if (statusCode != null) {
                    result.put("statusCode", statusCode)
                }

                call.resolve(result)
            }

            override fun onError(error: LicenseError) {
                val result = JSObject()
                result.put("paid", false)
                result.put("error", error.message)
                result.put("errorCode", error.code)
                call.resolve(result)
            }
        })
    }

    @PluginMethod
    fun purchaseLicense(call: PluginCall) {
        val licenseUuid = call.getString("licenseUuid")
        
        if (licenseUuid.isNullOrBlank()) {
            call.reject("licenseUuid is required")
            return
        }

        validator?.purchaseLicense(context, licenseUuid, object : LicenseCallback {
            override fun onSuccess(response: Map<String, Any>) {
                val result = JSObject()
                result.put("success", response["success"] as? Boolean ?: false)
                result.put("paid", response["paid"] as? Boolean ?: false)
                result.put("license", response["license"] as? String ?: "")
                result.put("username", response["username"] as? String ?: "")
                
                val error = response["error"] as? String
                if (error != null) {
                    result.put("error", error)
                }

                call.resolve(result)
            }

            override fun onError(error: LicenseError) {
                val result = JSObject()
                result.put("paid", false)
                result.put("error", error.message)
                result.put("errorCode", error.code)
                call.resolve(result)
            }
        })
    }
}
