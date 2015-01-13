package com.cognitivecreations.dao.mongo


import reactivemongo.core.commands.LastError

trait MongoLogging { self: Logging =>
  /**
   * Logs all errors and returns the list of failures
   */
  protected def logLastError(lastErrorList: Seq[LastError]): Seq[LastError] = {
    val badLastErrors = lastErrorList.filter(_.ok == false)
    badLastErrors foreach { error =>
      warn(lastErrorToLogMessage(error))
    }

    badLastErrors
  }

  protected def logLastErrorOnce(lastErrorList: Seq[LastError]) {
    lastErrorList.find(_.ok == false) map { error =>
      warn(lastErrorToLogMessage(error))
    }
  }

  protected def lastErrorToLogMessage(error: LastError) =
    s"${error.code.getOrElse(0)}:${error.err.getOrElse("")} ${error.errMsg.getOrElse("Unknown Error")}"


}
