package com.ubirch.swagger.example

import org.scalatra._
import org.scalatra.swagger._
import org.scalatra.CorsSupport
import com.ubirch.swagger.example.Gremlin
import org.slf4j.{Logger, LoggerFactory}

// JSON-related libraries
import org.json4s.{DefaultFormats, Formats}

// JSON handling support from Scalatra
import org.scalatra.json._

class ApiController(implicit val swagger: Swagger) extends ScalatraServlet
  with NativeJsonSupport with SwaggerSupport with CorsSupport {

  def log : Logger = LoggerFactory.getLogger( this.getClass )


  // Allows CORS support to display the swagger UI when using the same network
  options("/*") {
    response.setHeader(
      "Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers")
    )
  }

  // Stops the FlowerController from being abstract
  protected val applicationDescription = "The native API, working with internal scalatra database"

  // Sets up automatic case class to JSON output serialization
  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  // Before every action runs, set the content type to be in JSON format.
  before() {
    contentType = formats("json")
  }

  /**
   * Retrieve a list of Vertex
   */
  val getVertexes =
    (apiOperation[List[Vertex]]("getVertexes")
      summary "Show all vertex"
      schemes "http" // Force swagger ui to use http instead of https, only need to say it once
      description "Shows all the vertexes in the vertex shop. You can search it too."
      parameter queryParam[Option[Int]]("id").description("An id to search for")
      responseMessage ResponseMessage(404, "404: Can't find vertex with the ID: idNumber")
      )


  get("/getVertexes", operation(getVertexes)) {
    params.get("id") match {
      case Some(id) =>
        VertexData.all.filter(_.id equals id.toInt) match {
          case res if res.nonEmpty =>
            VertexData.all filter (_.id equals id.toInt)
          case _ =>
            halt(404, "404: Can't find vertex with the ID: " + id)
        }
      case None => VertexData.all
    }
  }


  /**
    * Post a couple of vertex
    */
  val postCoupleVertex =
    (apiOperation[Vertex]("addNewVertex")
      summary "Add a couple of vertexes to the database and link them"
      parameters(
        pathParam[Int]("id1").description("id of the first vertex"),
        queryParam[Option[List[String]]]("properties1").description("Optional: Properties of the first vertex"),
        pathParam[Int]("id2").description("id of the second vertex"),
        queryParam[Option[List[String]]]("properties2").description("Optional: Properties of the first vertex"),
        queryParam[Option[List[String]]]("propertiesEdge").description("Optional: Properties of the edge that will link the two vertexes")
      )
    )

  post("/addVertexes/:id1/:id2", operation(postCoupleVertex)) {

    val propertiesVertex1 = params.get("properties1") match {
      case Some(_) => params("properties1").split(",").toList
      case None => List("")
    }

    val propertiesVertex2 = params.get("properties2") match {
      case Some(_) => params("properties2").split(",").toList
      case None => List("")
    }

    val propertiesOfTheEdge = params.get("propertiesEdge") match {
      case Some(_) => params("propertiesEdge").split(",").toList
      case None => List("")
    }

    val newVertex1 = new Vertex(params("id1").toInt, propertiesVertex1)
    val newVertex2 = new Vertex(params("id2").toInt, propertiesVertex2)

    // TODO: modify vertex id so that no duplicate can be made: change randon generation of ids for vertex
    // TODO: change properties of vertex
    val ourRand = new scala.util.Random
    val newEdge = new Edge(ourRand.nextInt(1000), params("id1").toInt, params("id2").toInt, propertiesOfTheEdge)

    // Adding the new vertexes and edges to the database
    VertexData.all = VertexData.all :+ newVertex1
    VertexData.all = VertexData.all :+ newVertex2
    EdgesData.all = EdgesData.all :+ newEdge

    println("newVertex1: " + newVertex1)
    println("newVertex2: " + newVertex2)
    println("newEdge   : " + newEdge)
  }

  val addToJanus =
    (apiOperation[Vertex]("addToJanusTwoVertexes")
      summary "Add two to JanusGraph"
      description "Just here to test JanusGraph"
      parameters(
        pathParam[Int]("id1").description("id of the first vertex"),
        queryParam[Option[String]]("name1").description("the name of the first vertex"),
        pathParam[Int]("id2").description("id of the second vertex"),
      queryParam[Option[String]]("name2").description("the name of the second vertex")
      )
    )



  /**
    * Get our precious edges
    */
  val getEdges =
    (apiOperation[List[Edge]]("getEdges")
      summary "Show all edges"
      description "Shows all the edges in the edge shop. You can search it too."
      parameter queryParam[Option[Int]]("id").description("An id to search for")
      responseMessage ResponseMessage(404, "404: Can't find edge with the ID: idNumber")
      )

  get("/getEdges", operation(getEdges)) {

    params.get("id") match {
      case Some(id) =>
        EdgesData.all.filter(_.id equals id.toInt) match {
          case res if res.nonEmpty =>
            EdgesData.all filter (_.id equals id.toInt)
          case _ =>
            halt(404, "404: Can't find edge with the ID: " + id)
        }
      case None => EdgesData.all
    }

  }



}

/**
  * Define our vertex data structure
  */
case class Vertex(id: Int, properties: List[String])

object VertexData {
  /**
    * Some vertexes to test our stuff
    * */
  var all: Seq[Vertex] = List(
    Vertex(1, List("OneOne", "OneTwo")),
    Vertex(2, List("TwoOne", "TwoTwo")),
    Vertex(3, List("ThreeOne", "ThreeTwo")),
    Vertex(11, List("ElevenOne", "ElevenTwo")),
  )
}


/**
  * Define the edge data structure
  */

case class Edge(id: Int, idV1: Int, idV2: Int, properties: List[String])

object EdgesData{
        /**
         * Some nice edges
         */
        var all:Seq[Edge]=List(
        Edge(100,1,2,List("Those two are linked cause they're prime")),
        Edge(101,3,11,List("Those two are linked cause: ","why not?")),
        )
  }
