package com.jpro.routing

import simplefx.experimental._
import java.util.function.Predicate

@FunctionalInterface
trait Route {
  def apply(r: Request): FXFuture[Response]

  def and(x: Route): Route = { request =>
    val r = apply(request)
    if(r == null) {
      x.apply(request)
    } else {
      r.flatMap{ r =>
        if(r == null) {
          x.apply(request)
        } else FXFuture.unit(r)
      }
    }
  }
  def path(path: String, route: Route): Route = and((r: Request) => {
    if(r.path.startsWith(path + "/")) {
      val r2 = r.copy(path = r.path.drop(path.length))
      route.apply(r2)
    } else {
      null
    }
  })
  def filter(filter: Filter): Route = filter(this)
  def filterWhen(cond: Predicate[Request], filter: Filter): Route = { r =>
    if(cond.test(r)) {
      filter(this).apply(r)
    } else {
      this.apply(r)
    }
  }
  def when(cond: Predicate[Request], _then: Route): Route = and(r => {
    val condResult = cond.test(r)
    val r2: FXFuture[Response] = if(condResult) _then(r) else null
    println("got condResult: " + condResult)
    println("got R: " + r2)
    r2
  })
  def when(cond: java.util.function.Function[Request, FXFuture[java.lang.Boolean]], _then: Response): Route = and(r => {
    cond.apply(r).map(condResult => if(condResult) _then else null)
  })
  def when(cond: Predicate[Request], _then: Route, _else: Route): Route = and(r => {
    if(cond.test(r)) _then(r) else _else(r)
  })
}
