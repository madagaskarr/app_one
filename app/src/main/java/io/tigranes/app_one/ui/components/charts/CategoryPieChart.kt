package io.tigranes.app_one.ui.components.charts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import io.tigranes.app_one.data.model.Category

@Composable
fun CategoryPieChart(
    categoryStats: Map<Category, Pair<Int, Int>>, // Category to (completed, total)
    modifier: Modifier = Modifier
) {
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val backgroundColor = MaterialTheme.colorScheme.background
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    
    AndroidView(
        factory = { context ->
            PieChart(context).apply {
                description.isEnabled = false
                isDrawHoleEnabled = true
                setHoleColor(backgroundColor.toArgb())
                holeRadius = 58f
                transparentCircleRadius = 61f
                setDrawCenterText(true)
                centerText = "Tasks by\nCategory"
                setCenterTextSize(12f)
                setCenterTextColor(onSurfaceColor.toArgb())
                
                legend.apply {
                    isEnabled = true
                    textColor = onSurfaceColor.toArgb()
                    textSize = 12f
                }
                
                setEntryLabelColor(onSurfaceColor.toArgb())
                setEntryLabelTextSize(12f)
                
                setUsePercentValues(true)
                setExtraOffsets(20f, 10f, 20f, 10f)
            }
        },
        update = { chart ->
            val entries = categoryStats.mapNotNull { (category, stats) ->
                val (completed, total) = stats
                if (total > 0) {
                    PieEntry(total.toFloat(), category.name.lowercase().capitalize())
                } else null
            }
            
            if (entries.isNotEmpty()) {
                val dataSet = PieDataSet(entries, "").apply {
                    colors = categoryStats.keys.map { category ->
                        when (category) {
                            Category.LIFE -> primaryColor.toArgb()
                            Category.WORK -> tertiaryColor.toArgb()
                            Category.RELATIONSHIPS -> secondaryColor.toArgb()
                        }
                    }
                    valueTextColor = onSurfaceColor.toArgb()
                    valueTextSize = 12f
                    valueFormatter = PercentFormatter(chart)
                    sliceSpace = 3f
                    selectionShift = 5f
                }
                
                chart.data = PieData(dataSet)
                chart.invalidate()
            } else {
                chart.clear()
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
    )
}