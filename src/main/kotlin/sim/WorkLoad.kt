package sim

import minus
import plus
import java.lang.Math.random
import kotlin.random.Random
import kotlin.random.nextInt

abstract class WorkLoad(
    val id: Int
) {
    companion object {
        fun newRandom(id: Int): WorkLoad {
            return if (random() < 1) IntervalJob.newRandom(id) else EventHandler.newRandom(id)
        }
    }
    var remainingSteps = Triple(0, 0, 0)

    abstract fun requiredResources(timeStep: Int): Triple<Int, Int, Int>

    fun runWithResources(res: Triple<Int, Int, Int>) {
        remainingSteps -= res
    }
}

//TODO mechanic for limiting resources in a single step

class IntervalJob(id: Int, val intervalStep: Int, val averageResources: Triple<Int, Int, Int>, val volatility: Triple<Double, Double, Double>): WorkLoad(id) {
    companion object {
        fun newRandom(id: Int) = IntervalJob(id, Random.nextInt(100.. 500), Triple(Random.nextInt(500), Random.nextInt(500), Random.nextInt(500)), Triple(random()*2, random()*2, random()*2))
    }
    val requiredResources = Triple(
        (averageResources.first * volatility.first * random()).toInt(),
        (averageResources.second * volatility.second * random()).toInt(),
        (averageResources.third * volatility.third * random()).toInt()
    )

    override fun requiredResources(timeStep: Int): Triple<Int, Int, Int> {
        if (timeStep % intervalStep == 0) {
            remainingSteps += requiredResources
        }

        return remainingSteps
    }
}

class EventHandler(id: Int, val triggerChance: Double, val averageResources: Triple<Int, Int, Int>, val volatility: Triple<Double, Double, Double>): WorkLoad(id) {
    companion object {
        fun newRandom(id: Int) = EventHandler(id, random()*0.001, Triple(Random.nextInt(500), Random.nextInt(500), Random.nextInt(500)), Triple(random()*2, random()*2, random()*2))
    }

    override fun requiredResources(timeStep: Int): Triple<Int, Int, Int> {
        if (random() < triggerChance) {
            remainingSteps += Triple(
                (averageResources.first * volatility.first * random()).toInt(),
                (averageResources.second * volatility.second * random()).toInt(),
                (averageResources.third * volatility.third * random()).toInt()
            )
        }

        return remainingSteps
    }

}