package io.github.free.lock.sjson

class ErrorJSONTest extends org.scalatest.FunSuite {
  test("tokenParser: string end up with \\") {
    try {
      JSON.parse(s""""1234\\""")
      throw new Exception("should have error")
    } catch {
      case e: Exception => {
        assert(e.getMessage.indexOf("parse error") !== -1)
      }
    }
  }

  test("tokenParser: missing \"") {
    try {
      JSON.parse(s""""1234""")
      throw new Exception("should have error")
    } catch {
      case e: Exception => {
        assert(e.getMessage.indexOf("parse error") !== -1)
      }
    }
  }

  test("tokenParser: - without number") {
    try {
      JSON.parse(s"""-""")
      throw new Exception("should have error")
    } catch {
      case e: Exception => {
        assert(e.getMessage.indexOf("expect number after '-'") !== -1)
      }
    }
  }

  test("tokenParse: unrecorgnized symbol") {
    try {
      JSON.parse(s"""1234k""")
      throw new Exception("should have error")
    } catch {
      case e: Exception => {
        assert(e.getMessage.indexOf("unrecorgnized symbol 'k'") !== -1)
      }
    }
  }

  test("tokenParse: unit type") {
    val fun = () => {}
    assert(JSON.stringify(fun()) == "null")
  }

  test("tokenParse: escape error") {
    try {
      JSON.parse(s""""\\a"""")
      throw new Exception("should have error")
    } catch {
      case e: Exception => {
        assert(e.getMessage.indexOf("errored char a after escape char") !== -1)
      }
    }
  }

  test("tokenParse: unicode error") {
    try {
      JSON.parse(s""""\\u"""")
      throw new Exception("should have error")
    } catch {
      case e: Exception => {
        assert(e.getMessage.indexOf("unicode format example") !== -1)
      }
    }
  }

  test("tokenParse: unicode error2") {
    try {
      JSON.parse(s""""\\u12"""")
      throw new Exception("should have error")
    } catch {
      case e: Exception => {
        assert(e.getMessage.indexOf("unicode format example") !== -1)
      }
    }
  }

  test("tokenParse: unicode error3") {
    try {
      JSON.parse(s""""\\u12b"""")
      throw new Exception("should have error")
    } catch {
      case e: Exception => {
        assert(e.getMessage.indexOf("unicode format example") !== -1)
      }
    }
  }
}
