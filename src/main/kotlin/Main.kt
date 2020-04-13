import dto.Response
import io.ktor.application.Application
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get

const val BASE_URL = "https://api.openweathermap.org"

suspend fun foo(city: String, apiKey: String): Double? {
    val client = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = GsonSerializer()
        }
    }
    val data = client.get<Response> {
        url("${BASE_URL}/data/2.5/weather")
        parameter("q", city)
        parameter("appid", apiKey)
        parameter("units", "metric")
        parameter("lang", "ru")
    }
    client.close()
    return data.main?.temp
}

data class Weather(val city: String, val temp: Double)
data class Error(val message: String)

fun Application.main() {
    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation) {
        gson()
    }
    install(Routing) {
        get("/city") {
            val city = call.parameters["name"]
            val apiKey = application.environment.config
                .property("ktor.api.key")?.getString()
            if (city != null) {
                val temp = foo(city, apiKey)
                if (temp != null) {
                    call.respond(Weather(city, temp))
                } else {
                    call.respond(Error("Температура не определена"))
                }
            } else {
                call.respond(Error("Город указан неверно"))
            }
        }
    }
}