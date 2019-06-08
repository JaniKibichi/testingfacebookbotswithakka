package com.janikibichi.utils

import scala.concurrent.{ExecutionContext,Future}
import akka.http.scaladsl.model.{StatusCode,HttpHeader,StatusCodes}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.janikibichi.models.EventMarshalling
import MessengerBot._
import com.janikibichi.utils.HttpClient._
import spray.json._

object FacebookService extends EventMarshalling{

    def verifyToken(token:String, mode:String, challenge:String)(implicit executioncontext: ExecutionContext): Option[Either[String,String]] ={

        if( mode.equals("subscribe") && token.equals(BotConfig.facebook.verifyToken) ){
            println(s"Verify webhook token: ${token}")

            Some(Left(challenge))
        }else{
            println(s"Invalid webhook token: ${token}, mode ${mode}")

            None
        }
    }

    def handleMessage(facebookpayloadobject: FacebookPayloadObject)(implicit executioncontext: ExecutionContext,system:ActorSystem, materializer:ActorMaterializer): Option[Either[String,String]] = {
        println(s"Receive a facebookpayloadobject:${facebookpayloadobject}")
        facebookpayloadobject.entry.foreach{
            entry =>
            entry.messaging.foreach{ msg =>
                val senderId = msg.sender.id 
                val message = msg.message
                //received message, create Airtime Seller with name=senderId, get menu
                
                
                //assemble and reply with quick reply
                message.text match{
                    case Some(text) =>
                        val facebookMessage = FacebookMessageEventOut(
                            recipient = FacebookRecipient(senderId),
                            message = FacebookMessage(
                                text = Some(s"Scala messenger bot"),
                                metadata = Some("DEVELOPER_DEFINED_METADATA")
                            )
                        ).toJson.toString().getBytes

                        HttpClient
                            .post(s"${BotConfig.facebook.responseUri}?access_token=${BotConfig.facebook.pageAccessToken}",facebookMessage)
                            .map(_ => ())
                    case None =>
                        println("Receive image")
                        Future.successful(())
                        
                }


            }
        }
        None
    }
}

