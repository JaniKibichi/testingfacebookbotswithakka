package com.janikibichi.utils
import scala.concurrent.ExecutionContext
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import scala.concurrent.Future
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.MediaTypes
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.model.Uri
import java.io.IOException
import scala.util.Success,scala.util.Failure

object HttpClient{

    def post(uri:String, body:Array[Byte])(implicit executionContext: ExecutionContext,system:ActorSystem,materializer:ActorMaterializer): Future[Unit]={
        val entity = HttpEntity(MediaTypes.`application/json`,body)
        val response: Future[HttpResponse] = Http().singleRequest(HttpRequest(HttpMethods.POST,Uri(uri),entity=entity))

        val result = response.flatMap{ response =>
            response.status match{
                case status if status.isSuccess =>
                    Future.successful()

                case status =>
                    Future.successful(throw new IOException(s"Token request failed with status ${response.status} and error ${response.entity}"))
            }
        }

        result.onComplete{
            case Success(response) => 
                println(s"Success after sending response $response")
            
            case Failure(exception) =>
                println(s"Failure after sending response to $uri $exception")
        }
        
        result
    }

}