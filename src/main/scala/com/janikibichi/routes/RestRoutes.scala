package com.janikibichi.routes

import akka.actor._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.ExecutionContext
import com.janikibichi.models.{EventMarshalling,DistributorApi,Error,Distributor}
import com.janikibichi.models.VerifyFacebook.{FacebookVerified,FBNotVerified,Verify}
import com.janikibichi.utils.RouteSupport
import akka.http.scaladsl.model.{StatusCode,HttpHeader,StatusCodes}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.HttpRequest
import com.janikibichi.utils.MessengerBot
import akka.stream.ActorMaterializer

trait RestRoutes extends DistributorApi with EventMarshalling with RouteSupport{
    import StatusCodes._
    
    def routes: Route = facebookVerifyRoute ~ facebookRoute
    def facebookVerifyRoute = 
        pathPrefix("facebook"){ 
            pathEndOrSingleSlash{
                get{
                    //GET /facebook
                    parameters("hub.mode","hub.verify_token","hub.challenge").as(Verify){ verify =>
                        onSuccess(verifyFacebook(verify)){
                            case FacebookVerified(challenge) => 
                                complete{
                                    (StatusCodes.OK, List.empty[HttpHeader],challenge)
                                }
                            case FBNotVerified =>
                                complete {
                                    StatusCodes.Forbidden
                                }
                                
                        }
                    }

                }
            }
        }

    def facebookRoute = {
        extractRequest{ request: HttpRequest =>

            pathPrefix("facebook" / Segment){ facebook =>

                pathEndOrSingleSlash{

                    post{
                        //POST /facebook/:facebook
                        entity(as[MessengerBot.FacebookPayloadObject]){ facebookPayloadObject =>
                            onSuccess(createMenu(facebookPayloadObject)){
                                case Distributor.MenuCreated(menu) =>
                                    complete{
                                        StatusCodes.OK
                                    }
                                case Distributor.MenuNotCreated =>
                                    complete{
                                        StatusCodes.BadRequest
                                    }
                            }

                        }
                        
                    }
                }
            }
        }
    }
    
}