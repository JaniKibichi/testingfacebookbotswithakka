package com.janikibichi.utils

import akka.http.scaladsl.server.{Directive0,Directives}
import akka.http.scaladsl.model.HttpRequest
import akka.stream.Materializer
import akka.util.ByteString
import java.nio.charset.StandardCharsets
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import org.apache.commons.codec.binary.Hex
import scala.concurrent.{Await,ExecutionContext}
import akka.http.scaladsl.model.{StatusCodes,StatusCode,HttpHeader,HttpRequest}
import scala.concurrent.duration._
import spray.json.JsonFormat

trait RouteSupport extends Directives {

    def verifyPayload(req: HttpRequest)(implicit materializer: Materializer, executioncontext: ExecutionContext): Directive0 = {

        def isValid(payload: Array[Byte], secret:String, expected:String): Boolean = {
            val secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),"HmacSHA1")
            val mac = Mac.getInstance("HmacSHA1")
            mac.init(secretKeySpec)
            val result = mac.doFinal(payload)

            val computedHash = Hex.encodeHex(result).mkString
            println(s"Computed hash: $computedHash")

            computedHash.equals(expected)
        }

        req.headers.find( _.name.equals("X-Hub-Signature")).map(_.value()) match{
            case Some(token)=>
                val payload = Await.result(req.entity.toStrict(5 seconds).map(_.data.decodeString("UTF-8")),5 second)
                    println(s"Token found. Receive token $token and payload $payload")
                val elements = token.split("=")
                val method = elements(0)
                val signatureHash = elements(1)

                if(isValid(payload.getBytes, BotConfig.facebook.appSecret,signatureHash))
                    pass
                else{
                    println(s"Tokens are different, expected ${signatureHash}")
                    complete(StatusCodes.Forbidden)
                }                    
            case None =>
                println(s"X-Hub-Signature is not defined")
                complete(StatusCodes.Forbidden)
        }
    }
}