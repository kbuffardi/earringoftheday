package com.eotd;

import com.eotd.model.EarringOfTheDay;
import com.eotd.repository.EarringOfTheDayRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.ZoneId;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class EarringOfTheDayControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EarringOfTheDayRepository eotdRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        eotdRepository.deleteAll();
    }

    @Test
    void getAll_returnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/eotd"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void createEotd_andGetAll() throws Exception {
        EarringOfTheDay eotd = new EarringOfTheDay();
        eotd.setDate(LocalDate.of(2024, 1, 15));
        eotd.setBrand("TestBrand");
        eotd.setProductName("Test Hoop");
        eotd.setReferralLink("https://example.com/product");
        eotd.setInstructions("**Great earrings!**");
        eotd.setProductImageUrl("https://example.com/image.jpg");
        eotd.setInstagramPostUrl("https://www.instagram.com/p/abc123/");
        eotd.setDisplayOrder(0);

        mockMvc.perform(post("/api/eotd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eotd)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.brand", is("TestBrand")))
                .andExpect(jsonPath("$.productName", is("Test Hoop")));

        mockMvc.perform(get("/api/eotd"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void updateEotd_updatesFields() throws Exception {
        EarringOfTheDay eotd = new EarringOfTheDay();
        eotd.setDate(LocalDate.of(2024, 1, 15));
        eotd.setBrand("OldBrand");
        eotd.setDisplayOrder(0);
        EarringOfTheDay saved = eotdRepository.save(eotd);

        saved.setBrand("NewBrand");

        mockMvc.perform(put("/api/eotd/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand", is("NewBrand")));
    }

    @Test
    void deleteEotd_removesEntry() throws Exception {
        EarringOfTheDay eotd = new EarringOfTheDay();
        eotd.setDate(LocalDate.of(2024, 1, 15));
        eotd.setDisplayOrder(0);
        EarringOfTheDay saved = eotdRepository.save(eotd);

        mockMvc.perform(delete("/api/eotd/" + saved.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/eotd"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getToday_returnsEntriesForToday() throws Exception {
        LocalDate today = LocalDate.now(ZoneId.of("America/Los_Angeles"));

        EarringOfTheDay eotd1 = new EarringOfTheDay();
        eotd1.setDate(today);
        eotd1.setBrand("Brand1");
        eotd1.setDisplayOrder(0);

        EarringOfTheDay eotd2 = new EarringOfTheDay();
        eotd2.setDate(today);
        eotd2.setBrand("Brand2");
        eotd2.setDisplayOrder(1);

        EarringOfTheDay yesterday = new EarringOfTheDay();
        yesterday.setDate(today.minusDays(1));
        yesterday.setBrand("Old");
        yesterday.setDisplayOrder(0);

        eotdRepository.save(eotd1);
        eotdRepository.save(eotd2);
        eotdRepository.save(yesterday);

        mockMvc.perform(get("/api/eotd/today"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].brand", is("Brand1")))
                .andExpect(jsonPath("$[1].brand", is("Brand2")));
    }

    @Test
    void redirect_returnsFoundAndRecordsClick() throws Exception {
        EarringOfTheDay eotd = new EarringOfTheDay();
        eotd.setDate(LocalDate.of(2024, 1, 15));
        eotd.setReferralLink("https://example.com/buy");
        eotd.setDisplayOrder(0);
        EarringOfTheDay saved = eotdRepository.save(eotd);

        mockMvc.perform(get("/api/eotd/" + saved.getId() + "/redirect"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com/buy"));
    }

    @Test
    void redirect_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/eotd/9999/redirect"))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_notFound_returns404() throws Exception {
        EarringOfTheDay eotd = new EarringOfTheDay();
        eotd.setDate(LocalDate.of(2024, 1, 15));
        eotd.setDisplayOrder(0);

        mockMvc.perform(put("/api/eotd/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eotd)))
                .andExpect(status().isNotFound());
    }
}
