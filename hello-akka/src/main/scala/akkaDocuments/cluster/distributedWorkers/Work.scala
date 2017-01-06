package akkaDocuments.cluster.distributedWorkers

/**
  * Created by fybai on 06/01/2017.
  */
case class Work(workId: String, job: Any)

case class WorkResult(workId: String, result: Any)
