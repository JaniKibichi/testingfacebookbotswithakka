package com.janikibichi.models

import scala.concurrent.Future
import akka.actor._
import akka.util.Timeout
import com.janikibichi.utils._

object VerifyFacebook{
    def props(implicit timeout:Timeout) = Props(new VerifyFacebook)
    def name="verifyfacebook"

    case class Verify(mode:String,token: String, challenge: String) 

    sealed trait VerifyResponse
    case class FacebookVerified(signature:String) extends VerifyResponse
    case object FBNotVerified extends VerifyResponse
}

class VerifyFacebook(implicit timeout: Timeout) extends Actor with ActorLogging{
    import VerifyFacebook._
    import context._

    def receive: Actor.Receive = {
        case Verify(mode,token,challenge) =>
            log.info(s"Received $mode mode, $token token, $challenge challenge")
            val challengeOrNot: Option[Either[String,String]] = FacebookService.verifyToken(token, mode, challenge)

            if(challengeOrNot.getOrElse("NoChallenge").equals("NoChallenge")){
                sender() ! FBNotVerified
            }else{
                sender() ! FacebookVerified(challenge)
            }            
    }
}