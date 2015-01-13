package com.cognitivecreations.helpers

import java.io.StringReader
import java.util.Date
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.sax.SAXResult

import com.thoughtworks.xstream.XStream
import org.joda.time.format.DateTimeFormatter
import org.w3c.dom.{Element => JElement, Node => JNode}
import org.xml.sax.InputSource

import scala.util.Try
import scala.util.control.Exception._
import scala.xml._
import scala.xml.parsing.NoBindingFactoryAdapter

trait XmlUtil {
  def prettyPrint(ns: NodeSeq): String = {
    val sb = new StringBuilder
    val pp = new PrettyPrinter(180, 3)
    pp.format(ns.asInstanceOf[Node], sb)
    sb.toString()
  }

  def nullSafeStr(str: Any): String = {
    if (str == null) "" else str.toString
  }

  def retrieveAsOption(nodeName: String, response: NodeSeq, strict: Boolean = false): Option[String] = {
    val textValue =
      if (strict) (response \ nodeName).text.trim else (response \\ nodeName).text.trim

    if (textValue.trim.isEmpty)
      None
    else
      Some(textValue)
  }
  /**
   * Retrieve an attribute value from the response as an Int.
   */
  def retrieveAsInt(nodeName: String, response: NodeSeq, strict: Boolean = false): Int = {
    retrieveAsOption(nodeName, response, strict).map(_.toInt).getOrElse(0)
  }

  def retrieveAsOptionalInt(nodeName: String, response: NodeSeq, strict: Boolean = false): Option[Int] = {
    retrieveAsOption(nodeName, response, strict).flatMap(x => Try(x.toInt).toOption)
  }

  /** Retrieve an attribute value as a BigDecimal.  If the value is empty, return None */
  def retrieveAsBigDecimal(nodeName: String, response: NodeSeq, strict: Boolean = false): Option[BigDecimal] = {
    val amountString = if(strict){
      (response \ nodeName).text.trim
    } else {
      (response \\ nodeName).text.trim.replace(",", "")
    }
    if (amountString.isEmpty) {
      None
    } else {
      try {
        Some(BigDecimal(amountString))
      } catch {
        case e: java.lang.NumberFormatException => e.printStackTrace(); None
      }
    }
  }

  def retrieveAsOptionalBigDecimal(nodeName: String, node: NodeSeq): BigDecimal = {
    retrieveAsOption(nodeName, node, strict = true).flatMap{ text =>
      allCatch.opt(BigDecimal(text))
    }.getOrElse(BigDecimal(0))
  }

  /** Retrieve the text as a date by giving in a date formatter */
  def retrieveAsDate(nodeName: String, response: NodeSeq, dateFormatter: DateTimeFormatter): Option[Date] = {
    val dateString = (response \ nodeName).text
    if (dateString.isEmpty) {
      None
    } else {
      try {
        Option(dateFormatter.parseDateTime(dateString).toDate)
      } catch {
        case ignore: Exception => None
      }
    }
  }

  /**
   * Retrieve an attribute value from the response as a currency amount.  A currency amount is
   * represented as a double.
   */
  def retrieveAsCurrency(nodeName: String, response: NodeSeq): Double = {
    val amountString = (response \\ nodeName).text.trim.replace(",", "")
    if (amountString.isEmpty) {
      0d
    } else {
      try {
        amountString.toDouble
      } catch {
        case e: java.lang.NumberFormatException => e.printStackTrace(); 0d
      }
    }
  }

  /**
   * Retrieve an attribute value from the response as a currency amount.  A currency amount is
   * represented as a double.
   */
  def retrieveAsCurrency(nodeName: String, response: NodeSeq, decimalPosition: Int, strict: Boolean = true): Double = {

    val amountString = (if (strict) (response \ nodeName).text else (response \\ nodeName).text).trim.replace(",", "")

    if (amountString.isEmpty) {
      0d
    } else {
      try {
        val amount = amountString.toDouble
        val multiplier = math.pow(10, decimalPosition)
        if (multiplier != 0 && !amountString.contains("."))
          amount / multiplier
        else
          amount
      } catch {
        case e: java.lang.NumberFormatException => e.printStackTrace(); 0d
      }
    }
  }

  /**
   *  Given:  val ns: NodeSeq = <span id="321">foo</span>
   *
   *  Call: getNodeTextByAttribVal(ns,"span","id","321")
   *
   *  to get "foo"
   *
   */
  def getNodeTextByAttribVal(ns: NodeSeq,
                             tagName: String,
                             attrName: String,
                             attrVal: String) : String = {
    getNodeSeqByAttribVal(ns,tagName,attrName,attrVal).text
  }

  /**
   *  Given: val ns: NodeSeq = <span id="321">foo</span>
   *
   *  Call: getNodeSeqByAttribVal(ns,"span","id","321")
   *
   *  to get the span Node with Id == 321
   *
   */
  def getNodeSeqByAttribVal(ns: NodeSeq,
                            tagName: String,
                            attrName: String,
                            attrVal: String): NodeSeq = {
    (ns \\ tagName) filter { n : Node => (n \ ("@"+attrName)).text == attrVal}
  }

  /**Converts an amount in String format with the number of decimal positions specified. */
  def convertToCurrency(amount: String, decimalPosition: Int): Double = {
    val amountString = if (amount == null) "" else amount.trim.replace(",", "")
    if (amountString.isEmpty) {
      0d
    } else {
      try {
        val amount = amountString.toDouble
        if (!amountString.contains(".")){
          val divisor = math.pow(10, decimalPosition)
          amount / divisor
        } else {
          amount
        }
      } catch {
        case e: java.lang.NumberFormatException => e.printStackTrace(); 0d
      }
    }
  }


  /**
   * Converts a string representing a number to a fixed precision with the specified decimal places
   * @param amount - string representing a number, without a decimal point
   * @param decimalPosition - number of decimal places required
   * @return
   */
  def convertToBigDecimal(amount: String, decimalPosition: Int): scala.math.BigDecimal = {
    val javaValue = new java.math.BigDecimal(amount).movePointLeft(decimalPosition)
    BigDecimal(javaValue)
  }

  /**
   * As above, but for Option[String]
   * @param amount - Option[String] representing a number, without a decimal point
   * @param decimalPosition - number of decimal places required
   * @return
   */
  def convertToBigDecimal(amount: Option[String], decimalPosition: Int): scala.math.BigDecimal = {
    amount match {
      case Some(value) => convertToBigDecimal(value,decimalPosition)
      case None        => BigDecimal(0)
    }
  }

  /**
   * Finds the node to replace in the current NodeSeq by using the label from the specified replacement node.  The
   * method will return a copy of the current NodeSeq with the current node replaced with the replacementNode
   * specified.
   *
   * @param currentNodeSequence   The NodeSeq to be updated.
   * @param replacementNodeSeq    The desired version of the node to be replaced.
   * @return                      A new version of the NodeSeq with the replacement node.
   */
  def replaceXmlNode(currentNodeSequence: NodeSeq, replacementNodeSeq: NodeSeq): NodeSeq = {
    val replacementNode = replacementNodeSeq.head
    val currentNode = (currentNodeSequence \\ replacementNode.label).head
    val currentNodeIndex = currentNodeSequence.indexOf(currentNode)
    currentNodeSequence.updated(currentNodeIndex, replacementNode)
  }

  /**
   * Java's BigDecimal seems to have a bug such that nnnn.n0 gets returned as
   * nnnn.n by toString even though the precision is specified as 2.  This routine
   * will attempt to pad on the right with 0 to make the precision match what is
   * needed.
   */
  def addTrailingZeroes(value: String, neededPrecision: Int): String = {
    val realPrecision = value.length - value.indexOf('.')
    print("realPrecision, neededPrecision, value:  " + realPrecision + ", " + neededPrecision + ", " + value)

    if (realPrecision <= 0 || realPrecision >= neededPrecision) value
    else addTrailingZeroes(value + "0", neededPrecision)
  }

  /**
   *
   * selects an element by id(or attrName) and returns it's text
   */
  def elementText(attrValue: String, attrName: String = "id", tag: String = "div")(implicit xml: NodeSeq): String = {
    (xml \\ tag filter( n => (n \ ("@"+attrName)).text == attrValue)).text
  }

  /**
   *
   * @param elem      The element to be updated
   * @param name      The attribute name
   * @param valueOpt  Option holding value for attribute to optionally attach. Only attached if the Option is a Some.
   * @return          Returns element with attached attribute. If valueOpt is None then will return original element.
   */
  def attachAttribute(elem: Elem, name: String, valueOpt: Option[String]) : Elem = valueOpt match {
    case Some(value) => elem % Attribute(None, name, Text(value), scala.xml.Null)
    case None => elem
  }

}

trait XmlSerialization {

  private lazy val xmlLogger = org.slf4j.LoggerFactory.getLogger(this.getClass)
  private lazy val documentFactory = DocumentBuilderFactory.newInstance()
  private lazy val transformerFactory = TransformerFactory.newInstance()

  def asXml(): String = {
    xstream.toXML(this)
  }

  def toXml(obj: Any): String = {
    try {
      xstream.toXML(obj)
    } catch {
      case e: Throwable => xmlLogger.warn("Error Serializing Xml For: " + obj, e)
        null
    }
  }

  def fromXml(xml: NodeSeq): Any = {
    fromXml(xml.toString())
  }

  def fromXml(obj: String): Any = {
    try {
      xstream.fromXML(obj)
    } catch {
      case e: Throwable => xmlLogger.warn("Error DeSerializing Xml For: " + obj, e)
        null
    }
  }

  protected def xstream: XStream = {
    new XStream()
  }

  // TODO: better ways to do conversion
  def nodes2elem(nodes: NodeSeq): JElement = nodes2elem(nodes.toString())
  def nodes2elem(nodes: String): JElement = {
    val builder = documentFactory.newDocumentBuilder()
    val is = new InputSource(new StringReader(nodes))
    val doc = builder.parse(is)
    doc.getDocumentElement
  }

  def jnode2scala(node: JNode) = {
    val source = new DOMSource(node)
    val adapter = new NoBindingFactoryAdapter
    val saxResult = new SAXResult(adapter)
    val transformer = transformerFactory.newTransformer()
    transformer.transform(source, saxResult)
    adapter.rootElem
  }
}
