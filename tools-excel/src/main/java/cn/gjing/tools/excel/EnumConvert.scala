package cn.gjing.tools.excel

/**
 * Enumeration converter
 * @author Gjing
 **/
trait EnumConvert[T <: Enum[_], E] {
  /**
   * Converted to entity enum field
   *
   * @param e Excel value
   * @return Entity value
   */
  def toEntityAttribute(e: E): T

  /**
   * Converted to excel value
   *
   * @param t Entity value
   * @return E Excel value
   */
  def toExcelAttribute(t: T): E
}
