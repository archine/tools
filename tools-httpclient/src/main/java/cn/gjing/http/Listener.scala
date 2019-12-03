package cn.gjing.http

/**
 * @author Gjing
 * Result Listener
 **/
trait Listener[T] {
  /**
   * Notify listener
   * @param t result
   */
  def notify(t: T): Unit
}
