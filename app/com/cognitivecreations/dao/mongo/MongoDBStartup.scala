package com.cognitivecreations.dao.mongo

import com.cognitivecreations.dao.mongo.MDCExecutionContext.Implicits.global
import play.api.{Mode, Application, GlobalSettings}
import scala.concurrent.duration._
import com.cognitivecreations.dao.mongo.{MongoDBManager, MongoDBUtils}

trait MongoDBStartUp extends GlobalSettings {
  override def beforeStart(app: Application) {
    super.beforeStart(app)
    if(app.mode != Mode.Test) {
      MongoDBUtils.connectToMongo()

      /**
       * TODO - this is a hack to try and get the asynchronous reactive mongo authentication requests for each db to happen
       * before any app tries to grab its own DB instance and use it. So far as I can tell there isn't a good way to guarantee the
       * authentication happens in time (short of writing a bunch of ugly code to authenticate manually and force other instances
       * to wait for the response), but this seems to help.
       */
      MongoDBManager.connect
    }
  }

  override def onStop(app: Application) {
    super.onStop(app)
    if(app.mode != Mode.Test) {
      MongoDBUtils.disconnectFromMongo()

      MongoDBManager.connection.askClose()(10.seconds).onComplete {
        case e => {
          MongoDBManager.driver.close
        }
      }
    }
  }
}
