package com.jpro.routing.filter.container

import com.jpro.routing.Request
import javafx.scene.Node

trait ContainerFactory {
  def isContainer(x: Node): Boolean

  def createContainer(): Node

  def setContent(c: Node, x: Node): Unit

  def getContent(c: Node): Node

  def setRequest(c: Node, r: Request): Unit
}
