# Sample Data

- Video: <https://www.youtube.com/watch?v=n9mE5MXGkaA>
- Data: <https://github.com/sematext/berlin-buzzwords-samples/tree/master/2014/sample-documents>

## Set up sharding

```js
fetch('http://localhost:9200/videosearch', {
    method: 'PUT',
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({
        settings: {
            number_of_shards: 3,
            number_of_replicas: 1
        }
    })
}).then(res => res.json()).then(data => console.log(data));
```

## Add data

```sh
cd sample-documents
for file in *.json; do echo -n $file; curl -XPOST -H 'Content-Type:application/json' localhost:9200/videosearch/_doc/ -d "`cat $file`"; echo; done
```


```js
fetch('http://localhost:9200/videosearch/_doc', {
    method: 'POST',
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({
		id: "1",
		url: "https://www.youtube.com/watch?v=I06NWbtj4nQ",
		title: "Search Analytics",
		uploaded_by: "LuceneSolrRevolution",
		upload_date: "2024-10-24",
		views: 70,
		tags: ["business value", "big data", "search analytics"]
	})
}).then(res => res.json()).then(data => console.log(data));
```

## View data

View index: <http://localhost:9200/_cat/shards/videosearch?v>

```js
fetch('http://localhost:9200/videosearch/_search', {
    method: 'POST',
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({
		query: {
			match: {
				title: "SEMATEXT"
			}
		}
	})
}).then(res => res.json()).then(data => console.log(data));
```
