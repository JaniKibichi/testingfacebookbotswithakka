package com.janikibichi.models

import spray.json._
import com.janikibichi.utils.MessengerBot._

case class Error(message:String)

case class FacebookInput()

trait EventMarshalling extends DefaultJsonProtocol{
    //Facebook Messages
    implicit val quickreplyFormat = jsonFormat1(QuickReply)
    implicit val payloadFormat = jsonFormat1(Payload)
    implicit val attachmentFormat = jsonFormat4(Attachment)
    implicit val facebookmessageFormat = jsonFormat6(FacebookMessage)
    implicit val facebooksenderFormat = jsonFormat1(FacebookSender)
    implicit val facebookrecipientFormat = jsonFormat1(FacebookRecipient)
    implicit val facebookmessageeventinFormat = jsonFormat4(FacebookMessageEventIn)
    implicit val facebookmessageeventoutFormat = jsonFormat2(FacebookMessageEventOut)
    implicit val facebookentryFormat = jsonFormat3(FacebookEntry)
    implicit val facebookpayloadobjectFormat = jsonFormat2(FacebookPayloadObject)
}