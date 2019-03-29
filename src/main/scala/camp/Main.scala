
import com.github.tototoshi.csv.{CSVReader, SourceLineReader}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.sql.{SaveMode, SparkSession}


object Main extends App {
  Logger.getLogger("org").setLevel(Level.OFF)
  Logger.getLogger("akka").setLevel(Level.OFF)

  val res = getClass.getClassLoader.getResources("crimes")

  val conf = new SparkConf().setMaster("local").setAppName("event")
  val sc = new JavaSparkContext(conf)
  val spark = SparkSession.builder.config(conf).getOrCreate()
  val df = spark.read.option("header", "true").csv(res.nextElement().getPath)
  df.createOrReplaceTempView("crimes")
  df.printSchema()

  val crimesIdNotNull = df.filter(df("Crime ID").isNotNull)
  crimesIdNotNull.show() // From CSV files gather the crime records with non-empty crimeID.

  val coordinates = df.sqlContext.sql("SELECT *, CONCAT(Latitude, ' ',Latitude) as coordinates FROM crimes").filter(df("Crime ID").isNotNull)
  coordinates.show() //Group resulting list by coordinate pairs.

  val count  = coordinates.groupBy("coordinates").count().orderBy(org.apache.spark.sql.functions.col("count").desc)
  count.show() // Sort coordinate pairs by total number of crimes, most repeated crime locations first.

  val thefts = df.sqlContext.sql("SELECT *, CONCAT(Latitude,' ',Latitude) as coordinates FROM crimes " +
                              " where `Crime type` in ('Robbery','Burglary','Shoplifting','Other theft')")

  val topFive = thefts.groupBy("coordinates").count().orderBy(org.apache.spark.sql.functions.col("count").desc).limit(5)
  topFive.show()

  for( i <- 0 to 4){ //Print out top 5 crime locations, each with list of associated theft incidents
    println(topFive.take(5)(i)(0))
    thefts.filter(thefts("coordinates").isin(topFive.take(5)(i)(0))).show()
  }
}