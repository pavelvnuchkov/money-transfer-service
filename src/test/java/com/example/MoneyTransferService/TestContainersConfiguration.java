package com.example.MoneyTransferService;

import com.example.MoneyTransferService.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestContainersConfiguration {
    @Autowired
    private TestRestTemplate template;
    @Container
    private final GenericContainer<?> myApp = new GenericContainer<>("transfer3.0").withExposedPorts(8085);


    @Test
    void testTransferOk() {
        OperationDto operationDto = new OperationDto();
        operationDto.setCardFromNumber("1111111111111111");
        operationDto.setCardFromValidTill("11/26");
        operationDto.setCardFromCVV("123");
        operationDto.setCardToNumber("2222222222222222");
        operationDto.setAmount(new Amount(20000, "RUR"));
        AnswerOkDto answerOkDto = new AnswerOkDto("1");
        Integer port = myApp.getMappedPort(8085);
        ResponseEntity<AnswerOkDto> entity = template.postForEntity("http://localhost:" + port + "/transfer", operationDto, AnswerOkDto.class);
        System.out.println(entity.getBody());
        Assertions.assertEquals(answerOkDto, entity.getBody());
    }

    @Test
    void testTransferNotCard() {
        OperationDto operationDto = new OperationDto();
        operationDto.setCardFromNumber("1111111111111112");
        operationDto.setCardFromValidTill("11/26");
        operationDto.setCardFromCVV("123");
        operationDto.setCardToNumber("2222222222222222");
        operationDto.setAmount(new Amount(20000, "RUR"));
        ExceptionDto dataException = new ExceptionDto("1", "1");
        Integer port = myApp.getMappedPort(8085);
        ResponseEntity<String> entity = template.postForEntity("http://localhost:" + port + "/transfer", operationDto, String.class);
        System.out.println(entity.getStatusCode());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
    }

    @Test
    void testTransferNotEnough() {
        OperationDto operationDto = new OperationDto();
        operationDto.setCardFromNumber("1111111111111111");
        operationDto.setCardFromValidTill("11/26");
        operationDto.setCardFromCVV("123");
        operationDto.setCardToNumber("2222222222222222");
        operationDto.setAmount(new Amount(200000000, "RUR"));
        Integer port = myApp.getMappedPort(8085);
        ResponseEntity<String> entity = template.postForEntity("http://localhost:" + port + "/transfer", operationDto, String.class);
        System.out.println(entity.getStatusCode());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, entity.getStatusCode());
    }

    @Test
    void testTransferWrongMany() {
        OperationDto operationDto = new OperationDto();
        operationDto.setCardFromNumber("1111111111111111");
        operationDto.setCardFromValidTill("11/26");
        operationDto.setCardFromCVV("123");
        operationDto.setCardToNumber("2222222222222222");
        operationDto.setAmount(new Amount(200000000, "RUR"));
        Integer port = myApp.getMappedPort(8085);
        ResponseEntity<String> entity = template.postForEntity("http://localhost:" + port + "/transfer", operationDto, String.class);
        System.out.println(entity.getStatusCode());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, entity.getStatusCode());
    }

    @Test
    void testConfirmOperationOk() {
        OperationDto operationDto = new OperationDto();
        operationDto.setCardFromNumber("1111111111111111");
        operationDto.setCardFromValidTill("11/26");
        operationDto.setCardFromCVV("123");
        operationDto.setCardToNumber("2222222222222222");
        operationDto.setAmount(new Amount(20000, "RUR"));
        AnswerOkDto answerOkDto = new AnswerOkDto("1");
        Integer port = myApp.getMappedPort(8085);
        template.postForEntity("http://localhost:" + port + "/transfer", operationDto, AnswerOkDto.class);
        CodeDto codeDto = new CodeDto("0000", "1");
        ResponseEntity<AnswerOkDto> entity = template.postForEntity("http://localhost:" + port + "/confirmOperation", codeDto, AnswerOkDto.class);
        Assertions.assertEquals(answerOkDto, entity.getBody());
    }

    @Test
    void testConfirmOperationExceptionCode() {
        OperationDto operationDto = new OperationDto();
        operationDto.setCardFromNumber("1111111111111111");
        operationDto.setCardFromValidTill("11/26");
        operationDto.setCardFromCVV("123");
        operationDto.setCardToNumber("2222222222222222");
        operationDto.setAmount(new Amount(20000, "RUR"));
        Integer port = myApp.getMappedPort(8085);
        ResponseEntity<AnswerOkDto> entity1 = template.postForEntity("http://localhost:" + port + "/transfer", operationDto, AnswerOkDto.class);
        CodeDto codeDto = new CodeDto("000", "1");
        ResponseEntity<String> entity = template.postForEntity("http://localhost:" + port + "/confirmOperation", codeDto, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
    }

    @Test
    void testConfirmOperationExceptionId() {
        OperationDto operationDto = new OperationDto();
        operationDto.setCardFromNumber("1111111111111111");
        operationDto.setCardFromValidTill("11/26");
        operationDto.setCardFromCVV("123");
        operationDto.setCardToNumber("2222222222222222");
        operationDto.setAmount(new Amount(20000, "RUR"));
        Integer port = myApp.getMappedPort(8085);
        ResponseEntity<AnswerOkDto> entity1 = template.postForEntity("http://localhost:" + port + "/transfer", operationDto, AnswerOkDto.class);
        CodeDto codeDto = new CodeDto("000", "");
        ResponseEntity<String> entity = template.postForEntity("http://localhost:" + port + "/confirmOperation", codeDto, String.class);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, entity.getStatusCode());
    }

}
