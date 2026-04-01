package com.eotd;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
    "app.frontend-url=http://localhost:5173",
    "app.admin-emails=redrachelmason@gmail.com,mydatacollection@gmail.com"
})
class EarringOfTheDayApplicationTests {

    @Test
    void contextLoads() {
    }
}
