package com.example.service

import com.example.model.ConnectionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CameraService(private val networkService: NetworkService) {

    fun getStreamUrl(ip: String, port: Int): String {
        return "http://$ip:$port/stream"
    }

    fun getCaptureUrl(ip: String, port: Int): String {
        return "http://$ip:$port/capture"
    }

    suspend fun testCamera(ip: String, streamPort: Int, capturePort: Int): Pair<ConnectionState, Long> {
        return withContext(Dispatchers.IO) {
            val captureUrl = getCaptureUrl(ip, capturePort)
            val latency = networkService.checkLatency(captureUrl)
            if (latency >= 0) {
                Pair(ConnectionState.ONLINE, latency)
            } else {
                Pair(ConnectionState.OFFLINE, 0L)
            }
        }
    }
}
