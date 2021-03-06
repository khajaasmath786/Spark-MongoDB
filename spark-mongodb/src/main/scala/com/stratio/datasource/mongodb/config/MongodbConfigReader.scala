/**
 * Copyright (C) 2015 Stratio (http://stratio.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stratio.datasource.mongodb.config

import com.mongodb.casbah.Imports._
import com.mongodb.{MongoCredential, ServerAddress}
import com.stratio.datasource.mongodb.config.MongodbConfig._
import com.stratio.datasource.util.Config

object MongodbConfigReader {

  implicit class MongodbConfigFunctions(config: Config) {
    @transient protected[mongodb] val hosts : List[ServerAddress] =
      config[List[String]](MongodbConfig.Host)
        .map(add => new ServerAddress(add))

    @transient protected[mongodb] val credentials: List[MongoCredential] =
      config.getOrElse[List[MongodbCredentials]](MongodbConfig.Credentials, MongodbConfig.DefaultCredentials).map{
        case MongodbCredentials(user,database,password) =>
          MongoCredential.createCredential(user,database,password)
      }

    @transient protected[mongodb] val sslOptions: Option[MongodbSSLOptions] =
      config.get[MongodbSSLOptions](MongodbConfig.SSLOptions)

    @transient protected[mongodb] val writeConcern: WriteConcern = config.get[String](MongodbConfig.WriteConcern) match {
      case Some(wConcern) => parseWriteConcern(wConcern)
      case None => DefaultWriteConcern
    }

    protected[mongodb] val clientOptions = config.properties.filterKeys(_.contains(MongodbConfig.ListMongoClientOptions))
  }

}