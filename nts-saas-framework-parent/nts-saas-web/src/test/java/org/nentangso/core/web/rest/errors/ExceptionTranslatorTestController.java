package org.nentangso.core.web.rest.errors;

import org.nentangso.core.service.errors.NtsValidationException;
import org.nentangso.core.service.errors.NtsNotFoundException;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collections;

@RestController
@RequestMapping("/api/exception-translator-test")
public class ExceptionTranslatorTestController {

    @GetMapping("/access-denied")
    public void accessdenied() {
        throw new AccessDeniedException("test access denied!");
    }

    @GetMapping("/unauthorized")
    public void unauthorized() {
        throw new BadCredentialsException("test authentication failed!");
    }

    @GetMapping("/response-status")
    public void exceptionWithResponseStatus() {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "test bad request!");
    }

    @GetMapping("/concurrency-failure")
    public void concurrencyFailure() {
        throw new ConcurrencyFailureException("test concurrency failure");
    }

    @GetMapping("/not-found")
    public void notFound() {
        throw new NtsNotFoundException("test not found!");
    }

    @PostMapping("/bad-request-alert")
    public void badRequestAlert() {
        throw new BadRequestAlertException("Shop name is required", "shop", "name");
    }

    @PostMapping(value = "/form-validation", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void formValidation() {
        throw new NtsValidationException("phone", "Phone number is invalid");
    }

    @PostMapping("/media-type-not-acceptable")
    public void mediaTypeNotAcceptable() throws HttpMediaTypeNotAcceptableException {
        throw new HttpMediaTypeNotAcceptableException(Collections.singletonList(MediaType.APPLICATION_JSON));
    }
    @PostMapping("/method-argument")
    public void methodArgument(@Valid @RequestBody TestDTO testDTO) {
    }

    @GetMapping("/missing-servlet-request-part")
    public void missingServletRequestPartException(@RequestPart String part) {
    }

    @GetMapping("/missing-servlet-request-parameter")
    public void missingServletRequestParameterException(@RequestParam String param) {
    }

    @GetMapping("/internal-server-error")
    public void internalServerError() {
        throw new RuntimeException();
    }

    @GetMapping("/internal-server-error-with-message")
    public void internalServerErrorWithMessage() throws Exception {
        throw new Exception("test internal server error!");
    }

    public static class TestDTO {

        @NotNull
        private String test;

        public String getTest() {
            return test;
        }

        public void setTest(String test) {
            this.test = test;
        }
    }
}
