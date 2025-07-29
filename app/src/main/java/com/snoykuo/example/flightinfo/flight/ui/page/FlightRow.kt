package com.snoykuo.example.flightinfo.flight.ui.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.snoykuo.example.flightinfo.flight.data.FlightInfo

@Composable
fun FlightRow(
    flight: FlightInfo,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "航班：${flight.flightNumber}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = flight.status,
                    style = MaterialTheme.typography.bodyMedium,
                    color = when (flight.status) {
                        "已到Arrived", "已出發Departed" -> Color(0xFF4CAF50) // 綠色
                        "延遲Delayed" -> Color(0xFFFF9800) // 橘色
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row {
                Text(
                    text = "時間：${flight.scheduledDate} ${flight.scheduledTime}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row {
                Text(
                    text = "地點：${flight.destinationZh}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row {
                Text(
                    text = "航廈：${flight.terminal}　登機門/機坪：${flight.gate}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview(name = "抵達航班 - 準時")
@Composable
fun PreviewArrivalOnTime() {
    FlightRow(
        flight = FlightInfo(
            terminal = "1",
            type = "A",
            airlineCode = "JL",
            airlineName = "日本航空",
            flightNumber = "JL809",
            gate = "B5",
            scheduledDate = "2025/07/26",
            scheduledTime = "15:30",
            estimatedDate = "2025/07/26",
            estimatedTime = "15:40",
            destinationCode = "NRT",
            destinationEn = "Narita",
            destinationZh = "成田",
            status = "已到Arrived",
            aircraftType = "B787"
        )
    )
}

@Preview(name = "起飛航班 - 延遲")
@Composable
fun PreviewDepartureDelayed() {
    FlightRow(
        flight = FlightInfo(
            terminal = "2",
            type = "D",
            airlineCode = "BR",
            airlineName = "長榮航空",
            flightNumber = "BR712",
            gate = "D4",
            scheduledDate = "2025/07/26",
            scheduledTime = "16:45",
            estimatedDate = "2025/07/26",
            estimatedTime = "17:20",
            destinationCode = "HKG",
            destinationEn = "Hong Kong",
            destinationZh = "香港",
            status = "延遲Delayed",
            aircraftType = "A330"
        )
    )
}
