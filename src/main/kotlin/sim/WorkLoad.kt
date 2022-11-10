package sim

import minus
import plus
import java.lang.Math.random
import kotlin.random.Random


enum class WorkLoadLevel {
    ALWAYS_RUNNING,
    BEST_EFFORT;

    companion object {
        fun random() = if (Math.random() > 0.5) ALWAYS_RUNNING else BEST_EFFORT
    }
}

class WorkLoad(
    val id: Int,
    val level: WorkLoadLevel,
    averageResources: Triple<Int, Int, Int>,
    volatility: Triple<Double, Double, Double>
) {
    companion object {
        fun newRandom(id: Int) = WorkLoad(
            id,
            WorkLoadLevel.random(),
            Triple(Random.nextInt(200), Random.nextInt(200), Random.nextInt(200)),
            Triple(random() * 2, random() * 2, random() * 2)
        )
    }

    val requiredResources = Triple(
        (averageResources.first * volatility.first * random()).toInt(),
        (averageResources.second * volatility.second * random()).toInt(),
        (averageResources.third * volatility.third * random()).toInt()
    )

    val maxRequiredResources =
        Triple(
            (averageResources.first * volatility.first).toInt(),
            (averageResources.second * volatility.second).toInt(),
            (averageResources.third * volatility.third).toInt()
        )
}