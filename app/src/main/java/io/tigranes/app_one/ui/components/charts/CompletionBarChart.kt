package io.tigranes.app_one.ui.components.charts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import io.tigranes.app_one.data.model.Category

@Composable
fun CompletionBarChart(
    categoryStats: Map<Category, Float>, // Category to completion rate (0-1)
    modifier: Modifier = Modifier
) {
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    
    AndroidView(
        factory = { context ->
            BarChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = false
                setDrawGridBackground(false)
                setTouchEnabled(false)
                
                // Configure X axis
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    granularity = 1f
                    textColor = onSurfaceColor.toArgb()
                    valueFormatter = CategoryValueFormatter()
                }
                
                // Configure Y axis
                axisLeft.apply {
                    setDrawGridLines(true)
                    gridColor = surfaceVariantColor.toArgb()
                    axisMinimum = 0f
                    axisMaximum = 100f
                    granularity = 20f
                    textColor = onSurfaceColor.toArgb()
                    valueFormatter = PercentageValueFormatter()
                }
                
                axisRight.isEnabled = false
                
                setFitBars(true)
                setExtraOffsets(10f, 10f, 10f, 10f)
            }
        },
        update = { chart ->
            val entries = Category.values().mapIndexed { index, category ->
                val rate = categoryStats[category] ?: 0f
                BarEntry(index.toFloat(), rate * 100f)
            }
            
            val dataSet = BarDataSet(entries, "Completion Rate").apply {
                colors = Category.values().map { category ->
                    when (category) {
                        Category.LIFE -> primaryColor.toArgb()
                        Category.WORK -> tertiaryColor.toArgb()
                        Category.RELATIONSHIPS -> secondaryColor.toArgb()
                    }
                }
                valueTextColor = onSurfaceColor.toArgb()
                valueTextSize = 10f
                valueFormatter = PercentageValueFormatter()
            }
            
            chart.data = BarData(dataSet)
            chart.invalidate()
        },
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}

class CategoryValueFormatter : ValueFormatter() {
    private val categories = Category.values().map { it.name.lowercase().capitalize() }
    
    override fun getFormattedValue(value: Float): String {
        val index = value.toInt()
        return if (index in categories.indices) {
            categories[index]
        } else {
            ""
        }
    }
}

class PercentageValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return "${value.toInt()}%"
    }
}