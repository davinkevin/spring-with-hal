package com.github.davinkevin.springwithhal

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux
import reactor.core.publisher.toFlux
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
            podcasts.withDomain(host)

}

data class Podcast(val id: UUID, val title: String, val _links: HalLinks = HalLinks()) {

    init { _links.computeIfAbsent("self") { "/api/v1/podcasts/$id" } }
    fun addDomain(host: String) = copy(_links = _links.addDomain(host))

}

class HalLinks(m: Map<String, String> = mapOf()) : HashMap<String, String>(m) {
    fun addDomain(host: String): HalLinks = HalLinks(mapValues { "$host${it.value}" })
}

private fun Flux<Podcast>.withDomain(host: String) = this.map { it.addDomain(host) }