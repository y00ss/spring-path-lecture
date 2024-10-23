package com.springpath.cashcard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cashcards")
public class CashCardController {

    private final CashCardRepository cashCardRepository;

    public CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CashCard> findById(@PathVariable Long id) {

        Optional<CashCard> cashCardOptional = cashCardRepository.findById(id);
        return cashCardOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping()
    public ResponseEntity<List<CashCard>> findAll(Pageable pageable) {
        Page<CashCard> page = cashCardRepository.findAll(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize()
                ));
        return page.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(page.getContent());
    }

    @PostMapping("")
    public ResponseEntity<CashCard> create(@RequestBody CashCard cashCard, UriComponentsBuilder ucb) {
        CashCard savedCashCard = cashCardRepository.save(cashCard);
        URI locationOfNewCashCard = ucb
                .path("cashcards/{id}")
                .buildAndExpand(savedCashCard.id())
                .toUri();
        return ResponseEntity
                .created(locationOfNewCashCard).body(savedCashCard);
    }
}
