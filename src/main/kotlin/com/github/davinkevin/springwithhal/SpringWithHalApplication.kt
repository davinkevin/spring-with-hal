package com.github.davinkevin.springwithhal

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.toFlux
import java.net.URL
import java.util.*

@SpringBootApplication
class SpringWithHalApplication

fun main(args: Array<String>) {
    runApplication<SpringWithHalApplication>(*args)
}


val podcasts = listOf(
        Podcast(UUID.randomUUID(), "GDG Toulouse"),
        Podcast(UUID.randomUUID(), "Le Rendez vous tech")
).toFlux()

@RestController("/api/v1/podcasts")
class PodcastController {

    @GetMapping
    fun findAll(exchange: ServerWebExchange, @RequestHeader("Host") host: String) =
            podcasts
                    .map { PodcastHAL(it, host) }

}

data class Podcast(val id: UUID, val title: String)

class PodcastHAL(p: Podcast, host: String) {

    val id = p.id
    val title = p.title

    @JsonProperty("_links")
    val links = mutableMapOf<String, URL>().apply {
        this["self"] = URL("https://$host/api/v1/podcasts/${p.id}")
    }
}