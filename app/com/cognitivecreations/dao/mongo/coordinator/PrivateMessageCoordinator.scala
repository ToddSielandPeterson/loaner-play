package com.cognitivecreations.dao.mongo.coordinator

import java.util.UUID

import com.cognitivecreations.dao.mongo.dao.PrivateMessageDao
import com.cognitivecreations.modelconverters.PrivateMessageConverter
import models.PrivateMessage
import reactivemongo.core.commands.LastError

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by Todd Sieland-Peteson on 5/25/15.
 */
class PrivateMessageCoordinator(implicit ec: ExecutionContext) extends PrivateMessageConverter with CoordinatorBase[PrivateMessage] {

  val privateMessageDao = new PrivateMessageDao()

  def findByPrimary(uuid: UUID): Future[Option[PrivateMessage]] = {
    for {
      optPrivateMessage <- privateMessageDao.findById(uuid.toString)
    } yield optPrivateMessage.map(fromMongo)
  }

  def findByFromUser(uuid: UUID): Future[List[PrivateMessage]] = {
    for {
      imageList <- privateMessageDao.findByFromUser(uuid)
    } yield imageList.map(fromMongo)
  }

  def findByToUser(uuid: UUID): Future[List[PrivateMessage]] = {
    for {
      imageList <- privateMessageDao.findByToUser(uuid)
    } yield imageList.map(fromMongo)
  }

  def insert(privateMessage: PrivateMessage): Future[LastError] = {
    privateMessageDao.insert(toMongo(privateMessage))
  }

  def update(privateMessage: PrivateMessage): Future[LastError] = {
    findByPrimary(privateMessage.id).flatMap {
      case None => failed(s"Product ${privateMessage.id.toString} does not exist")
      case Some(s) => privateMessageDao.update(toMongo(privateMessage))
    }
  }


}
