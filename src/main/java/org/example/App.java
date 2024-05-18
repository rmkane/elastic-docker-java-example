package org.example;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.io.StringReader;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Slf4j
public class App {
    record Person(int age, String fullName, String dateOfBirth) {
    }

    // Method to convert epoch to LocalDate
    public static LocalDate convertEpochToLocalDate(long epoch) {
        return Instant.ofEpochMilli(epoch)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static String convertEpochToString(long epoch) {
        return convertEpochToLocalDate(epoch).format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public App() throws IOException {
        ElasticsearchClient client = createClient();

        createPerson(client);
        createPersonString(client);
        searchPerson(client);
    }

    private void searchPerson(ElasticsearchClient client) throws IOException {
        String searchText = "John*";
        SearchResponse<Person> searchResponse = client.search(s -> s
                .index("person")
                .query(q -> q
                        .wildcard(t -> t
                                .field("fullName")
                                .value(searchText))), Person.class);

        List<Hit<Person>> hits = searchResponse.hits().hits();

        log.info("Found {}", hits);

        assertEquals(1, hits.size());
        assertEquals("John Doe", hits.getFirst().source().fullName());
    }

    private void createPersonString(ElasticsearchClient client) throws IOException {
        String jsonString = """
                {
                  "age": 10,
                  "fullName": "John Doe",
                  "dateOfBirth": "2016-08-17"
                }
                """;
        StringReader stringReader = new StringReader(jsonString);
        IndexResponse response = client.index(i -> i
                .index("person")
                .id("John Doe")
                .withJson(stringReader));

        log.info("Indexed with version: {}", response.version());
        assertEquals(Result.Created, response.result());
        assertEquals("person", response.index());
        assertEquals("John Doe", response.id());
    }

    private void createPerson(ElasticsearchClient client) throws IOException {
        Person person = new Person(20, "Mark Doe", convertEpochToString(1471466076564L));
        IndexResponse response = client.index(i -> i
                .index("person")
                .id(person.fullName())
                .document(person));

        log.info("Indexed with version: {}", response.version());
        assertEquals(Result.Created, response.result());
        assertEquals("person", response.index());
        assertEquals("Mark Doe", response.id());
    }

    private ElasticsearchClient createClient() {
        RestClient restClient = RestClient
                .builder(HttpHost.create("http://localhost:9200"))
                .build();

        // Create a custom ObjectMapper and register the JavaTimeModule
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Create the JacksonJsonpMapper with the custom ObjectMapper
        JacksonJsonpMapper jsonpMapper = new JacksonJsonpMapper(objectMapper);

        ElasticsearchTransport transport = new RestClientTransport(restClient, jsonpMapper);

        return new ElasticsearchClient(transport);
    }

    private static boolean assertEquals(Object a, Object b) {
        boolean areEqual = Objects.equals(a, b);
        log.info("ASSERT: {} =?= {} => {}", a, b, areEqual);
        return areEqual;
    }

    public static void main(String[] args) throws IOException {
        new App();
    }
}
