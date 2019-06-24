import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    val port = System.getenv("PORT") ?: "8080"
    embeddedServer(Netty, port.toInt()) {
        install(ContentNegotiation) {
            jackson {
                enable(SerializationFeature.INDENT_OUTPUT)
            }
        }
        routing {
            get("/") {
                call.respondText("Hello :)")
            }
            route("/posts") {
                get {
                    call.respond(initPosts())
                }
                route("/{id}") {
                    get {
                        val result = initPosts().filter { it.id == call.parameters["id"] }.getOrNull(0)
                        result?.let { call.respond(it) } ?: call.respond(HttpStatusCode.NotFound)
                    }
                }
            }
        }
    }.start(wait = true)
}

fun initPosts() = listOf(
        Post("immutable-collections", "Immutable Collections In Java – Not Now, Not Ever", "nipa"),
        Post("guide-to-java12", "Definitive Guide To Java 12", "nipa"),
        Post("rest-vs-websockets", "REST vs WebSockets", "Kumar Chandrakant")
)
