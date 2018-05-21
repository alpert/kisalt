package com.github.alpert.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.alpert.AbstractTest;
import com.github.alpert.entities.Url;
import com.github.alpert.exception.UrlNotFoundException;
import com.github.alpert.service.UrlService;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class UrlControllerTest extends AbstractTest {

    private final ObjectMapper mapper = new ObjectMapper();
    private       MockMvc      mockMvc;

    @MockBean
    private UrlService urlService;

    @Before
    public void setUp() {
        UrlController urlController = new UrlController(urlService);

        this.mockMvc = MockMvcBuilders.standaloneSetup(urlController).build();
    }

    @Test
    public void shorten_a_valid_url() throws Exception {
        String validUrl = "http://alpert.github.com";

        Url url = new Url("", "", validUrl, System.currentTimeMillis());
        given(urlService.createUrl(validUrl))
                .willReturn(url);

        MockHttpServletResponse response = mockMvc.perform(
                post("/v1/shorten")
                        .content(validUrl))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        String valueAsString = mapper.writeValueAsString(url);
        assertThat(response.getContentAsString()).isEqualTo(valueAsString);
        ;
    }

    @Test
    public void throw_exception_when_non_existent_short_url() throws Exception {
        String nonExistentId = "dfasd";

        given(urlService.getUrl(nonExistentId))
                .willThrow(new UrlNotFoundException("URL for [dfasd] not found"));

        MockHttpServletResponse response = mockMvc.perform(
                get("/v1/dfasd"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void get_info_for_url() throws Exception {
        String validUrl = "http://alpert.github.com";

        Url value = new Url("", "", validUrl, System.currentTimeMillis());
        given(urlService.getUrl("dfasd"))
                .willReturn(value);

        MockHttpServletResponse response = mockMvc.perform(
                get("/v1/infos/dfasd"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        String valueAsString = mapper.writeValueAsString(value);
        assertThat(response.getContentAsString()).isEqualTo(valueAsString);
    }

    @Test
    public void redirect_to_short_url() throws Exception {
        String validUrl = "http://alpert.github.com";

        Url value = new Url("", "", validUrl, System.currentTimeMillis());
        given(urlService.getUrl("dfasd")).willReturn(value);
        given(urlService.createUri(value)).willReturn(new URI(validUrl));

        MockHttpServletResponse response = mockMvc.perform(
                get("/v1/dfasd"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.SEE_OTHER.value());
    }
}