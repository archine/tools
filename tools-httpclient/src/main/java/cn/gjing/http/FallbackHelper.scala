package cn.gjing.http

/**
 * @author Gjing
 **/
@FunctionalInterface
trait FallbackHelper[T, R] {
  /**
   * Fallback in case of an error
   * @param t param
   * @return R
   */
  def fallBack(t: T): R
}
