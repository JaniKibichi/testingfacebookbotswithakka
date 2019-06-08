package com.janikibichi.models

import scala.concurrent.duration.FiniteDuration
import akka.actor.{Actor,ActorLogging,FSM,Props}
import AirtimeSeller._
import scala.concurrent.Future
import akka.util.Timeout
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit

object AirtimeSeller{
    def props(recipientId:String) = Props(new AirtimeSeller(recipientId))
    def name="airtimesller"

    //Data passed in FSM
    sealed trait AirtimeData
    case object NoPurchase extends AirtimeData
    case class HandlePhone(phoneNumber:String) extends AirtimeData
    case class HandleAmount(amount: String) extends AirtimeData

    //State to move user through
    sealed trait AirtimeSellerState
    case object WaitingForAccept extends AirtimeSellerState //Ask for Acceptance
    case object AskingAmount extends AirtimeSellerState //Ask for Amount
    case object AskingWhoToTopUp extends AirtimeSellerState //Ask for Number
    case object AskMPESAPIN extends AirtimeSellerState //Ask for PIN
    case object AwaitingAirtimeDeliveryReport extends AirtimeSellerState // Ask Airtime Delivery Report

    //Event passed in FSM
    sealed trait AirtimeDistributionEvent
    case object TakePoisonPill extends AirtimeDistributionEvent //Terminate Actor
    case object NoAnswer extends AirtimeDistributionEvent //No Answer from User
    case class UserInitiates(phoneNumber: String) extends AirtimeDistributionEvent //User Dials In
    case class TermsAccepted(terms: String) extends AirtimeDistributionEvent //User Accepts Terms
    case class AmountEntered(amount: String) extends AirtimeDistributionEvent //User Enters Amount
    case class NumberEntered(number:String) extends AirtimeDistributionEvent //User Enters Phone to Top Up
    case class MpesaConfirmation(mpesastatus:String) extends AirtimeDistributionEvent //AT Sends MPESA API Confirmation
    case class AirtimeConfirmation(airtimestatus:String) extends AirtimeDistributionEvent //AT Sends AIRTIME API Confirmation
}

class AirtimeSeller(recipientId:String) extends Actor with ActorLogging with FSM[AirtimeSellerState,AirtimeData]{
    import context.dispatcher

    implicit lazy val timeout = Timeout(5.seconds)
    val poisonPillTimeout: FiniteDuration = FiniteDuration(40, TimeUnit.SECONDS)

    startWith(WaitingForAccept,NoPurchase)
    context.system.scheduler.scheduleOnce(poisonPillTimeout,self,TakePoisonPill)

    //Ask User to Accept Terms and Conditions
    when(WaitingForAccept){
        case Event(UserInitiates(phoneNumber),NoPurchase) =>
        log.info("Phone Number is here, serving T&C Menu")
        stay()

        case Event(TermsAccepted(terms),_) =>
        log.info("User accepts T&C, Store Consent")
        goto(AskingAmount) 
    }

    when(AskingAmount){
        case Event(AmountEntered(amount),_) =>
        log.info("User entered amount. Pass amount data internally.")
        goto(AskingWhoToTopUp) 
    }

    when(AskingWhoToTopUp){
        case Event(NumberEntered(number),_) =>
        log.info("Getting number to top up")
        if(number.equals("1")){
            log.info("User topping up own phone")
            goto(AskMPESAPIN) using HandlePhone(number)
        }else if(number.length >=10){
            log.info("Input phone to top up")
            goto(AskMPESAPIN) using HandlePhone(number)
        }else{
            log.info("User topping up other phone, serve request for phoneNumber")
            stay()
        }
    }

    when(AskMPESAPIN){
        case Event(MpesaConfirmation(mpesastatus),_) =>
        log.info("Mpesa confirmation")
        if(mpesastatus.equals("Success")) {
            goto(AwaitingAirtimeDeliveryReport) 
        }else {
            log.info("Mpesa Failed")
            stay()
        }

    }

    when(AwaitingAirtimeDeliveryReport){
        case Event(AirtimeConfirmation(airtimestatus),_) =>
        log.info("Airtime confirmation")
        if(airtimestatus.equals("Success")){
            stop()
        }else{
            log.info("You should send again.")
            stay()
        }
    }

    whenUnhandled{
        case Event(TakePoisonPill, _) =>
        log.info("Wrong pill...")
        stop()
    }

    initialize()

}