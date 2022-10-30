import sim.ComputeNode
import sim.WorkLoad
import java.lang.Math.random
import kotlin.math.sin

//TODO refactor to function that takes nodes and workloads as input and gives

fun main(args: Array<String>) {
    var nodes = (1..20).map { ComputeNode(it) }
    var workloads = (1..100).map {WorkLoad.newRandom(it)}

    val population = (1..50).map { (1..10).map { if (random() < 0.5) 0 else 1 } }

    val algorithm = GeneticAlgorithm(
        population,
        score = { it.sum().toDouble() },
        //TODO two/single point crossovers
        cross = { it.first.mapIndexed { index, i -> if (random() < 0.5) i else it.second[index] } },
        mutate = { it.map { if (random() < 0.9) it else if (random() < 0.5) 0 else 1 } },
        select = ::rouletteWheelSelection
    )

    val result = algorithm.run()

    print("Best individual: ")
    result.forEach { print(it) }
}