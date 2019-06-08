package com.janikibichi.utils

object MessengerBot{
    case class QuickReply(payload:String)
    case class Payload(url:String)
    case class Attachment(
        `type`:Option[String]=None,
        payload:Option[Payload]=None,
        title: Option[String]=None,
        URL: Option[String]=None
    )
    case class FacebookMessage( 
        mid: Option[String]=None, 
        seq: Option[Long]=None, 
        text: Option[String]=None, 
        metadata: Option[String] = None, 
        attachments: List[Option[Attachment]] = List(None),    
        quick_reply: Option[QuickReply]=None
    )
    case class FacebookSender(id: String)
    case class FacebookRecipient(id: String)
    case class FacebookMessageEventIn(
        sender: FacebookSender,
        recipient: FacebookRecipient,
        timestamp: Long,
        message: FacebookMessage
    )
    case class FacebookMessageEventOut(
        recipient: FacebookRecipient,
        message: FacebookMessage
    )
    case class FacebookEntry(
        id: String,
        time: Long,
        messaging: List[FacebookMessageEventIn]
    )
    case class FacebookPayloadObject(
        `object`:String,
        entry: List[FacebookEntry]
    )
}