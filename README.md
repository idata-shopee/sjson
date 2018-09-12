# sjson

JSON library for Scala

> Converting JSON and Scala automaticly

<!-- toc -->

- [JSON specification](#json-specification)
- [Features](#features)
- [Quick example](#quick-example)
- [Install](#install)
- [Main Apis](#main-apis)
  * [JSON.stringify(value: Any[, replacer]): String](#jsonstringifyvalue-any-replacer-string)
  * [JSON.parse(jsonTxt: String[, replacer]): Any](#jsonparsejsontxt-string-replacer-any)
  * [JSON.convert\[T](value: Any): T](#jsonconverttvalue-any-t)
  * [JSON.parseTo\[T](txt) = JSON.convert\[T](JSON.parse(txt))](#jsonparsetottxt--jsonconverttjsonparsetxt)
- [Streaming example](#streaming-example)

<!-- tocstop -->

## JSON specification

- http://json.org/
- https://www.ietf.org/rfc/rfc4627.txt

## Features

- Type converting automaticly

- Support customization converting

- Streaming support

## Quick example

```scala
import io.github.shopee.idata.sjson.JSON

// stringify
JSON.stringify(List(1,2,3)) // [1,2,3]
JSON.stringify(Map(
  "items": List(1,2,3),
  "type": "test"
)) // {"items": [1,2,3], "type": "testl"}

case class User(name: String, age: Int)
JSON.stringify(User("NoName", 7)) // {"name": "NoName", age: 7}

// parse
JSON.parse("[1,2,3]") // List(1,2,3)
JSON.parse(s"""{"a":1,"b":2}""") // Map("a" -> 1, "b" -> 2)
```

## Install

- sbt

```
libraryDependencies ++= Seq(
  "io.github.idata-shopee" %% "sjson" % "0.0.2"
)
```

## Main Apis

### JSON.stringify(value: Any[, replacer]): String

Convert a scala object to json text.

- Default converting rules

At most times, you just need to follow the basic rules and do not need to write any converters by yourself. SJSON would use following basic rules, stringify value recursively.

scala type | json type |
--- | --- | ---
String | string
Char | string
java.sql.Timestamp | string
java.util.Date | string
Number | number
Boolean | bool
None | null
Null | null
Unit | null
AbstractSeq[_] | array
scala.collection.mutable.AbstractSeq[_] | array
Array[_] | array
scala.collection.mutable.Map[_, _\] | object
scala.collection.immutable.Map[_, _\] | object 
other class | object (field -> value)

```scala
import io.github.shopee.idata.sjson.JSON

// case class
case class User(name: String, age: Int)
val user = User("ddchen", 10)
JSON.stringify(user) // {"name":"ddchen","age":10}
JSON.stringify(List(1, 2, 3)) // [1,2,3]
JSON.stringify(Map(
  "user" -> user,
  "data" -> List(1,2,3)
)) // {"user":{"name":"ddchen","age":10},"data":[1,2,3]}
```

- Customize replacer

If you want to change the process of stringify, you can use optional paramater `replacer`.

`replacer:  (data, path) -> Option[newData]`, data is the value you are stringifying currently and path is the reversed json path.

```scala
case class User(name: String, age: Int, login: java.util.Date)
val user = User("ddchen", 10, new java.util.Date(1990 - 1900, 2, 12))
JSON.stringify(user, (data, path) => {
  if (path.reserse.mkString(".") == "login") { // path stored json paths in a reversed way.
    val date = data.asInstanceOf[java.util.Date]
    Some(s""""${date.getYear() + 1900},${date.getMonth() + 1},${date.getDate()}"""") // new stringify result for data
  } else {
    None // do not change
  }
}) // {"name":"ddchen","age":10,"login":"1990,3,12"}
```

### JSON.parse(jsonTxt: String[, replacer]): Any

Convert a json text to a scala object

json type | scala type |
--- | --- | ---
string | String
number | Number
bool | Boolean
null | Null
array | List[Any]
object | Map[String, Any]

After parsing, you will get a plain scala object. Then you can use `JSON.convert[T](obj)` to convert this object to target scala type.

### JSON.convert\[T](value: Any): T

- Default converting rules

plain scala type | scala type |
--- | --- | ---
same type | same type 
List | Array
List | Vector
Map | Map
Map | Case class
Nothing | Null

### JSON.parseTo\[T](txt) = JSON.convert\[T](JSON.parse(txt))

## Streaming example

```scala
import io.github.shopee.idata.sjson.{JSON, AsyncIterator}
val textIter = new AsyncIterator[Char]()

// stream handle part
val parseIter = JSON.parseAsyncIterator(textIter, (data, pathStack, _) => {
    if (JSON.toJsonPath(pathStack) == "data.[0]") { // because we wiped this data, so the json path should always be "data.[0]"
      // handle data here
      print(data) // can caputure all data like {"value":"datak"}

      JSON.WIPE_VALUE // do not store data in memory
    } else data
  }
)

// another place to accept streaming data
textIter.pushList(s"""{"type": "normal", """) // 1st chunk
// data part
textIter.pushList(s""""data": [""") // 2th chunk
textIter.pushList(s"""{"value":"data1"}, {"value":"data2"}, """)
textIter.pushList(s"""{"value":"data3"}, {"value":"data4"}, """)
textIter.pushList(s"""{"value":"data5"}, {"value":"data6"}, """)
// ...
textIter.pushList(s"""{"value":"datan"}""")
textIter.pushList("]}") // last chunk
```
