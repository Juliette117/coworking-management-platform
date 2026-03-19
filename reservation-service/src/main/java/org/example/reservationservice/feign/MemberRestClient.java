package org.example.reservationservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// interroge l'annuaire Eureka pour trouver le "member-service"
@FeignClient(name = "member-service")
public interface MemberRestClient {

    // Récupère les données d'un membre spécifique (pour vérifier s'il est suspendu ou non)
    @GetMapping("/api/members/{id}")
    MemberDTO getMemberById(@PathVariable("id") Long id);
}
