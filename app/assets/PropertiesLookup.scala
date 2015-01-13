package assets

import java.io._
import java.util.{Properties, Enumeration}

/**
 * Read property values from various sources in follow order of preference. system property properties file environment variable internal default value
 *
 * Consider adding database table
 */

object PropertiesLookup {
  val defaultPropertiesFileProperty = "ap.config.file"
  val defaultPropertiesFileName = "conf/app.cfg"

  val defaultCredentialsFileProperty = "ap.credentials.file"
  val defaultCredentialsFileName = "conf/credentials.cfg"
  lazy val credentialsProperties = loadCredentialsConfig

  /* Default value for Dashboard cache time out - will be overridden in test*/
  val DASHBOARD_CACHE_TIMEOUT_DEFAULT = 300000L

  def asString(x:String): String = x
  def asInt(x:String): Int = x.toInt
  def asDouble(x:String): Double = x.toDouble
  def asFloat(x:String): Float = x.toFloat
  def asLong(x:String): Long = x.toLong

  def getValue(name: String, defaultValue: String):String = getValue(name, defaultValue, asString)
  def getValue(name: String, defaultValue: Int):Int = getValue(name, defaultValue, asInt)
  def getValue(name: String, defaultValue: Long):Long = getValue(name, defaultValue, asLong)
  def getValue(name: String, defaultValue: Double):Double = getValue(name, defaultValue, asDouble)
  def getValue(name: String, defaultValue: Float):Float = getValue(name, defaultValue, asFloat)

  def asTripCommandProcessorTimeoutMillis = getValue("ap.trip.command.timeout", 120000l)

  def getValue2[T](name1: String, name2: String, defaultValue: T, conv: String => T): T = {
    getValue(name1, getValue(name2, defaultValue, conv), conv)
  }

  def getValue[T](name: String, defaultVal: T, conv: String => T): T = {
    try {
      val prop = System.getProperty(name)
      val sys = System.getenv(name.replace('.', '_'))

      if (prop == null) {
        if (sys == null) defaultVal
        else conv(sys)
      } else conv(prop)
    } catch {
      case ex: Exception =>
        println(s"Problem converting $name")
        defaultVal
    }
  }

  val properties = loadAppConfigurationProperties
  // Add in overriding properties
  val overridingProperties = System.getProperties
  val e = overridingProperties.propertyNames

  while (e.hasMoreElements) {
    val key = e.nextElement().toString
    properties.setProperty(key, overridingProperties.getProperty(key))
  }

  System.setProperties(properties)

  // NOTE: Credentials value will be read from file one. One cannot overwrite those on the command line
  def getCredentialsValue2(name1:String, name2: String, defaultVal: String):String =
    getCredentialsValue(name1, getCredentialsValue(name2, defaultVal))

  def getCredentialsValue(name: String, defaultVal: String): String =
    if (credentialsProperties == null)  defaultVal
    else credentialsProperties.getProperty (name, defaultVal)

  def getATPCOIdenUser:String = getValue("ap.atpco.iden.user", "bagtest")

  def getATPCOIdenPwd:String = getValue("ap.atpco.iden.pwd", "bagtest")

  def getATPCOProviderSession:String = getValue("ap.atpco.provider.session", "TEST")

  def getMongoDBURLs = getValue("ap.mongodb.url", "mongodb://localhost")
  def getMongoDBUser = getValue("ap.mongodb.user", "")
  def getMongoDBPassword = getValue("ap.mongodb.password", "")

  def getMongoConnectionsPerHost: Int = {
    return getValue("ap.db.mongo.connnections.per.host", 10)
  }

  def loadAppConfigurationProperties : Properties = {
    val filename = System.getProperty(defaultPropertiesFileProperty, defaultPropertiesFileName)
    loadPropertiesFromFile(filename)
  }

  def loadCredentialsConfig : Properties = {
    val filename = System.getProperty(defaultCredentialsFileProperty, defaultCredentialsFileName)
    loadPropertiesFromFile(filename)
  }

  def loadPropertiesFromFile(filename : String) : Properties = {
    val properties = new Properties

    try {
      val reader = new FileReader(filename)

      properties.load(reader)
    }
    catch {
      case fnfe: FileNotFoundException => 
        println(s"Configuration file '$filename' was not found. Resolving to defaults. ${fnfe.getMessage}")
        new Properties
      case ioe: IOException => 
        throw new RuntimeException(ioe)
    }
    properties
  }
}

