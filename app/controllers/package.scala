import java.util.UUID

import org.joda.time.DateTime

/**
 * Created by Todd Sieland-Peteson on 6/2/15.
 */
package object controllers {
  def stringToDateTime(s: String): DateTime = DateTime.parse(s)
  def optionDateTime(optdt: Option[String]): Option[DateTime] = optdt.map(stringToDateTime)

  def optionInt(optint: Option[String]): Option[Int] = optint.map(_.toInt)
  def optionId(optint: Option[String]): Option[UUID] = optint.map(UUID.fromString(_))


}
