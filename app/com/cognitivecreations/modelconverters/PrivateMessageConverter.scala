package com.cognitivecreations.modelconverters

import java.util.UUID

import com.cognitivecreations.dao.mongo.dao.mongomodel.PrivateMessageMongo
import models.PrivateMessage
import org.joda.time.DateTime

/**
 * Created by Todd Sieland-Peteson on 5/25/15.
 */
class PrivateMessageConverter extends ModelConverterBase[PrivateMessage, PrivateMessageMongo] {
  def fromMongo(privateMessage: PrivateMessageMongo): PrivateMessage = {
    PrivateMessage(
      id = Some(privateMessage.id),
      fromUser = Some(privateMessage.fromUser),
      toUser = Some(privateMessage.toUser),
      subject = privateMessage.subject,
      body = privateMessage.body,
      attachments = privateMessage.attachments,
      newMessage = Some(privateMessage.newMessage),
      read = Some(privateMessage.read),
      replyId = privateMessage.replyId,
      created = privateMessage.created,
      updated = privateMessage.lastUpdated,
      opened = privateMessage.opened
    )
  }

  def toMongo(privateMessage: PrivateMessage): PrivateMessageMongo = {
    PrivateMessageMongo(
      id = privateMessage.id.getOrElse(UUID.randomUUID()),
      fromUser = privateMessage.fromUser.get,
      toUser = privateMessage.toUser.get,
      subject = privateMessage.subject,
      body = privateMessage.body,
      attachments = privateMessage.attachments,
      newMessage = privateMessage.newMessage.getOrElse(true),
      read = privateMessage.read.getOrElse(false),
      replyId = privateMessage.replyId,
      created = privateMessage.created,
      lastUpdated = privateMessage.updated,
      opened = privateMessage.opened
   )
  }
}
