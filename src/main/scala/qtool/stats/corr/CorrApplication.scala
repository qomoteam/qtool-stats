package qtool.stats.corr

import org.apache.commons.math3.linear.BlockRealMatrix
import org.apache.commons.math3.stat.correlation.{SpearmansCorrelation, PearsonsCorrelation}
import org.apache.spark.{SparkConf, SparkContext}

object CorrApplication extends App {

  val parser = new scopt.OptionParser[Option]("Qtool-Stats-Corr") {
    head("Qtool-Stats-Corr", "1.0")

    opt[String]('i', "in") required() valueName "<input>" action { (x, c) =>
      c.copy(in = x) } text "Input file"

    opt[String]('o', "out") action { (x, c) =>
      c.copy(out = x) } text "Output file"

    opt[String]('m', "method") valueName "<method>" action { (x, c) =>
      c.copy(method = x) } text "Method: pearson"

    opt[String]('s', "sep") action { (x, c) =>
      val v = x match {
        case "tab" => "\t"
        case "comma" => ","
        case _ => x
      }
      c.copy(seperator = v) } text "Seperator"

  }


  parser.parse(args, Option()) match {
    case Some(opt) => run(opt)
    case None =>
  }

  def run(opt: Option) = {
    val conf = new SparkConf()
      .setAppName(this.getClass.getName)
      .setIfMissing("spark.master", "local[5]")
      .set("spark.app.id", "qtool.stats.corr")

    val sc = new SparkContext(conf)

    val records = sc.textFile(opt.in).filter(!_.startsWith("#"))

    records.cartesian(records).map { case (row1, row2) =>
      val cols1 = row1.split(opt.seperator)
      val cols2 = row2.split(opt.seperator)

      val l1 = cols1.head
      val l2 = cols2.head

      (l1, l2, cols1, cols2)
    }.filter { case (l1, l2, _, _) =>
      l1 < l2
    }.map { case (l1, l2, cols1, cols2) =>
      val c1 = cols1.tail.map(_.toDouble)
      val c2 = cols2.tail.map(_.toDouble)
      val m = new BlockRealMatrix(Array(c1, c2)).transpose()

      if (opt.method.equals("spearmans")) {
        val spc =  new SpearmansCorrelation(m)
        val co = spc.getCorrelationMatrix.getEntry(0, 1)
        Array(l1, l2, co).mkString(opt.seperator)
      } else {
        val pc = new PearsonsCorrelation(m)
        val co = pc.getCorrelationMatrix.getEntry(0, 1)
        val pvalue = pc.getCorrelationPValues.getEntry(0, 1)
        Array(l1, l2, co, pvalue).mkString(opt.seperator)
      }

    }.saveAsTextFile(opt.out)

  }

}
