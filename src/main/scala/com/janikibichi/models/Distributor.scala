package com.janikibichi.models

import scala.concurrent.{Future,Await,ExecutionContext}
import akka.stream.ActorMaterializer
import akka.actor._
import akka.util.Timeout
import scala.concurrent.duration._
import akka.pattern.ask 
import com.janikibichi.utils.{MessengerBot,FacebookService}

object Distributor{
    def props(implicit timeout:Timeout) = Props(new Distributor)
    def name = "distributor"
    final case class CreateMenu(facebookPayloadObject: MessengerBot.FacebookPayloadObject)
    final case class Menu(title:String,body:String,options:String,menuLevel:String)
    final case class Menus(menus: Vector[Menu])
    sealed trait MenuResponse
    case class MenuCreated(menu: Menu) extends MenuResponse
    case object MenuNotCreated extends MenuResponse

}

class Distributor(implicit timeout: Timeout) extends Actor with ActorLogging{
    import Distributor._
    import context._

    protected implicit def executioncontext: ExecutionContext
    protected implicit val materializer: ActorMaterializer

    def createAirtimeSeller(name:String) = context.actorOf(AirtimeSeller.props(name),AirtimeSeller.name)

    def receive: Actor.Receive = {
        case CreateMenu(facebookPayloadObject) =>
            log.info("Getting apppropriate menu")
            val expectingnone:  Option[Either[String,String]] = FacebookService.handleMessage(facebookPayloadObject)

            if(expectingnone.getOrElse("ExpectedResponse").equals("ExpectedResponse")){

            }

            lazy val airtimeSeller = createAirtimeSeller(senderID)
            val menuFuture = airtimeSeller.ask(CreateMenu(userInput,senderID)).mapTo[MenuResponse]
            sender() ! Await.result(menuFuture,5.seconds)
    }
}