package com.atguigu.bigdata.spark.core.framework.application

import com.atguigu.bigdata.spark.core.framework.common.TApplication
import com.atguigu.bigdata.spark.core.framework.controller.WordCountController

object WordCountApplication extends App with TApplication{

    // 启动应用程序
    // TODO: scala的控制抽象语法：传一段代码（大括号中的代码）过去
    start(){
        val controller = new WordCountController()
        controller.dispatch()
    }

}
