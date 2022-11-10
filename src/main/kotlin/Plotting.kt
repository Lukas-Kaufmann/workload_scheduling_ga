import jetbrains.datalore.base.values.Color
import org.jetbrains.letsPlot.export.ggsave
import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.ggsize
import org.jetbrains.letsPlot.letsPlot

fun plotFitness(data: TrainingInfo, filename: String) {
    val graphData = mapOf (
        "generation" to data.map { it.numberOfGeneration },
        "best" to data.map {it.bestFitness},
        "average" to data.map {it.averageFitness},
        "worst" to data.map {it.worstFitness}
    )

    val p = letsPlot(graphData) + geomLine { x = "generation"; y = "best" } +
            geomLine(color= Color.LIGHT_GRAY) {x = "generation"; y = "average";} +
            geomLine(color= Color.VERY_LIGHT_GRAY) {x = "generation"; y = "worst"} +
            ggsize(500, 250)

    ggsave(p, filename)
}