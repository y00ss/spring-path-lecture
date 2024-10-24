package com.springpath.cashcard;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)

class CashcardApplicationTests {

	@Autowired
	TestRestTemplate restTemplate;


	@Test
	void shouldCreateANewCashCard() {
		CashCard newCashCard = new CashCard(null, 250.00, null);
		ResponseEntity<String> response = restTemplate.withBasicAuth("system-principal", "abc123")
				.postForEntity("/cashcards", newCashCard, String																																									.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		// Add assertions such as these
		DocumentContext documentContext = JsonPath.parse(response.getBody());
		Number id = documentContext.read("$.id");
		Double amount = documentContext.read("$.amount");

		assertThat(id).isNotNull();
		assertThat(amount).isEqualTo(250.00);
	}

	@Test
	void sholdReturnListOfCashCards() {

		ResponseEntity<String> response = restTemplate
				.withBasicAuth("system-principal", "abc123")
				.getForEntity("/cashcards", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		Number id = documentContext.read("$[0].id");
		Double amount = documentContext.read("$[0].amount");
		assertThat(id).isNotNull();
		assertThat(amount).isNotNull();
	}

	@Test
	void shouldReturnACashCardWhenDataIsSaved(){
		ResponseEntity<String> response = restTemplate.withBasicAuth("system-principal", "abc123")
				.getForEntity("/cashcards/99", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		Number id = documentContext.read("$.id");
		assertThat(id).isNotNull();
	}

	@Test
	void  shouldNotReturnCashCardWhenUsingWrongPassword(){

		ResponseEntity<String> response = restTemplate.withBasicAuth("system-principal", "WRONG-PASSWORD")
				.getForEntity("/cashcards/99", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	void shouldNoReturnCardWhenUserNotOwner() {
		ResponseEntity<String> response = restTemplate.withBasicAuth("system-principal-02", "abc123")
				.getForEntity("/cashcards/99", String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

	}

	@Test
	void shouldNotAllowToCashCardWhenUserNotOwner() {
		ResponseEntity<String> response = restTemplate.withBasicAuth("system-principal", "abc123")
				.getForEntity("/cashcards/101", String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}
}
