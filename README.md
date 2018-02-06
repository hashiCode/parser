parser
========
[![Build Status](https://travis-ci.org/hashiCode/parser.svg?branch=master)](https://travis-ci.org/hashiCode/parser)
[![Coverage Status](https://coveralls.io/repos/github/hashiCode/parser/badge.svg?branch=master)](https://coveralls.io/github/hashiCode/parser?branch=master)

### Parameters

* **accesslog**: path to you log file.
* **startDate**: date in "yyyy-MM-dd.HH:mm:ss" format.
* **duration**: can be "hourly" or "daily".
* **threshold**: an integer.

### Example
```bash
java -cp "parser.jar" com.ef.Parser --accesslog=/path/to/file --startDate=2017-01-01.13:00:00 --duration=hourly --threshold=100
```