package org.nentangso.core.web.rest.errors;

import org.junit.jupiter.api.Test;
import org.nentangso.core.NtsSaasApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests {@link NtsExceptionTranslator} controller advice.
 */
@AutoConfigureMockMvc
@SpringBootTest(classes = NtsSaasApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ExceptionTranslatorIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testUnauthorized() throws Exception {
        mockMvc
            .perform(get("/api/exception-translator-test/unauthorized"))
            .andExpect(status().isUnauthorized())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.errors").value("[API] Invalid API key or access token (unrecognized login or wrong password)"));
    }

    @Test
    void testAccessDenied() throws Exception {
        mockMvc
            .perform(get("/api/exception-translator-test/access-denied"))
            .andExpect(status().isForbidden())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.errors").value("[API] This action requires merchant approval for the necessary scope."));
    }

    @Test
    void testConcurrencyFailure() throws Exception {
        mockMvc
            .perform(get("/api/exception-translator-test/concurrency-failure"))
            .andExpect(status().isConflict())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.errors").value("Conflict"));
    }

    @Test
    void testExceptionWithResponseStatus() throws Exception {
        mockMvc
            .perform(get("/api/exception-translator-test/response-status"))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.errors").value("Bad Request"));
    }

    @Test
    void testNotFound() throws Exception {
        mockMvc
            .perform(get("/api/exception-translator-test/not-found"))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.errors").value("Not Found"));
    }

    @Test
    void testBadRequestAlert() throws Exception {
        mockMvc
            .perform(
                post("/api/exception-translator-test/bad-request-alert").content("{}").contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.errors.name[0]").value("Shop name is required"));
    }

    @Test
    void testFormValidation() throws Exception {
        mockMvc
            .perform(
                post("/api/exception-translator-test/form-validation").content("{}").contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.errors.phone[0]").value("Phone number is invalid"));
    }

    @Test
    void testMethodNotSupported() throws Exception {
        mockMvc
            .perform(post("/api/exception-translator-test/access-denied"))
            .andExpect(status().isNotAcceptable())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.errors").value("Not Acceptable"));
    }

    @Test
    void testMediaTypeNotSupported() throws Exception {
        mockMvc
            .perform(
                post("/api/exception-translator-test/form-validation").content("Hello world").contentType(MediaType.TEXT_PLAIN)
            )
            .andExpect(status().isUnsupportedMediaType())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.errors").value("Unsupported Media Type"));
    }

    @Test
    void testMediaTypeNotAcceptable() throws Exception {
        mockMvc
            .perform(
                post("/api/exception-translator-test/media-type-not-acceptable").content("Hello world").contentType(MediaType.TEXT_PLAIN)
            )
            .andExpect(status().isNotAcceptable())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.errors").value("Not Acceptable"));
    }

    @Test
    void testMethodArgumentNotValid() throws Exception {
        mockMvc
            .perform(
                post("/api/exception-translator-test/method-argument").content("{}").contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.errors.test[0]").value("must not be null"));
    }

    @Test
    void testMissingServletRequestPartException() throws Exception {
        mockMvc
            .perform(get("/api/exception-translator-test/missing-servlet-request-part"))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.errors.part[0]").value("Required request part 'part' is not present"));
    }

    @Test
    void testMissingServletRequestParameterException() throws Exception {
        mockMvc
            .perform(get("/api/exception-translator-test/missing-servlet-request-parameter"))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.errors.param[0]").value("Required request parameter 'param' for method parameter type String is not present"));
    }

    @Test
    void testInternalServerError() throws Exception {
        mockMvc
            .perform(get("/api/exception-translator-test/internal-server-error"))
            .andExpect(status().isInternalServerError())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.errors").value("Internal Server Error"));
    }

    @Test
    void testInternalServerErrorWithMessage() throws Exception {
        mockMvc
            .perform(get("/api/exception-translator-test/internal-server-error-with-message"))
            .andExpect(status().isInternalServerError())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.errors").value("test internal server error!"));
    }
}
