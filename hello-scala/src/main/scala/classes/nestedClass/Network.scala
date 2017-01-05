package classes.nestedClass

/**
  * Created by fybai on 04/01/2017.
  */
import scala.collection.mutable.ArrayBuffer


/**
  * 在内嵌类中，可以通过外部类.this的方式来访问外部类的this引用，就像Java那样
  *
  * 也可以用如下的语法建立一个指向该引用的别名，其中outer变量指向Network.this ==> 自身类型
  */
class Network { outer =>

  class Member(val name: String) {
    val contacts = new ArrayBuffer[Network#Member]()
  }

  private val members = new ArrayBuffer[Member]()

  def join(name: String): Member = {
    val m = new Member(name)
    members += m
    m
  }
}


object Network extends App {
  /**
    * 在Scala中，每个实例都有它自己的Member类，就和它们有自己的members字段一样
    * 也就是说，chatter.Member和myFace.Member是不同的类
    *
    * 在Java中内部类从属于外部类，Scala采用的方式更符合常规~
    */
  val chatter = new Network
  val myFace = new Network

  val fred = chatter.join("Fred")
  val wilma = chatter.join("Wilma")
  fred.contacts += wilma // OK

  val barney = myFace.join("Barney")
  fred.contacts += barney  // Wrong
}
