import sbt._

object MicroServiceBuild extends Build with MicroService {

  import scala.util.Properties.envOrElse

  val appName = "eeitt"
  val appVersion = envOrElse("EEITT_VERSION", "999-SNAPSHOT")

  override lazy val appDependencies: Seq[ModuleID] = AppDependencies()
}

private object AppDependencies {

  import play.core.PlayVersion
  import play.sbt.PlayImport._

  private val microserviceBootstrapVersion = "5.8.0"
  private val playAuthVersion = "4.2.0"
  private val playHealthVersion = "2.0.0"
  private val logbackJsonLoggerVersion = "3.1.0"
  private val playUrlBindersVersion = "2.0.0"
  private val playConfigVersion = "4.2.0"
  private val domainVersion = "4.0.0"
  private val hmrcTestVersion = "2.2.0"
  private val scalaTestVersion = "3.0.1"
  private val pegdownVersion = "1.6.0"
  private val httpCachingClientVersion = "6.1.0"

  private val playReactivemongoVersion = "5.1.0"
  private val reactiveMongoTestVersion = "2.0.0"

  val compile = Seq(
    "uk.gov.hmrc" %% "play-reactivemongo" % playReactivemongoVersion,

    ws,
    "uk.gov.hmrc" %% "microservice-bootstrap" % microserviceBootstrapVersion,
    "uk.gov.hmrc" %% "play-authorisation" % playAuthVersion,
    "uk.gov.hmrc" %% "play-health" % playHealthVersion,
    "uk.gov.hmrc" %% "play-url-binders" % playUrlBindersVersion,
    "uk.gov.hmrc" %% "play-config" % playConfigVersion,
    "uk.gov.hmrc" %% "logback-json-logger" % logbackJsonLoggerVersion,
    "uk.gov.hmrc" %% "domain" % domainVersion,
    "uk.gov.hmrc" %% "http-caching-client" % httpCachingClientVersion)

  trait TestDependencies {
    lazy val scope: String = "test"
    lazy val test: Seq[ModuleID] = ???
    protected val httpVerbsTest = "0.1.0"
  }

  object Test {
    def apply() = new TestDependencies {
      override lazy val test = Seq(
        "uk.gov.hmrc" %% "hmrctest" % hmrcTestVersion % scope,
        "org.scalatest" %% "scalatest" % scalaTestVersion % scope,
        "org.pegdown" % "pegdown" % pegdownVersion % scope,
        "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
        "uk.gov.hmrc" %% "reactivemongo-test" % reactiveMongoTestVersion % scope,
        "com.typesafe.akka" %% "akka-testkit" % "2.3.2" % scope,
        "org.scalamock" %% "scalamock-scalatest-support" % "3.4.2" % scope,
        "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0-M1" % scope
      )
    }.test
  }

  object IntegrationTest {
    def apply() = new TestDependencies {

      override lazy val scope: String = "it"

      override lazy val test = Seq(
        "uk.gov.hmrc" %% "hmrctest" % hmrcTestVersion % scope,
        "org.scalatest" %% "scalatest" % scalaTestVersion % scope,
        "org.pegdown" % "pegdown" % pegdownVersion % scope,
        "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
        "uk.gov.hmrc" %% "reactivemongo-test" % reactiveMongoTestVersion % scope,
        "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0-M1" % scope,
        "uk.gov.hmrc" %% "http-verbs-test" % httpVerbsTest % scope
      )
    }.test
  }

  def apply() = compile ++ Test() ++ IntegrationTest()
}
