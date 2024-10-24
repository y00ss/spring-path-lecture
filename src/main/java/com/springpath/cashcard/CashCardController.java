package com.springpath.cashcard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/cashcards")
public class CashCardController {

    private final CashCardRepository cashCardRepository;

    public CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CashCard> findById(@PathVariable Long id, Principal  principal) {

        CashCard cashCard = findCashCardByIdAndOwner(id, principal);

        if (cashCard == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cashCard);
    }



    @GetMapping()
    public ResponseEntity<List<CashCard>> findAll(Pageable pageable, Principal principal) {
        Page<CashCard> page = cashCardRepository.findByOwner(principal.getName(),
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize()
                ));
        return page.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(page.getContent());
    }

    @PostMapping("")
    public ResponseEntity<CashCard> create(@RequestBody CashCard cashCard, UriComponentsBuilder ucb, Principal principal) {
        cashCard.setOwner(principal.getName());
        CashCard savedCashCard = cashCardRepository.save(cashCard);
        URI locationOfNewCashCard = ucb
                .path("cashcards/{id}")
                .buildAndExpand(savedCashCard.getId())
                .toUri();
        return ResponseEntity
                .created(locationOfNewCashCard).body(savedCashCard);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody CashCard cashCardUpdate, Principal principal){
        CashCard cashCard = findCashCardByIdAndOwner(id, principal);

        if (cashCard == null) {
            return ResponseEntity.notFound().build();
        }

        cashCard.setAmount(cashCardUpdate.getAmount());
        cashCardRepository.save(cashCard);
        return ResponseEntity.noContent().build();

    }

    private CashCard findCashCardByIdAndOwner(Long id, Principal principal) {
        return cashCardRepository.findByIdAndOwner(id, principal.getName());
    }
}
