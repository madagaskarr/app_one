package io.tigranes.app_one.ui.components.charts

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import io.tigranes.app_one.data.model.DailyMood
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import java.time.format.DateTimeFormatter

@Composable
fun MoodLineChart(
    moods: List<DailyMood>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = false
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(false)
                setPinchZoom(false)
                setDrawGridBackground(false)
                
                // Configure X axis
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    granularity = 1f
                    textColor = onSurfaceColor.toArgb()
                    valueFormatter = DateValueFormatter()
                }
                
                // Configure Y axis
                axisLeft.apply {
                    setDrawGridLines(true)
                    gridColor = surfaceVariantColor.toArgb()
                    axisMinimum = 0.5f
                    axisMaximum = 5.5f
                    granularity = 1f
                    textColor = onSurfaceColor.toArgb()
                    valueFormatter = MoodValueFormatter()
                }
                
                axisRight.isEnabled = false
                
                // Set margins
                setExtraOffsets(10f, 10f, 10f, 10f)
            }
        },
        update = { chart ->
            if (moods.isNotEmpty()) {
                val entries = moods.mapIndexed { index, mood ->
                    Entry(index.toFloat(), mood.rating.toFloat())
                }
                
                val dataSet = LineDataSet(entries, "Mood").apply {
                    color = primaryColor.toArgb()
                    valueTextColor = onSurfaceColor.toArgb()
                    lineWidth = 2f
                    circleRadius = 4f
                    setCircleColor(primaryColor.toArgb())
                    setDrawCircleHole(false)
                    setDrawValues(false)
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                }
                
                chart.data = LineData(dataSet)
                chart.xAxis.valueFormatter = DateValueFormatter(moods.map { it.date })
                chart.invalidate()
            } else {
                chart.clear()
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}

class DateValueFormatter(private val dates: List<LocalDate> = emptyList()) : ValueFormatter() {
    private val formatter = DateTimeFormatter.ofPattern("MM/dd")
    
    override fun getFormattedValue(value: Float): String {
        val index = value.toInt()
        return if (index in dates.indices) {
            dates[index].toJavaLocalDate().format(formatter)
        } else {
            ""
        }
    }
}

class MoodValueFormatter : ValueFormatter() {
    private val moods = listOf("", "😔", "😟", "😐", "😊", "😄", "")
    
    override fun getFormattedValue(value: Float): String {
        val index = value.toInt()
        return if (index in 1..5) {
            moods[index]
        } else {
            ""
        }
    }
}