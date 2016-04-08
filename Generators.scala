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
    class SamplerDIY(f: String){
        val file = new TextFile(f)
        val size = file.length
        def run(x: Int) {
            val r = scala.util.Random
            file(r.nextInt % size)
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
def test2() {
    //val p = new TextFile("email.txt")
    val s = new Generators.SamplerDIY("dicLocations.txt")
    val m = new Generators.SamplerDIY("email.txt")
    val i = Generators.GenID
    val l : List[_] = List(s,m,i)
    /*var namesv = l(1).run(10)
    var mails = l(2).run(10)
    val ids = l(3).run(10)*/
    var namesv = Runtime.reflectMethod(l(0), "run", 10)
    var mails = Runtime.reflectMethod(l(1), "run", 10)
    var ids = Runtime.reflectMethod(l(2), "run", 10)


    var Rids = ids.asInstanceOf[RDD[Int]]
    var Rnamesv = namesv.asInstanceOf[RDD[String]]
    var Rmails = mails.asInstanceOf[RDD[String]]
    var aa = Rids.zip(Rnamesv.map(x => List(x.split(" ")(1))))
    var bb = Rids.zip(Rmails.map(x => List(x)))
    /*
    aa = aa.asInstanceOf[RDD[(Int, List[String])]]
    bb = bb.asInstanceOf[RDD[(Int, List[String])]]*/
    Generators.GenPersons.run(aa,bb).foreach(println)
    //var unit = aa.union(bb).reduceByKey((x,y) => x ++ y).foreach(println)
}
//This function assumes that the generators return the RDD directly.
def test() {
    //val p = new TextFile("email.txt")
    val s = new Generators.Sampler("dicLocations.txt")
    val m = new Generators.Sampler("email.txt")
    val i = Generators.GenID
    val l : List[_] = List(s,m,i)
    /*var namesv = l(1).run(10)
    var mails = l(2).run(10)
    val ids = l(3).run(10)*/
    var namesv = Runtime.reflectMethod(l(0), "run", 10)
    var mails = Runtime.reflectMethod(l(1), "run", 10)
    var ids = Runtime.reflectMethod(l(2), "run", 10)


    var Rids = ids.asInstanceOf[RDD[Int]]
    var Rnamesv = namesv.asInstanceOf[RDD[String]]
    var Rmails = mails.asInstanceOf[RDD[String]]
    var aa = Rids.zip(Rnamesv.map(x => List(x.split(" ")(1))))
    var bb = Rids.zip(Rmails.map(x => List(x)))
    /*
    aa = aa.asInstanceOf[RDD[(Int, List[String])]]
    bb = bb.asInstanceOf[RDD[(Int, List[String])]]*/
    Generators.GenPersons.run(aa,bb).foreach(println)
    //var unit = aa.union(bb).reduceByKey((x,y) => x ++ y).foreach(println)
}
