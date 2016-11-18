package uk.gov.hmrc.eeitt.repositories

import play.api.Logger
import play.api.libs.json.Json
import reactivemongo.api.DB
import reactivemongo.api.indexes.{ Index, IndexType }
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.eeitt.model.{ GroupId, IndividualRegistration, RegimeId, RegisterAgentRequest, RegisterBusinessUserRequest }
import uk.gov.hmrc.mongo.ReactiveRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ ExecutionContext, Future }

trait RegistrationRepository {
  def findRegistrations(groupId: GroupId, regimeId: RegimeId): Future[List[IndividualRegistration]]

  def register(registrationRequest: RegisterBusinessUserRequest): Future[Either[String, Unit]]
}

class MongoRegistrationRepository(implicit mongo: () => DB)
    extends ReactiveRepository[IndividualRegistration, BSONObjectID]("registrationBusinessUsers", mongo, IndividualRegistration.oFormat) with RegistrationRepository {

  override def ensureIndexes(implicit ec: ExecutionContext): Future[Seq[Boolean]] = {
    Future.sequence(Seq(
      collection.indexesManager.ensure(Index(Seq("groupId" -> IndexType.Ascending), name = Some("groupId"), unique = true, sparse = false))
    ))
  }

  def findRegistrations(groupId: GroupId, regimeId: RegimeId): Future[List[IndividualRegistration]] = {
    Logger.debug(s"lookup individual registration with group id '${groupId.value}' and regime id ${regimeId.value} in database ${collection.db.name}")
    find(
      "groupId" -> groupId,
      "regimeId" -> regimeId
    )
  }

  def register(rr: RegisterBusinessUserRequest): Future[Either[String, Unit]] = {
    val registration = IndividualRegistration(rr.groupId, rr.registrationNumber, rr.regimeId)
    insert(registration) map {
      case r if r.ok => Right(())
      case r => Left(r.message)
    }
  }
}
