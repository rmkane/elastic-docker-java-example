# Elasticsearch

- Based on: <https://www.baeldung.com/elasticsearch-java>
- Code: <https://github.com/eugenp/tutorials/tree/master/persistence-modules/spring-data-elasticsearch>
- See also: <https://logz.io/blog/elasticsearch-tutorial/>
- Java client: <https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/current/getting-started-java.html>

## Delete Index

```js
fetch('http://localhost:9200/person', { method: 'DELETE' })
```

## View Index

See: <http://localhost:9200/person/_search?pretty>
