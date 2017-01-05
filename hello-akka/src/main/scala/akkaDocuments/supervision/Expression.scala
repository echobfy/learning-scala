package akkaDocuments.supervision

/**
  * Created by fybai on 03/01/2017.
  */
trait Expression {
  def left: Expression
  def right: Expression
}


case class Add(left: Expression, right: Expression) extends Expression {
  override def toString: String = s"($left + $right)"
}

case class Multiply(left: Expression, right: Expression) extends Expression {
  override def toString: String = s"($left * $right)"
}

case class Divide(left: Expression, right: Expression) extends Expression {
  override def toString: String = s"($left / $right)"
}

case class Const(value: Int) extends Expression {
  override def left: Expression = this

  override def right: Expression = this

  override def toString: String = String.valueOf(value)
}
