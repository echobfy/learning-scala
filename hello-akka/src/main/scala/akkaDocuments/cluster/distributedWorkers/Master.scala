package akkaDocuments.cluster.distributedWorkers

/**
  * Created by fybai on 06/01/2017.
  */

/**
  * 核心在于Master actor需要管理未完成的任务（outstanding），然后有任务时，通知注册上的workers
  *
  * Master actor作为backend存在，在整个集群中为单点，整个集群中只存在一个活跃的master
  *
  * 当master节点失败时，其他备用master节点将自动顶上。备用master将负责未完成任务
  * master的状态可以根据event sourcing重建
  */
class Master {

}
