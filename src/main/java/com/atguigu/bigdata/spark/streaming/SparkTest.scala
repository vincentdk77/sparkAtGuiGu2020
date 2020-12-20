package com.atguigu.bigdata.spark.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SparkTest {
  /**
    * 程序的核心代码逻辑
    * @return
    */
  def createSSC(): _root_.org.apache.spark.streaming.StreamingContext = {
    val update: (Seq[Int], Option[Int]) => Some[Int] = (values: Seq[Int], status:
    Option[Int]) => {
      //当前批次内容的计算
      val sum: Int = values.sum
      //取出状态信息中上一次状态
      val lastStatu: Int = status.getOrElse(0)
      Some(sum + lastStatu)
    }
    val sparkConf: SparkConf = new
        SparkConf().setMaster("local[4]").setAppName("SparkTest")
    //设置优雅的关闭
    sparkConf.set("spark.streaming.stopGracefullyOnShutdown", "true")
    val ssc = new StreamingContext(sparkConf, Seconds(5))
    ssc.checkpoint("./ck")
    val line: ReceiverInputDStream[String] = ssc.socketTextStream("linux1", 9999)
    val word: DStream[String] = line.flatMap(_.split(" "))
    val wordAndOne: DStream[(String, Int)] = word.map((_, 1))
    val wordAndCount: DStream[(String, Int)] = wordAndOne.updateStateByKey(update)
    wordAndCount.print()
    ssc
  }

  def main(args: Array[String]): Unit = {
    //创建ssc环境或者恢复上次关闭的数据
    val ssc: StreamingContext = StreamingContext.getActiveOrCreate("./ck", () =>
      createSSC())
    //配置优雅的关闭 (一个单独线程，监控hdfs的stopSpark节点是否存在，存在就是要进行关闭！)
    new Thread(new MonitorStop(ssc)).start()
    ssc.start()
    ssc.awaitTermination()
  }
}