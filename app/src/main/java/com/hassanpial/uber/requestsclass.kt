package com.hassanpial.uber

class requestsclass(
    ratinggiven:String,
    triprating:String,
triptype:String,
    drivername:String,
    tripid:String,
    driverphone:String,
    time: String,
    var price:String,
    destination:String,
    pickuplocation:String,
    var reachornot:String,
    driveruid:String) {
    var rtnggvn=ratinggiven
    var name=drivername
    var id=tripid
    var phn=driverphone
    var tm=time
    var dest=destination
    var pkloc=pickuplocation
    var rchornt=reachornot
    var prc=price
    var drvruid=driveruid
    var trptp=triptype
    var trprating=triprating
}