package akkaDocuments.cluster.distributedWorkers

/**
  * Created by fybai on 06/01/2017.
  */

/**
  * 该程序使用akka的cluster功能实现分布式的worker
  * 该实现基于Balancing Workload Across Nodes with Akka 2 -> http://letitcrash.com/post/29044669086/balancing-workload-across-nodes-with-akka-2
  * 该文章描述了让workers从master获取任务而不是master push任务给worker的优势
  *
  * 目标：
  *   1. 从客户端接收任务的frontend节点可收缩
  *   2. 工作节点workers与actor可收缩
  *   3. 支持上千个workers
  *   4. 任务不可以丢失，如果一个worker失败，该任务应该被重试
  */
class Main {

}
