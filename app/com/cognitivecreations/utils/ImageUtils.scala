package com.cognitivecreations.utils

import java.io.{FileNotFoundException, InputStream}
import java.net.{MalformedURLException, URL}

import com.cognitivecreations.dao.mongo.Logging
import org.apache.commons.codec.binary.Base64
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils

/**
 * Shared utilities related to image manipulations.
 */
trait ImageUtils extends Logging {

  private val DATA_URL_TEMPLATE = "data:%s;base64,%s"

  private val EXT_PATTERN = """.*[.]([^.]+)""".r

  /**
   * Create a data url by encoding the given stream as Base64 string. Note, one should use this method with caution
   * since it can lead to memory and performance problem.
   *
   * @param stream the stream to encode. This stream should point to a byte array that can fit in memory. The stream will
   *               be closed after the method returns.
   * @param mimeType the mime type such as 'image/png', image/jpg' or other.
   * @return the data url.
   */
  def encodeImageToDataUrl(stream: InputStream, mimeType: String): String = {

    try {

      val useMimeType = if (StringUtils.isBlank(mimeType)) "application/octet-stream" else mimeType
      val bytes = IOUtils.toByteArray(stream)
      val encodedData = Base64.encodeBase64String(bytes)

      DATA_URL_TEMPLATE.format(useMimeType, encodedData)
    } catch {
      case ex: Throwable => {
        warn("Failed to encode the given input stream to a data url.", ex)
        ""
      }

    } finally {
      IOUtils.closeQuietly(stream)
    }
  }

  /**
   * Encodes the given image url to a data url using Base64 encoder.  Note, one should use this method with caution
   * since it can lead to memory and performance problem.
   *
   * @param imageUrlText the url to an image.
   * @return the data url.
   */
  def encodeImageToDataUrl(imageUrlText: String): String = {
    def mimeType = imageUrlText match {
      case EXT_PATTERN("jpg") => "image/jpeg"
      case EXT_PATTERN(ext) => "image/" + ext
      case _ => "application/octet-stream"
    }

    try {
      encodeImageToDataUrl(new URL(imageUrlText).openStream(), mimeType)

    } catch {
      case ex: MalformedURLException => ""
      case ex: FileNotFoundException => ""
      case ex: Throwable =>
        log.warn("Unknown error while encoding image (URL: " + imageUrlText + "): " + ex)
        ""
    }
  }

  // proxy the image URL using front end server
  def proxyImageUrl(url: String): String =  if (StringUtils.trimToEmpty(url).startsWith("http")) {
    "/external/images?url=" + url
  } else {
    url
  }

  def proxySSLImage(url: String, ssl: Boolean) = {
    if(ssl){
      proxyImageUrl(url)
    } else {
      url
    }
  }

}

object ImageUtils extends ImageUtils
