import org.apache.spark._
import org.apache.spark.rdd.RDD

object Config {
    val conf = new SparkConf().setAppName("Simple Application")
    //val scp = new SparkContext(conf)
    val scp = sc
}
object Generators {
    class Sampler(file: String) {
        def run(n: Int) = {
            val r = Config.scp.textFile(file)
            val sampled = r.takeSample(true, n, 3)
            Config.scp.parallelize(sampled)
        }
    }

    object GenID{
        def run(n: Int) = {
            Config.scp.parallelize(1 to n)
        }
    }
    object GenPersons{
        def run(mails: RDD[(Int,List[String])], countries: RDD[(Int,List[String])]) = {
            mails.union(countries).reduceByKey((x,y) => x ++ y)
        }
    }
}

def test() {
    val s = new Generators.Sampler("dicLocations.txt")
    val m = new Generators.Sampler("email.txt")
    val i = Generators.GenPersons
    val l : List[_] = List(s,m,i)
    var namesv = l(1).run(10)
    var mails = l(2).run(10)
    val ids = l(3).run(10)
    var aa = ids.zip(namesv.map(x => List(x.split(" ")(1))))
    var bb = ids.zip(mails.map(x => List(x)))
    Generators.GenPersons.run(aa,bb).foreach(println)
    //var unit = aa.union(bb).reduceByKey((x,y) => x ++ y).foreach(println)
}
