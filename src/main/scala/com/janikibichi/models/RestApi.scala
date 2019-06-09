package com.janikibichi.models

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.ExecutionContext
import com.janikibichi.routes._
import com.janikibichi.utils._
import akka.stream.ActorMaterializer


class RestApi(system: ActorSystem, timeout: Timeout) extends RestRoutes{
    implicit val requestTimeout = timeout
    implicit def executionContext = system.dispatcher

    def createDistributor    = system.actorOf(Distributor.props,Distributor.name)
    def createVerifyFacebook = system.actorOf(VerifyFacebook.props, VerifyFacebook.name)
}

trait DistributorApi{
    import Distributor._
    import VerifyFacebook._

    def createDistributor(): ActorRef
    def createVerifyFacebook():ActorRef
    
    implicit def executionContext: ExecutionContext
    implicit def requestTimeout: Timeout

    lazy val distributor    = createDistributor()
    lazy val verifyfacebook = createVerifyFacebook()

    def verifyFacebook(verify: VerifyFacebook.Verify)                                = verifyfacebook.ask(Verify(verify.mode,verify.token,verify.challenge)).mapTo[VerifyResponse]
    def createMenu(facebookPayloadObject: MessengerBot.FacebookPayloadObject)        = distributor.ask(CreateMenu(facebookPayloadObject)).mapTo[MenuResponse]
}