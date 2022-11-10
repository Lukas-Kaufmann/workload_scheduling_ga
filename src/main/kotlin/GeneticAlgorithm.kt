import java.lang.Math.random
import kotlin.random.Random

class GeneticAlgorithm<T>(
    var population: Collection<T>,
    val score: (individual: T) -> Double,
    val cross: (parents: Pair<T, T>) -> T,
    val mutate: (individual: T) -> T,
    val select: (scoredPopulation: Collection<Pair<Double, T>>) -> Collection<T>) {

    fun run(epochs: Int = 200000, mutationProbability: Double = 0.2): Pair<T, TrainingInfo> {
        val trainingData = mutableListOf<GenerationInfo>()

        var scoredPopulation = population.map { Pair(score(it), it) }.sortedByDescending { it.first }

        for (i in 0..epochs) {
            if (trainingData.isStale()) {
                break
            }

            scoredPopulation = select(scoredPopulation)
                .asSequence()
                .shuffled()
                .chunked(2) {Pair(it[0], it[1])}
                .map { cross(it) }
                .map { if (random() <= mutationProbability) mutate(it) else it }
                .map { Pair(score(it), it) }
                .sortedByDescending { it.first }
                .toList()

            trainingData.add(GenerationInfo(i, scoredPopulation.first().first, scoredPopulation.map { it.first }.average(), scoredPopulation.last().first))
        }

        return Pair(scoredPopulation.maxBy { it.first }.second, trainingData)
    }
}

typealias TrainingInfo = List<GenerationInfo>
data class GenerationInfo(val numberOfGeneration: Int, val bestFitness: Double, val averageFitness: Double, val worstFitness: Double)

fun TrainingInfo.isStale(): Boolean {
    val generationSpan = 1000
    if (this.size < generationSpan) {
        return false
    }

    val threshold = 0.005

    val gens = this.subList(this.size - generationSpan, this.size)
    val max = gens.maxOfOrNull { it.bestFitness }
    val min = gens.minOfOrNull { it.bestFitness }
    if (max != null && min != null) {
        val span = max - min
        if (span < threshold) {
            return true
        }
    }
    return false
}

//selects the best individiual twice and then performs roulethe wheel selection
// on the better half of the population
fun <T> selection(scoredPopulation: Collection<Pair<Double, T>>): Collection<T> {
    val average = scoredPopulation.map { it.first }.average()
    val fittest = scoredPopulation.maxBy {it.first}.second

    val newPopulation = mutableListOf(fittest, fittest, fittest, fittest)

    val bestHalf = scoredPopulation.filter { it.first > average }
    repeat((scoredPopulation.size * 2) - 4) {
        var value = bestHalf.sumOf { it.first } * random()

        var found = false

        for ((fitness, individual) in bestHalf) {
            value -= fitness
            if (value <= 0){
                newPopulation.add(individual)
                found = true
                break
            }
        }
        if (!found)
            newPopulation.add(scoredPopulation.last().second)
    }
    return newPopulation
}

fun twoPointCrossOver(parents: Pair<List<Int>, List<Int>>): List<Int> {
    val start = Random.nextInt(parents.first.size)
    val end = Random.nextInt(parents.first.size)

    val child = parents.first.toMutableList()

    var index = start
    while (end != index) {
        child[index] = parents.second[index]

        index = (index + 1) % parents.first.size
    }
    return child
}