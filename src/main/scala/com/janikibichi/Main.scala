package com.janikibichi
import com.typesafe.config.{Config,ConfigFactory}
import akka.util.Timeout
import akka.event.Logging
import akka.actor.ActorSystem
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.Http
import com.janikibichi.models.RestApi
import akka.stream.ActorMaterializer
import scala.concurrent.Future


object Main extends App with RequestTimeout{
    val config = ConfigFactory.load()
    val host = config.getString("http.host")
    val port = config.getInt("http.port")

    implicit val system = ActorSystem()
    implicit val executioncontext = system.dispatcher
    implicit val materializer = ActorMaterializer()   

    val api = new RestApi(system,requestTimeout(config)).routes
 
    val bindingFuture: Future[ServerBinding] = Http().bindAndHandle(api,host,port)

    val log = Logging(system.eventStream,"airtimeseller")

    bindingFuture.map{ serverBinding =>
        log.info(s"RestAPI bound to ${serverBinding.localAddress}")
    }.onFailure{
        case exception:Exception =>
            log.error(exception,"Failed to bind to {}:{}!",host,port)
            system.terminate()
    }
}
trait RequestTimeout{
    import scala.concurrent.duration._
    def requestTimeout(config:Config): Timeout ={
        val time = config.getString("akka.http.server.request-timeout")
        val date = Duration(time)
        FiniteDuration(date.length,date.unit)
    }
}