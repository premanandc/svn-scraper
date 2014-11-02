svn-scraper
===========

Runs a web app to provide commit stats in json.

To run from the command line
```
gradle clean build
java -jar build/libs/svn-scraper-0.0.1-SNAPSHOT.war
curl http://localhost:8080/revisions?url=http://achartengine.googlecode.com/svn/trunk
```
This will produce which looks like
```json
[
   {
       "testPercentage": 0,
       "size": 1,
       "author": "dandromereschi@gmail.com",
       "hash": "r569",
       "ts": 1410852123
   },
   {
       "testPercentage": 0,
       "size": 3,
       "author": "dandromereschi@gmail.com",
       "hash": "r568",
       "ts": 1410851534
   }
]
```