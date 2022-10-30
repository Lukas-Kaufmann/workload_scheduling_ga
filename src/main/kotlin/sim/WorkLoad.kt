package sim

import java.lang.Math.random

open class WorkLoad(
    val id: Int
) {
    companion object {
        fun newRandom(id: Int): WorkLoad {
            val r = random()
            return if (r < 0.3) CronJob(id) else if (r < 0.7) Job(id) else EventHandler(id)
        }
    }
    
}

class CronJob(id: Int): WorkLoad(id) {

}

class Job(id: Int): WorkLoad(id) {

}

class EventHandler(id: Int): WorkLoad(id) {

}