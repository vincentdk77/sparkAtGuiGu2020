package com.atguigu.bigdata.spark.core.framework.common

import com.atguigu.bigdata.spark.core.framework.controller.WordCountController
import com.atguigu.bigdata.spark.core.framework.util.EnvUtil
import org.apache.spark.{SparkConf, SparkContext}

trait TApplication {

    // TODO: scala中的“控制抽象”语法，将一段代码传到方法中去  ( op : => Unit 或者 op : ()=> Unit)
    def start(master:String ="local[*]", app:String = "Application")( op : => Unit ): Unit = {
        val sparConf = new SparkConf().setMaster(master).setAppName(app)
        val sc = new SparkContext(sparConf)
        EnvUtil.put(sc)

        try {
            op
        } catch {
            case ex => println(ex.getMessage)
        }

        // TODO 关闭连接
        sc.stop()
        EnvUtil.clear()
    }
}
